import { Cascader, Form } from '@arco-design/web-react';
import { FilterEntityFields, type AppEntity, type AppEntityField } from '@onebase/app';
import { getDictDataListByType, getDictDetail } from '@onebase/platform-center';
import {
  COLOR_MODE_TYPES,
  COMPONENT_FIELD_MAP,
  CONFIG_TYPES,
  DEFAULT_OPTIONS_TYPE,
  FORM_COMPONENT_TYPES,
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

const DynamicFieldConfig: React.FC<DynamicFieldConfigProps> = ({
  handlePropsChange,
  handleConfigsChange,
  item,
  configs
}) => {
  useSignals();
  const autoCodeKey = 'autoCodeConfig';
  const autoCodeDisabledKey = 'autoCodeDisabled';
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
    // 根据不同组件类型匹配不同的可选择字段, 从pageComponentSchemas中获取全部字段，原先components中没有布局内的子元素
    // const fieldType = components.find((ele: any) => ele.id === configs.id)?.type;
    const fieldType = pageComponentSchemas[configs.id]?.type;

    const cpTypes = COMPONENT_FIELD_MAP[fieldType];
    const mainEntityTree = mainEntity.fields
      .filter((field: AppEntityField) => !FilterEntityFields.includes(field.fieldName))
      .filter((field: AppEntityField) => !cpTypes || cpTypes.length === 0 || cpTypes.includes(field.fieldType))
      .map((field: AppEntityField) => ({
        // value: field.fieldId,
        value: field.fieldName,
        label: field.displayName
      }));

    const subEntityTree = subEntities.entities.map((entity: AppEntity) => ({
      // value: entity.entityId,
      value: entity.entityName,
      label: entity.entityName,
      children: entity.fields
        .filter((field: AppEntityField) => !FilterEntityFields.includes(field.fieldName))
        .filter((field: AppEntityField) => !cpTypes || cpTypes.length === 0 || cpTypes.includes(field.fieldType))
        .map((field: AppEntityField) => ({
          //   value: field.fieldId,
          value: field.fieldName,
          label: field.displayName
        }))
    }));

    setEntityTree([
      {
        // value: mainEntity.entityId,
        value: mainEntity.entityName,
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

  const handleDefaultOptions = async (value: (string | string[])[]) => {
    const type = components.find((ele: any) => ele.id === configs.id)?.type;
    // const isMainEntity = value?.includes(mainEntity.entityId);
    // const currentMainField = mainEntity.fields?.find((ele: AppEntityField) => value.includes(ele.fieldId));
    // const isSubEntity = subEntities.entities?.find((ele:any) => value?.includes(ele.entityId));
    // const currentSubField = isSubEntity?.fields.find((ele: AppEntityField) => value.includes(ele.fieldId));

    const isMainEntity = value?.includes(mainEntity.entityName);
    const currentMainField = mainEntity.fields?.find((ele: AppEntityField) => value.includes(ele.fieldName));
    const isSubEntity = subEntities.entities?.find((ele: any) => value?.includes(ele.entityName));
    const currentSubField = isSubEntity?.fields.find((ele: AppEntityField) => value.includes(ele.fieldName));

    if (isMainEntity && currentMainField) {
      const newConfig = await getDefaultOptions(currentMainField, type);
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
        defaultOptionsConfig: { ...configs.defaultOptionsConfig, ...newConfig }
      };
      handleConfigsChange(newConfigs);
    } else if (isSubEntity && currentSubField) {
      const newConfig = await getDefaultOptions(currentSubField, type);
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
        defaultOptionsConfig: { ...configs.defaultOptionsConfig, ...newConfig }
      };
      handleConfigsChange(newConfigs);
    } else {
      // 未找到字段对应配置
      handlePropsChange(item.key, value);
    }
  };

  const getDefaultOptions = async (field: any, type: string) => {
    if (
      type !== FORM_COMPONENT_TYPES.SELECT_ONE &&
      type !== FORM_COMPONENT_TYPES.SELECT_MUTIPLE &&
      type !== FORM_COMPONENT_TYPES.RADIO &&
      type !== FORM_COMPONENT_TYPES.CHECKBOX
    ) {
      return {};
    }
    let newConfig: any = {
      defaultOptions: []
    };
    if (field.dictTypeId) {
      const res = await getDictDetail(field.dictTypeId);
      const dictDataList = res?.type ? await getDictDataListByType(res.type) : [];
      const dictOptions = dictDataList?.filter((e: any) => e.status === 1); // 只显示启用状态的字典数据
      if (dictOptions.length) {
        newConfig.type = DEFAULT_OPTIONS_TYPE.DICT;
        newConfig.disabled = true;
        newConfig.dictTypeId = field.dictTypeId;
        newConfig.colorMode = true;
        newConfig.colorModeType = COLOR_MODE_TYPES.POINT;
        newConfig.defaultOptions = dictOptions;
      }
    } else if (field.options?.length) {
      newConfig.type = DEFAULT_OPTIONS_TYPE.CUSTOM;
      newConfig.disabled = true;
      newConfig.defaultOptions = field.options?.map((e: any) => ({
        label: e.optionLabel,
        value: e.optionValue
      }));
    }
    return newConfig;
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
            ? // ? entityTree.filter((ele) => ele.value !== mainEntity.entityId)
              // : entityTree.filter((ele) => ele.value === mainEntity.entityId)
              entityTree.filter((ele) => ele.value !== mainEntity.entityName)
            : entityTree.filter((ele) => ele.value === mainEntity.entityName)
        }
        onChange={(value) => {
          handleDataFieldChange(value);
          handleDefaultOptions(value);
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
