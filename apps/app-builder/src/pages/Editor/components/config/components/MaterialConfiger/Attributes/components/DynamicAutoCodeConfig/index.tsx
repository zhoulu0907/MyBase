import AutoCodeNumberSettingsModal from '@/pages/CreateApp/pages/DataFactory/pages/Entity/components/Modals/ConfigFieldModal/components/AutoCodeNumberSettingsModal';
import type { AutoNumberRuleResponce } from '@/pages/CreateApp/pages/DataFactory/pages/Entity/components/Modals/ConfigFieldModal/types';
import {
  AUTO_CODE_NUMBER_MODE,
  AUTO_CODE_RESET_CYCLE,
  AUTO_CODE_RULE_TYPE,
  DIGIT_DEFAULT
} from '@/pages/CreateApp/pages/DataFactory/pages/Entity/components/Modals/ConfigFieldModal/utils/const';
import { findFieldPath } from '@/pages/CreateApp/pages/DataFactory/pages/Entity/components/Modals/ConfigFieldModal/utils/transform';
import { Button, Cascader, Dropdown, Form, Input, Menu, Select, Tooltip } from '@arco-design/web-react';
import { IconDelete, IconDragDotVertical, IconPen, IconPlus } from '@arco-design/web-react/icon';
import { FilterEntityFields, type AppEntity, type AppEntityField } from '@onebase/app';
import { CONFIG_TYPES, getPopupContainer, useAppEntityStore } from '@onebase/ui-kit';
import React, { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';

export interface DynamicAutoCodeConfigProps {
  handlePropsChange: (key: string, value: any) => void;
  handleConfigsChange: (config: any) => void;
  item: any;
  configs: any;
  id: string;
}

const dataOptions = [
  { label: '年月日', value: '年月日' },
  { label: '年月', value: '年月' },
  { label: '年', value: '年' },
  { label: '年月日时分', value: '年月日时分' },
  { label: '年月日时分秒', value: '年月日时分秒' },
  { label: '自定义', value: '自定义' }
];

const DynamicAutoCodeConfig: React.FC<DynamicAutoCodeConfigProps> = ({
  handlePropsChange,
  handleConfigsChange,
  item,
  configs,
  id
}) => {
  const autoCodeKey = 'autoCodeConfig';
  const autoCodeDisabledKey = 'autoCodeDisabled';
  const { mainEntity, subEntities } = useAppEntityStore();

  const [entityTree, setEntityTree] = useState<any[]>([]);
  const [editRuleVisible, setEditRuleVisible] = useState(false);
  const [customDateFormatStatusMap, setCustomDateFormatStatusMap] = useState<Record<string, 'error' | undefined>>({});

  const getDisplayText = (sequenceConfig: any) => {
    if (!sequenceConfig) {
      return '';
    }
    const numberingMethodText =
      sequenceConfig.numberMode === AUTO_CODE_NUMBER_MODE.NATURAL ? '自然数编号' : '指定位数编号';
    const digitsText =
      sequenceConfig.numberMode === AUTO_CODE_NUMBER_MODE.NATURAL ? '' : `${sequenceConfig.digitWidth}位数`;
    const resetText = sequenceConfig.resetCycle === AUTO_CODE_RESET_CYCLE.NONE ? '不自动重置' : '自动重置';
    return `${numberingMethodText},${digitsText ? digitsText + ',' : ''}${resetText}`;
  };
  const [displayText, setDisplayText] = useState('');

  useEffect(() => {
    if (configs[autoCodeKey]?.rules?.length) {
      const newConfig = configs[autoCodeKey]?.rules.find((ele: any) => ele.itemType === AUTO_CODE_RULE_TYPE.SEQUENCE);
      setDisplayText(getDisplayText(newConfig));
    } else {
      // 默认值
      const sequenceConfig = {
        numberMode: AUTO_CODE_NUMBER_MODE.FIXED_DIGITS,
        digitWidth: DIGIT_DEFAULT,
        continueIncrement: true,
        startValue: 1,
        nextRecordStartValue: false,
        resetCycle: AUTO_CODE_RESET_CYCLE.NONE,
        itemType: AUTO_CODE_RULE_TYPE.SEQUENCE
      };
      setDisplayText(getDisplayText(sequenceConfig));
      handlePropsChange(autoCodeKey, { ...configs[autoCodeKey], rules: [sequenceConfig] });
    }
  }, [configs[autoCodeKey]]);

  useEffect(() => {
    if (mainEntity) {
      initTreeData();
    }
  }, [mainEntity]);

  useEffect(() => {
    getConfigRulesOptions();
  }, []);

  const getConfigRulesOptions = () => {
    const value = configs.dataField;
    const isMainEntity = value?.includes(mainEntity.entityId);
    const currentMainField = mainEntity.fields?.find((ele: any) => value.includes(ele.fieldId));
    const isSubEntity = subEntities.entities?.find((ele: any) => value?.includes(ele.entityId));
    const currentSubField = isSubEntity?.fields.find((ele: any) => value.includes(ele.fieldId));
    if (isMainEntity && currentMainField) {
      // 主表
      if (currentMainField.autoNumberConfig?.id) {
        const newAutoNumberConfig = { ...currentMainField.autoNumberConfig };
        const newConfig = {
          [autoCodeKey]: newAutoNumberConfig,
          [autoCodeDisabledKey]: true
        };
        handleConfigsChange(newConfig);
      }
    } else if (isSubEntity && currentSubField) {
      // 子表
      if (currentSubField.autoNumberConfig?.id) {
        const newAutoNumberConfig = { ...currentSubField.autoNumberConfig };
        const newConfig = {
          [autoCodeKey]: newAutoNumberConfig,
          [autoCodeDisabledKey]: true
        };
        handleConfigsChange(newConfig);
      }
    }
  };

  const initTreeData = async () => {
    const mainEntityTree = mainEntity.fields
      .filter((field: AppEntityField) => !FilterEntityFields.includes(field.fieldName))
      .map((field: AppEntityField) => ({
        value: field.id,
        label: field.displayName
      }));

    const subEntityTree = subEntities.entities.map((entity: AppEntity) => ({
      value: entity.entityId,
      label: entity.entityName,
      children: entity.fields
        .filter((field: AppEntityField) => !FilterEntityFields.includes(field.fieldName))
        .map((field: AppEntityField) => ({
          value: field.id,
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
  // 添加规则
  const addRule = (type: any) => {
    const newRule = {
      itemType: type,
      format: ''
    };
    handlePropsChange(autoCodeKey, { ...configs[autoCodeKey], rules: [...configs[autoCodeKey].rules, newRule] });
  };
  // 自动编号编辑
  const openEditRuleModal = () => {
    if (configs[autoCodeDisabledKey]) {
      return;
    }
    setEditRuleVisible(true);
  };
  // 自动编号 删除
  const removeRule = (index: number) => {
    const newRules = configs[autoCodeKey].rules.filter((_ele: any, i: number) => i !== index);
    handlePropsChange(autoCodeKey, { ...configs[autoCodeKey], rules: newRules });
  };
  const updateRule = (index: number, key: string, value: any) => {
    const newRules = configs[autoCodeKey].rules.map((ele: any, i: number) => {
      // 赋值
      if (i === index) {
        return { ...ele, [key]: value };
      }
      return ele;
    });
    handlePropsChange(autoCodeKey, { ...configs[autoCodeKey], rules: newRules });
  };

  // 渲染每一项规则
  const renderRuleConfig = (rule: any, index: number) => {
    switch (rule.itemType) {
      case AUTO_CODE_RULE_TYPE.SEQUENCE:
        // 自动编号
        return (
          <>
            <span className={styles.autoCodeItemLable}>自动编号:</span>
            <Input
              value={displayText}
              readOnly
              className={styles.ruleInput}
              disabled={configs[autoCodeDisabledKey]}
              suffix={<IconPen className={styles.editBtn} onClick={openEditRuleModal} />}
            />
          </>
        );

      case AUTO_CODE_RULE_TYPE.DATE:
        // 创建时间
        return (
          <>
            <span className={styles.autoCodeItemLable}>创建时间:</span>
            <div className={styles.ruleInput}>
              <Select
                disabled={configs[autoCodeDisabledKey]}
                value={rule.format}
                style={{ marginBottom: '4px' }}
                onChange={(value) => updateRule(index, 'format', value)}
                options={dataOptions}
                getPopupContainer={getPopupContainer}
              ></Select>
              {rule.format === '自定义' && (
                <Input
                  disabled={configs[autoCodeDisabledKey]}
                  value={(rule.textValue as string) || ''}
                  placeholder="例如：yyyyMMddHHmmss"
                  onChange={(value) => {
                    const ruleId = rule.id!;
                    if (!/^[dhmstyHM]+$/.test(value as string)) {
                      setCustomDateFormatStatusMap((prev) => ({ ...prev, [ruleId]: 'error' }));
                    } else {
                      setCustomDateFormatStatusMap((prev) => ({ ...prev, [ruleId]: undefined }));
                    }
                    updateRule(ruleId, 'textValue', value);
                  }}
                  className={styles.ruleInput}
                  status={customDateFormatStatusMap[rule.id!]}
                />
              )}
              {customDateFormatStatusMap[rule.id!] === 'error' && (
                <span className={styles.ruleInputError}>{`例如：yyyyMMddHHmmss`}</span>
              )}
            </div>
          </>
        );

      case AUTO_CODE_RULE_TYPE.TEXT:
        // 固定字符
        return (
          <>
            <span className={styles.autoCodeItemLable}>固定字符:</span>
            <Input
              disabled={configs[autoCodeDisabledKey]}
              value={rule.textValue}
              placeholder="请输入内容"
              onChange={(value) => updateRule(index, 'textValue', value)}
              className={styles.ruleInput}
            />
          </>
        );

      case AUTO_CODE_RULE_TYPE.FIELD_REF:
        // 表单字段
        return (
          <>
            <span className={styles.autoCodeItemLable}>表单字段:</span>
            <Cascader
              disabled={configs[autoCodeDisabledKey]}
              placeholder="请选择字段"
              className={styles.ruleInput}
              options={entityTree}
              animation
              value={findFieldPath(rule.format, entityTree)}
              onChange={(value) => updateRule(index, 'format', value?.[1] || '')}
              getPopupContainer={getPopupContainer}
            />
          </>
        );
      default:
        return null;
    }
  };

  const handleAutoCodeConfigConfirm = (config: AutoNumberRuleResponce) => {
    setDisplayText(getDisplayText(config));
    const newRules = configs[autoCodeKey].rules.map((ele: any) => {
      if (ele.itemType === AUTO_CODE_RULE_TYPE.SEQUENCE) {
        return { ...ele, ...config };
      }
      return { ...ele };
    });
    handlePropsChange(autoCodeKey, { ...configs[autoCodeKey], rules: newRules });
  };

  return (
    <>
      <Form.Item layout="vertical" label={'编号规则配置'} className={styles.formItem}>
        <ReactSortable
          list={configs[autoCodeKey]?.rules}
          setList={() => {}}
          sort={!configs[autoCodeDisabledKey]}
          handle=".autocode-item-handle"
          forceFallback={true}
          animation={150}
          onAdd={(e) => {
            // console.log('onAdd: ', e);
          }}
          group={{
            name: 'autocode-col-item'
          }}
          onSort={(e) => {
            const newList = [...configs[autoCodeKey]?.rules];
            // 根据 onSort 事件中的 oldIndex 和 newIndex 交换数组元素
            const { oldIndex, newIndex } = e;
            console.log(oldIndex, newIndex);
            if (oldIndex !== undefined && newIndex !== undefined && oldIndex !== newIndex) {
              // 复制一份新数组
              const movedList = [...newList];
              // 取出被移动的元素
              const [movedItem] = movedList.splice(oldIndex, 1);
              // 插入到新位置
              movedList.splice(newIndex, 0, movedItem);
              handlePropsChange(autoCodeKey, { ...configs[autoCodeKey], rules: movedList });
            }
          }}
        >
          {configs[autoCodeKey]?.rules.map((rule: any, index: number) => (
            <Tooltip key={index} content="如需修改请前往数据建模" disabled={!configs[autoCodeDisabledKey]}>
              <div className={styles.autoCodeItem}>
                {!configs[autoCodeDisabledKey] && (
                  <IconDragDotVertical
                    // 支持拖拽的图标，别误删了：）
                    className="autocode-item-handle"
                    style={{
                      cursor: 'move',
                      color: '#555'
                    }}
                  />
                )}
                {renderRuleConfig(rule, index)}
                {!configs[autoCodeDisabledKey] && (
                  <Button
                    type="text"
                    status="danger"
                    icon={<IconDelete />}
                    onClick={() => removeRule(index)}
                    disabled={configs[autoCodeKey]?.rules.length <= 1 || rule.itemType === AUTO_CODE_RULE_TYPE.SEQUENCE}
                    className={styles.ruleActionBtn}
                  />
                )}
              </div>
            </Tooltip>
          ))}
        </ReactSortable>
        {!configs[autoCodeDisabledKey] && (
          <Dropdown
            trigger="click"
            getPopupContainer={getPopupContainer}
            droplist={
              <Menu>
                <Menu.Item
                  key={AUTO_CODE_RULE_TYPE.DATE}
                  onClick={() => addRule(AUTO_CODE_RULE_TYPE.DATE)}
                  disabled={
                    configs[autoCodeKey]?.rules.filter((rule: any) => rule.itemType === AUTO_CODE_RULE_TYPE.DATE)
                      ?.length >= 1
                  }
                >
                  创建时间
                </Menu.Item>
                <Menu.Item key={AUTO_CODE_RULE_TYPE.TEXT} onClick={() => addRule(AUTO_CODE_RULE_TYPE.TEXT)}>
                  固定字符
                </Menu.Item>
                <Menu.Item key={AUTO_CODE_RULE_TYPE.FIELD_REF} onClick={() => addRule(AUTO_CODE_RULE_TYPE.FIELD_REF)}>
                  表单字段
                </Menu.Item>
              </Menu>
            }
          >
            <Button type="outline" icon={<IconPlus />} className={styles.addRuleBtn}>
              添加规则
            </Button>
          </Dropdown>
        )}
      </Form.Item>
      {/* 自动编号配置弹窗 */}
      <AutoCodeNumberSettingsModal
        visible={editRuleVisible}
        onVisibleChange={setEditRuleVisible}
        onConfirm={handleAutoCodeConfigConfirm}
        initialConfig={
          configs[autoCodeKey]?.rules.find(
            (rule: any) => rule.itemType === AUTO_CODE_RULE_TYPE.SEQUENCE
          ) as unknown as AutoNumberRuleResponce
        }
      />
    </>
  );
};

export default DynamicAutoCodeConfig;

registerConfigRenderer(
  CONFIG_TYPES.AUTO_CODE_RULES,
  ({ id, handlePropsChange, handleConfigsChange, item, configs }) => (
    <DynamicAutoCodeConfig
      id={id}
      handlePropsChange={handlePropsChange}
      handleConfigsChange={handleConfigsChange}
      item={item}
      configs={configs}
    />
  )
);
