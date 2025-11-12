package com.cmsr.onebase.module.etl.build.controller.datasource;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.etl.build.service.datasource.ETLDatasourceService;
import com.cmsr.onebase.module.etl.build.service.datasource.vo.ColumnDefine;
import com.cmsr.onebase.module.etl.build.service.datasource.vo.ETLDatasourceCreateReqVO;
import com.cmsr.onebase.module.etl.build.service.datasource.vo.ETLDatasourceUpdateReqVO;
import com.cmsr.onebase.module.etl.build.service.datasource.vo.TestConnectionVO;
import com.cmsr.onebase.module.etl.build.service.preview.vo.DataPreviewVO;
import com.cmsr.onebase.module.etl.build.service.preview.vo.TablePreviewVO;
import com.cmsr.onebase.module.etl.core.vo.datasource.DatasourcePageReqVO;
import com.cmsr.onebase.module.etl.core.vo.datasource.DatasourceRespVO;
import com.cmsr.onebase.module.etl.core.vo.datasource.MetaBriefVO;
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

    // GETs
    @PostMapping("/ping")
    @Operation(summary = "测试数据源连接")
    public CommonResult<Boolean> testConnection(@Validated @RequestBody TestConnectionVO requestVO) {
        Boolean connected = etlDatasourceService.pingDatasource(requestVO);
        return CommonResult.success(connected);
    }

    @GetMapping("/page")
    public CommonResult<PageResult<DatasourceRespVO>> getETLDatasourcePage(@Validated DatasourcePageReqVO pageReqVO) {
        PageResult<DatasourceRespVO> pageResult = etlDatasourceService.getETLDatasourcePage(pageReqVO);
        return CommonResult.success(pageResult);
    }

    @GetMapping("/{id}")
    public CommonResult<DatasourceRespVO> queryDatasourceDetail(@PathVariable("id") Long datasourceId) {
        DatasourceRespVO datasourceVO = etlDatasourceService.queryDatasourceDetail(datasourceId);
        return CommonResult.success(datasourceVO);
    }

    // POSTs
    @PostMapping("/create")
    @Operation(summary = "创建数据源")
    public CommonResult<Long> createETLDatasource(@Validated @RequestBody ETLDatasourceCreateReqVO createReqVO) {
        return etlDatasourceService.createDatasource(createReqVO);
    }

    @PostMapping("/update")
    @Operation(summary = "更新数据源")
    public CommonResult<Boolean> updateETLDatasource(@Validated @RequestBody ETLDatasourceUpdateReqVO updateReqVO) {
        etlDatasourceService.updateDatasource(updateReqVO);
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/collect")
    @Operation(summary = "采集元数据信息")
    public CommonResult<Boolean> runMetadataCollect(@RequestParam("id") Long id) {
        etlDatasourceService.executeMetadataCollectJob(id);
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除数据源")
    public CommonResult<Boolean> deleteETLDatasource(@RequestParam("id") Long id) {
        etlDatasourceService.deleteDatasource(id);
        return CommonResult.success(Boolean.TRUE);
    }

    @GetMapping("/list")
    public CommonResult<List<MetaBriefVO>> listDatasources(@RequestParam("applicationId") Long applicationId,
                                                           @RequestParam(value = "writable", required = false) Integer writable) {
        List<MetaBriefVO> briefVOList = etlDatasourceService.listDatasources(applicationId, writable);
        return CommonResult.success(briefVOList);
    }

    @GetMapping("/tables")
    public CommonResult<List<MetaBriefVO>> listDatasourceTables(@RequestParam("id") Long id,
                                                                @RequestParam(value = "writable", required = false) Integer writable) {
        List<MetaBriefVO> briefVOList = etlDatasourceService.listDatasourceTables(id, writable);
        return CommonResult.success(briefVOList);
    }

    @GetMapping("/table/columns")
    public CommonResult<List<ColumnDefine>> listTableColumns(@RequestParam("tableId") Long tableId) {
        List<ColumnDefine> columnDefines = etlDatasourceService.listTableColumns(tableId);
        return CommonResult.success(columnDefines);
    }

    @PostMapping("/preview")
    @Operation(summary = "预览表数据")
    public CommonResult<DataPreviewVO> previewTableData(@Validated @RequestBody TablePreviewVO tablePreviewVO) {
        DataPreviewVO dataPreviewVO = etlDatasourceService.previewTable(tablePreviewVO);
        return CommonResult.success(dataPreviewVO);
    }
}
