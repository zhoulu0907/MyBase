package com.cmsr.onebase.module.formula.api.function;

/**
 * 函数 API 实现类
 *
 * @author matianyu
 * @date 2025-08-28
 */
// @RestController // 提供 RESTful API 接口，给 Feign 调用
// @Validated
// public class FormulaFunctionApiImpl implements FunctionApi {
//
//     @Resource(name = "formulaFunctionService") // 使用指定的bean名称
//     private FormulaFunctionService formulaFunctionService;
//
//     @Override
//     public CommonResult<FunctionRespDTO> getFunction(Long id) {
//         FunctionDO function = formulaFunctionService.getFunction(id);
//         return success(BeanUtils.toBean(function, FunctionRespDTO.class));
//     }
//
//     @Override
//     public CommonResult<List<FunctionRespDTO>> getFunctionList(Collection<Long> ids) {
//         List<FunctionDO> functions = formulaFunctionService.getFunctionList(ids);
//         return success(BeanUtils.toBean(functions, FunctionRespDTO.class));
//     }
//
//     @Override
//     public CommonResult<Boolean> validateFunctionList(Collection<Long> ids) {
//         formulaFunctionService.validateFunctionList(ids);
//         return success(true);
//     }
//
// }
