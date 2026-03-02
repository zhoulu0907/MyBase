import type { AppEntities, AppEntity, AppEntityField, ChildEntity, EntityFieldOption } from '@onebase/app';
import { ENTITY_TYPE } from '@onebase/app';
import { getDictDataListByTypeId, getDictDataByTypes, type DictData } from '@onebase/platform-center';

// 获取单个字段的配置
export const getFieldConfig = (dataField: string[], mainEntity: AppEntity, subEntities: AppEntities) => {
  if (!dataField || dataField.length === 0) {
    return null;
  }

  const [tableName, fieldName] = dataField;

  if (!tableName || !fieldName || !mainEntity.tableName) {
    return null;
  }
  let realTableName = tableName;
  let realFieldName = fieldName;
  const index = fieldName?.indexOf('.');
  const lastIndex = fieldName?.lastIndexOf('.');
  if (index !== -1) {
    // 表格中实际的名称
    realTableName = fieldName.slice(0, index);
    realFieldName = lastIndex === -1 ? fieldName : fieldName.slice(lastIndex + 1);
  }
  if (mainEntity.tableName === realTableName) {
    // 主表
    // 当前字段
    const currentField = mainEntity.fields.find((ele: AppEntityField) => ele.fieldName === realFieldName);
    return currentField;
  } else {
    // 子表
    const currentSubEntity = subEntities.entities?.find((ele: AppEntity) => ele.tableName === realTableName);
    // 字段
    const currentField = currentSubEntity?.fields.find((ele: AppEntityField) => ele.fieldName === realFieldName);
    return currentField;
  }
};

// 通过配置获取下拉选项
export const getFieldOptionsConfig = async (
  dataField: string[],
  mainEntity: AppEntity,
  subEntities: AppEntities,
  dictMap?: any
) => {
  const currentField = getFieldConfig(dataField, mainEntity, subEntities);
  if (!currentField) {
    return [];
  }
  if (currentField.dictTypeId) {
    let dictDataList: DictData[] = [];
    if (dictMap) {
      dictDataList = dictMap[currentField.dictTypeId] || [];
    }
    if (dictDataList.length == 0) {
      dictDataList = await getDictDataListByTypeId(currentField.dictTypeId);
    }

    const dictOptions = dictDataList?.filter((e: DictData) => e.status === 1); // 只显示启用状态的字典数据
    currentField.options = dictOptions;
    return dictOptions || [];
  } else if (currentField.options?.length) {
    const newOptions = currentField.options.map((ele: EntityFieldOption) => ({
      id: ele.optionUuid || ele.id,
      sort: 0,
      label: ele.optionLabel,
      value: ele.optionValue,
      status: 1
    }));
    return newOptions || [];
  }
  return [];
};

export const getFieldAutoCodeConfig = async (dataField: string[], mainEntity: AppEntity, subEntities: AppEntities) => {
  const currentField = getFieldConfig(dataField, mainEntity, subEntities);
  if (!currentField) {
    return [];
  }
  if (currentField.autoNumberConfig?.rules?.length) {
    return [...currentField.autoNumberConfig?.rules];
  }
  return [];
};

// 设置实体及其字段配置
export const setMainMetaData = async (
  entityWithChildren: any,
  setMainEntity: (mainEntity: any) => void,
  setSubEntities: (subEntities: any) => void,
  setDictData?: (dictMap: any) => void
) => {
  if (entityWithChildren) {
    setMainEntity({
      entityId: entityWithChildren.entityId,
      entityUuid: entityWithChildren.entityUuid,
      tableName: entityWithChildren.tableName,
      entityName: entityWithChildren.entityName,
      entityType: ENTITY_TYPE.MAIN,
      fields: entityWithChildren.fields
    });

    const subEntities = entityWithChildren.childEntities?.map((entity: ChildEntity) => ({
      entityId: entity.childEntityId,
      entityUuid: entity.childEntityUuid,
      tableName: entity.childTableName,
      entityName: entity.childEntityName,
      entityType: entity.relationshipType === 'SUBTABLE_ONE_TO_MANY' ? ENTITY_TYPE.SUB : ENTITY_TYPE.INDEP, // 细分子实体类型，用于暂先隐藏子表以外的关联表
      fields: entity.childFields,
    }))
    setSubEntities({
      entities: subEntities || []
    });

    // 收集主表字段中的 dictTypeId
    const mainDictTypeIds = entityWithChildren.fields
      .filter((field: AppEntityField) => field.dictTypeId)
      .map((field: AppEntityField) => field.dictTypeId!);

    // 收集子表字段中的 dictTypeId
    const childDictTypeIds: string[] = [];
    if (entityWithChildren.childEntities && entityWithChildren.childEntities.length > 0) {
      entityWithChildren.childEntities.forEach((childEntity: ChildEntity) => {
        if (childEntity.childFields) {
          const childFieldDictTypeIds = childEntity.childFields
            .filter((field: AppEntityField) => field.dictTypeId)
            .map((field: AppEntityField) => field.dictTypeId!);
          childDictTypeIds.push(...childFieldDictTypeIds);
        }
      });
    }

    // 合并并去重
    const dictTypeIds = Array.from(new Set([...mainDictTypeIds, ...childDictTypeIds]));
    console.log('dictTypeIds: ', dictTypeIds);

    const res = await getDictDataByTypes({ dictTypeIds: dictTypeIds });
    console.log('dictDataList: ', res);

    if (setDictData) {
      setDictData(res);
    }
  }
}
