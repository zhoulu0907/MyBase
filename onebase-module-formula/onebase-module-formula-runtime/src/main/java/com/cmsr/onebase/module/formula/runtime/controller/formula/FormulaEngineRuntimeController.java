package com.cmsr.onebase.module.formula.runtime.controller.formula;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.formula.api.formula.dto.FormulaExecuteReqDTO;
import com.cmsr.onebase.module.formula.api.formula.dto.FormulaExecuteRespDTO;
import com.cmsr.onebase.module.formula.service.engine.FormulaEngineService;
import com.cmsr.onebase.module.formula.vo.formula.FormulaExecuteReqVO;
import com.cmsr.onebase.module.formula.vo.formula.FormulaExecuteRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
@Tag(name = "公式引擎Runtime")
@RestController
@RequestMapping("/formula/engine")
@Validated
@Slf4j
public class FormulaEngineRuntimeController {

    @Resource
    private FormulaEngineService formulaEngineService;

    @PostMapping("/debug-formula")
    @Operation(summary = "调试公式计算")
    public CommonResult<FormulaExecuteRespVO> debugFormula(@Valid @RequestBody FormulaExecuteReqVO reqVO) {
        log.info("debugFormula -->开始 formula: {} , params: {} " ,reqVO.getFormula(), reqVO.getParameters());

        long startTime = System.currentTimeMillis();
        Object result = formulaEngineService.executeFormulaWithParams(reqVO.getFormula(), reqVO.getParameters());
        long executionTime = System.currentTimeMillis() - startTime;
        FormulaExecuteRespVO respVO = FormulaExecuteRespVO.success(result, executionTime);

        log.info("debugFormula -->结束，公式：{}，结果：{}，耗时：{}ms", reqVO.getFormula(), result, executionTime);
        return CommonResult.success(respVO);
    }

    @PostMapping("/execute-formula")
    @Operation(summary = "执行公式计算")
    public CommonResult<FormulaExecuteRespDTO> executeFormula(@Valid @RequestBody FormulaExecuteReqDTO reqDTO) {
        log.info("executeFormula -->开始 formula: {} , params: {} " ,reqDTO.getFormula(), reqDTO.getParameters());

        long startTime = System.currentTimeMillis();
        Object result = formulaEngineService.executeFormulaWithParamsForFlow(reqDTO.getFormula(), reqDTO.getParameters(), reqDTO.getContextData());
        long executionTime = System.currentTimeMillis() - startTime;
        FormulaExecuteRespVO respVO = FormulaExecuteRespVO.success(result, executionTime);

        log.info("executeFormula -->结束，公式：{}，结果：{}，耗时：{}ms", reqDTO.getFormula(), result, executionTime);

        return CommonResult.success(BeanUtils.toBean(respVO, FormulaExecuteRespDTO.class));
    }

}