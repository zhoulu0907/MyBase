package com.cmsr.onebase.module.formula.build.controller.formula;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.formula.vo.formula.FormulaExecuteReqVO;
import com.cmsr.onebase.module.formula.vo.formula.FormulaExecuteRespVO;
import com.cmsr.onebase.module.formula.vo.formula.FormulaValidateReqVO;
import com.cmsr.onebase.module.formula.service.engine.FormulaEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公式引擎控制器
 * 提供Excel公式计算功能的REST API接口
 *
 * @author matianyu
 * @date 2025-09-01
 */
@Tag(name = "公式引擎")
@RestController
@RequestMapping("/formula/engine")
@Validated
@Slf4j
@PermitAll
public class FormulaEngineController {

    @Resource
    private FormulaEngineService formulaEngineService;

    @PostMapping("/debugFormula")
    @Operation(summary = "执行公式计算")
    @PreAuthorize("@ss.hasPermission('formula:engine:execute')")
    public CommonResult<FormulaExecuteRespVO> debugFormula(@Valid @RequestBody FormulaExecuteReqVO reqVO) {
        long startTime = System.currentTimeMillis();
        log.info("############: "+reqVO.getFormula());
        log.info("############: "+reqVO.getParameters());
        Object result = formulaEngineService.executeFormulaWithParams(reqVO.getFormula(), reqVO.getParameters());

        long executionTime = System.currentTimeMillis() - startTime;

        FormulaExecuteRespVO respVO = FormulaExecuteRespVO.success(result, executionTime);

        log.info("公式执行成功，公式：{}，结果：{}，耗时：{}ms", reqVO.getFormula(), result, executionTime);

        return CommonResult.success(respVO);
    }

    @PostMapping("/executeForFlow")
    @Operation(summary = "执行公式计算")
    @PreAuthorize("@ss.hasPermission('formula:engine:execute')")
    public CommonResult<FormulaExecuteRespVO> executeFormulaForFlow(@Valid @RequestBody FormulaExecuteReqVO reqVO) {
        long startTime = System.currentTimeMillis();
        log.info("############: "+reqVO.getFormula());
        Object result = formulaEngineService.executeFormulaWithParams(reqVO.getFormula(), reqVO.getParameters(),null);
        long executionTime = System.currentTimeMillis() - startTime;

        FormulaExecuteRespVO respVO = FormulaExecuteRespVO.success(result, executionTime);

        log.info("公式执行成功，公式：{}，结果：{}，耗时：{}ms", reqVO.getFormula(), result, executionTime);

        return CommonResult.success(respVO);
    }


    @PostMapping("/validate")
    @Operation(summary = "验证公式语法")
    @PreAuthorize("@ss.hasPermission('formula:engine:validate')")
    public CommonResult<Boolean> validateFormula(@Valid @RequestBody FormulaValidateReqVO reqVO) {
        boolean isValid = formulaEngineService.validateFormula(reqVO.getFormula());

        log.info("公式验证完成，公式：{}，结果：{}", reqVO.getFormula(), isValid);

        return CommonResult.success(isValid);
    }

}
