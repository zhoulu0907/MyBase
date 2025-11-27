package com.cmsr.onebase.module.etl.build.service.datasource;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.etl.build.vo.datasource.DatasourceRespVO;
import com.cmsr.onebase.module.etl.build.vo.datasource.EtlDatasourceCreateReqVO;
import com.cmsr.onebase.module.etl.build.vo.datasource.EtlDatasourceUpdateReqVO;
import com.cmsr.onebase.module.etl.build.vo.datasource.MetaBriefVO;
import com.cmsr.onebase.module.etl.common.preview.ColumnDefine;
import com.cmsr.onebase.module.etl.core.vo.DatasourcePageReqVO;

import java.util.List;

public interface EtlDatasourceService {

    DatasourceRespVO queryDatasourceDetail(Long datasourceId);

    PageResult<DatasourceRespVO> getEtlDatasourcePage(DatasourcePageReqVO pageReqVO);

    CommonResult<String> createDatasource(EtlDatasourceCreateReqVO createReqVO);

    void updateDatasource(EtlDatasourceUpdateReqVO updateReqVO);

    void deleteDatasource(Long datasourceId);

    void executeMetadataCollectJob(Long datasourceId);

    List<MetaBriefVO> listDatasources(Long applicationId, Integer writable);

    List<MetaBriefVO> listDatasourceTables(String datasourceUuid, Integer writable);

    List<ColumnDefine> listTableColumns(String tableUuid);
}
