import { RELATIONSHIP_TYPE } from '@/pages/CreateApp/pages/DataFactory/utils/types';
import { useAppStore } from '@/store/store_app';
import type { TreeSelectDataType } from '@arco-design/web-react/es/TreeSelect/interface';
import {
  getEntityFields,
  getEntityFieldsWithChildren,
  getEntityListByApp,
  getFieldCheckTypeApi,
  RELATION_TYPE,
  type AppEntityField,
  type ChildEntity,
  type EntityFieldValidationTypes,
  type MetadataEntityPair
} from '@onebase/app';
import { useEffect, useState } from 'react';

/**
 * 获取过滤后的主表实体列表
 */
export const useMainEntityList = () => {
  const { curAppId } = useAppStore();
  const [mainEntityList, setMainEntityList] = useState<MetadataEntityPair[]>([]);

  const loadMainEntityList = async () => {
    const res = await getEntityListByApp(curAppId);
    const curMainEntities = res.filter(
      (item: MetadataEntityPair) =>
        item.relationType !== RELATION_TYPE.SLAVE ||
        (item.relationType === RELATION_TYPE.SLAVE &&
          !item.relationshipTypes.includes(RELATIONSHIP_TYPE.SUBTABLE_ONE_TO_MANY))
    );
    setMainEntityList(curMainEntities);
  };

  useEffect(() => {
    loadMainEntityList();
  }, []);

  return { mainEntityList, loadMainEntityList };
};

/**
 * 处理实体字段数据的 hook
 */
export const useEntityFields = () => {
  const { curAppId } = useAppStore();
  const [subEntityList, setSubEntityList] = useState<MetadataEntityPair[]>([]);
  const [mainEntityFields, setMainEntityFields] = useState<TreeSelectDataType>([]);
  const [subEntityFields, setSubEntityFields] = useState<TreeSelectDataType[]>([]);
  const [dataNodeEntityFields, setDataNodeEntityFields] = useState<TreeSelectDataType>([]);
  const [validationTypes, setValidationTypes] = useState<EntityFieldValidationTypes[]>([]);
  const [fieldDataList, setFieldDataList] = useState<AppEntityField[]>([]);

  /**
   * 处理主表变更：获取子表列表、主表字段、子表字段、验证类型
   */
  const handleMainTableChange = async (
    mainTableName: string,
    mainEntityList: MetadataEntityPair[],
    options?: { onlySubEntityList?: boolean }
  ) => {
    const mainEntityId = mainEntityList.find((item) => item.tableName === mainTableName)?.entityId;
    if (!mainEntityId) {
      return;
    }

    const fieldIds: string[] = [];
    const res = await getEntityFieldsWithChildren(mainEntityId);

    // 设置子表列表
    const newSubEntityList = (res.childEntities || []).map((item: any) => ({
      entityId: item.childEntityId,
      tableName: item.childTableName,
      entityName: item.childEntityName
    }));
    setSubEntityList(newSubEntityList);

    // 如果只需要子表列表，提前返回
    if (options?.onlySubEntityList) {
      return;
    }

    // 处理主表字段
    if (res.parentFields) {
      const fields = res.parentFields.map((item: AppEntityField) => {
        fieldIds.push(item.fieldId);
        return {
          key: `${res.tableName}.${item.fieldName}`,
          title: item.displayName,
          fieldType: item.fieldType
        };
      });
      setMainEntityFields({
        key: res.entityId,
        title: res.entityName,
        children: fields
      });

      // 设置字段数据列表（用于 FieldEditor）
      res.parentFields.forEach((item: AppEntityField) => {
        item.fieldKey = `${res.tableName}.${item.fieldName}`;
      });
      setFieldDataList(res.parentFields);
    }

    // 处理子表字段
    if (res.childEntities) {
      const subFields: TreeSelectDataType[] = [];
      res.childEntities.forEach((subEntity: ChildEntity) => {
        const fields = subEntity.childFields.map((item: AppEntityField) => {
          fieldIds.push(item.fieldId);
          return {
            key: `${subEntity.childTableName}.${item.fieldName}`,
            title: item.displayName,
            fieldType: item.fieldType
          };
        });
        subFields.push({
          key: subEntity.childTableName,
          title: subEntity.childEntityName,
          children: fields
        });
      });
      setSubEntityFields(subFields);
    }

    // 获取验证类型
    const newValidationTypes = await getFieldCheckTypeApi(fieldIds);
    newValidationTypes.forEach((item: EntityFieldValidationTypes) => {
      const fieldName =
        [...res.parentFields].find((field: AppEntityField) => field.fieldId == item.fieldId)?.fieldName || '';
      item.fieldKey = `${res.tableName}.${fieldName}`;

      if (!fieldName) {
        for (const subEntity of res.childEntities) {
          const foundField = subEntity.childFields.find((field: AppEntityField) => field.fieldId == item.fieldId);
          if (foundField) {
            item.fieldKey = `${subEntity.childTableName}.${foundField.fieldName}`;
          }
        }
      }
    });
    setValidationTypes(newValidationTypes);
  };

  /**
   * 处理子表变更：获取子表字段
   */
  const handleSubTableChange = async (subTableName: string, subEntityList: MetadataEntityPair[]) => {
    const subEntityId = subEntityList.find((item) => item.tableName === subTableName)?.entityId;
    if (!subEntityId) {
      return;
    }

    const res = await getEntityFields({ entityId: subEntityId });
    res.forEach((item: any) => {
      item.fieldKey = `${subTableName}.${item.fieldName}`;
    });
    setFieldDataList(res);
  };

  /**
   * 从节点数据初始化字段数据
   */
  const initFromNodeData = async (
    nodeData: any,
    mainEntityList: MetadataEntityPair[],
    dataTypeField: string,
    tableNameField: string
  ) => {
    if (!nodeData || !nodeData[dataTypeField] || !nodeData[tableNameField]) {
      return;
    }

    const mainEntityId = mainEntityList.find(
      (item: MetadataEntityPair) => item.tableName === nodeData[tableNameField]
    )?.entityId;

    if (!mainEntityId) {
      return;
    }

    await handleMainTableChange(nodeData[tableNameField], mainEntityList);

    // 如果是子表，还需要处理子表字段
    if (nodeData[dataTypeField] === 'SUB_TABLE' && nodeData.subTableName) {
      const curSubEntityList =
        (await getEntityFieldsWithChildren(mainEntityId)).childEntities?.map((item: any) => ({
          entityId: item.childEntityId,
          tableName: item.childTableName,
          entityName: item.childEntityName
        })) || [];
      await handleSubTableChange(nodeData.subTableName, curSubEntityList);
    }
  };

  /**
   * 直接设置字段数据列表（用于简单的字段编辑场景，如 data-add）
   */
  const setFieldDataListDirectly = async (tableName: string, entityList: MetadataEntityPair[]) => {
    if (!tableName) {
      return;
    }
    const entityId = entityList.find((item) => item.tableName === tableName)?.entityId;
    if (!entityId) {
      return;
    }

    const res = await getEntityFields({ entityId });
    res.forEach((item: any) => {
      item.fieldKey = `${tableName}.${item.fieldName}`;
    });
    setFieldDataList(res);
  };

  /**
   * 处理数据节点变更：从数据节点获取字段
   */
  const handleDataNodeChange = async (dataNodeId: string, getDataNodeSource: (nodeId: string) => string) => {
    const originDataSource = getDataNodeSource(dataNodeId);
    if (!originDataSource) {
      return;
    }

    const entityList = await getEntityListByApp(curAppId);
    const entityId = entityList.find((item: MetadataEntityPair) => item.tableName === originDataSource)?.entityId;
    if (!entityId) {
      return;
    }

    const fieldIds: string[] = [];
    const res = await getEntityFieldsWithChildren(entityId);

    if (res.parentFields) {
      const fields = res.parentFields.map((item: AppEntityField) => {
        fieldIds.push(item.fieldId);
        return {
          key: `${originDataSource}.${item.fieldName}`,
          title: item.displayName,
          fieldType: item.fieldType
        };
      });

      setDataNodeEntityFields({
        key: res.entityId,
        title: res.entityName,
        children: fields
      });
    }

    const newValidationTypes = await getFieldCheckTypeApi(fieldIds);
    newValidationTypes.forEach((item: EntityFieldValidationTypes) => {
      const fieldName =
        [...res.parentFields].find((field: AppEntityField) => field.fieldId == item.fieldId)?.fieldName || '';
      item.fieldKey = `${originDataSource}.${fieldName}`;
    });
    setValidationTypes(newValidationTypes);
  };

  /**
   * 重置所有字段数据
   */
  const resetFields = () => {
    setSubEntityList([]);
    setMainEntityFields([]);
    setSubEntityFields([]);
    setDataNodeEntityFields([]);
    setValidationTypes([]);
    setFieldDataList([]);
  };

  return {
    subEntityList,
    mainEntityFields,
    subEntityFields,
    dataNodeEntityFields,
    validationTypes,
    fieldDataList,
    handleMainTableChange,
    handleSubTableChange,
    handleDataNodeChange,
    setFieldDataListDirectly,
    initFromNodeData,
    resetFields
  };
};
