import React, { useState } from 'react';
import { Button, Input, Select, Dropdown, Menu } from '@arco-design/web-react';
import { IconDelete, IconDragDotVertical, IconPlus } from '@arco-design/web-react/icon';
import { ReactSortable } from 'react-sortablejs';
import styles from '../modal.module.less';

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
  onConfirm: (rules: AutoCodeRule[]) => void;
  initialRules?: AutoCodeRule[];
  onCancel?: () => void; // 新增：取消回调
}

export interface AutoCodeRule {
  id?: string;
  type: 'auto_number' | 'create_time' | 'fixed_char' | 'form_field';
  config: {
    digits?: number;
    reset?: boolean;
    dateFormat?: string;
    fixedText?: string;
    fieldName?: string;
  };
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
  initialRules = []
}) => {
  const [rules, setRules] = useState<AutoCodeRule[]>(
    initialRules.length > 0
      ? initialRules
      : [
          {
            // id: '1',
            type: 'auto_number',
            config: { digits: 4, reset: false }
          },
          {
            // id: '1',
            type: 'create_time',
            config: { dateFormat: '年月日' }
          },
          {
            // id: '1',
            type: 'fixed_char',
            config: { fixedText: '' }
          },
          {
            // id: '1',
            type: 'form_field',
            config: { fieldName: 'field1' }
          }
        ]
  );

  const addRule = (type: AutoCodeRule['type']) => {
    const newRule: AutoCodeRule = {
      // id: Date.now().toString(),
      type,
      config: {}
    };
    setRules([...rules, newRule]);
  };

  const removeRule = (id: string) => {
    setRules(rules.filter((rule) => rule.id !== id));
  };

  const updateRule = (id: string, updates: Partial<AutoCodeRule>) => {
    setRules(rules.map((rule) => (rule.id === id ? { ...rule, ...updates } : rule)));
  };

  const handleConfirm = () => {
    onConfirm(rules);
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
    switch (rule.type) {
      case 'auto_number':
        return (
          <div className={styles['rule-content']}>
            <IconDragDotVertical className={styles['drag-handle']} />
            <span className={styles['rule-label']}>自动编号:</span>
            <Input
              value={`${rule.config.digits || 4}位数,${rule.config.reset ? '自动重置' : '不自动重置'}`}
              readOnly
              className={styles['rule-input']}
            />
          </div>
        );

      case 'create_time':
        return (
          <div className={styles['rule-content']}>
            <IconDragDotVertical className={styles['drag-handle']} />
            <span className={styles['rule-label']}>创建时间:</span>
            <Select
              value={rule.config.dateFormat || '年月日'}
              onChange={(value) => updateRule(rule.id, { config: { ...rule.config, dateFormat: value } })}
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
              onClick={() => removeRule(rule.id)}
              className={styles['rule-action-btn']}
            />
          </div>
        );

      case 'fixed_char':
        return (
          <div className={styles['rule-content']}>
            <IconDragDotVertical className={styles['drag-handle']} />
            <span className={styles['rule-label']}>固定字符:</span>
            <Input
              value={rule.config.fixedText || ''}
              placeholder="请输入内容"
              onChange={(value) => updateRule(rule.id, { config: { ...rule.config, fixedText: value } })}
              className={styles['rule-input']}
            />
            <Button
              type="text"
              status="danger"
              icon={<IconDelete />}
              onClick={() => removeRule(rule.id)}
              className={styles['rule-action-btn']}
            />
          </div>
        );

      case 'form_field':
        return (
          <div className={styles['rule-content']}>
            <IconDragDotVertical className={styles['drag-handle']} />
            <span className={styles['rule-label']}>表单字段:</span>
            <Select
              value={rule.config.fieldName || ''}
              placeholder="请选择字段"
              onChange={(value) => updateRule(rule.id, { config: { ...rule.config, fieldName: value } })}
              className={styles['rule-input']}
            >
              <Select.Option value="field1">XX字段</Select.Option>
              <Select.Option value="field2">YY字段</Select.Option>
            </Select>
            <Button
              type="text"
              status="danger"
              icon={<IconDelete />}
              onClick={() => removeRule(rule.id)}
              className={styles['rule-action-btn']}
            />
          </div>
        );

      default:
        return null;
    }
  };

  return (
    <div className={styles['field-type-config']}>
      <h4>自动编号规则</h4>
      <div className={styles['rule-items']}>
        <ReactSortable list={rules} setList={setRules}>
          {rules.map((rule, index) => (
            <div key={index}>{renderRuleConfig(rule)}</div>
          ))}
        </ReactSortable>
      </div>

      <div className={styles['add-rule-dropdown']}>
        <Dropdown
          trigger="click"
          droplist={
            <Menu>
              <Menu.Item key="create_time" onClick={() => addRule('create_time')}>
                创建时间
              </Menu.Item>
              <Menu.Item key="fixed_char" onClick={() => addRule('fixed_char')}>
                固定字符
              </Menu.Item>
              <Menu.Item key="form_field" onClick={() => addRule('form_field')}>
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
