package com.cmsr.onebase.module.etl.build.controller.datasource;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.etl.build.service.datasource.ETLDatasourceService;
import com.cmsr.onebase.module.etl.build.service.preview.DataInspectService;
import com.cmsr.onebase.module.etl.build.vo.datasource.*;
import com.cmsr.onebase.module.etl.build.vo.preview.TablePreviewVO;
import com.cmsr.onebase.module.etl.common.preview.ColumnDefine;
import com.cmsr.onebase.module.etl.common.preview.DataPreview;
import com.cmsr.onebase.module.etl.core.vo.DatasourcePageReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "数据工厂 - 数据源管理")
@RestController
@RequestMapping("/etl/datasource")
@Validated
public class ETLDatasourceController {

    @Resource
    private ETLDatasourceService etlDatasourceService;

    @Resource
    private DataInspectService dataInspectService;

    // GETs
    @Operation(summary = "测试数据源连接")
    @PostMapping("/ping")
    public CommonResult<Boolean> testConnection(@Validated @RequestBody TestConnectionVO requestVO) {
        Boolean connected = dataInspectService.testConnection(requestVO);
        return CommonResult.success(connected);
    }

    @Operation(summary = "分页查询数据源")
    @GetMapping("/page")
    public CommonResult<PageResult<DatasourceRespVO>> getETLDatasourcePage(@Validated DatasourcePageReqVO pageReqVO) {
        PageResult<DatasourceRespVO> pageResult = etlDatasourceService.getETLDatasourcePage(pageReqVO);
        return CommonResult.success(pageResult);
    }

    @Operation(summary = "查询数据源配置详情")
    @GetMapping("/{id}")
    public CommonResult<DatasourceRespVO> queryDatasourceDetail(@PathVariable("id") Long datasourceId) {
        DatasourceRespVO datasourceVO = etlDatasourceService.queryDatasourceDetail(datasourceId);
        return CommonResult.success(datasourceVO);
    }

    // POSTs
    @Operation(summary = "创建数据源")
    @PostMapping("/create")
    public CommonResult<Long> createETLDatasource(@Validated @RequestBody ETLDatasourceCreateReqVO createReqVO) {
        return etlDatasourceService.createDatasource(createReqVO);
    }

    @Operation(summary = "更新数据源")
    @PostMapping("/update")
    public CommonResult<Boolean> updateETLDatasource(@Validated @RequestBody ETLDatasourceUpdateReqVO updateReqVO) {
        etlDatasourceService.updateDatasource(updateReqVO);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "采集元数据信息")
    @PostMapping("/collect")
    public CommonResult<Boolean> runMetadataCollect(@RequestParam("id") Long id) {
        etlDatasourceService.executeMetadataCollectJob(id);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "删除数据源")
    @PostMapping("/delete")
    public CommonResult<Boolean> deleteETLDatasource(@RequestParam("id") Long id) {
        etlDatasourceService.deleteDatasource(id);
        return CommonResult.success(Boolean.TRUE);
    }

    @Operation(summary = "查询数据源列表")
    @GetMapping("/list")
    public CommonResult<List<MetaBriefVO>> listDatasources(@RequestParam("applicationId") Long applicationId,
                                                           @RequestParam(value = "writable", required = false) Integer writable) {
        List<MetaBriefVO> briefVOList = etlDatasourceService.listDatasources(applicationId, writable);
        return CommonResult.success(briefVOList);
    }

    @Operation(summary = "查询数据源下的表")
    @GetMapping("/tables")
    public CommonResult<List<MetaBriefVO>> listDatasourceTables(@RequestParam("id") Long id,
                                                                @RequestParam(value = "writable", required = false) Integer writable) {
        List<MetaBriefVO> briefVOList = etlDatasourceService.listDatasourceTables(id, writable);
        return CommonResult.success(briefVOList);
    }

    @Operation(summary = "查询表的列")
    @GetMapping("/table/columns")
    public CommonResult<List<ColumnDefine>> listTableColumns(@RequestParam("tableId") Long tableId) {
        List<ColumnDefine> columnDefines = etlDatasourceService.listTableColumns(tableId);
        return CommonResult.success(columnDefines);
    }

    @Operation(summary = "预览表数据")
    @PostMapping("/preview")
    public CommonResult<DataPreview> previewTableData(@Validated @RequestBody TablePreviewVO tablePreviewVO) {
        DataPreview dataPreview = dataInspectService.previewData(tablePreviewVO);
        return CommonResult.success(dataPreview);
    }
}
