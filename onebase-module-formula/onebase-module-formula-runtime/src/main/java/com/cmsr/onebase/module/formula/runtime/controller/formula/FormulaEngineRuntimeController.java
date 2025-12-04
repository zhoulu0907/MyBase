package com.cmsr.onebase.module.formula.runtime.controller.formula;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.formula.api.formula.dto.FormulaExecuteReqDTO;
import com.cmsr.onebase.module.formula.api.formula.dto.FormulaExecuteRespDTO;
import com.cmsr.onebase.module.formula.service.engine.FormulaEngineService;
import com.cmsr.onebase.module.formula.vo.formula.FormulaExecuteReqVO;
import com.cmsr.onebase.module.formula.vo.formula.FormulaExecuteRespVO;
import com.cmsr.onebase.module.formula.vo.formula.FormulaValidateReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
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
@RequestMapping("/formula/excute/engine")
@Validated
@Slf4j
public class FormulaEngineRuntimeController {

    @Resource
    private FormulaEngineService formulaEngineService;

    @PostMapping("/execute-formula1")
    @Operation(summary = "执行公式计算")
    public CommonResult<FormulaExecuteRespDTO> executeFormula(@Valid @RequestBody FormulaExecuteReqDTO reqDTO) {
        long startTime = System.currentTimeMillis();
        log.info("############: "+reqDTO.getFormula());
        Object result = formulaEngineService.executeFormulaWithParamsForFlow(reqDTO.getFormula(), reqDTO.getParameters(),
                reqDTO.getContextData());

        long executionTime = System.currentTimeMillis() - startTime;

        FormulaExecuteRespVO respVO = FormulaExecuteRespVO.success(result, executionTime);

        log.info("公式执行成功，公式：{}，结果：{}，耗时：{}ms", reqDTO.getFormula(), result, executionTime);

        return CommonResult.success(BeanUtils.toBean(respVO, FormulaExecuteRespDTO.class));

    }

}