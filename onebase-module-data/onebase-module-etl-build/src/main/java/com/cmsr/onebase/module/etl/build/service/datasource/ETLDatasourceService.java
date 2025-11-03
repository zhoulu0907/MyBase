package com.cmsr.onebase.module.etl.build.service.datasource;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.etl.build.service.datasource.vo.DatabaseTypeVO;
import com.cmsr.onebase.module.etl.build.service.datasource.vo.ETLDatasourceCreateReqVO;
import com.cmsr.onebase.module.etl.build.service.datasource.vo.ETLDatasourcePingVO;
import com.cmsr.onebase.module.etl.build.service.datasource.vo.ETLDatasourceUpdateReqVO;
import com.cmsr.onebase.module.etl.core.vo.datasource.ETLDatasourcePageReqVO;
import com.cmsr.onebase.module.etl.core.vo.datasource.ETLDatasourceRespVO;

import java.util.List;

public interface ETLDatasourceService {

    List<DatabaseTypeVO> getSupportedDatabaseTypes();

    Boolean pingDatasource(ETLDatasourcePingVO pingVO);

    ETLDatasourceRespVO queryDatasourceDetail(Long datasourceId);

    PageResult<ETLDatasourceRespVO> getETLDatasourcePage(ETLDatasourcePageReqVO pageReqVO);

    Long createDatasource(ETLDatasourceCreateReqVO createReqVO);

    void updateDatasource(ETLDatasourceUpdateReqVO updateReqVO);

    void deleteDatasource(Long datasourceId);

    void executeMetadataCollectJob(Long datasourceId);
}
