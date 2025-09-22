// 实体管理服务
import {
    CreateRuleReqVO,
    UpdateRuleReqVO
} from '../types';
import { metadataService } from './clients';

/**
 * 获得校验规则分组详情
 * @param id 校验规则分组ID
 * @returns 校验规则分组详情
 */
export const getRuleById = (id: string) => {
  return metadataService.post('/validation-rule-group/get?id=' + id);
};

/**
 * 获取实体数据规则分页
 * @param entityId 实体ID
 * @returns 数据规则列表
 */
export const getEntityRules = (params: object) => {
  return metadataService.post('/validation-rule-group/page', params);
};

/**
 * 创建数据规则
 * @param data 数据规则信息
 * @returns 规则ID
 */
export const createRule = (data: CreateRuleReqVO) => {
  return metadataService.post('/validation-rule-group/create', data);
};

/**
 * 创建必填校验
 * @param data 数据规则信息
 */
export const createRequiredRule = (data: object) => {
  return metadataService.post('/validation/required/create', data);
};

/**
 * 更新必填校验
 * @param data 数据规则信息
 */
export const updateRequiredRule = (data: object) => {
  return metadataService.post('/validation/required/update', data);
};

/**
 * 删除必填校验
 */
export const deleteRequiredRule = (id: string) => {
  return metadataService.post('/validation/required/delete-by-field?id=' + id);
};

/**
 * 创建唯一校验
 * @param data 数据规则信息
 */
export const createUniqueRule = (data: object) => {
  return metadataService.post('/validation/unique/create', data);
};

/**
 * 更新唯一校验
 * @param data 数据规则信息
 */
export const updateUniqueRule = (data: object) => {
  return metadataService.post('/validation/unique/update', data);
};

/**
 * 删除唯一校验
 * @param id 唯一校验ID
 */
export const deleteUniqueRule = (id: string) => {
  return metadataService.post('/validation/unique/delete-by-field?id=' + id);
};

/**
 * 创建长度校验
 * @param data 数据规则信息
 */
export const createLengthRule = (data: object) => {
  return metadataService.post('/validation/length/create', data);
};

/**
 * 更新长度校验
 * @param data 数据规则信息
 */
export const updateLengthRule = (data: object) => {
  return metadataService.post('/validation/length/update', data);
};

/**
 * 删除长度校验
 * @param id 长度校验ID
 */
export const deleteLengthRule = (id: string) => {
  return metadataService.post('/validation/length/delete-by-field?id=' + id);
};

/**
 * 创建格式校验
 * @param data 数据规则信息
 */
export const createFormatRule = (data: object) => {
  return metadataService.post('/validation/format/create', data);
};

/**
 * 更新格式校验
 * @param data 数据规则信息
 */
export const updateFormatRule = (data: object) => {
  return metadataService.post('/validation/format/update', data);
};

/**
 * 创建范围校验
 * @param data 数据规则信息
 */
export const createRangeRule = (data: object) => {
  return metadataService.post('/validation/range/create', data);
};

/**
 * 更新范围校验
 * @param data 数据规则信息
 */
export const updateRangeRule = (data: object) => {
  return metadataService.post('/validation/range/update', data);
};

/**
 * 删除范围校验
 * @param id 范围校验ID
 */
export const deleteRangeRule = (id: string) => {
  return metadataService.post('/validation/range/delete-by-field?id=' + id);
};

/**
 * 创建子表空行校验
 * @param data 数据规则信息
 */
export const createChildNotEmptyRule = (data: object) => {
  return metadataService.post('/validation/child-not-empty/create', data);
};

/**
 * 更新子表空行校验
 * @param data 数据规则信息
 */
export const updateChildNotEmptyRule = (data: object) => {
  return metadataService.post('/validation/child-not-empty/update', data);
};

/**
 * 删除子表空行校验
 * @param id 子表空行校验ID
 */
export const deleteChildNotEmptyRule = (id: string) => {
  return metadataService.post('/validation/child-not-empty/delete-by-field?id=' + id);
};

/**
 * 更新数据规则
 * @param data 数据规则信息
 * @returns 操作结果
 */
export const updateRule = (data: UpdateRuleReqVO) => {
  return metadataService.post('/validation-rule-group/update', data);
};

/**
 * 删除数据规则
 * @param id 规则ID
 * @returns 操作结果
 */
export const deleteRule = (id: string) => {
  return metadataService.post('/validation-rule-group/delete?id=' + id);
};

