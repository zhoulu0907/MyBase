package com.cmsr.onebase.module.etl.build.service.datasource;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.etl.build.vo.datasource.DatasourceRespVO;
import com.cmsr.onebase.module.etl.build.vo.datasource.ETLDatasourceCreateReqVO;
import com.cmsr.onebase.module.etl.build.vo.datasource.ETLDatasourceUpdateReqVO;
import com.cmsr.onebase.module.etl.build.vo.datasource.MetaBriefVO;
import com.cmsr.onebase.module.etl.common.preview.ColumnDefine;
import com.cmsr.onebase.module.etl.core.vo.DatasourcePageReqVO;

import java.util.List;

public interface ETLDatasourceService {

    DatasourceRespVO queryDatasourceDetail(Long datasourceId);

    PageResult<DatasourceRespVO> getETLDatasourcePage(DatasourcePageReqVO pageReqVO);

    CommonResult<String> createDatasource(ETLDatasourceCreateReqVO createReqVO);

    void updateDatasource(ETLDatasourceUpdateReqVO updateReqVO);

    void deleteDatasource(Long datasourceId);

    void executeMetadataCollectJob(Long datasourceId);

    List<MetaBriefVO> listDatasources(Long applicationId, Integer writable);

    List<MetaBriefVO> listDatasourceTables(Long datasourceId, Integer writable);

    List<ColumnDefine> listTableColumns(Long tableId);
}
