package com.cmsr.onebase.module.etl.build.service.datasource;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.etl.build.service.DatasourceFactory;
import com.cmsr.onebase.module.etl.build.service.collector.MetadataCollector;
import com.cmsr.onebase.module.etl.build.service.collector.MetadataManager;
import com.cmsr.onebase.module.etl.build.vo.datasource.DatasourceRespVO;
import com.cmsr.onebase.module.etl.build.vo.datasource.EtlDatasourceCreateReqVO;
import com.cmsr.onebase.module.etl.build.vo.datasource.EtlDatasourceUpdateReqVO;
import com.cmsr.onebase.module.etl.build.vo.datasource.MetaBriefVO;
import com.cmsr.onebase.module.etl.common.entity.CatalogData;
import com.cmsr.onebase.module.etl.common.entity.ColumnData;
import com.cmsr.onebase.module.etl.common.entity.TableData;
import com.cmsr.onebase.module.etl.common.preview.ColumnDefine;
import com.cmsr.onebase.module.etl.core.dal.database.*;
import com.cmsr.onebase.module.etl.core.dal.dataobject.EtlDatasourceDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.EtlTableDO;
import com.cmsr.onebase.module.etl.core.enums.CollectStatus;
import com.cmsr.onebase.module.etl.core.enums.EtlErrorCodeConstants;
import com.cmsr.onebase.module.etl.core.vo.DatasourcePageReqVO;
import com.github.f4b6a3.uuid.UuidCreator;
import com.mybatisflex.core.row.Db;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.metadata.type.DatabaseType;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class EtlDatasourceServiceImpl implements EtlDatasourceService {

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Resource
    private EtlDatasourceRepository datasourceRepository;

    @Resource
    private EtlCatalogRepository catalogRepository;

    @Resource
    private EtlSchemaRepository schemaRepository;

    @Resource
    private EtlTableRepository tableRepository;

    @Resource
    private EtlWorkflowTableRepository workflowTableRepository;

    @Resource
    private EtlFlinkMappingRepository flinkMappingRepository;

    @Resource
    private MetadataManager metadataManager;

    @Resource
    private MetadataCollector metadataCollector;

    @Resource
    private DatasourceFactory datasourceFactory;

    @Override
    public DatasourceRespVO queryDatasourceDetail(Long datasourceId) {
        EtlDatasourceDO datasourceDO = datasourceRepository.getById(datasourceId);
        if (datasourceDO == null) {
            throw ServiceExceptionUtil.exception(EtlErrorCodeConstants.DATASOURCE_NOT_EXIST);
        }
        DatasourceRespVO datasourceRespVO = DatasourceRespVO.convertFrom(datasourceDO);
        if (datasourceDO.getConfig() != null) {
            datasourceRespVO.setConfig(JsonUtils.parseTree(datasourceDO.getConfig()));
        }
        return datasourceRespVO;
    }

    @Override
    public PageResult<DatasourceRespVO> getEtlDatasourcePage(DatasourcePageReqVO pageReqVO) {
        PageResult<EtlDatasourceDO> pageDOs = datasourceRepository.getEtlDatasourcePage(pageReqVO);
        List<DatasourceRespVO> respVOs = pageDOs.getList()
                .stream()
                .map(DatasourceRespVO::convertFrom)
                .toList();

        return new PageResult<>(respVOs, pageDOs.getTotal());
    }

    @Override
    public CommonResult<String> createDatasource(EtlDatasourceCreateReqVO createReqVO) {
        Long applicationId = createReqVO.getApplicationId();
        String datasourceType = createReqVO.getDatasourceType();

        EtlDatasourceDO datasourceDO = new EtlDatasourceDO();
        datasourceDO.setApplicationId(applicationId);
        String uuid = UuidCreator.getTimeOrderedEpoch().toString();
        datasourceDO.setDatasourceUuid(uuid);
        datasourceDO.setDatasourceName(createReqVO.getDatasourceName());
        datasourceDO.setDeclaration(createReqVO.getDeclaration());
        datasourceDO.setDatasourceType(datasourceType);
        datasourceDO.setConfig(JsonUtils.toJsonString(createReqVO.getConfig()));
        datasourceDO.setReadonly(createReqVO.getReadonly());
        // initialize collect status to `none`, infer datasource will be empty.
        datasourceDO.setCollectStatus(CollectStatus.NONE);
        complementJdbcDatasourceProperties(datasourceDO);
        datasourceRepository.save(datasourceDO);
        Long datasourceId = datasourceDO.getId();
        boolean withCollect = BooleanUtils.toBoolean(createReqVO.getWithCollect());
        if (withCollect) {
            try {
                boolean collectResult = runMetadataCollect(LocalDateTime.now(), datasourceDO);
                if (!collectResult) {
                    throw ServiceExceptionUtil.exception(EtlErrorCodeConstants.METADATA_COLLECT_FAILED.getCode(),
                            EtlErrorCodeConstants.METADATA_COLLECT_FAILED.getMsg(),
                            datasourceId);
                }
            } catch (Exception e) {
                CommonResult.error(
                        EtlErrorCodeConstants.METADATA_COLLECT_FAILED.getCode(),
                        EtlErrorCodeConstants.METADATA_COLLECT_FAILED.getMsg() + ": " + e.getMessage(),
                        datasourceId);
            }
        }
        return CommonResult.success(uuid);
    }

    @Override
    public void updateDatasource(EtlDatasourceUpdateReqVO updateReqVO) {
        EtlDatasourceDO oldDatasource = datasourceRepository.getById(updateReqVO.getId());
        if (oldDatasource == null) {
            throw ServiceExceptionUtil.exception(EtlErrorCodeConstants.DATASOURCE_NOT_EXIST);
        }
        oldDatasource.setDatasourceName(updateReqVO.getDatasourceName());
        oldDatasource.setDeclaration(updateReqVO.getDeclaration());
        oldDatasource.setConfig(JsonUtils.toJsonString(updateReqVO.getConfig()));
        oldDatasource.setReadonly(updateReqVO.getReadonly());
        // udpate collect status to `required`, demonds user to execute at least once
        oldDatasource.setCollectStatus(CollectStatus.REQUIRED);
        complementJdbcDatasourceProperties(oldDatasource);
        datasourceRepository.updateById(oldDatasource);
    }

    private void complementJdbcDatasourceProperties(EtlDatasourceDO datasourceDO) {
        String datasourceType = datasourceDO.getDatasourceType();
        DatabaseType databaseType = DatasourceFactory.parseDatabaseType(datasourceType);
        Map connectionProperties = JsonUtils.parseObject(datasourceDO.getConfig(), Map.class);
        connectionProperties.put("driver", databaseType.driver());
        String jdbcUrl = DatasourceFactory.buildJdbcConnectionString(datasourceType, connectionProperties);
        connectionProperties.put("jdbcUrl", jdbcUrl);
        datasourceDO.setConfig(JsonUtils.toJsonString(connectionProperties));
    }

    @Override
    public void deleteDatasource(Long datasourceId) {
        EtlDatasourceDO entity = datasourceRepository.getById(datasourceId);
        if (entity == null) {
            throw ServiceExceptionUtil.exception(EtlErrorCodeConstants.DATASOURCE_NOT_EXIST);
        }
        String datasourceUuid = entity.getDatasourceUuid();
        boolean existsTableReffered = workflowTableRepository.existsByDatasource(datasourceUuid);
        if (existsTableReffered) {
            throw ServiceExceptionUtil.exception(EtlErrorCodeConstants.DATASOURCE_IN_USAGE);
        }
        Db.tx(() -> {
            tableRepository.deleteAllByDatasource(datasourceUuid);
            schemaRepository.deleteAllByDatasource(datasourceUuid);
            catalogRepository.deleteAllByDatasource(datasourceUuid);
            datasourceRepository.removeById(datasourceId);
            return true;
        });
    }

    @Override
    public void executeMetadataCollectJob(Long datasourceId) {
        EtlDatasourceDO datasourceDO = datasourceRepository.getById(datasourceId);
        if (datasourceDO == null) {
            throw ServiceExceptionUtil.exception(EtlErrorCodeConstants.DATASOURCE_NOT_EXIST);
        }
        LocalDateTime plannedTime = LocalDateTime.now();
        checkDatasourceCollectRunnable(datasourceDO, plannedTime);

        threadPoolTaskExecutor.submit(() -> this.runMetadataCollect(plannedTime, datasourceDO));
    }

    private boolean runMetadataCollect(LocalDateTime plannedTime, EtlDatasourceDO datasourceDO) {
        datasourceRepository.changeCollectStatus(datasourceDO.getId(), CollectStatus.RUNNING, plannedTime);
        Long applicationId = datasourceDO.getApplicationId();
        Long datasourceId = datasourceDO.getId();
        String datasourceUuid = datasourceDO.getDatasourceUuid();
        log.info("提交元数据采集任务，数据源ID: {}", datasourceId);
        try {
            DataSource datasource = datasourceFactory.constructDataSource(datasourceDO, false);
            CatalogData catalogData = metadataCollector.collectCatalog(datasourceId, datasource);
            metadataManager.saveMetadata(applicationId, datasourceUuid, catalogData);
            LocalDateTime endTime = LocalDateTime.now();
            long timeCost = Duration.between(plannedTime, endTime).toMillis();
            datasourceRepository.changeCollectStatus(datasourceDO.getId(), CollectStatus.SUCCESS, endTime);
            log.info("元数据采集任务执行成功，数据源ID：{}，耗时：{} ms", datasourceId, timeCost);
            return true;
        } catch (Exception e) {
            LocalDateTime endTime = LocalDateTime.now();
            long timeCost = Duration.between(plannedTime, endTime).toMillis();
            datasourceRepository.changeCollectStatus(datasourceDO.getId(), CollectStatus.FAILED, endTime);
            log.error("元数据采集任务执行失败，数据源ID：{}，耗时：{} ms", datasourceId, timeCost, e);
            return false;
        }
    }

    @Override
    public List<MetaBriefVO> listDatasources(Long applicationId, Integer writable) {
        List<EtlDatasourceDO> datasourceDOList = datasourceRepository.findAllByApplicationIdWithWritable(applicationId, writable);

        return datasourceDOList.stream()
                .map(datasourceDO -> {
                    MetaBriefVO briefVO = new MetaBriefVO();
                    briefVO.setId(String.valueOf(datasourceDO.getId()));
                    briefVO.setUuid(datasourceDO.getDatasourceUuid());
                    briefVO.setName(datasourceDO.getDatasourceName());
                    return briefVO;
                }).toList();
    }

    @Override
    public List<MetaBriefVO> listDatasourceTables(String datasourceUuid, Integer writable) {
        EtlDatasourceDO datasourceDO = datasourceRepository.getByUuid(datasourceUuid);
        if (datasourceDO == null) {
            throw ServiceExceptionUtil.exception(EtlErrorCodeConstants.DATASOURCE_NOT_EXIST);
        }
        boolean isWritable = writable != null && writable != 0;
        if (BooleanUtils.toBoolean(datasourceDO.getReadonly()) && isWritable) {
            throw ServiceExceptionUtil.exception(EtlErrorCodeConstants.DATASOURCE_READONLY);
        }
        List<EtlTableDO> tableDOList = tableRepository.findAllByDatasource(datasourceDO.getDatasourceUuid(), isWritable);

        return tableDOList.stream()
                .map(tableDO -> {
                    MetaBriefVO briefVO = new MetaBriefVO();
                    briefVO.setId(String.valueOf(tableDO.getId()));
                    briefVO.setUuid(tableDO.getTableUuid());
                    briefVO.setName(tableDO.getTableName());
                    briefVO.setDisplayName(tableDO.getDisplayName());
                    return briefVO;
                }).toList();
    }

    @Override
    public List<ColumnDefine> listTableColumns(String tableUuid) {
        EtlTableDO tableDO = tableRepository.getByUuid(tableUuid);
        if (tableDO == null) {
            throw ServiceExceptionUtil.exception(EtlErrorCodeConstants.TABLE_NOT_EXIST);
        }
        EtlDatasourceDO datasourceDO = datasourceRepository.getByUuid(tableDO.getDatasourceUuid());
        if (datasourceDO == null) {
            throw ServiceExceptionUtil.exception(EtlErrorCodeConstants.DATASOURCE_NOT_EXIST);
        }
        Map<String, String> flinkTypeMappings = flinkMappingRepository.findAllMappingsByDatasourceType(datasourceDO.getDatasourceType());
        TableData tableData = JsonUtils.parseObject(tableDO.getMetaInfo(), TableData.class);
        List<ColumnData> columns = tableData.getColumns();
        return columns.stream()
                .map(columnMeta -> {
                    ColumnDefine columnDefine = new ColumnDefine();
                    String fqn = String.format("%s.%s.%s.%s.%s", datasourceDO.getDatasourceUuid(),
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

    private void checkDatasourceCollectRunnable(EtlDatasourceDO datasourceDO, LocalDateTime plannedTime) {
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
            throw ServiceExceptionUtil.exception(EtlErrorCodeConstants.METADATA_COLLECT_RUNNING);
        }
    }
}
