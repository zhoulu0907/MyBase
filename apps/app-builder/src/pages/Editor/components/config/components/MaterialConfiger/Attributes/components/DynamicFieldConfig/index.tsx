import { Cascader, Form } from '@arco-design/web-react';
import { FilterEntityFields, type AppEntity, type AppEntityField } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import styles from '../../index.module.less';
import { useSignals } from '@preact/signals-react/runtime';
import {
  COMPONENT_FIELD_MAP,
  FORM_COMPONENT_TYPES,
  usePageEditorSignal,
  useFormEditorSignal,
  useAppEntityStore,
  getPopupContainer
} from '@onebase/ui-kit';
import { getDictDetail, getDictDataListByType } from '@onebase/platform-center';

const FormItem = Form.Item;

export interface DynamicFieldConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  handleConfigsChange: (config: any) => void;
  item: any;
  configs: any;
}

const DynamicFieldConfig: React.FC<DynamicFieldConfigProps> = ({
  handlePropsChange,
  handleConfigsChange,
  item,
  configs
}) => {
  useSignals();
  const autoCodeKey = 'autoCodeConfig';
  const autoCodeDisabledKey = 'autoCodeDisabled';
  const selectKey = 'defaultOptions';
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
    // 根据不同组件类型匹配不同的可选择字段
    const fieldType = components.find((ele) => ele.id === configs.id)?.type;
    const cpTypes = COMPONENT_FIELD_MAP[fieldType];
    // debugger fieldType
    const mainEntityTree = mainEntity.fields
      .filter((field: AppEntityField) => !FilterEntityFields.includes(field.fieldName))
      .filter((field: AppEntityField) => !cpTypes || cpTypes.length === 0 || cpTypes.includes(field.fieldType))
      .map((field: AppEntityField) => ({
        value: field.fieldId,
        label: field.displayName
      }));

    const subEntityTree = subEntities.entities.map((entity: AppEntity) => ({
      value: entity.entityId,
      label: entity.entityName,
      children: entity.fields
        .filter((field: AppEntityField) => !FilterEntityFields.includes(field.fieldName))
        .filter((field: AppEntityField) => !cpTypes || cpTypes.length === 0 || cpTypes.includes(field.fieldType))
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

  const handleAutoCode = async (value: (string | string[])[]) => {
    const type = components.find((ele) => ele.id === configs.id)?.type;
    const isMainEntity = value?.includes(mainEntity.entityId);
    const currentMainField = mainEntity.fields?.find((ele: AppEntityField) => value.includes(ele.fieldId));
    const isSubEntity = subEntities.entities?.find((ele) => value?.includes(ele.entityId));
    const currentSubField = isSubEntity?.fields.find((ele: AppEntityField) => value.includes(ele.fieldId));

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
        [item.key]: value,
        // 自动编号
        [autoCodeKey]: type === FORM_COMPONENT_TYPES.AUTO_CODE ? { ...currentMainField.autoNumberConfig } : undefined,
        [autoCodeDisabledKey]:
          type === FORM_COMPONENT_TYPES.AUTO_CODE ? (currentMainField?.autoNumberConfig?.id ? true : false) : undefined,
        //  字段选项列表（单/多选）
        [selectKey]:
          type === FORM_COMPONENT_TYPES.SELECT_ONE || type === FORM_COMPONENT_TYPES.SELECT_MUTIPLE
            ? await getDefaultOptions(currentMainField)
            : undefined
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
        [item.key]: value,
        // 自动编号
        [autoCodeKey]: type === FORM_COMPONENT_TYPES.AUTO_CODE ? { ...currentSubField.autoNumberConfig } : undefined,
        [autoCodeDisabledKey]:
          type === FORM_COMPONENT_TYPES.AUTO_CODE ? (currentSubField?.autoNumberConfig?.id ? true : false) : undefined,
        //  字段选项列表（单/多选）
        [selectKey]:
          type === FORM_COMPONENT_TYPES.SELECT_ONE || type === FORM_COMPONENT_TYPES.SELECT_MUTIPLE
            ? await getDefaultOptions(currentSubField)
            : undefined
      };
      handleConfigsChange(newConfigs);
    } else {
      // 未找到字段对应配置
      handlePropsChange(item.key, value);
    }
  };

  const getDefaultOptions = async (field: any) => {
    let newOptions: any = [];
    if (field.dictTypeId) {
      const res = await getDictDetail(field.dictTypeId);
      const dictDataList = res?.type ? await getDictDataListByType(res.type) : [];
      const dictOptions = dictDataList?.filter((e: any) => e.status === 1); // 只显示启用状态的字典数据
      if (dictOptions.length) {
        newOptions = dictOptions;
      }
    } else if (field.options?.length) {
      newOptions = field.options?.map((e: any) => ({
        chosen: field.defaultValue && e.optionValue === field.defaultValue,
        label: e.optionLabel,
        value: e.optionValue
      }));
    }
    return newOptions;
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
            ? entityTree.filter((ele) => ele.value !== mainEntity.entityId)
            : entityTree.filter((ele) => ele.value === mainEntity.entityId)
        }
        onChange={(value) => {
          handleDataFieldChange(value);
          handleAutoCode(value);
        }}
      />
    </FormItem>
  );
};

export default DynamicFieldConfig;
