package com.cmsr.onebase.module.formula.api.function;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.formula.api.function.dto.FunctionRespDTO;
import com.cmsr.onebase.module.formula.dal.dataobject.FunctionDO;
import com.cmsr.onebase.module.formula.service.function.FunctionService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * 函数 API 实现类
 *
 * @author matianyu
 * @date 2025-08-28
 */
@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class FunctionApiImpl implements FunctionApi {

    @Resource(name = "formulaFunctionService") // 使用指定的bean名称
    private FunctionService functionService;

    @Override
    public CommonResult<FunctionRespDTO> getFunction(Long id) {
        FunctionDO function = functionService.getFunction(id);
        return success(BeanUtils.toBean(function, FunctionRespDTO.class));
    }

    @Override
    public CommonResult<List<FunctionRespDTO>> getFunctionList(Collection<Long> ids) {
        List<FunctionDO> functions = functionService.getFunctionList(ids);
        return success(BeanUtils.toBean(functions, FunctionRespDTO.class));
    }

    @Override
    public CommonResult<Boolean> validateFunctionList(Collection<Long> ids) {
        functionService.validateFunctionList(ids);
        return success(true);
    }

}
