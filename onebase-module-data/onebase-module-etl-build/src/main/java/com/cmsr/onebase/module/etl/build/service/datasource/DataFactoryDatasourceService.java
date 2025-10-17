package com.cmsr.onebase.module.etl.build.service.datasource;

import com.cmsr.onebase.module.etl.build.service.datasource.vo.DatabaseTypeVO;

import java.util.List;

public interface DataFactoryDatasourceService {

    /**
     * 列出所有受支持的 数据工厂-数据源 类型
     *
     * @return datasourceTypes
     */
    List<DatabaseTypeVO> getSupportedDatabaseTypes();


}
