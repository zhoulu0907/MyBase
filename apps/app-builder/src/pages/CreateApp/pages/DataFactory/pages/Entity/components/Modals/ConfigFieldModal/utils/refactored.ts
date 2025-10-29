import { AUTO_CODE_NUMBER_MODE, AUTO_CODE_RESET_CYCLE, AUTO_CODE_RULE_TYPE, DIGIT_DEFAULT } from './const';
import type { AutoNumberRule, AutoNumberRuleItem, AutoCodeRule } from '../types';

/**
 * 数组移动工具函数
 */
export const arrayMove = <T>(array: T[], from: number, to: number): T[] => {
  const newArray = [...array];
  const startIndex = to < 0 ? newArray.length + to : to;

  if (startIndex >= 0 && startIndex < newArray.length) {
    const item = newArray.splice(from, 1)[0];
    newArray.splice(startIndex, 0, item);
  }

  return newArray;
};

/**
 * 根据字段ID找到完整的路径
 */
export const findFieldPath = (fieldId: string, fields: unknown[]): string[] => {
  if (!fieldId || !fields?.length) {
    return [];
  }

  for (const entity of fields) {
    if (entity && typeof entity === 'object' && 'children' in entity) {
      const entityWithChildren = entity as { value: string; children?: { value: string }[] };

      if (entityWithChildren.children) {
        for (const field of entityWithChildren.children) {
          if (field.value === fieldId) {
            return [entityWithChildren.value, field.value];
          }
        }
      }
    }
  }

  return [];
};

/**
 * 将自动编号组件数组格式转换为 AutoNumberRule 对象格式
 */
export const convertAutoCodeCompoToAutoNumberRule = (autoCodeRules: AutoCodeRule[]): AutoNumberRule => {
  if (!autoCodeRules?.length) {
    return getDefaultAutoNumberRule();
  }

  const sequenceRule = autoCodeRules.find((rule) => rule.itemType === AUTO_CODE_RULE_TYPE.SEQUENCE);
  const otherRules = convertOtherRules(autoCodeRules);

  if (sequenceRule?.config) {
    return createAutoNumberRuleFromSequence(sequenceRule.config, otherRules);
  }

  return getDefaultAutoNumberRule();
};

/**
 * 将 AutoNumberRule 对象格式转换为自动编号组件数组格式
 */
export const convertAutoNumberRuleToAutoCodeComp = (
  autoNumberRule: AutoNumberRule,
  fields?: unknown[]
): AutoCodeRule[] => {
  if (!autoNumberRule) {
    return [];
  }

  const rules: AutoCodeRule[] = [];

  // 主配置转换为 SEQUENCE 规则
  rules.push(createSequenceRule(autoNumberRule));

  // 其他规则转换
  autoNumberRule.rules?.forEach((rule, index) => {
    rules.push(convertRuleToAutoCode(rule, index, fields));
  });

  return rules;
};

/**
 * 获取默认的自动编号规则
 */
const getDefaultAutoNumberRule = (): AutoNumberRule => ({
  isEnabled: 1,
  numberMode: AUTO_CODE_NUMBER_MODE.FIXED_DIGITS,
  digitWidth: DIGIT_DEFAULT,
  overflowContinue: 1,
  initialValue: 1,
  resetCycle: AUTO_CODE_RESET_CYCLE.NONE,
  rules: []
});

/**
 * 转换其他规则
 */
const convertOtherRules = (autoCodeRules: AutoCodeRule[]): AutoNumberRuleItem[] => {
  return autoCodeRules
    .filter((rule) => rule.itemType !== AUTO_CODE_RULE_TYPE.SEQUENCE)
    .map((rule, index) => ({
      id: rule.id?.startsWith('rule-') ? '' : rule.id,
      itemType: rule.itemType,
      itemOrder: index + 1,
      isEnabled: 1,
      format: getRuleFormat(rule)
    }));
};

/**
 * 获取规则格式
 */
const getRuleFormat = (rule: AutoCodeRule): string => {
  return (
    (rule.config?.dateFormat as string) ||
    (rule.config?.fixedText as string) ||
    (rule.config?.fieldName as string) ||
    ''
  );
};

/**
 * 从序列规则创建自动编号规则
 */
const createAutoNumberRuleFromSequence = (
  config: Record<string, unknown>,
  otherRules: AutoNumberRuleItem[]
): AutoNumberRule => ({
  isEnabled: 1,
  numberMode: (config.numberMode as string) || AUTO_CODE_NUMBER_MODE.FIXED_DIGITS,
  digitWidth: (config.digitWidth as number) || DIGIT_DEFAULT,
  overflowContinue: config.continueIncrement ? 1 : 0,
  initialValue: (config.startValue as number) || 1,
  resetCycle:
    config.resetCycle === AUTO_CODE_RESET_CYCLE.NONE
      ? AUTO_CODE_RESET_CYCLE.NONE
      : (config.resetCycle as string) || AUTO_CODE_RESET_CYCLE.NONE,
  nextRecordStartValue: (config.nextRecordStartValue as number) || undefined,
  rules: otherRules
});

/**
 * 创建序列规则
 */
const createSequenceRule = (autoNumberRule: AutoNumberRule): AutoCodeRule => ({
  id: Date.now().toString(),
  itemType: AUTO_CODE_RULE_TYPE.SEQUENCE,
  config: {
    numberMode: autoNumberRule.numberMode,
    digitWidth: autoNumberRule.digitWidth,
    continueIncrement: autoNumberRule.overflowContinue === 1,
    startValue: autoNumberRule.initialValue,
    nextRecordStartValue: autoNumberRule.nextRecordStartValue,
    resetCycle:
      autoNumberRule.resetCycle === AUTO_CODE_RESET_CYCLE.NONE ? AUTO_CODE_RESET_CYCLE.NONE : autoNumberRule.resetCycle
  }
});

/**
 * 转换规则为自动编号组件格式
 */
const convertRuleToAutoCode = (rule: AutoNumberRuleItem, index: number, fields?: unknown[]): AutoCodeRule => {
  const autoCodeRule: AutoCodeRule = {
    id: rule.id || `rule_${index + 1}`,
    itemType: rule.itemType,
    config: {}
  };

  switch (rule.itemType) {
    case AUTO_CODE_RULE_TYPE.DATE:
      autoCodeRule.config = { dateFormat: rule.format || '年月日' };
      break;
    case AUTO_CODE_RULE_TYPE.TEXT:
      autoCodeRule.config = { fixedText: rule.format || '' };
      break;
    case AUTO_CODE_RULE_TYPE.FIELD_REF: {
      const fieldPath = fields ? findFieldPath(rule.format || '', fields) : rule.format ? [rule.format] : [];
      autoCodeRule.config = {
        fieldName: rule.format || '',
        fieldPath: fieldPath
      };
      break;
    }
    default:
      autoCodeRule.config = { format: '' };
  }

  return autoCodeRule;
};

// 用于计算自定义字段index
export const systemFieldsLength = 10;
