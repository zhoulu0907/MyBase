package com.cmsr.onebase.module.etl.build.service.datasource;

import com.cmsr.onebase.module.etl.build.controller.datasource.vo.DataFactoryDatasourceReqVO;
import com.cmsr.onebase.module.etl.build.service.datasource.vo.DatabaseTypeVO;

import java.util.List;

public interface DataFactoryDatasourceService {

    /**
     * 列出所有受支持的 数据工厂-数据源 类型
     *
     * @return datasourceTypes
     */
    List<DatabaseTypeVO> getSupportedDatabaseTypes();

    Boolean pingDatasource(DataFactoryDatasourceReqVO requestVO);

    /**
     * 创建Datasource
     *
     * @param requestVO 创建实体
     * @return 创建成功的对象ID
     */
    Long createDatasource(DataFactoryDatasourceReqVO requestVO);

    void updateDatasource(DataFactoryDatasourceReqVO requestVO);

    void deleteDatasource(Long datasourceId);

    void executeMetadataCollectJob(Long datasourceId);
}
