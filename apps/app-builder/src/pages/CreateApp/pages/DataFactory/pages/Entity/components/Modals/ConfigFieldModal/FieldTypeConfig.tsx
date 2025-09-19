import React, { useState } from 'react';
import { Button, Input, Select, Dropdown, Menu, Cascader } from '@arco-design/web-react';
import { IconDelete, IconDragDotVertical, IconPlus, IconEdit } from '@arco-design/web-react/icon';
import { ReactSortable } from 'react-sortablejs';
import styles from './index.module.less';
import AutoCodeConfigModal from './AutoCodeConfigModal';
import type { AutoNumberRule, AutoCodeRule } from './types';
import { convertAutoCodeCompoToAutoNumberRule, convertAutoNumberRuleToAutoCodeComp } from './utils';

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
    <div className={styles['field-type-config']}>
      <h4>选项配置</h4>
      <Select value={optionType} onChange={setOptionType} style={{ width: '100%', marginBottom: 16 }}>
        <Select.Option value="custom">自定义</Select.Option>
        <Select.Option value="system">引用字典</Select.Option>
      </Select>

      <div>
        {options.map((option, index) => (
          <div key={index} className={styles['option-item']}>
            <Input
              value={option.optionLabel}
              onChange={(value) => updateOption(index, value)}
              placeholder="请输入选项内容"
              className={styles['option-input']}
            />
            {options.length > 1 && index > 1 && (
              <Button
                type="text"
                status="danger"
                icon={<IconDelete />}
                onClick={() => removeOption(index)}
                className={styles['delete-btn']}
              />
            )}
          </div>
        ))}
      </div>

      <Button type="dashed" icon={<IconPlus />} onClick={addOption} className={styles['add-option-btn']}>
        新增选项
      </Button>

      <div className={styles['field-type-config-footer']}>
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
    console.log('initialConfig', initialConfig);
    if (initialConfig) {
      // 如果传入的是 AutoNumberRule 格式，转换为数组格式
      return convertAutoNumberRuleToAutoCodeComp(initialConfig);
    }

    // 默认规则
    return [
      {
        id: 'rule-1',
        itemType: 'SEQUENCE',
        config: {
          numberMode: 'FIXED_DIGITS',
          digitWidth: 4,
          continueIncrement: true,
          startValue: 1,
          nextRecordStartValue: false,
          resetCycle: 'NONE'
        }
      },
      {
        id: 'rule-2',
        itemType: 'DATE',
        config: { dateFormat: '年月日' }
      },
      {
        id: 'rule-3',
        itemType: 'TEXT',
        config: { fixedText: '' }
      },
      {
        id: 'rule-4',
        itemType: 'FIELD_REF',
        config: { fieldName: '' }
      }
    ];
  };

  const [rules, setRules] = useState<AutoCodeRule[]>(getInitialRules());

  const autoCodeConfig = rules[0].config;

  const getDisplayText = (config: AutoCodeRule['config']) => {
    const numberingMethodText = config.numberMode === 'NATURAL' ? '自然数编号' : '指定位数编号';
    const digitsText = config.digitWidth ? `${config.digitWidth}位数` : '';
    const resetText = config.resetCycle === 'NONE' ? '不自动重置' : '自动重置';
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
    console.log('config', config);
    updateRule(editingRuleId, { config: config });
    setDisplayText(getDisplayText(config));
    setEditingRuleId('');
  };

  const removeRule = (id: string) => {
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
      case 'SEQUENCE': {
        return (
          <div className={styles['rule-content']}>
            <IconDragDotVertical className={styles['drag-handle']} />
            <span className={styles['rule-label']}>自动编号:</span>
            <Input
              value={displayText}
              readOnly
              className={styles['rule-input']}
              suffix={<IconEdit onClick={() => editRule(rule.id || '')} className={styles['edit-btn']} />}
            />
          </div>
        );
      }

      case 'DATE':
        return (
          <div className={styles['rule-content']}>
            <IconDragDotVertical className={styles['drag-handle']} />
            <span className={styles['rule-label']}>创建时间:</span>
            <Select
              value={(rule.config.dateFormat as string) || '年月日'}
              onChange={(value) => updateRule(rule.id!, { config: { ...rule.config, dateFormat: value } })}
              className={styles['rule-input']}
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
              className={styles['rule-action-btn']}
            />
          </div>
        );

      case 'TEXT':
        return (
          <div className={styles['rule-content']}>
            <IconDragDotVertical className={styles['drag-handle']} />
            <span className={styles['rule-label']}>固定字符:</span>
            <Input
              value={(rule.config.fixedText as string) || ''}
              placeholder="请输入内容"
              onChange={(value) => updateRule(rule.id!, { config: { ...rule.config, fixedText: value } })}
              className={styles['rule-input']}
            />
            <Button
              type="text"
              status="danger"
              icon={<IconDelete />}
              onClick={() => removeRule(rule.id!)}
              className={styles['rule-action-btn']}
            />
          </div>
        );

      case 'FIELD_REF':
        return (
          <div className={styles['rule-content']}>
            <IconDragDotVertical className={styles['drag-handle']} />
            <span className={styles['rule-label']}>表单字段:</span>
            <Cascader
              placeholder="请选择字段"
              className={styles['rule-input']}
              options={fields}
              onChange={(value) =>
                updateRule(rule.id!, { config: { ...rule.config, fieldName: value[value.length - 1] } })
              }
            />
            <Button
              type="text"
              status="danger"
              icon={<IconDelete />}
              onClick={() => removeRule(rule.id!)}
              className={styles['rule-action-btn']}
            />
          </div>
        );

      default:
        return null;
    }
  };

  return (
    <>
      <div className={styles['field-type-config']}>
        <h4>自动编号规则</h4>
        <div className={styles['rule-items']}>
          <ReactSortable list={rules} setList={(newList) => setRules(newList as AutoCodeRule[])} animation={200}>
            {rules.map((rule) => (
              <div key={rule.id}>{renderRuleConfig(rule)}</div>
            ))}
          </ReactSortable>
        </div>

        <div className={styles['add-rule-dropdown']}>
          <Dropdown
            trigger="click"
            droplist={
              <Menu>
                <Menu.Item key="DATE" onClick={() => addRule('DATE')}>
                  创建时间
                </Menu.Item>
                <Menu.Item key="TEXT" onClick={() => addRule('TEXT')}>
                  固定字符
                </Menu.Item>
                <Menu.Item key="FIELD_REF" onClick={() => addRule('FIELD_REF')}>
                  表单字段
                </Menu.Item>
              </Menu>
            }
          >
            <Button type="dashed" icon={<IconPlus />} className={styles['add-rule-btn']}>
              添加规则
            </Button>
          </Dropdown>
        </div>

        <div className={styles['field-type-config-footer']}>
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
        initialConfig={rules.find((rule) => rule.itemType === 'SEQUENCE')?.config}
      />
    </>
  );
};

// 单选列表配置组件
export const PicklistConfig: React.FC<{
  visible?: boolean;
  onVisibleChange?: (visible: boolean) => void;
  onConfirm: (options: string[]) => void;
  initialOptions?: string[];
  onCancel?: () => void;
}> = (props) => {
  return <OptionConfig {...props} />;
};

// 多选列表配置组件
export const MultiPicklistConfig: React.FC<{
  visible?: boolean;
  onVisibleChange?: (visible: boolean) => void;
  onConfirm: (options: string[]) => void;
  initialOptions?: string[];
  onCancel?: () => void;
}> = (props) => {
  return <OptionConfig {...props} />;
};
