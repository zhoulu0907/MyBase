import type { TreeSelectDataType } from '@arco-design/web-react/es/TreeSelect/interface';
import {
  getEntityFieldsWithChildren,
  getEntityListByApp,
  getFieldCheckTypeApi,
  type AppEntityField,
  type ConditionField,
  type EntityFieldValidationTypes
} from '@onebase/app';
import { getHashQueryParam } from '@onebase/common';
import { useEffect, useState } from 'react';

/**
 * 处理 start 节点的实体字段数据
 * 用于 start_entity 和 start_date_field 等节点
 */
export const useStartEntityFields = () => {
  const [entityList, setEntityList] = useState<any[]>([]);
  const [conditionFields, setConditionFields] = useState<TreeSelectDataType[]>([]);
  const [validationTypes, setValidationTypes] = useState<EntityFieldValidationTypes[]>([]);

  useEffect(() => {
    const appId = getHashQueryParam('appId');
    if (appId) {
      handleGetEntityListByApp(appId);
    }
  }, []);

  const handleGetEntityListByApp = async (appId: string) => {
    const res = await getEntityListByApp(appId);
    setEntityList(res);
  };

  /**
   * 根据实体ID获取字段和验证类型
   */
  const loadEntityFields = async (entityId: string, tableName: string) => {
    const res = await getEntityFieldsWithChildren(entityId);

    if (res && res.parentFields) {
      const conditions: ConditionField[] = [];
      const fieldIds: string[] = [];

      res.parentFields.forEach((item: AppEntityField) => {
        fieldIds.push(item.fieldId);
        conditions.push({
          label: item.displayName,
          value: `${res.tableName}.${item.fieldName}`,
          fieldType: item.fieldType
        });
      });

      if (fieldIds?.length) {
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
      }

      const conditionFieldsData: TreeSelectDataType = {
        key: res.tableName,
        title: res.entityName,
        children: conditions.map((item) => ({
          key: item.value,
          title: item.label,
          fieldType: item.fieldType
        }))
      };

      setConditionFields([conditionFieldsData]);

      return { conditions, conditionFieldsData };
    }

    return { conditions: [], conditionFieldsData: null };
  };

  return {
    entityList,
    conditionFields,
    validationTypes,
    loadEntityFields,
    handleGetEntityListByApp
  };
};
