package com.cmsr.onebase.module.etl.build.service.datasource;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.etl.build.service.DatasourceFactory;
import com.cmsr.onebase.module.etl.build.service.collector.MetadataCollector;
import com.cmsr.onebase.module.etl.build.service.collector.MetadataManager;
import com.cmsr.onebase.module.etl.build.service.datasource.vo.*;
import com.cmsr.onebase.module.etl.common.preview.ColumnDefine;
import com.cmsr.onebase.module.etl.build.service.preview.DataInspectService;
import com.cmsr.onebase.module.etl.common.preview.DataPreview;
import com.cmsr.onebase.module.etl.build.service.preview.vo.TablePreviewVO;
import com.cmsr.onebase.module.etl.common.entity.CatalogData;
import com.cmsr.onebase.module.etl.common.entity.ColumnData;
import com.cmsr.onebase.module.etl.common.entity.TableData;
import com.cmsr.onebase.module.etl.core.dal.database.*;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLDatasourceDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLTableDO;
import com.cmsr.onebase.module.etl.core.enums.CollectStatus;
import com.cmsr.onebase.module.etl.core.enums.ETLErrorCodeConstants;
import com.cmsr.onebase.module.etl.core.vo.DatasourcePageReqVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.metadata.type.DatabaseType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

@Service
@Slf4j
public class ETLDatasourceServiceImpl implements ETLDatasourceService {

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Resource
    private ETLDatasourceRepository datasourceRepository;

    @Resource
    private ETLCatalogRepository catalogRepository;

    @Resource
    private ETLSchemaRepository schemaRepository;

    @Resource
    private ETLTableRepository tableRepository;

    @Resource
    private ETLWorkflowTableRepository workflowTableRepository;

    @Resource
    private ETLFlinkMappingRepository flinkMappingRepository;

    @Resource
    private MetadataManager metadataManager;

    @Resource
    private MetadataCollector metadataCollector;

    @Resource
    private DatasourceFactory datasourceFactory;

    @Resource
    private DataInspectService dataInspectService;

    @Override
    public Boolean pingDatasource(TestConnectionVO pingVO) {
        ETLDatasourceDO datasourceDO = BeanUtils.toBean(pingVO, ETLDatasourceDO.class);
        return dataInspectService.testConnection(datasourceDO);
    }

    @Override
    public DatasourceRespVO queryDatasourceDetail(Long datasourceId) {
        ETLDatasourceDO datasourceDO = datasourceRepository.findById(datasourceId);
        if (datasourceDO == null) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.DATASOURCE_NOT_EXIST);
        }
        DatasourceRespVO datasourceRespVO = DatasourceRespVO.convertFrom(datasourceDO);
        datasourceRespVO.setConfig(datasourceDO.getConfig());
        return datasourceRespVO;
    }

    @Override
    public PageResult<DatasourceRespVO> getETLDatasourcePage(DatasourcePageReqVO pageReqVO) {
        PageResult<ETLDatasourceDO> pageDOs = datasourceRepository.getETLDatasourcePage(pageReqVO);
        List<DatasourceRespVO> respVOs = pageDOs.getList()
                .stream()
                .map(DatasourceRespVO::convertFrom)
                .toList();

        return new PageResult<>(respVOs, pageDOs.getTotal());
    }

    @Override
    public CommonResult<Long> createDatasource(ETLDatasourceCreateReqVO createReqVO) {
        Long applicationId = createReqVO.getApplicationId();
        String datasourceType = createReqVO.getDatasourceType();

        ETLDatasourceDO datasourceDO = new ETLDatasourceDO();
        datasourceDO.setApplicationId(applicationId);
        UUID uuid = UUID.randomUUID();
        datasourceDO.setDatasourceCode(uuid.toString());
        datasourceDO.setDatasourceName(createReqVO.getDatasourceName());
        datasourceDO.setDeclaration(createReqVO.getDeclaration());
        datasourceDO.setDatasourceType(datasourceType);
        datasourceDO.setConfig(createReqVO.getConfig());
        datasourceDO.setReadonly(createReqVO.getReadonly());
        // initialize collect status to `none`, infer datasource will be empty.
        datasourceDO.setCollectStatus(CollectStatus.NONE);
        complementJdbcDatasourceProperties(datasourceDO);
        datasourceDO = datasourceRepository.insert(datasourceDO);
        Long datasourceId = datasourceDO.getId();
        Boolean withCollect = createReqVO.getWithCollect();
        if (withCollect) {
            try {
                boolean collectResult = runMetadataCollect(LocalDateTime.now(), datasourceDO);
                if (!collectResult) {
                    ServiceExceptionUtil.exception(ETLErrorCodeConstants.METADATA_COLLECT_FAILED.getCode(),
                            ETLErrorCodeConstants.METADATA_COLLECT_FAILED.getMsg(),
                            datasourceId);
                }
            } catch (Exception e) {
                CommonResult.error(
                        ETLErrorCodeConstants.METADATA_COLLECT_FAILED.getCode(),
                        ETLErrorCodeConstants.METADATA_COLLECT_FAILED.getMsg() + ": " + e.getMessage(),
                        datasourceId);
            }
        }
        return CommonResult.success(datasourceId);
    }

    @Override
    public void updateDatasource(ETLDatasourceUpdateReqVO updateReqVO) {
        ETLDatasourceDO oldDatasource = datasourceRepository.findById(updateReqVO.getId());
        oldDatasource.setDatasourceName(updateReqVO.getDatasourceName());
        oldDatasource.setDeclaration(updateReqVO.getDeclaration());
        oldDatasource.setConfig(updateReqVO.getConfig());
        oldDatasource.setReadonly(updateReqVO.getReadonly());
        // udpate collect status to `required`, demonds user to execute at least once
        oldDatasource.setCollectStatus(CollectStatus.REQUIRED);
        complementJdbcDatasourceProperties(oldDatasource);
        datasourceRepository.update(oldDatasource);
    }

    private void complementJdbcDatasourceProperties(ETLDatasourceDO datasourceDO) {
        String datasourceType = datasourceDO.getDatasourceType();
        DatabaseType databaseType = DatasourceFactory.parseDatabaseType(datasourceType);
        Properties connectionProperties = JsonUtils.parseObject(datasourceDO.getConfig(), Properties.class);
        connectionProperties.put("driver", databaseType.driver());
        String jdbcUrl = DatasourceFactory.buildJdbcConnectionString(datasourceType, connectionProperties);
        connectionProperties.put("jdbcUrl", jdbcUrl);
        datasourceDO.setConfig(connectionProperties);
    }

    @Override
    public void deleteDatasource(Long datasourceId) {
        boolean entityExists = datasourceRepository.existsById(datasourceId);
        if (!entityExists) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.DATASOURCE_NOT_EXIST);
        }
        boolean existsTableReffered = workflowTableRepository.existsByDatasourceId(datasourceId);
        if (existsTableReffered) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.DATASOURCE_IN_USAGE);
        }
        deleteAllRelated(datasourceId);
    }

    @Transactional(rollbackFor = Exception.class)
    private void deleteAllRelated(Long datasourceId) {
        tableRepository.deleteAllByDatasourceId(datasourceId);
        schemaRepository.deleteAllByDatasourceId(datasourceId);
        catalogRepository.deleteAllByDatasourceId(datasourceId);
        datasourceRepository.deleteById(datasourceId);
    }

    @Override
    public void executeMetadataCollectJob(Long datasourceId) {
        ETLDatasourceDO datasourceDO = datasourceRepository.findById(datasourceId);
        if (datasourceDO == null) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.DATASOURCE_NOT_EXIST);
        }
        LocalDateTime plannedTime = LocalDateTime.now();
        checkDatasourceCollectRunnable(datasourceDO, plannedTime);

        threadPoolTaskExecutor.submit(() -> this.runMetadataCollect(plannedTime, datasourceDO));
    }

    private boolean runMetadataCollect(LocalDateTime plannedTime, ETLDatasourceDO datasourceDO) {
        Long applicationId = datasourceDO.getApplicationId();
        Long datasourceId = datasourceDO.getId();
        log.info("提交元数据采集任务，数据源ID: {}", datasourceId);
        try {
            DataSource datasource = datasourceFactory.constructDataSource(datasourceDO, false);
            CatalogData catalogData = metadataCollector.collectCatalog(datasourceId, datasource);
            metadataManager.saveMetadata(applicationId, datasourceId, catalogData);
            long timeCost = Duration.between(plannedTime, LocalDateTime.now()).toMillis();
            log.info("元数据采集任务执行成功，数据源ID：{}，耗时：{} ms", datasourceId, timeCost);
            return true;
        } catch (Exception e) {
            long timeCost = Duration.between(plannedTime, LocalDateTime.now()).toMillis();
            log.error("元数据采集任务执行失败，数据源ID：{}，耗时：{} ms", datasourceId, timeCost);
            return false;
        }
    }

    @Override
    public DataPreview previewTable(TablePreviewVO tablePreviewVO) {
        return dataInspectService.previewData(tablePreviewVO);
    }

    @Override
    public List<MetaBriefVO> listDatasources(Long applicationId, Integer writable) {
        List<ETLDatasourceDO> datasourceDOList = datasourceRepository.findAllByApplicationIdWithWritable(applicationId, writable);

        return datasourceDOList.stream()
                .map(datasourceDO -> {
                    MetaBriefVO briefVO = new MetaBriefVO();
                    briefVO.setId(String.valueOf(datasourceDO.getId()));
                    briefVO.setName(datasourceDO.getDatasourceName());
                    return briefVO;
                }).toList();
    }

    @Override
    public List<MetaBriefVO> listDatasourceTables(Long datasourceId, Integer writable) {
        ETLDatasourceDO datasourceDO = datasourceRepository.findById(datasourceId);
        if (datasourceDO == null) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.DATASOURCE_NOT_EXIST);
        }
        Boolean isWritable = writable != null && writable != 0;
        if (datasourceDO.getReadonly() && isWritable) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.DATASOURCE_READONLY);
        }

        List<ETLTableDO> tableDOList = tableRepository.findAllByDatasourceId(datasourceId, isWritable);

        return tableDOList.stream()
                .map(tableDO -> {
                    MetaBriefVO briefVO = new MetaBriefVO();
                    briefVO.setId(String.valueOf(tableDO.getId()));
                    briefVO.setName(tableDO.getTableName());
                    briefVO.setDisplayName(tableDO.getDisplayName());
                    return briefVO;
                }).toList();
    }

    @Override
    public List<ColumnDefine> listTableColumns(Long tableId) {
        ETLTableDO tableDO = tableRepository.findById(tableId);
        if (tableDO == null) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.TABLE_NOT_EXIST);
        }
        ETLDatasourceDO datasourceDO = datasourceRepository.findById(tableDO.getDatasourceId());
        if (datasourceDO == null) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.UNKNOWN_ERROR);
        }
        Map<String, String> flinkTypeMappings = flinkMappingRepository.findAllMappingsByDatasourceType(datasourceDO.getDatasourceType());
        TableData tableData = tableDO.getMetaInfo();
        List<ColumnData> columns = tableData.getColumns();
        return columns.stream()
                .map(columnMeta -> {
                    ColumnDefine columnDefine = new ColumnDefine();
                    String fqn = String.format("%s.%s.%s.%s.%s", datasourceDO.getId(),
                            tableData.getCatalogName(),
                            tableData.getSchemaName(),
                            tableData.getName(),
                            columnMeta.getName());
                    columnDefine.setFieldFqn(fqn);
                    String tableName = columnMeta.getName();
                    String displayName = columnMeta.getDisplayName();
                    String comment = columnMeta.getComment();
                    String declaration = columnMeta.getDeclaration();
                    columnDefine.setFieldName(tableName);
                    columnDefine.setDisplayName(tableName);
                    if (StringUtils.isNotBlank(comment)) columnDefine.setDisplayName(comment);
                    if (StringUtils.isNotBlank(declaration) && !StringUtils.equals(declaration, comment))
                        columnDefine.setDisplayName(declaration);
                    if (StringUtils.isNotBlank(displayName) && !StringUtils.equals(tableName, displayName))
                        columnDefine.setDisplayName(displayName);
                    columnDefine.setFieldType(flinkTypeMappings.get(columnMeta.getType()));
                    return columnDefine;
                }).toList();
    }

    private void checkDatasourceCollectRunnable(ETLDatasourceDO datasourceDO, LocalDateTime plannedTime) {
        CollectStatus currentStatus = datasourceDO.getCollectStatus();
        // case (none, required, success, failed) -> running
        if (!CollectStatus.RUNNING.equals(currentStatus)) {
            return;
        }
        // case running -> running
        LocalDateTime perviousStartTime = datasourceDO.getCollectStartTime();
        Duration timeBetween = Duration.between(perviousStartTime, plannedTime);
        long minuteScale = timeBetween.toMinutes();
        if (minuteScale < 5L) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.METADATA_COLLECT_RUNNING);
        }
    }
}
