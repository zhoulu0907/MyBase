import { isRuntimeEnv } from '@onebase/common';
import { formulaParams } from '../types/formula';
import { formulaService, runtimeFormulaService } from './clients';

/**
 * 获取函数精简信息列表
 * @returns 函数精简信息列表
 */
export const getFormulaFunctionSimpleList = () => {
  return formulaService.get('/function/list-group-by-type');
};

/**
 * 获取函数精简信息
 * @returns 函数精简信息列表
 */
export const getFormulaById = (id: string) => {
  return formulaService.get(`/function/get?id=${id}`);
};

/**
 * 执行公式计算
 * @returns 返回计算结果
 */
export const debugFormula = (formulaData: formulaParams) => {
  return (isRuntimeEnv() ? runtimeFormulaService : formulaService).post('/engine/debug-formula', formulaData);
}