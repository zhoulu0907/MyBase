import React, { useState, useCallback } from 'react';
import { Button, Input, Select, Dropdown, Menu, Cascader, Space, Message } from '@arco-design/web-react';
import { IconDelete, IconDragDotVertical, IconPlus, IconEdit } from '@arco-design/web-react/icon';
import { ENTITY_FIELD_TYPE } from '@onebase/ui-kit';
import { ReactSortable } from 'react-sortablejs';
import AutoCodeNumberSettingsModal from './AutoCodeNumberSettingsModal';
import type { AutoNumberRule, AutoCodeRule, AutoNumberRuleResponce, EntityFieldsWithChildren } from '../types';
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
  AUTO_CODE_NUMBER_DEFAULT_CONFIG,
  DATE_FORMAT_OPTIONS,
  DATE_FORMAT_VALUES
} from '../utils/const';
import styles from '../index.module.less';

interface AutoCodeRuleConfigProps {
  onVisibleChange?: (visible: boolean) => void;
  onConfirm: (config: AutoNumberRule) => void;
  initialConfig?: AutoNumberRule;
  onCancel?: () => void;
  fields: EntityFieldsWithChildren[];
}

// 仅支持部分类型字段作为编号组成部分
const getFieldOptions = (entitys: EntityFieldsWithChildren[]) => {
  const filterTypes = [
    ENTITY_FIELD_TYPE.TEXT.VALUE,
    ENTITY_FIELD_TYPE.NUMBER.VALUE,
    ENTITY_FIELD_TYPE.DATE.VALUE,
    ENTITY_FIELD_TYPE.EMAIL.VALUE,
    ENTITY_FIELD_TYPE.SELECT.VALUE,
    ENTITY_FIELD_TYPE.PHONE.VALUE,
    ENTITY_FIELD_TYPE.ADDRESS.VALUE
  ];
  return entitys?.map((entity) => {
    return {
      label: entity.label,
      value: entity.value,
      children: entity.children
        ?.filter((field) => filterTypes.includes(field.fieldType))
        ?.map((field) => ({
          label: field.label,
          value: field.value
        }))
    };
  });
};

export const AutoCodeRuleConfig: React.FC<AutoCodeRuleConfigProps> = ({
  onVisibleChange,
  onConfirm,
  onCancel,
  initialConfig,
  fields
}) => {
  // 使用对象存储每个规则的校验状态，key 为 rule.id
  const [fixedTextStatusMap, setFixedTextStatusMap] = useState<Record<string, 'error' | undefined>>({});
  const [customDateFormatStatusMap, setCustomDateFormatStatusMap] = useState<Record<string, 'error' | undefined>>({});

  // 默认规则
  const getInitialRules = useCallback((): AutoCodeRule[] => {
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
  }, [initialConfig, fields]);

  const [rules, setRules] = useState<AutoCodeRule[]>(getInitialRules());
  const [autoCodeModalVisible, setAutoCodeModalVisible] = useState(false);
  const [editingRuleId, setEditingRuleId] = useState<string>('');
  const getDisplayText = (config?: AutoCodeRule['config']) => {
    const sequenceConfig = config || rules.find((rule) => rule.itemType === AUTO_CODE_RULE_TYPE.SEQUENCE)?.config;
    if (!sequenceConfig) {
      return '';
    }
    const numberingMethodText =
      sequenceConfig.numberMode === AUTO_CODE_NUMBER_MODE.NATURAL ? '自然数编号' : '指定位数编号';
    const digitsText =
      sequenceConfig.numberMode === AUTO_CODE_NUMBER_MODE.NATURAL ? '' : `${sequenceConfig.digitWidth}位数`;
    const resetText = sequenceConfig.resetCycle === AUTO_CODE_RESET_CYCLE.NONE ? '不自动重置' : '自动重置';
    return `${numberingMethodText},${digitsText}${resetText}`;
  };

  const [displayText, setDisplayText] = useState(getDisplayText());

  const addRule = (type: AutoCodeRule['itemType']) => {
    let config: AutoCodeRule['config'] = {};
    switch (type) {
      case AUTO_CODE_RULE_TYPE.SEQUENCE:
        config = { ...AUTO_CODE_NUMBER_DEFAULT_CONFIG };
        setDisplayText(getDisplayText(config));
        break;
      case AUTO_CODE_RULE_TYPE.DATE:
        config = { dateFormat: DATE_FORMAT_DEFAULT };
        break;
      default:
        config = {};
        break;
    }
    setRules([...rules, { id: 'rule-' + Date.now().toString(), itemType: type, config }]);
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
    setRules(rules.filter((rule) => rule.id !== id));
    // 清理对应的校验状态
    setFixedTextStatusMap((prev) => {
      const next = { ...prev };
      delete next[id];
      return next;
    });
    setCustomDateFormatStatusMap((prev) => {
      const next = { ...prev };
      delete next[id];
      return next;
    });
  };

  const updateRule = (id: string, updates: Partial<AutoCodeRule>) => {
    setRules(rules.map((rule) => (rule.id === id ? { ...rule, ...updates } : rule)));
  };

  const handleConfirm = () => {
    // 至少保留一条规则
    if (rules.length < 1) {
      Message.error('至少保留一条规则');
      return;
    }

    // 检查所有规则的校验状态
    const hasFixedTextError = Object.values(fixedTextStatusMap).some((status) => status === 'error');
    const hasCustomDateFormatError = Object.values(customDateFormatStatusMap).some((status) => status === 'error');

    if (hasFixedTextError || hasCustomDateFormatError) {
      Message.error('请检查输入内容');
      return;
    }
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
          </div>
        );
      }

      case AUTO_CODE_RULE_TYPE.DATE:
        return (
          <div className={styles.ruleContent}>
            <IconDragDotVertical className={styles.dragHandle} />
            <span className={styles.ruleLabel}>创建时间:</span>
            <Space direction="vertical">
              <Select
                value={(rule.config.dateFormat as string) || DATE_FORMAT_DEFAULT}
                onChange={(value) => {
                  // 清除该规则的校验状态
                  setCustomDateFormatStatusMap((prev) => ({ ...prev, [rule.id!]: undefined }));
                  updateRule(rule.id!, { config: { ...rule.config, dateFormat: value, fixedText: '' } });
                }}
                className={styles.ruleInput}
              >
                {DATE_FORMAT_OPTIONS.map((option) => (
                  <Select.Option key={option.value} value={option.value}>
                    {option.label}
                  </Select.Option>
                ))}
              </Select>
              {rule.config.dateFormat === DATE_FORMAT_VALUES.CUSTOM && (
                <Input
                  value={(rule.config.fixedText as string) || ''}
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
            </Space>
            <Button
              type="text"
              status="danger"
              icon={<IconDelete />}
              onClick={() => removeRule(rule.id!)}
              className={styles.ruleActionBtn}
            />
          </div>
        );

      case AUTO_CODE_RULE_TYPE.TEXT:
        return (
          <div className={styles.ruleContent}>
            <IconDragDotVertical className={styles.dragHandle} />
            <span className={styles.ruleLabel}>固定字符:</span>
            <Space direction="vertical">
              <Input
                value={(rule.config.fixedText as string) || ''}
                placeholder="请输入内容"
                maxLength={10}
                onChange={(value) => {
                  const ruleId = rule.id!;
                  if (!/^[0-9a-zA-Z_\-+=/()<>[\]{}.~、#%&*]+$/.test(value as string)) {
                    setFixedTextStatusMap((prev) => ({ ...prev, [ruleId]: 'error' }));
                  } else {
                    setFixedTextStatusMap((prev) => ({ ...prev, [ruleId]: undefined }));
                  }
                  updateRule(ruleId, { config: { ...rule.config, fixedText: value } });
                }}
                className={styles.ruleInput}
                status={fixedTextStatusMap[rule.id!]}
              />
              {fixedTextStatusMap[rule.id!] === 'error' && (
                <span className={styles.ruleInputError}>{`支持字母数字和特殊字符_-=+()<>[]{}.~、#%&*`}</span>
              )}
            </Space>
            <Button
              type="text"
              status="danger"
              icon={<IconDelete />}
              onClick={() => removeRule(rule.id!)}
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
              options={getFieldOptions(fields)}
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
