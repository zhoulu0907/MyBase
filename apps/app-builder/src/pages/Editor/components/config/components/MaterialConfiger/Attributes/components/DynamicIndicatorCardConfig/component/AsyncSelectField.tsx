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
  value: any;
  entityFieldValidationTypes?: EntityFieldValidationTypes[];
  onChange: (value: any) => void;
  curDataField?: string[];
}

const AsyncSelectField: React.FC<AsyncSelectFieldProps> = ({
  fieldName,
  fieldKey,
  entityFieldValidationTypes,
  value,
  onChange,
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
      dataField = fieldKey.split('.');
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
    <Select
      value={value}
      placeholder="请选择静态值"
      options={options.map((item) => ({ value: item.id, label: item.label }))}
      loading={loading}
      onChange={(value) => onChange(value)}
    />
  );
};

export default AsyncSelectField;
