import { formulaService } from './clients';

/**
 * 获取函数精简信息列表
 * @returns 函数精简信息列表
 */
export const getFormulaFunctionSimpleList = () => {
  return formulaService.get('/function/simple-list');
};

/**
 * 获取函数精简信息
 * @returns 函数精简信息列表
 */
export const getFormulaById = (id: string) => {
  return formulaService.get(`/function/get?id=${id}`);
};