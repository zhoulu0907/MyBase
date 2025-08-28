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
 * 创建唯一校验
 * @param data 数据规则信息
 */
export const createUniqueRule = (data: object) => {
  return metadataService.post('/validation/unique/create', data);
};

/**
 * 创建长度校验
 * @param data 数据规则信息
 */
export const createLengthRule = (data: object) => {
  return metadataService.post('/validation/length/create', data);
};

/**
 * 创建正则校验
 * @param data 数据规则信息
 */
export const createRegexRule = (data: object) => {
  return metadataService.post('/validation/regex/create', data);
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

