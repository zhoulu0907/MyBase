import React, { useState } from 'react';
import { Button, Input, Select, Dropdown, Menu, Cascader } from '@arco-design/web-react';
import { IconDelete, IconDragDotVertical, IconPlus, IconEdit } from '@arco-design/web-react/icon';
import { ReactSortable } from 'react-sortablejs';
import styles from './index.module.less';
import AutoCodeConfigModal from './AutoCodeConfigModal';
import type { AutoNumberRule, AutoCodeRule, AutoNumberRuleResponce } from './types';
import {
  convertAutoCodeCompoToAutoNumberRule,
  convertAutoNumberRuleToAutoCodeComp,
  findFieldPath
} from './utils/transform';
import {
  AUTO_CODE_NUMBER_MODE,
  AUTO_CODE_RESET_CYCLE,
  AUTO_CODE_RULE_TYPE,
  DATE_FORMAT_DEFAULT,
  DIGIT_DEFAULT
} from './utils/const';

// 选项配置组件
interface OptionConfigProps {
  onVisibleChange?: (visible: boolean) => void;
  onConfirm: (options: object[]) => void;
  initialOptions?: { optionLabel: string; optionValue: string }[] | undefined;
  onCancel?: () => void; // 新增：取消回调
}

const initialThreeOptions = [
  { optionLabel: '选项1', optionValue: '选项1' },
  { optionLabel: '选项2', optionValue: '选项2' },
  { optionLabel: '选项3', optionValue: '选项3' }
];

export const OptionConfig: React.FC<OptionConfigProps> = ({ onVisibleChange, onConfirm, onCancel, initialOptions }) => {
  const [options, setOptions] = useState(
    initialOptions && initialOptions?.length > 0 ? initialOptions : initialThreeOptions
  );
  const [optionType, setOptionType] = useState('custom');

  const addOption = () => {
    const newopt = { optionLabel: `选项${options.length + 1}`, optionValue: `选项${options.length + 1}` };
    setOptions([...options, newopt]);
  };

  const removeOption = (index: number) => {
    if (options.length > 1) {
      const newOptions = options.filter((_, i) => i !== index);
      setOptions(newOptions);
    }
  };

  const updateOption = (index: number, value: string) => {
    const newOptions = [...options];
    newOptions[index] = { optionLabel: value, optionValue: value };
    setOptions(newOptions);
  };

  const handleConfirm = () => {
    onConfirm(options);
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

  return (
    <div className={styles.fieldTypeConfig}>
      <h4>选项配置</h4>
      <Select value={optionType} onChange={setOptionType} style={{ width: '100%', marginBottom: 16 }}>
        <Select.Option value="custom">自定义</Select.Option>
        <Select.Option value="system">引用字典</Select.Option>
      </Select>

      <div>
        {options.map((option, index) => (
          <div key={index} className={styles.optionItem}>
            <Input
              value={option.optionLabel}
              onChange={(value) => updateOption(index, value)}
              placeholder="请输入选项内容"
              className={styles.optionInput}
            />
            {options.length > 1 && index > 1 && (
              <Button
                type="text"
                status="danger"
                icon={<IconDelete />}
                onClick={() => removeOption(index)}
                className={styles.deleteBtn}
              />
            )}
          </div>
        ))}
      </div>

      <Button type="dashed" icon={<IconPlus />} onClick={addOption} className={styles.addOptionBtn}>
        新增选项
      </Button>

      <div className={styles.fieldTypeConfigFooter}>
        <Button type="outline" size="small" onClick={handleCancel}>
          取消
        </Button>
        <Button type="primary" size="small" onClick={handleConfirm}>
          确定
        </Button>
      </div>
    </div>
  );
};

// 自动编号规则配置组件
interface AutoCodeConfigProps {
  onVisibleChange?: (visible: boolean) => void;
  onConfirm: (config: AutoNumberRule) => void;
  initialConfig?: AutoNumberRule;
  onCancel?: () => void; // 新增：取消回调
  fields: { label: string; value: string }[];
}

const dataOptions = [
  { label: '年月日', value: '年月日' },
  { label: '年月', value: '年月' },
  { label: '年', value: '年' },
  { label: '年月日时分', value: '年月日时分' },
  { label: '年月日时分秒', value: '年月日时分秒' },
  { label: '自定义', value: '自定义' }
];

export const AutoCodeConfig: React.FC<AutoCodeConfigProps> = ({
  onVisibleChange,
  onConfirm,
  onCancel,
  initialConfig,
  fields
}) => {
  // 初始化规则：如果传入的是 AutoNumberRule 格式，则转换为数组格式
  const getInitialRules = (): AutoCodeRule[] => {
    if (initialConfig) {
      // 如果传入的是 AutoNumberRule 格式，转换为数组格式
      return convertAutoNumberRuleToAutoCodeComp(initialConfig, fields);
    }

    // 默认规则
    return [
      {
        id: 'rule-1',
        itemType: AUTO_CODE_RULE_TYPE.SEQUENCE,
        config: {
          numberMode: AUTO_CODE_NUMBER_MODE.FIXED_DIGITS,
          digitWidth: DIGIT_DEFAULT,
          continueIncrement: true,
          startValue: 1,
          nextRecordStartValue: false,
          resetCycle: AUTO_CODE_RESET_CYCLE.NONE
        }
      },
      {
        id: 'rule-2',
        itemType: AUTO_CODE_RULE_TYPE.DATE,
        config: { dateFormat: DATE_FORMAT_DEFAULT }
      },
      {
        id: 'rule-3',
        itemType: AUTO_CODE_RULE_TYPE.TEXT,
        config: { fixedText: '' }
      },
      {
        id: 'rule-4',
        itemType: AUTO_CODE_RULE_TYPE.FIELD_REF,
        config: { fieldName: '', fieldPath: [] }
      }
    ];
  };

  const [rules, setRules] = useState<AutoCodeRule[]>(getInitialRules());

  const autoCodeConfig = rules[0].config;

  const getDisplayText = (config: AutoCodeRule['config']) => {
    const numberingMethodText = config.numberMode === AUTO_CODE_NUMBER_MODE.NATURAL ? '自然数编号' : '指定位数编号';
    const digitsText = config.digitWidth ? `${config.digitWidth}位数` : '';
    const resetText = config.resetCycle === AUTO_CODE_RESET_CYCLE.NONE ? '不自动重置' : '自动重置';
    return `${numberingMethodText},${digitsText},${resetText}`;
  };

  const [autoCodeModalVisible, setAutoCodeModalVisible] = useState(false);
  const [editingRuleId, setEditingRuleId] = useState<string>('');
  const [displayText, setDisplayText] = useState(getDisplayText(autoCodeConfig));

  const addRule = (type: AutoCodeRule['itemType']) => {
    const newRule: AutoCodeRule = {
      id: 'rule-' + Date.now().toString(),
      itemType: type,
      config: {}
    };
    setRules([...rules, newRule]);
  };

  const editRule = (id: string) => {
    setEditingRuleId(id);
    setAutoCodeModalVisible(true);
  };

  const handleAutoCodeConfigConfirm = (config: AutoNumberRule) => {
    updateRule(editingRuleId, { config: config as unknown as Record<string, unknown> });
    setDisplayText(getDisplayText(config as unknown as AutoCodeRule['config']));
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
              {dataOptions.map((option) => (
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

      {/* 自动编号配置弹窗 */}
      <AutoCodeConfigModal
        visible={autoCodeModalVisible}
        onVisibleChange={setAutoCodeModalVisible}
        onConfirm={handleAutoCodeConfigConfirm}
        initialConfig={
          rules.find((rule) => rule.itemType === AUTO_CODE_RULE_TYPE.SEQUENCE)
            ?.config as unknown as AutoNumberRuleResponce
        }
      />
    </>
  );
};

// 单选列表配置组件
export const PicklistConfig: React.FC<{
  visible?: boolean;
  onVisibleChange?: (visible: boolean) => void;
  onConfirm: (options: string[]) => void;
  initialOptions?: { optionLabel: string; optionValue: string }[];
  onCancel?: () => void;
}> = (props) => {
  return <OptionConfig {...props} />;
};

// 多选列表配置组件
export const MultiPicklistConfig: React.FC<{
  visible?: boolean;
  onVisibleChange?: (visible: boolean) => void;
  onConfirm: (options: string[]) => void;
  initialOptions?: { optionLabel: string; optionValue: string }[];
  onCancel?: () => void;
}> = (props) => {
  return <OptionConfig {...props} />;
};
