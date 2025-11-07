import React, { useState } from 'react';
import { Button, Input, Select, Dropdown, Menu, Cascader } from '@arco-design/web-react';
import { IconDelete, IconDragDotVertical, IconPlus, IconEdit } from '@arco-design/web-react/icon';
import { ReactSortable } from 'react-sortablejs';
import AutoCodeNumberSettingsModal from './AutoCodeNumberSettingsModal';
import type { AutoNumberRule, AutoCodeRule, AutoNumberRuleResponce } from '../types';
import {
  convertAutoCodeCompoToAutoNumberRule,
  convertAutoNumberRuleToAutoCodeComp,
  findFieldPath
} from '../utils/transform';
import {
  AUTO_CODE_NUMBER_MODE,
  AUTO_CODE_RESET_CYCLE,
  AUTO_CODE_RULE_TYPE,
  DATE_FORMAT_DEFAULT,
  AUTO_CODE_NUMBER_DEFAULT_CONFIG
} from '../utils/const';
import styles from '../index.module.less';

interface AutoCodeRuleConfigProps {
  onVisibleChange?: (visible: boolean) => void;
  onConfirm: (config: AutoNumberRule) => void;
  initialConfig?: AutoNumberRule;
  onCancel?: () => void;
  fields: { label: string; value: string }[];
}

const DATE_FORMAT_OPTIONS = [
  { label: '年月日', value: '年月日' },
  { label: '年月', value: '年月' },
  { label: '年', value: '年' },
  { label: '年月日时分', value: '年月日时分' },
  { label: '年月日时分秒', value: '年月日时分秒' },
  { label: '自定义', value: '自定义' }
];

export const AutoCodeRuleConfig: React.FC<AutoCodeRuleConfigProps> = ({
  onVisibleChange,
  onConfirm,
  onCancel,
  initialConfig,
  fields
}) => {
  // 默认规则
  const getInitialRules = (): AutoCodeRule[] => {
    if (initialConfig) {
      return convertAutoNumberRuleToAutoCodeComp(initialConfig, fields);
    }

    return [
      {
        id: 'rule-1',
        itemType: AUTO_CODE_RULE_TYPE.SEQUENCE,
        config: {
          ...AUTO_CODE_NUMBER_DEFAULT_CONFIG
        }
      }
    ];
  };

  const [rules, setRules] = useState<AutoCodeRule[]>(getInitialRules());
  const [autoCodeModalVisible, setAutoCodeModalVisible] = useState(false);
  const [editingRuleId, setEditingRuleId] = useState<string>('');

  const autoCodeConfig =
    rules.find((rule) => rule.itemType === AUTO_CODE_RULE_TYPE.SEQUENCE)?.config || rules[0].config;

  const getDisplayText = (config: AutoCodeRule['config']) => {
    const numberingMethodText = config.numberMode === AUTO_CODE_NUMBER_MODE.NATURAL ? '自然数编号' : '指定位数编号';
    const digitsText = config.numberMode === AUTO_CODE_NUMBER_MODE.NATURAL ? '' : `${config.digitWidth}位数`;
    const resetText = config.resetCycle === AUTO_CODE_RESET_CYCLE.NONE ? '不自动重置' : '自动重置';
    return `${numberingMethodText},${digitsText}${resetText}`;
  };

  const [displayText, setDisplayText] = useState(getDisplayText(autoCodeConfig));

  const addRule = (type: AutoCodeRule['itemType']) => {
    const newRule: AutoCodeRule = {
      id: 'rule-' + Date.now().toString(),
      itemType: type,
      config: type === AUTO_CODE_RULE_TYPE.DATE ? { dateFormat: DATE_FORMAT_DEFAULT } : {}
    };
    setRules([...rules, newRule]);
  };

  const editRule = (id: string) => {
    setEditingRuleId(id);
    setAutoCodeModalVisible(true);
  };

  const handleAutoCodeNumberSettingsConfirm = (config: AutoNumberRule) => {
    const ruleConfig: AutoCodeRule['config'] = {
      ...config,
      startValue: config.initialValue
    };

    updateRule(editingRuleId, { config: ruleConfig });
    setDisplayText(getDisplayText(ruleConfig));
    setEditingRuleId('');
  };

  const removeRule = (id: string) => {
    // 至少保留一条规则
    if (rules.length === 1) {
      return;
    }
    setRules(rules.filter((rule) => rule.id !== id));
  };

  const updateRule = (id: string, updates: Partial<AutoCodeRule>) => {
    setRules(rules.map((rule) => (rule.id === id ? { ...rule, ...updates } : rule)));
  };

  const handleConfirm = () => {
    // 将数组格式转换为 AutoNumberRule 对象格式
    const autoNumberRule = convertAutoCodeCompoToAutoNumberRule(rules);
    onConfirm(autoNumberRule);
    if (onVisibleChange) {
      onVisibleChange(false);
    }
  };

  const handleCancel = () => {
    if (onCancel) {
      onCancel();
    } else if (onVisibleChange) {
      onVisibleChange(false);
    }
  };

  const renderRuleConfig = (rule: AutoCodeRule) => {
    switch (rule.itemType) {
      case AUTO_CODE_RULE_TYPE.SEQUENCE: {
        return (
          <div className={styles.ruleContent}>
            <IconDragDotVertical className={styles.dragHandle} />
            <span className={styles.ruleLabel}>自动编号:</span>
            <Input
              value={displayText}
              readOnly
              className={styles.ruleInput}
              suffix={<IconEdit onClick={() => editRule(rule.id || '')} className={styles.editBtn} />}
            />
            <Button
              type="text"
              status="danger"
              icon={<IconDelete />}
              onClick={() => removeRule(rule.id!)}
              disabled={rules.length === 1}
              className={styles.ruleActionBtn}
            />
          </div>
        );
      }

      case AUTO_CODE_RULE_TYPE.DATE:
        return (
          <div className={styles.ruleContent}>
            <IconDragDotVertical className={styles.dragHandle} />
            <span className={styles.ruleLabel}>创建时间:</span>
            <Select
              value={(rule.config.dateFormat as string) || DATE_FORMAT_DEFAULT}
              onChange={(value) => updateRule(rule.id!, { config: { ...rule.config, dateFormat: value } })}
              className={styles.ruleInput}
            >
              {DATE_FORMAT_OPTIONS.map((option) => (
                <Select.Option key={option.value} value={option.value}>
                  {option.label}
                </Select.Option>
              ))}
            </Select>
            <Button
              type="text"
              status="danger"
              icon={<IconDelete />}
              onClick={() => removeRule(rule.id!)}
              disabled={rules.length === 1}
              className={styles.ruleActionBtn}
            />
          </div>
        );

      case AUTO_CODE_RULE_TYPE.TEXT:
        return (
          <div className={styles.ruleContent}>
            <IconDragDotVertical className={styles.dragHandle} />
            <span className={styles.ruleLabel}>固定字符:</span>
            <Input
              value={(rule.config.fixedText as string) || ''}
              placeholder="请输入内容"
              onChange={(value) => updateRule(rule.id!, { config: { ...rule.config, fixedText: value } })}
              className={styles.ruleInput}
            />
            <Button
              type="text"
              status="danger"
              icon={<IconDelete />}
              onClick={() => removeRule(rule.id!)}
              disabled={rules.length === 1}
              className={styles.ruleActionBtn}
            />
          </div>
        );

      case AUTO_CODE_RULE_TYPE.FIELD_REF:
        return (
          <div className={styles.ruleContent}>
            <IconDragDotVertical className={styles.dragHandle} />
            <span className={styles.ruleLabel}>表单字段:</span>
            <Cascader
              placeholder="请选择字段"
              className={styles.ruleInput}
              options={fields}
              value={findFieldPath(rule.config.fieldName as string, fields)}
              onChange={(value) => {
                updateRule(rule.id!, {
                  config: {
                    ...rule.config,
                    fieldName: value[value.length - 1],
                    fieldPath: value
                  }
                });
              }}
            />
            <Button
              type="text"
              status="danger"
              icon={<IconDelete />}
              onClick={() => removeRule(rule.id!)}
              disabled={rules.length === 1}
              className={styles.ruleActionBtn}
            />
          </div>
        );

      default:
        return null;
    }
  };

  return (
    <>
      <div className={styles.fieldTypeConfig}>
        <h4>自动编号规则</h4>
        <div className={styles.ruleConfigItems}>
          <ReactSortable
            list={rules as unknown as any[]}
            setList={(newList) => setRules(newList as AutoCodeRule[])}
            animation={200}
          >
            {rules.map((rule) => (
              <div key={rule.id}>{renderRuleConfig(rule)}</div>
            ))}
          </ReactSortable>
        </div>

        <div className={styles.addRuleDropdown}>
          <Dropdown
            trigger="click"
            droplist={
              <Menu>
                <Menu.Item
                  key={AUTO_CODE_RULE_TYPE.DATE}
                  onClick={() => addRule(AUTO_CODE_RULE_TYPE.DATE)}
                  disabled={rules.filter((rule) => rule.itemType === AUTO_CODE_RULE_TYPE.DATE)?.length > 1}
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
            <Button type="dashed" icon={<IconPlus />} className={styles.addRuleBtn}>
              添加规则
            </Button>
          </Dropdown>
        </div>

        <div className={styles.fieldTypeConfigFooter}>
          <Button type="outline" size="small" onClick={handleCancel}>
            取消
          </Button>
          <Button type="primary" size="small" onClick={handleConfirm}>
            确定
          </Button>
        </div>
      </div>

      {/* 自动编号设置弹窗 */}
      <AutoCodeNumberSettingsModal
        visible={autoCodeModalVisible}
        onVisibleChange={setAutoCodeModalVisible}
        onConfirm={handleAutoCodeNumberSettingsConfirm}
        initialConfig={
          rules.find((rule) => rule.itemType === AUTO_CODE_RULE_TYPE.SEQUENCE)
            ?.config as unknown as AutoNumberRuleResponce
        }
      />
    </>
  );
};

