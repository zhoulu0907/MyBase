package com.cmsr.onebase.module.etl.build.service.datasource;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.etl.build.controller.datasource.vo.DataFactoryDatasourceReqVO;
import com.cmsr.onebase.module.etl.build.service.datasource.vo.DatabaseTypeVO;
import com.cmsr.onebase.module.etl.core.dal.database.DataFactoryDatasourceRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.DataFactoryDatasourceDO;
import com.cmsr.onebase.module.etl.core.enums.DataFactoryErrorCodeConstants;
import com.cmsr.onebase.module.etl.core.service.collector.MetadataCollectorService;
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
public class DataFactoryDatasourceServiceImpl implements DataFactoryDatasourceService {

    @Resource
    private DataFactoryDatasourceRepository dataFactoryDatasourceRepository;

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
    public Boolean pingDatasource(DataFactoryDatasourceReqVO requestVO) {
        DataFactoryDatasourceDO datasourceDO = BeanUtils.toBean(requestVO, DataFactoryDatasourceDO.class);
        validDatasourceTypeSupported(datasourceDO.getDatasourceType());
        return metadataCollectorService.testConnection(datasourceDO);
    }

    @Override
    public Long createDatasource(DataFactoryDatasourceReqVO requestVO) {
        validDatasourceCodeDuplicate(requestVO.getDatasourceCode(), null);
        validDatasourceTypeSupported(requestVO.getDatasourceType());

        DataFactoryDatasourceDO datasourceDO = BeanUtils.toBean(requestVO, DataFactoryDatasourceDO.class);
        datasourceDO.setId(null);
        datasourceDO = dataFactoryDatasourceRepository.insert(datasourceDO);
        return datasourceDO.getId();
    }

    @Override
    public void updateDatasource(DataFactoryDatasourceReqVO requestVO) {
        validDatasourceCodeDuplicate(requestVO.getDatasourceCode(), requestVO.getId());
        validDatasourceTypeSupported(requestVO.getDatasourceType());

        DataFactoryDatasourceDO datasourceDO = BeanUtils.toBean(requestVO, DataFactoryDatasourceDO.class);
        dataFactoryDatasourceRepository.update(datasourceDO);
    }

    private void validDatasourceCodeDuplicate(String datasourceCode, Long datasourceId) {
        if (datasourceId == null) {
            if (dataFactoryDatasourceRepository.findOneByDatasourceCode(datasourceCode) != null) {
                throw ServiceExceptionUtil.exception(DataFactoryErrorCodeConstants.DATASOURCE_CODE_DUPLICATE);
            }
        } else {
            if (dataFactoryDatasourceRepository.findOneByDatasourceCodeAndIdNe(datasourceCode, datasourceId) != null) {
                throw ServiceExceptionUtil.exception(DataFactoryErrorCodeConstants.DATASOURCE_CODE_DUPLICATE);
            }
        }
    }

    private void validDatasourceTypeSupported(String datasourceType) {
        if (!supportedDbs.containsKey(datasourceType)) {
            throw ServiceExceptionUtil.exception(DataFactoryErrorCodeConstants.DATASOURCE_NOT_SUPPORTED);
        }
    }
//    public void test() {
//        BeanUtils.toBean()
//    }
}
