package com.cmsr.onebase.module.flow.build.controller;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.build.service.FlowExecutionLogService;
import com.cmsr.onebase.module.flow.build.vo.ExecutionLogVO;
import com.cmsr.onebase.module.flow.core.enums.FlowErrorCodeConstants;
import com.cmsr.onebase.module.flow.core.vo.PageExecutionLogReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 流程管理 - 管理员控制器
 */
@Setter
@RestController
@RequestMapping("/flow/log")
@Tag(name = "流程日志", description = "流程日志相关接口")
@Validated
public class FlowExecutionLogController {

    @Autowired
    private FlowExecutionLogService flowExecutionLogService;

    @GetMapping("/page")
    @Operation(summary = "分页查询执行日志")
    public CommonResult<PageResult<ExecutionLogVO>> pageList(PageExecutionLogReqVO reqVO) {
        PageResult<ExecutionLogVO> pageResult = flowExecutionLogService.pageList(reqVO);
        return CommonResult.success(pageResult);
    }

    @GetMapping("/get")
    @Operation(summary = "获取日志详情")
    public CommonResult<ExecutionLogVO> getDetail(@RequestParam("id") Long id) {
        ExecutionLogVO executionLogVO = flowExecutionLogService.getDetail(id);
        if (executionLogVO == null) {
            return CommonResult.error(FlowErrorCodeConstants.LOG_NOT_EXIST);
        }
        return CommonResult.success(executionLogVO);
    }

    @GetMapping("/statistic-tody")
    @Operation(summary = "统计执行日志")
    public CommonResult<Map<String, Object>> statisticTody(@RequestParam("applicationId") Long applicationId) {
        Map<String, Object> result = flowExecutionLogService.statisticTody(applicationId);
        return CommonResult.success(result);
    }
}
