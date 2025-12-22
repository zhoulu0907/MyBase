import React, { useState, useCallback } from 'react';
import { Button, Input, Select, Dropdown, Menu, Cascader, Space, Message } from '@arco-design/web-react';
import { IconDelete, IconDragDotVertical, IconPlus, IconEdit } from '@arco-design/web-react/icon';
import { ENTITY_FIELD_TYPE, FIELD_TYPE } from '@onebase/ui-kit';
import { ReactSortable } from 'react-sortablejs';
import AutoCodeNumberSettingsModal from './AutoCodeNumberSettingsModal';
import type { AutoNumberRule, AutoNumberRuleResponce, EntityFieldsWithChildren, AutoNumberRuleItem } from '../types';
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
  AUTO_CODE_SEQUENCE_DEFAULT_CONFIG,
  DATE_FORMAT_OPTIONS,
  DATE_FORMAT_VALUES,
  AUTO_CODE_INITIAL_RULES
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
      children: entity.children?.filter(
        (field) => filterTypes.includes(field.fieldType) && field.isSystemField === FIELD_TYPE.CUSTOM
      )
    };
  });
};

type RuleItem = AutoNumberRuleItem & { id: string };

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
  const getInitialRules = useCallback((): RuleItem[] => {
    if (initialConfig) {
      return convertAutoNumberRuleToAutoCodeComp(initialConfig as AutoNumberRule, fields).map((rule, index) => ({
        ...rule,
        id: rule.id || `rule-${index + 1}`
      }));
    }
    return (AUTO_CODE_INITIAL_RULES as AutoNumberRuleItem[]).map((rule, index) => ({
      ...rule,
      id: rule.id || `rule-${index + 1}`
    }));
  }, [initialConfig, fields]);

  const [rules, setRules] = useState<RuleItem[]>(getInitialRules());
  const [autoCodeModalVisible, setAutoCodeModalVisible] = useState(false);
  const [editingRuleId, setEditingRuleId] = useState<string>('');
  const getDisplayText = (config?: AutoNumberRuleItem) => {
    const sequenceRule = config || rules.find((rule) => rule.itemType === AUTO_CODE_RULE_TYPE.SEQUENCE);
    if (!sequenceRule) {
      return '';
    }
    const numberingMethodText =
      sequenceRule.numberMode === AUTO_CODE_NUMBER_MODE.NATURAL ? '自然数编号' : '指定位数编号';
    const digitsText =
      sequenceRule.numberMode === AUTO_CODE_NUMBER_MODE.NATURAL ? '' : `${sequenceRule.digitWidth}位数`;
    const resetText = sequenceRule.resetCycle === AUTO_CODE_RESET_CYCLE.NONE ? '不自动重置' : '自动重置';
    return `${numberingMethodText},${digitsText}${resetText}`;
  };

  const [displayText, setDisplayText] = useState(getDisplayText());

  const addRule = (type: RuleItem['itemType']) => {
    let config: Partial<RuleItem> = {};
    switch (type) {
      case AUTO_CODE_RULE_TYPE.SEQUENCE:
        config = { ...AUTO_CODE_SEQUENCE_DEFAULT_CONFIG };
        setDisplayText(getDisplayText(config));
        break;
      case AUTO_CODE_RULE_TYPE.DATE:
        config = { format: DATE_FORMAT_DEFAULT };
        break;
      default:
        config = {};
        break;
    }
    const nextId = (config as RuleItem).id || `rule-${Date.now().toString()}`;
    setRules([...rules, { ...(config as RuleItem), id: nextId, itemType: type }]);
  };

  const editRule = (id: string) => {
    setEditingRuleId(id);
    setAutoCodeModalVisible(true);
  };

  const handleAutoCodeNumberSettingsConfirm = (config: AutoNumberRuleResponce) => {
    const ruleConfig: RuleItem = {
      ...config,
      itemType: AUTO_CODE_RULE_TYPE.SEQUENCE,
      startValue: config.initialValue,
      id: editingRuleId || `rule-${Date.now()}`
    };

    updateRule(editingRuleId, ruleConfig);
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

  const updateRule = (id: string, updates: Partial<RuleItem>) => {
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

  const renderRuleConfig = (rule: RuleItem) => {
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
                value={rule.format || DATE_FORMAT_DEFAULT}
                onChange={(value) => {
                  setCustomDateFormatStatusMap((prev) => ({ ...prev, [rule.id!]: undefined }));
                  updateRule(rule.id!, { format: value, textValue: '' });
                }}
                className={styles.ruleInput}
              >
                {DATE_FORMAT_OPTIONS.map((option) => (
                  <Select.Option key={option.value} value={option.value}>
                    {option.label}
                  </Select.Option>
                ))}
              </Select>
              {rule.format === DATE_FORMAT_VALUES.CUSTOM && (
                <Input
                  value={(rule.textValue as string) || ''}
                  placeholder="例如：yyyyMMddHHmmss"
                  onChange={(value) => {
                    const ruleId = rule.id!;
                    if (!/^[dhmstyHM]+$/.test(value as string)) {
                      setCustomDateFormatStatusMap((prev) => ({ ...prev, [ruleId]: 'error' }));
                    } else {
                      setCustomDateFormatStatusMap((prev) => ({ ...prev, [ruleId]: undefined }));
                    }
                    updateRule(ruleId, { textValue: value });
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
                value={(rule.textValue as string) || ''}
                placeholder="请输入内容"
                maxLength={10}
                onChange={(value) => {
                  const ruleId = rule.id!;
                  if (!/^[0-9a-zA-Z_\-+=/()<>[\]{}.~、#%&*]+$/.test(value as string)) {
                    setFixedTextStatusMap((prev) => ({ ...prev, [ruleId]: 'error' }));
                  } else {
                    setFixedTextStatusMap((prev) => ({ ...prev, [ruleId]: undefined }));
                  }
                  updateRule(ruleId, { textValue: value });
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
              value={findFieldPath((rule.refFieldUuid as string) || '', fields)}
              onChange={(value) => {
                updateRule(rule.id!, {
                  refFieldUuid: value[value.length - 1],
                  fieldPath: value
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
          <ReactSortable<RuleItem>
            list={rules}
            setList={(newList) =>
              setRules(
                (newList as RuleItem[]).map((item, index) => ({
                  ...item,
                  id: item.id || `rule-${Date.now()}-${index}`
                }))
              )
            }
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
        initialConfig={(() => {
          const sequenceRule = rules.find((rule) => rule.itemType === AUTO_CODE_RULE_TYPE.SEQUENCE);
          if (!sequenceRule) return undefined;
          return {
            ...sequenceRule,
            initialValue: sequenceRule.initialValue ?? sequenceRule.startValue
          } as AutoNumberRuleResponce;
        })()}
      />
    </>
  );
};
