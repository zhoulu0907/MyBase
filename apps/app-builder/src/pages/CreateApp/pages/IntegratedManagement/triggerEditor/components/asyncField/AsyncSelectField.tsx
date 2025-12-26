import { Form, Select } from '@arco-design/web-react';
import {
  ENTITY_TYPE,
  getEntityListWithFields,
  type AppEntities,
  type AppEntity,
  type ChildEntity,
  type EntityFieldValidationTypes
} from '@onebase/app';
import { type DictData } from '@onebase/platform-center';
import { getFieldOptionsConfig } from '@onebase/ui-kit';
import React, { useEffect, useState } from 'react';

/**
 * 异步加载选项的 Select 组件
 */
interface AsyncSelectFieldProps {
  fieldName: string;
  fieldKey?: string;
  entityFieldValidationTypes?: EntityFieldValidationTypes[];
  curDataField?: string[];
}

const AsyncSelectField: React.FC<AsyncSelectFieldProps> = ({
  fieldName,
  fieldKey,
  entityFieldValidationTypes,
  curDataField
}) => {
  const [options, setOptions] = useState<DictData[]>([]);
  const [loading, setLoading] = useState<boolean>(false);

  const loadOptions = async () => {
    let dataField: string[] = curDataField ? curDataField : [];
    if (!fieldKey && curDataField?.length === 0) {
      return;
    }

    if (fieldKey) {
      // 解析 fieldKey，格式可能是 tableName.fieldName 或 tableName.subTableName.fieldName
      // 如果是 nodeId.tableName.fieldName，需要从 entityFieldValidationTypes 中查找匹配的 fieldKey

      // 先尝试从 entityFieldValidationTypes 中查找匹配的 fieldKey
      const matched = entityFieldValidationTypes?.find((cc) => {
        return fieldKey === cc.fieldKey || fieldKey.endsWith('.' + cc.fieldKey);
      });

      console.log('matched: ', matched);
      console.log('entityFieldValidationTypes: ', entityFieldValidationTypes);

      // 使用匹配到的 fieldKey 或原始 fieldKey
      const actualFieldKey = matched?.fieldKey || fieldKey;
      const parts = actualFieldKey.split('.');

      if (parts.length === 2) {
        // 主表字段: tableName.fieldName
        dataField = parts;
      } else if (parts.length >= 3) {
        // 处理可能包含 nodeId 的情况（nodeId 通常长度 > 20）
        const startIndex = parts[0].length > 20 ? 1 : 0;

        if (parts.length - startIndex === 2) {
          // 去掉 nodeId 后是主表字段: tableName.fieldName
          dataField = [parts[startIndex], parts[startIndex + 1]];
        } else {
          // 子表字段: tableName.subTableName.fieldName
          // 第一个是主表名，第二个是子表名，第三个及以后是字段名
          const subTableName = parts[startIndex + 1];
          const subFieldName = parts.slice(startIndex + 2).join('.');
          dataField = [subTableName, subFieldName];
        }
      }
    }

    if (dataField.length !== 2) {
      return;
    }

    const entityListWithFields = await getEntityListWithFields({ tableNames: [dataField[0]] });

    const [entityWithChildren] = entityListWithFields;

    let mainEntity: AppEntity = {
      entityId: '',
      entityUuid: '',
      tableName: '',
      entityName: '',
      entityType: ENTITY_TYPE.MAIN,
      fields: []
    };
    let subEntities: AppEntities = { entities: [] };

    // 主表数据
    if (entityWithChildren) {
      mainEntity = {
        entityId: entityWithChildren.entityId,
        entityUuid: entityWithChildren.entityUuid,
        tableName: entityWithChildren.tableName,
        entityName: entityWithChildren.entityName,
        entityType: ENTITY_TYPE.MAIN,
        fields: entityWithChildren.fields
      };

      if (entityWithChildren.childEntities && entityWithChildren.childEntities.length > 0) {
        // 返回新Promise对象，当所有输入Promise成功时返回结果数组（顺序与输入一致）
        const allChildFields = await Promise.all(
          entityWithChildren.childEntities.map(async (entity: ChildEntity) => {
            return entity.childFields;
          })
        );
        subEntities.entities = entityWithChildren.childEntities.map((entity: ChildEntity, index: number) => ({
          entityId: entity.childEntityId,
          entityUuid: entity.childEntityUuid,
          tableName: entity.childTableName,
          entityName: entity.childEntityName,
          entityType: ENTITY_TYPE.SUB,
          fields: allChildFields[index]
        }));
      }
    }

    setLoading(true);
    try {
      const loadedOptions = await getFieldOptionsConfig(dataField, mainEntity, subEntities);
      console.log('loadedOptions: ', loadedOptions);
      setOptions(loadedOptions);
    } catch (error) {
      console.error('Failed to load field options:', error);
      setOptions([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadOptions();
  }, [fieldKey, entityFieldValidationTypes, curDataField]);

  return (
    <Form.Item field={fieldName}>
      <Select placeholder="请选择静态值" options={options} loading={loading} />
    </Form.Item>
  );
};

export default AsyncSelectField;
