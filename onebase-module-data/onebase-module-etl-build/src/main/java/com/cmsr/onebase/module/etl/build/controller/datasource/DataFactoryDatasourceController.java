package com.cmsr.onebase.module.etl.build.controller.datasource;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.etl.build.service.datasource.DataFactoryDatasourceService;
import com.cmsr.onebase.module.etl.build.service.datasource.vo.DatabaseTypeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

//    @PostMapping("/create")
//    @Operation(summary = "创建数据源")
//    public CommonResult createDataFactoryDatasource() {
//        return CommonResult.success();
//    }
}
