package com.cmsr.onebase.module.etl.build.controller.mgt;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.etl.build.service.mgt.ETLFlinkFunctionService;
import com.cmsr.onebase.module.etl.build.service.mgt.vo.FlinkFunctionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "ETL - Flink函数")
@RestController
@RequestMapping("/etl/flink-function")
@Validated
public class ETLFlinkFunctionController {

    @Resource
    private ETLFlinkFunctionService etlFlinkFunctionService;


    @GetMapping("/list-type")
    @Operation(summary = "查询Flink函数类型列表")
    public CommonResult<List<String>> listFlinkFunctionTypes() {
        List<String> workflowPage = etlFlinkFunctionService.listFlinkFunctionTypes();
        return CommonResult.success(workflowPage);
    }


    @GetMapping("/list")
    @Operation(summary = "查询Flink函数列表")
    public CommonResult<List<FlinkFunctionVO>> listFlinkFunctions(
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "key", required = false) String key) {
        List<FlinkFunctionVO> workflowPage = etlFlinkFunctionService.listFlinkFunctions(type, key);
        return CommonResult.success(workflowPage);
    }

}
