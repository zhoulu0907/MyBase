package com.cmsr.onebase.module.etl.build.controller.datasource;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.etl.build.controller.datasource.vo.DataFactoryDatasourceReqVO;
import com.cmsr.onebase.module.etl.build.service.datasource.DataFactoryDatasourceService;
import com.cmsr.onebase.module.etl.build.service.datasource.vo.DatabaseTypeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "管理后台 - 数据工厂 - 数据源管理")
@RestController
@RequestMapping("/datafactory/datasource")
@Validated
public class DataFactoryDatasourceController {

    @Resource
    private DataFactoryDatasourceService dataFactoryDatasourceService;

    @GetMapping("/supported")
    @Operation(summary = "获取所有支持的数据源类型")
    public CommonResult<List<DatabaseTypeVO>> getSupportedDatabaseTypes() {
        List<DatabaseTypeVO> supportedDatabaseTypes = dataFactoryDatasourceService.getSupportedDatabaseTypes();
        return CommonResult.success(supportedDatabaseTypes);
    }

    @PostMapping("/ping")
    @Operation(summary = "测试数据源连接")
    public CommonResult<Boolean> testConnection(@Valid @RequestBody DataFactoryDatasourceReqVO requestVO) {
        Boolean connected = dataFactoryDatasourceService.pingDatasource(requestVO);
        return CommonResult.success(connected);
    }

    @PostMapping("/create")
    @Operation(summary = "创建数据源")
//    @PreAuthorize("@ss.hasPermission('datafactory:datasource:create')")
    public CommonResult<Long> createDataFactoryDatasource(@Valid @RequestBody DataFactoryDatasourceReqVO requestVO) {
        Long datasourceId = dataFactoryDatasourceService.createDatasource(requestVO);
        return CommonResult.success(datasourceId);
    }

    @PostMapping("/update")
    @Operation(summary = "更新数据源")
    public CommonResult<Boolean> updateDataFactoryDatasource(@Valid @RequestBody DataFactoryDatasourceReqVO requestVO) {
        dataFactoryDatasourceService.updateDatasource(requestVO);
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/collect")
    @Operation(summary = "采集元数据信息")
    public CommonResult<Boolean> runMetadataCollect(@RequestParam("id") Long id) {
        dataFactoryDatasourceService.executeMetadataCollectJob(id);
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除数据源")
    public CommonResult<Boolean> deleteDataFactoryDatasource(@RequestParam("id") Long id) {

        return CommonResult.success(Boolean.TRUE);
    }
}
