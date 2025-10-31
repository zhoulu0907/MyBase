import { Cascader, Form } from '@arco-design/web-react';
import { FilterEntityFields, type AppEntity, type AppEntityField } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import styles from '../../index.module.less';
import { useSignals } from '@preact/signals-react/runtime';
import { usePageEditorSignal, useFormEditorSignal, useAppEntityStore } from '@onebase/ui-kit';

const FormItem = Form.Item;

export interface DynamicFieldConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  item: any;
  configs: any;
}

const DynamicFieldConfig: React.FC<DynamicFieldConfigProps> = ({ handlePropsChange, item, configs }) => {
  useSignals();
  const { curComponentSchema, pageComponentSchemas, setPageComponentSchemas } = usePageEditorSignal();
  const { subTableComponents } = useFormEditorSignal;
  const { mainEntity, subEntities } = useAppEntityStore();
  const [entityTree, setEntityTree] = useState<any[]>([]);
  const [isInSubTable, setIsInSubTable] = useState<boolean>(false);

  useEffect(() => {
    if (mainEntity) {
      //   console.log(mainEntity);
      initTreeData();
    }
  }, [mainEntity]);

  useEffect(() => {
    getIsInSubTable();
  }, []);

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
    const mainEntityTree = mainEntity.fields
      .filter((field: AppEntityField) => !FilterEntityFields.includes(field.fieldName))
      .map((field: AppEntityField) => ({
        value: field.fieldId,
        label: field.displayName
      }));

    const subEntityTree = subEntities.entities.map((entity: AppEntity) => ({
      value: entity.entityId,
      label: entity.entityName,
      children: entity.fields
        .filter((field: AppEntityField) => !FilterEntityFields.includes(field.fieldName))
        .map((field: AppEntityField) => ({
          value: field.fieldId,
          label: field.displayName
        }))
    }));

    setEntityTree([
      {
        value: mainEntity.entityId,
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
      const isSubComponent = ele.find((item: any) => item.id === curComponentSchema.config.id);
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

  return (
    <FormItem layout="vertical" labelAlign="left" label="数据字段配置" className={styles.formItem}>
      <Cascader
        value={configs[item.key]}
        placeholder="请选择数据字段"
        showEmptyChildren
        animation={false}
        unmountOnExit={false}
        style={{
          width: '100%'
        }}
        options={isInSubTable ? entityTree.filter((ele) => ele.value !== mainEntity.entityId) : entityTree}
        onChange={(value) => {
          handleDataFieldChange(value);
          handlePropsChange(item.key, value);
        }}
      />
    </FormItem>
  );
};

export default DynamicFieldConfig;
