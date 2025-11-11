package com.cmsr.onebase.module.etl.build.service.datasource;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.etl.build.service.datasource.vo.DatabaseTypeVO;
import com.cmsr.onebase.module.etl.build.service.datasource.vo.ETLDatasourceCreateReqVO;
import com.cmsr.onebase.module.etl.build.service.datasource.vo.ETLDatasourcePingVO;
import com.cmsr.onebase.module.etl.build.service.datasource.vo.ETLDatasourceUpdateReqVO;
import com.cmsr.onebase.module.etl.core.vo.datasource.*;

import java.util.List;

public interface ETLDatasourceService {

    List<DatabaseTypeVO> getSupportedDatabaseTypes();

    Boolean pingDatasource(ETLDatasourcePingVO pingVO);

    DatasourceRespVO queryDatasourceDetail(Long datasourceId);

    PageResult<DatasourceRespVO> getETLDatasourcePage(DatasourcePageReqVO pageReqVO);

    CommonResult<Long> createDatasource(ETLDatasourceCreateReqVO createReqVO);

    void updateDatasource(ETLDatasourceUpdateReqVO updateReqVO);

    void deleteDatasource(Long datasourceId);

    void executeMetadataCollectJob(Long datasourceId);

    DataPreviewVO previewTable(TablePreviewVO tablePreviewVO);

    List<MetaBriefVO> listDatasources(Long applicationId, Integer writable);

    List<MetaBriefVO> listDatasourceTables(Long datasourceId, Integer writable);

    List<ColumnDefine> listTableColumns(Long tableId);
}
