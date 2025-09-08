package com.cmsr.onebase.module.formula.controller.admin.formula;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.formula.controller.admin.formula.vo.FormulaExecuteReqVO;
import com.cmsr.onebase.module.formula.controller.admin.formula.vo.FormulaExecuteRespVO;
import com.cmsr.onebase.module.formula.controller.admin.formula.vo.FormulaValidateReqVO;
import com.cmsr.onebase.module.formula.service.FormulaEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import java.util.Arrays;
import java.util.List;

/**
 * 公式引擎控制器
 * 提供Excel公式计算功能的REST API接口
 *
 * @author matianyu
 * @date 2025-09-01
 */
@Tag(name = "管理后台 - 公式引擎")
@RestController
@RequestMapping("/formula/engine")
@Validated
@Slf4j
public class FormulaEngineController {

    @Resource
    private FormulaEngineService formulaEngineService;

    @PostMapping("/execute")
    @Operation(summary = "执行公式计算")
    @PreAuthorize("@ss.hasPermission('formula:engine:execute')")
    public CommonResult<FormulaExecuteRespVO> executeFormula(@Valid @RequestBody FormulaExecuteReqVO reqVO) {
        long startTime = System.currentTimeMillis();

        Object result = formulaEngineService.executeFormula(reqVO.getFormula(), reqVO.getParameters());

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

    @GetMapping("/functions")
    @Operation(summary = "获取支持的函数列表")
    @PreAuthorize("@ss.hasPermission('formula:engine:query')")
    public CommonResult<List<String>> getSupportedFunctions() {

        String[] functions = formulaEngineService.getSupportedFunctions();
        List<String> functionList = Arrays.asList(functions);

        log.debug("获取支持的函数列表，共{}个函数", functions.length);

        return CommonResult.success(functionList);

    }

    @PostMapping("/cache/clear")
    @Operation(summary = "清理公式缓存")
    @PreAuthorize("@ss.hasPermission('formula:engine:cache:clear')")
    public CommonResult<Boolean> clearCache() {
        formulaEngineService.clearCache();

        log.info("公式缓存清理成功");

        return CommonResult.success(true);
    }
}
