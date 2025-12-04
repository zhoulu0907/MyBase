// 实体管理服务
import {
  CreateEntityReqVO,
  CreateFieldReqVO,
  CreateMasterChildReqVO,
  CreateRelationReqVO,
  GetEntityPageParams,
  UpdateEntityReqVO,
  UpdateFieldReqVO,
  UpdateRelationReqVO
} from '../types';
import { metadataService, runtimeMetadataService } from './clients';

/**
 * 获取实体分页列表
 * @param params 分页查询参数
 * @returns 实体分页列表
 */
export const getEntityPage = (params: GetEntityPageParams) => {
  return metadataService.post('/business-entity/page', params);
};

/**
 * 根据数据源获得业务实体列表
 * @returns 实体列表
 */
export const getEntityList = (datasourceId: string) => {
  return metadataService.post('/business-entity/list-by-datasource?datasourceId=' + datasourceId);
};

/**
 * 根据ID获取实体详情
 * @param id 实体ID
 * @returns 实体详情
 */
export const getEntityById = (id: string) => {
  return metadataService.post('/business-entity/get?id=' + id);
};

/**
 * 根据编码获取实体
 * @param code 实体编码
 * @returns 实体详情
 */
export const getEntityByCode = (code: string) => {
  return metadataService.post('/business-entity/get-by-code', { params: { code } });
};

/**
 * 创建实体
 * @param data 实体信息
 * @returns 实体ID
 */
export const createEntity = (data: CreateEntityReqVO) => {
  return metadataService.post('/business-entity/create', data);
};

/**
 * 更新实体
 * @param data 实体信息
 * @returns 操作结果
 */
export const updateEntity = (data: UpdateEntityReqVO) => {
  return metadataService.post('/business-entity/update', data);
};

/**
 * 删除实体
 * @param id 实体ID
 * @returns 操作结果
 */
export const deleteEntity = (id: string) => {
  return metadataService.post('/business-entity/delete?id=' + id);
};

/**
 * 批量删除实体
 * @param ids 实体ID数组
 * @returns 操作结果
 */
export const batchDeleteEntities = (ids: string[]) => {
  return metadataService.post('/business-entity/batch-delete', { data: { ids } });
};

/**
 * 获取实体的字段列表
 * @param entityId 实体ID
 * @returns 字段列表
 */
export const getEntityFields = (params: object) => {
  return metadataService.post('/entity-field/list', params);
};

/**
 * 分页查询指定实体的字段列表
 * @param entityId 实体ID
 * @returns 字段列表
 */
export const getEntityFieldsPage = (params: object) => {
  return metadataService.post('/entity-field/page', params);
};

/**
 * 创建字段
 * @param data 字段信息
 * @returns 字段ID
 */
export const createField = (data: CreateFieldReqVO) => {
  return metadataService.post('/entity-field/create', data);
};

/**
 * 更新字段
 * @param data 字段信息
 * @returns 操作结果
 */
export const updateField = (data: UpdateFieldReqVO) => {
  return metadataService.post('/entity-field/update', data);
};

/**
 * 删除字段
 * @param id 字段ID
 * @returns 操作结果
 */
export const deleteField = (id: string) => {
  return metadataService.post('/entity-field/delete?id=' + id);
};

/**
 * 根据ID获取字段详细信息
 * @param id 字段ID
 * @returns 字段详细信息
 */
export const getFieldById = (id: string) => {
  return metadataService.post('/entity-field/get?id=' + id);
};

/**
 * 批量保存实体字段（增删改）
 * @param id 字段ID
 * @returns 字段详细信息
 */
export const batchSaveFields = (data: object) => {
  return metadataService.post('/entity-field/batch-save', data);
};

/**
 * 批量查询字段可选校验类型
 * @returns
 */
export const getFieldCheckTypeApi = (fieldIdList: string[]) => {
  return metadataService.post('/entity-field/validation-types/query', { fieldIdList });
};
/**
 * 获取系统支持的字段类型列表
 * @returns 字段类型列表
 */
export const getFieldTypes = () => {
  return metadataService.post('/entity-field/field-types');
};

/**
 * 获取实体的关联关系列表
 * @param entityId 实体ID
 * @returns 关联关系列表
 */
export const getEntityRelations = (params: object) => {
  return metadataService.post('/entity-relationship/page', params);
};

/**
 * 创建主子关系
 * @param data 关联关系信息
 * @returns 关联关系ID
 */
export const createMasterChild = (data: CreateMasterChildReqVO) => {
  return metadataService.post('/entity-relationship/create-parent-child', data);
};

/**
 * 创建关联关系
 * @param data 关联关系信息
 * @returns 关联关系ID
 */
export const createRelation = (data: CreateRelationReqVO) => {
  return metadataService.post('/entity-relationship/create', data);
};

/**
 * 更新关联关系
 * @param data 关联关系信息
 * @returns 操作结果
 */
export const updateRelation = (data: UpdateRelationReqVO) => {
  return metadataService.post('/entity-relationship/update', data);
};

/**
 * 删除关联关系
 * @param id 关联关系ID
 * @returns 操作结果
 */
export const deleteRelation = (id: string) => {
  return metadataService.post('/entity-relationship/delete?id=' + id);
};

/**
 * 根据ID获取关系详细信息
 * @param id 关联关系ID
 * @returns 关联关系详细信息
 */
export const getRelationById = (id: string) => {
  return metadataService.post('/entity-relationship/get?id=' + id);
};

/**
 * 根据实体ID查询实体名称及其关联的子表信息
 * @param entityId 实体ID
 * @returns 实体名称及其关联的子表信息
 */
export const getEntityFieldsWithChildren = (entityId: string, runtime?: boolean) => {
  return (runtime ? runtimeMetadataService : metadataService).post(
    '/entity-relationship/entity-with-children?entityId=' + entityId
  );
};

/**
 * 获取实体数据方法
 * @param entityId 实体ID
 * @returns 数据方法列表
 */
export const getEntityMethods = (params: object) => {
  return metadataService.post('/data-method/list', params);
};

/**
 * 根据ID查询数据详情
 * @param id 方法ID
 * @returns 数据详情
 */
export const getMethodDataById = (params: object) => {
  return metadataService.post('/data-method/detail', params);
};

/**
 * 获取实体统计信息
 * @returns 统计信息
 */
export const getEntityStats = () => {
  return metadataService.post('/entity/stats');
};

/**
 * 获取实体关系图数据
 * @param entityId 实体ID（可选，不传则获取所有实体的关系图）
 * @returns 关系图数据
 */
export const getEntityGraph = (datasourceId: string) => {
  return metadataService.post('/business-entity/er-diagram?datasourceId=' + datasourceId);
};

export const getEntityListByApp = (applicationId: string) => {
  return metadataService.post(`/business-entity/list-by-app?applicationId=${applicationId}`);
};

/**
 * 根据应用ID查询所有实体及字段信息
 * @param applicationId 应用ID
 * @returns 实体及字段信息
 */
export const getAppEntities = (applicationId: string) => {
  return metadataService.post(`/entity-relationship/app-entities?applicationId=${applicationId}`);
};

/**
 * 按字段ID获取选项列表
 * fieldId
 * @returns 字段配置信息
 */
export const getEntityFieldOptions = (fieldId: string) => {
  return metadataService.post(`/entity-field/option/list?fieldId=${fieldId}`);
};

/**
 * 按字段ID获取自动编号配置与规则
 * fieldId
 * @returns 自动编号配置规则
 */
export const getAutoNumberConfig = (fieldId: string) => {
  return metadataService.post(`/auto-number/config/get?fieldId=${fieldId}`);
};
