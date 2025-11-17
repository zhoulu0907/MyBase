import AutoCodeNumberSettingsModal from '@/pages/CreateApp/pages/DataFactory/pages/Entity/components/Modals/ConfigFieldModal/components/AutoCodeNumberSettingsModal';
import type {
  AutoNumberRule,
  AutoNumberRuleResponce
} from '@/pages/CreateApp/pages/DataFactory/pages/Entity/components/Modals/ConfigFieldModal/types';
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
import { getPopupContainer, useAppEntityStore } from '@onebase/ui-kit';
import React, { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import styles from '../../index.module.less';

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
  const [rules, setRules] = useState<any[]>([]);
  const [customDateFormatStatusMap, setCustomDateFormatStatusMap] = useState<Record<string, 'error' | undefined>>({});

  const getDisplayText = (config: any) => {
    if (!config) {
      return '';
    }
    const numberingMethodText = config.numberMode === AUTO_CODE_NUMBER_MODE.NATURAL ? '自然数编号' : '指定位数编号';
    const digitsText = config.digitWidth ? `${config.digitWidth}位数` : '';
    const resetText = config.resetCycle === AUTO_CODE_RESET_CYCLE.NONE ? '不自动重置' : '自动重置';
    return `${numberingMethodText},${digitsText},${resetText}`;
  };
  const [displayText, setDisplayText] = useState(getDisplayText(configs[autoCodeKey]));

  useEffect(() => {
    const keys = Object.keys(configs[autoCodeKey] || {});
    if (keys?.length) {
      const newRules = configs[autoCodeKey].rules || [];
      const newConfig = { ...configs[autoCodeKey], itemType: AUTO_CODE_RULE_TYPE.SEQUENCE };
      setDisplayText(getDisplayText(newConfig));
      setRules([newConfig, ...newRules]);
    } else {
      // 默认值
      const newConfig = {
        numberMode: AUTO_CODE_NUMBER_MODE.FIXED_DIGITS,
        digitWidth: DIGIT_DEFAULT,
        continueIncrement: true,
        startValue: 1,
        nextRecordStartValue: false,
        resetCycle: AUTO_CODE_RESET_CYCLE.NONE,
        itemType: AUTO_CODE_RULE_TYPE.SEQUENCE
      };
      setDisplayText(getDisplayText(newConfig));
      setRules([newConfig]);
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
  // 添加规则
  const addRule = (type: any) => {
    const newRule = {
      itemType: type,
      format: ''
    };
    setRules([...rules, newRule]);
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
    const newConfig = rules.filter((ele: any) => ele.itemType === AUTO_CODE_RULE_TYPE.SEQUENCE)?.[0] || {
      ...configs[autoCodeKey]
    };
    const newRules = rules
      .filter((_ele: any, i: number) => i !== index)
      .filter((ele: any) => ele.itemType !== AUTO_CODE_RULE_TYPE.SEQUENCE);

    handlePropsChange(autoCodeKey, { ...newConfig, rules: newRules });
  };
  const updateRule = (index: number, value: any) => {
    const newConfig = rules.filter((ele: any) => ele.itemType === AUTO_CODE_RULE_TYPE.SEQUENCE)?.[0] || {
      ...configs[autoCodeKey]
    };
    const newRules = rules
      .map((ele: any, i: number) => {
        // 先赋值 后过滤
        if (i === index) {
          return { ...ele, format: value };
        }
        return ele;
      })
      .filter((ele: any) => ele.itemType !== AUTO_CODE_RULE_TYPE.SEQUENCE);

    handlePropsChange(autoCodeKey, { ...newConfig, rules: newRules });
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
                onChange={(value) => updateRule(index, value)}
                options={dataOptions}
                getPopupContainer={getPopupContainer}
              ></Select>
              {rule.format === '自定义' && (
                <Input
                  value={(rule.fixedText as string) || ''}
                  placeholder="例如：yyyyMMddHHmmss"
                  onChange={(value) => {
                    const ruleId = rule.id!;
                    if (!/^[dhmstyHM]+$/.test(value as string)) {
                      setCustomDateFormatStatusMap((prev) => ({ ...prev, [ruleId]: 'error' }));
                    } else {
                      setCustomDateFormatStatusMap((prev) => ({ ...prev, [ruleId]: undefined }));
                    }
                    updateRule(ruleId, { config: { ...rule.config, fixedText: value } });
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
              value={rule.format}
              placeholder="请输入内容"
              onChange={(value) => updateRule(index, value)}
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
              onChange={(value) => updateRule(index, value?.[1] || '')}
              getPopupContainer={getPopupContainer}
            />
          </>
        );
    }
  };

  const handleAutoCodeConfigConfirm = (config: AutoNumberRule) => {
    setDisplayText(getDisplayText(config));
    const newConfig = { ...config };
    const newRules = rules.filter((ele: any) => ele.itemType !== AUTO_CODE_RULE_TYPE.SEQUENCE);
    handlePropsChange(autoCodeKey, { ...newConfig, rules: newRules });
  };

  return (
    <>
      <Form.Item layout="vertical" label={'编号规则配置'} className={styles.formItem}>
        <ReactSortable
          list={rules}
          setList={() => { }}
          sort={!configs[autoCodeDisabledKey]}
          handle=".autocode-item-handle"
          forceFallback={true}
          animation={150}
          onAdd={(e) => {
            console.log('onAdd: ', e);
          }}
          group={{
            name: 'autocode-col-item'
          }}
          onSort={(e) => {
            const newList = [...rules];
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
              // 更新属性
              const newConfig = movedList.filter((ele: any) => ele.itemType === AUTO_CODE_RULE_TYPE.SEQUENCE)?.[0] || {
                ...configs[autoCodeKey]
              };
              const newRules = movedList.filter((ele: any) => ele.itemType !== AUTO_CODE_RULE_TYPE.SEQUENCE);
              handlePropsChange(autoCodeKey, { ...newConfig, rules: newRules });
            }
          }}
        >
          {rules.map((rule: any, index: number) => (
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
                    disabled={rules.length <= 1 || rule.itemType === AUTO_CODE_RULE_TYPE.SEQUENCE}
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
                  disabled={rules.filter((rule) => rule.itemType === AUTO_CODE_RULE_TYPE.DATE)?.length >= 1}
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
          rules.find((rule) => rule.itemType === AUTO_CODE_RULE_TYPE.SEQUENCE) as unknown as AutoNumberRuleResponce
        }
      />
    </>
  );
};

export default DynamicAutoCodeConfig;
