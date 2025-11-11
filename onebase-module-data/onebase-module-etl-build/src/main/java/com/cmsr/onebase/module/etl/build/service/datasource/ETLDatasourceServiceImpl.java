package com.cmsr.onebase.module.etl.build.service.datasource;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.etl.build.service.datasource.vo.DatabaseTypeVO;
import com.cmsr.onebase.module.etl.build.service.datasource.vo.ETLDatasourceCreateReqVO;
import com.cmsr.onebase.module.etl.build.service.datasource.vo.ETLDatasourcePingVO;
import com.cmsr.onebase.module.etl.build.service.datasource.vo.ETLDatasourceUpdateReqVO;
import com.cmsr.onebase.module.etl.core.dal.database.*;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLDatasourceDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLTableDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.metainfo.MetaColumn;
import com.cmsr.onebase.module.etl.core.enums.CollectStatus;
import com.cmsr.onebase.module.etl.core.enums.ETLErrorCodeConstants;
import com.cmsr.onebase.module.etl.core.service.DataInspectService;
import com.cmsr.onebase.module.etl.core.service.MetadataCollectorService;
import com.cmsr.onebase.module.etl.core.vo.datasource.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.metadata.type.DatabaseType;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class ETLDatasourceServiceImpl implements ETLDatasourceService {

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
    private MetadataCollectorService metadataCollectorService;

    @Resource
    private DataInspectService dataInspectService;

    private Map<String, String> supportedDbs = Maps.newHashMap();

    @PostConstruct
    public void init() throws ClassNotFoundException {
        DatabaseType[] dbs = {
                DatabaseType.PostgreSQL,
                DatabaseType.KingBase,
                DatabaseType.ORACLE,
                DatabaseType.MySQL
        };
        for (DatabaseType db : dbs) {
            // 跳过非常规类型
            if (StringUtils.isBlank(db.driver())) {
                continue;
            }
            String driverName = db.driver();
            ClassUtils.getClass(driverName);
            supportedDbs.put(db.name(), db.title());
        }
    }

    @Override
    public List<DatabaseTypeVO> getSupportedDatabaseTypes() {
        List<DatabaseTypeVO> supportedDbVOs = Lists.newArrayList();
        for (String dbName : supportedDbs.keySet()) {
            DatabaseTypeVO typeVO = new DatabaseTypeVO();
            typeVO.setDatasourceType(dbName);
            typeVO.setDisplayName(supportedDbs.get(dbName));
            supportedDbVOs.add(typeVO);
        }
        return supportedDbVOs;
    }

    @Override
    public Boolean pingDatasource(ETLDatasourcePingVO pingVO) {
        validDatasourceTypeSupported(pingVO.getDatasourceType());
        ETLDatasourceDO datasourceDO = BeanUtils.toBean(pingVO, ETLDatasourceDO.class);
        return metadataCollectorService.testConnection(datasourceDO);
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
        validDatasourceTypeSupported(createReqVO.getDatasourceType());

        Long applicationId = createReqVO.getApplicationId();
        String datasourceType = createReqVO.getDatasourceType();
        ETLDatasourceDO datasourceDO = new ETLDatasourceDO();
        datasourceDO.setApplicationId(applicationId);
        UUID uuid = UUID.randomUUID();
        datasourceDO.setDatasourceCode(uuid.toString());
        datasourceDO.setDatasourceName(createReqVO.getDatasourceName());
        datasourceDO.setDeclaration(createReqVO.getDeclaration());
        datasourceDO.setDatasourceType(datasourceType);
        datasourceDO.setConfig(JsonUtils.toJsonString(createReqVO.getConfig()));
        datasourceDO.setReadonly(createReqVO.getReadonly());
        // initialize collect status to `none`, infer datasource will be empty.
        datasourceDO.setCollectStatus(CollectStatus.NONE);
        datasourceDO = datasourceRepository.insert(datasourceDO);
        Long datasourceId = datasourceDO.getId();
        Boolean withCollect = createReqVO.getWithCollect();
        if (withCollect) {
            try {
                boolean collectResult = metadataCollectorService.doCollection(applicationId, datasourceId, datasourceType);
                if (!collectResult) {
                    CommonResult.error(
                            ETLErrorCodeConstants.METADATA_COLLECT_FAILED.getCode(),
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
        validDatasourceCodeDuplicate(updateReqVO.getDatasourceCode(), updateReqVO.getId());
        validDatasourceTypeSupported(updateReqVO.getDatasourceType());

        ETLDatasourceDO oldDatasource = datasourceRepository.findById(updateReqVO.getId());
        oldDatasource.setDatasourceCode(updateReqVO.getDatasourceCode());
        oldDatasource.setDatasourceName(updateReqVO.getDatasourceName());
        oldDatasource.setDeclaration(updateReqVO.getDeclaration());
        oldDatasource.setConfig(JsonUtils.toJsonString(updateReqVO.getConfig()));
        oldDatasource.setReadonly(updateReqVO.getReadonly());
        // udpate collect status to `required`, demonds user to execute at least once
        oldDatasource.setCollectStatus(CollectStatus.REQUIRED);

        datasourceRepository.update(oldDatasource);
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
        // 托管给MetadataCollectorService
        metadataCollectorService.submitCollectJob(datasourceDO);
    }

    @Override
    public DataPreviewVO previewTable(TablePreviewVO tablePreviewVO) {
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
                    briefVO.setName(tableDO.getDisplayName());
                    return briefVO;
                }).toList();
    }

    @Override
    public List<ColumnDefine> listTableColumns(Long tableId) {
        ETLTableDO tableDO = tableRepository.findById(tableId);
        if (tableDO == null) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.TABLE_NOT_EXIST);
        }

        List<MetaColumn> columns = tableDO.getMetaInfo().getColumns();
        return columns.stream()
                .map(metaColumn -> {
                    ColumnDefine columnDefine = new ColumnDefine();
                    columnDefine.setId(metaColumn.getId());
                    columnDefine.setName(metaColumn.getDisplayName());
                    columnDefine.setType(metaColumn.getFlinkType());
                    return columnDefine;
                }).toList();
    }

    private void validDatasourceCodeDuplicate(String datasourceCode, Long filterId) {
        boolean exists = datasourceRepository.existsByDatasourceCodeFilterById(datasourceCode, filterId);
        if (exists) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.DATASOURCE_CODE_DUPLICATE);
        }
    }

    private void validDatasourceTypeSupported(String datasourceType) {
        if (!supportedDbs.containsKey(datasourceType)) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.DATASOURCE_NOT_SUPPORTED);
        }
    }
}
