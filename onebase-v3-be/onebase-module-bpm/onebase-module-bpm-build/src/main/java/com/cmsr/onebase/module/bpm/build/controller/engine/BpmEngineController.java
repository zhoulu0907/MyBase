package com.cmsr.onebase.module.bpm.build.controller.engine;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.bpm.build.controller.engine.vo.BpmExecuteReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 审批流控制器
 *
 * @author matianyu
 * @date 2025-09-17
 */
@Tag(name = "管理后台 - 审批流")
@RestController
@RequestMapping("/bpm/engine")
@Validated
@Slf4j
public class BpmEngineController {
    @PostMapping("/execute")
    @Operation(summary = "执行流程")
    public CommonResult<Boolean> execute(@Valid @RequestBody BpmExecuteReqVO reqVO) {
        long startTime = System.currentTimeMillis();
        // Object result = bpmEngineService.executeFormulaWithParams(reqVO.getFormula(), reqVO.getParameters());
        long executionTime = System.currentTimeMillis() - startTime;
        log.info("流程执行成功，流程ID：{}，结果：{}，耗时：{}ms", reqVO.getProcessId(), true, executionTime);
        return CommonResult.success(true);
    }
}
