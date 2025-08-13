// 实体管理服务
import { metadataService } from './clients';

// 实体相关接口类型定义
export interface CreateEntityReqVO {
  code: string;
  displayName: string;
  description: string;
}

export interface UpdateEntityReqVO extends CreateEntityReqVO {
  id: string;
}

export interface GetEntityPageParams {
  pageNo: number;
  pageSize: number;
  datasourceId: string;
  code?: string;
  title?: string;
}

export interface CreateFieldReqVO {
  appId: string;
  entityId: string;
  fieldCode: string;
  fieldName: string;
  description: string;
  fieldType: string;
  isSystemField?: number;
  displayName: string;
}

export interface UpdateFieldReqVO extends CreateFieldReqVO {
  id: string;
}

export interface CreateMasterChildReqVO {
  parentEntityId: string;
  parentFieldId: string;
  childEntityId: string;
  childFieldId: string;
  childTableCode: string;
  childTableName: string;
  childTableDescription: string;
  appId: string;
  datasourceId: string;
}

export interface CreateRelationReqVO {
  sourceEntityId: string;
  sourceFieldId: string;
  targetEntityId: string;
  targetFieldId: string;
  relationshipType?: string;
  relationName?: string;
}

export interface UpdateRelationReqVO extends CreateRelationReqVO {
  id: string;
}

// 数据规则相关接口
export interface CreateRuleReqVO {
  entityId: string;
  name: string;
  description?: string;
  ruleType: string;
  ruleContent: string;
  isEnabled?: boolean;
}

export interface UpdateRuleReqVO extends CreateRuleReqVO {
  id: string;
}


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
export const getEntity = (id: string) => {
  return metadataService.post('/business-entity/get', { params: { id } });
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
  return metadataService.post('/business-entity/relation/delete', { params: { id } });
};

/**
 * 获取实体数据规则
 * @param entityId 实体ID
 * @returns 数据规则列表
 */
export const getEntityRules = (params: object) => {
  return metadataService.post('/validation-rule/page', params);
};

/**
 * 创建数据规则
 * @param data 数据规则信息
 * @returns 规则ID
 */
export const createRule = (data: CreateRuleReqVO) => {
  return metadataService.post('/business-entity/rule/create', data);
};

/**
 * 更新数据规则
 * @param data 数据规则信息
 * @returns 操作结果
 */
export const updateRule = (data: UpdateRuleReqVO) => {
  return metadataService.post('/business-entity/rule/update', data);
};

/**
 * 删除数据规则
 * @param id 规则ID
 * @returns 操作结果
 */
export const deleteRule = (id: string) => {
  return metadataService.post('/business-entity/rule/delete', { params: { id } });
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
 * 导出实体数据
 * @param entityId 实体ID
 * @returns 导出文件
 */
export const exportEntity = (entityId: string) => {
  return metadataService.post('/business-entity/export', {
    params: { entityId },
    responseType: 'blob'
  });
};

/**
 * 导入实体数据
 * @param file 导入文件
 * @returns 操作结果
 */
export const importEntity = (file: File) => {
  const formData = new FormData();
  formData.append('file', file);
  return metadataService.post('/entity/import', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

/**
 * 验证实体编码是否唯一
 * @param code 实体编码
 * @param excludeId 排除的实体ID（用于编辑时验证）
 * @returns 是否唯一
 */
export const validateEntityCode = (code: string, excludeId?: string) => {
  return metadataService.post('/entity/validate-code', {
    params: { code, excludeId }
  });
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

export const getEntityListByApp = (appId: string) => {
  return metadataService.post(`/business-entity/list-by-app?appId=${appId}`);
};

/**
 * 根据应用ID查询所有实体及字段信息
 * @param appId 应用ID
 * @returns 实体及字段信息
 */
export const getAppEntities = (appId: string) => {
  return metadataService.post(`/entity-relationship/app-entities?appId=${appId}`);
};


export const getEntityWithChildren = (entityID: string) => {
    return metadataService.post(`/entity-relationship/entity-with-children?entityId=${entityID}`);
}