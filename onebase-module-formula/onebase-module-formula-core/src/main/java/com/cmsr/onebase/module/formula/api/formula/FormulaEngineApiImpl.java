package com.cmsr.onebase.module.formula.api.formula;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.formula.api.formula.dto.FormulaExecuteReqDTO;
import com.cmsr.onebase.module.formula.api.formula.dto.FormulaExecuteRespDTO;
import com.cmsr.onebase.module.formula.service.engine.FormulaEngineService;
import com.cmsr.onebase.module.formula.vo.formula.FormulaExecuteRespVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class FormulaEngineApiImpl implements FormulaEngineApi {

    /**
     * 注入用户API
     */
    @Resource
    private FormulaEngineService formulaEngineService;

    @Override
    public CommonResult<FormulaExecuteRespDTO> executeFormula(FormulaExecuteReqDTO reqDTO) {
        long startTime = System.currentTimeMillis();
        log.info("FormulaEngineApi executeFormula start ---> "+reqDTO.getFormula());
        Object result = formulaEngineService.executeFormulaWithParamsData(reqDTO.getFormula(),
                reqDTO.getParameters(),reqDTO.getContextData());

        long executionTime = System.currentTimeMillis() - startTime;

        FormulaExecuteRespVO respVO = FormulaExecuteRespVO.success(result, executionTime);

        log.info("FormulaEngineApi executeFormula end --->，公式：{}，结果：{}，耗时：{}ms", reqDTO.getFormula(), result, executionTime);

        return CommonResult.success(BeanUtils.toBean(respVO, FormulaExecuteRespDTO.class));

    }
}
