import { Cascader, Form } from '@arco-design/web-react';
import { FilterEntityFields, type AppEntity, type AppEntityField } from '@onebase/app';
import {
  COMPONENT_FIELD_MAP,
  CONFIG_TYPES,
  getPopupContainer,
  useAppEntityStore,
  useFormEditorSignal,
  usePageEditorSignal
} from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useEffect, useState } from 'react';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';

const FormItem = Form.Item;

export interface DynamicFieldConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  handleConfigsChange: (config: any) => void;
  item: any;
  configs: any;
}

/**
 * 对实体字段进行排序：系统字段排在后面，非系统字段排在前面，相同类型按 displayName 排序
 */
const sortEntityFields = (a: AppEntityField, b: AppEntityField): number => {
  if (a.isSystemField === b.isSystemField) {
    return a.displayName.localeCompare(b.displayName);
  }
  return a.isSystemField ? 1 : -1;
};

const DynamicFieldConfig: React.FC<DynamicFieldConfigProps> = ({
  handlePropsChange,
  handleConfigsChange,
  item,
  configs
}) => {
  useSignals();
  const { curComponentSchema, components, pageComponentSchemas, setPageComponentSchemas } = usePageEditorSignal();
  const { subTableComponents } = useFormEditorSignal;
  const { mainEntity, subEntities } = useAppEntityStore();
  const [entityTree, setEntityTree] = useState<any[]>([]);
  const [isInSubTable, setIsInSubTable] = useState<boolean>(false);

  useEffect(() => {
    if (mainEntity) {
      initTreeData();
    }
    getIsInSubTable();
  }, [mainEntity, configs.id]);

  // 判断是否是在子表单中
  const getIsInSubTable = () => {
    const keys = Object.keys(subTableComponents.value);
    let flag = false;
    for (let key of keys) {
      if (subTableComponents.value[key]) {
        const isada = subTableComponents.value[key]?.find((ele: any) => ele.id === configs.id);
        if (isada) {
          flag = true;
        }
      }
    }
    setIsInSubTable(flag);
  };

  const initTreeData = async () => {
    // 根据不同组件类型匹配不同的可选择字段, 从pageComponentSchemas中获取全部字段
    const fieldType = pageComponentSchemas[configs.id]?.type;

    const cpTypes = COMPONENT_FIELD_MAP[fieldType];
    const mainEntityTree = mainEntity.fields
      .sort(sortEntityFields)
      .filter((field: AppEntityField) => !FilterEntityFields.includes(field.fieldName))
      .filter((field: AppEntityField) => !cpTypes || cpTypes.length === 0 || cpTypes.includes(field.fieldType))
      .map((field: AppEntityField) => ({
        value: field.fieldName,
        label: field.displayName
      }));

    console.log('mainEntityTree', mainEntityTree);

    const subEntityTree = subEntities.entities.map((entity: AppEntity) => ({
      value: entity.tableName,
      label: entity.entityName,
      children: entity.fields
        .sort(sortEntityFields)
        .filter((field: AppEntityField) => !FilterEntityFields.includes(field.fieldName))
        .filter((field: AppEntityField) => !cpTypes || cpTypes.length === 0 || cpTypes.includes(field.fieldType))
        .map((field: AppEntityField) => ({
          value: field.fieldName,
          label: field.displayName
        }))
    }));

    setEntityTree([
      {
        value: mainEntity.tableName,
        label: mainEntity.entityName,
        children: mainEntityTree
      },
      ...subEntityTree
    ]);
  };

  const handleDataFieldChange = (value: any) => {
    const keys = Object.keys(subTableComponents.value);
    for (let key of keys) {
      const ele = subTableComponents.value[key];
      // 包含当前节点的子表单
      const isSubComponent = ele?.find((item: any) => item.id === curComponentSchema.config.id);
      if (isSubComponent) {
        ele.forEach((item: any) => {
          // 不是本身 并且和当前子表不一致
          if (
            item.id !== curComponentSchema.config.id &&
            value[0] &&
            pageComponentSchemas[item.id].config.dataField?.[0] !== value[0]
          ) {
            // 同一个子表的 置空
            const config = { ...pageComponentSchemas[item.id].config, dataField: [] };
            setPageComponentSchemas(item.id, { ...pageComponentSchemas[item.id], config });
          }
        });
      }
    }
  };

  const handleDefaultConfig = async (value: (string | string[])[]) => {
    const isMainEntity = value?.includes(mainEntity.tableName);
    const currentMainField = mainEntity.fields?.find((ele: AppEntityField) => value.includes(ele.fieldName));
    const isSubEntity = subEntities.entities?.find((ele: any) => value?.includes(ele.tableName));
    const currentSubField = isSubEntity?.fields.find((ele: AppEntityField) => value.includes(ele.fieldName));

    if (isMainEntity && currentMainField) {
      // 主表
      const newConfigs = {
        ...configs,
        defaultValue: currentMainField.defaultValue,
        tooltip: currentMainField.description,
        verify: {
          ...configs.verify,
          required: currentMainField.isRequired,
          noRepeat: currentMainField.isUnique
        },
        constraints: currentMainField.constraints,
        [item.key]: value
      };
      handleConfigsChange(newConfigs);
    } else if (isSubEntity && currentSubField) {
      // 子表
      const newConfigs = {
        ...configs,
        defaultValue: currentSubField.defaultValue,
        tooltip: currentSubField.description,
        verify: {
          ...configs.verify,
          required: currentSubField.isRequired,
          noRepeat: currentSubField.isUnique
        },
        constraints: currentSubField.constraints,
        [item.key]: value
      };
      handleConfigsChange(newConfigs);
    } else {
      // 未找到字段对应配置
      handlePropsChange(item.key, value);
    }
  };

  return (
    <FormItem layout="vertical" labelAlign="left" required label="数据绑定" className={styles.formItem}>
      <Cascader
        value={configs[item.key]}
        placeholder="请选择数据字段"
        showEmptyChildren
        animation={false}
        unmountOnExit={false}
        getPopupContainer={getPopupContainer}
        style={{
          width: '100%'
        }}
        options={
          isInSubTable
            ? entityTree.filter((ele) => ele.value !== mainEntity.tableName)
            : entityTree.filter((ele) => ele.value === mainEntity.tableName)
        }
        onChange={(value) => {
          handleDataFieldChange(value);
          handleDefaultConfig(value);
        }}
      />
    </FormItem>
  );
};

export default DynamicFieldConfig;

registerConfigRenderer(CONFIG_TYPES.FIELD_DATA, ({ handlePropsChange, handleConfigsChange, item, configs }) => (
  <DynamicFieldConfig
    handlePropsChange={handlePropsChange}
    handleConfigsChange={handleConfigsChange}
    item={item}
    configs={configs}
  />
));
