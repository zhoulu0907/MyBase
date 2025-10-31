package com.cmsr.onebase.module.etl.build.service.datasource;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.etl.build.service.datasource.vo.DatabaseTypeVO;
import com.cmsr.onebase.module.etl.build.service.datasource.vo.ETLDatasourceCreateReqVO;
import com.cmsr.onebase.module.etl.build.service.datasource.vo.ETLDatasourcePingVO;
import com.cmsr.onebase.module.etl.build.service.datasource.vo.ETLDatasourceUpdateReqVO;
import com.cmsr.onebase.module.etl.core.dal.database.ETLCatalogRepository;
import com.cmsr.onebase.module.etl.core.dal.database.ETLDatasourceRepository;
import com.cmsr.onebase.module.etl.core.dal.database.ETLSchemaRepository;
import com.cmsr.onebase.module.etl.core.dal.database.ETLTableRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLDatasourceDO;
import com.cmsr.onebase.module.etl.core.enums.CollectStatus;
import com.cmsr.onebase.module.etl.core.enums.ETLErrorCodeConstants;
import com.cmsr.onebase.module.etl.core.service.collector.MetadataCollectorService;
import com.cmsr.onebase.module.etl.core.vo.datasource.ETLDatasourcePageReqVO;
import com.cmsr.onebase.module.etl.core.vo.datasource.ETLDatasourceRespVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.metadata.type.DatabaseType;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
    private MetadataCollectorService metadataCollectorService;

    private Map<String, String> supportedDbs = Maps.newHashMap();

    @PostConstruct
    public void init() {
        for (DatabaseType db : DatabaseType.values()) {
            // 跳过非常规类型
            if (db == DatabaseType.NONE || db == DatabaseType.COMMON) {
                continue;
            }
            if (StringUtils.isBlank(db.driver())) {
                continue;
            }
            String driverName = db.driver();
            try {
                ClassUtils.getClass(driverName);
                supportedDbs.put(db.name(), db.title());
            } catch (ClassNotFoundException ex) {
                // do nothing, just pass
            }
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
    public ETLDatasourceRespVO queryDatasourceDetail(Long datasourceId) {
        ETLDatasourceDO datasourceDO = datasourceRepository.findById(datasourceId);
        if (datasourceDO == null) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.DATASOURCE_NOT_EXIST);
        }
        ETLDatasourceRespVO respVO = BeanUtils.toBean(datasourceDO, ETLDatasourceRespVO.class);
        return respVO;
    }

    @Override
    public PageResult<ETLDatasourceRespVO> getETLDatasourcePage(ETLDatasourcePageReqVO pageReqVO) {
        PageResult<ETLDatasourceDO> pageDOs = datasourceRepository.getETLDatasourcePage(pageReqVO);
        List<ETLDatasourceRespVO> respVOs = pageDOs.getList()
                .stream().map(dataobject -> {
                    ETLDatasourceRespVO respVO = BeanUtils.toBean(dataobject, ETLDatasourceRespVO.class);
                    // fill respVO with creator/updater information...
                    return respVO;
                }).toList();

        return new PageResult<>(respVOs, pageDOs.getTotal());
    }

    @Override
    public Long createDatasource(ETLDatasourceCreateReqVO createReqVO) {
        validDatasourceCodeDuplicate(createReqVO.getDatasourceCode(), null);
        validDatasourceTypeSupported(createReqVO.getDatasourceType());

        ETLDatasourceDO datasourceDO = BeanUtils.toBean(createReqVO, ETLDatasourceDO.class);
        // initialize collect status to `none`, infer datasource will be empty.
        datasourceDO.setCollectStatus(CollectStatus.NONE);
        datasourceDO = datasourceRepository.insert(datasourceDO);
        return datasourceDO.getId();
    }

    @Override
    public void updateDatasource(ETLDatasourceUpdateReqVO updateReqVO) {
        validDatasourceCodeDuplicate(updateReqVO.getDatasourceCode(), updateReqVO.getId());
        validDatasourceTypeSupported(updateReqVO.getDatasourceType());

        ETLDatasourceDO oldDatasource = datasourceRepository.findById(updateReqVO.getId());
        oldDatasource.setDatasourceCode(updateReqVO.getDatasourceCode());
        oldDatasource.setDatasourceName(updateReqVO.getDatasourceName());
        oldDatasource.setConfig(JsonUtils.toJsonString(updateReqVO.getConfig()));
        oldDatasource.setReadonly(updateReqVO.getReadonly());
        // udpate collect status to `required`, demonds user to execute at least once
        oldDatasource.setCollectStatus(CollectStatus.REQUIRED);

        datasourceRepository.update(oldDatasource);
    }

    @Override
    public void deleteDatasource(Long datasourceId) {
        boolean entityExists = datasourceRepository.existsById(datasourceId);
        if (entityExists) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.DATASOURCE_NOT_EXIST);
        }
        // check datasource(catalog, schema, table) not reffered by elsewhere
        // so far, delete datasource in application is not supported;
        throw new UnsupportedOperationException("暂不支持对数据源进行删除");
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
