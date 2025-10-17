package com.cmsr.onebase.module.etl.core.service.collector;

import com.cmsr.onebase.module.etl.core.dal.database.DataFactoryCatalogRepository;
import com.cmsr.onebase.module.etl.core.dal.database.DataFactorySchemaRepository;
import com.cmsr.onebase.module.etl.core.dal.database.DataFactoryTableRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.DataFactoryDatasourceDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.proxy.ServiceProxy;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Slf4j
@Service
public class MetadataCollectorServiceImpl implements MetadataCollectorService {
    @Resource
    private DataSourceFactory dataSourceFactory;

    @Resource
    private DataFactoryCatalogRepository catalogRepository;

    @Resource
    private DataFactorySchemaRepository schemaRepository;

    @Resource
    private DataFactoryTableRepository tableRepository;


    @Override
    public boolean testConnection(DataFactoryDatasourceDO datasourceDO) {
        DataSource datasource = dataSourceFactory.constructDataSource(datasourceDO);
        try {
            return ServiceProxy.temporary(datasource).validity();
        } catch (Exception ex) {
            log.error("测试数据源连接异常，数据源信息: {}", datasourceDO, ex);
            return false;
        }
    }

    @Override
    public boolean doCollection(Long datasourceId) {
        // TODO: implement this
        return false;
    }
}
