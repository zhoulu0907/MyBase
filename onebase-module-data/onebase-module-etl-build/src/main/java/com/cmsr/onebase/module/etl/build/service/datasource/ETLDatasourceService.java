package com.cmsr.onebase.module.etl.build.service.datasource;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.etl.build.service.datasource.vo.*;
import com.cmsr.onebase.module.etl.common.preview.DataPreview;
import com.cmsr.onebase.module.etl.build.service.preview.vo.TablePreviewVO;
import com.cmsr.onebase.module.etl.common.preview.ColumnDefine;
import com.cmsr.onebase.module.etl.core.vo.DatasourcePageReqVO;

import java.util.List;

public interface ETLDatasourceService {

    Boolean pingDatasource(TestConnectionVO pingVO);

    DatasourceRespVO queryDatasourceDetail(Long datasourceId);

    PageResult<DatasourceRespVO> getETLDatasourcePage(DatasourcePageReqVO pageReqVO);

    CommonResult<Long> createDatasource(ETLDatasourceCreateReqVO createReqVO);

    void updateDatasource(ETLDatasourceUpdateReqVO updateReqVO);

    void deleteDatasource(Long datasourceId);

    void executeMetadataCollectJob(Long datasourceId);

    DataPreview previewTable(TablePreviewVO tablePreviewVO);

    List<MetaBriefVO> listDatasources(Long applicationId, Integer writable);

    List<MetaBriefVO> listDatasourceTables(Long datasourceId, Integer writable);

    List<ColumnDefine> listTableColumns(Long tableId);
}
