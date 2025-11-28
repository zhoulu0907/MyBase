import {
  AUTO_CODE_SEQUENCE_DEFAULT_CONFIG,
  AUTO_CODE_NUMBER_MODE,
  AUTO_CODE_RULE_TYPE,
  DIGIT_DEFAULT,
  DATE_FORMAT_DEFAULT,
  START_VALUE_DEFAULT,
  CONSTANTS
} from './const';
import type { AutoNumberRule, AutoNumberRuleItem, AutoCodeRule } from '../types';

/**
 * 将自动编号组件数组格式转换为 AutoNumberRule 对象格式
 * @param autoCodeRules 自动编号组件数组格式
 * @returns 转换后的 AutoNumberRule 对象格式
 */
export const convertAutoCodeCompoToAutoNumberRule = (autoCodeRules: AutoCodeRule[]): AutoNumberRule => {
  if (!autoCodeRules || autoCodeRules.length === 0) {
    // 返回默认配置
    return {
      ...AUTO_CODE_SEQUENCE_DEFAULT_CONFIG,
      rules: []
    };
  }

  // 找到第一个 SEQUENCE 类型的规则作为主配置
  const sequenceRule = autoCodeRules.find((rule) => rule.itemType === AUTO_CODE_RULE_TYPE.SEQUENCE);

  // 其他规则转换为 rules 数组
  const otherRules: AutoNumberRuleItem[] = autoCodeRules
    .filter((rule) => rule.itemType !== AUTO_CODE_RULE_TYPE.SEQUENCE)
    .map((rule, index) => ({
      id: rule.id?.startsWith('rule-') ? '' : rule.id,
      itemType: rule.itemType,
      itemOrder: index + 1,
      isEnabled: CONSTANTS.ENABLED,
      format:
        (rule.config?.dateFormat as string) ||
        (rule.config?.fixedText as string) ||
        (rule.config?.fieldName as string) ||
        '',
      textValue: rule.config?.fixedText as string
    }));

  // 如果有 SEQUENCE 规则，使用其配置；否则使用默认配置
  if (sequenceRule && sequenceRule.config) {
    const config = sequenceRule.config;
    return {
      isEnabled: 1,
      numberMode: (config.numberMode as string) || AUTO_CODE_NUMBER_MODE.FIXED_DIGITS,
      digitWidth: (config.digitWidth as number) || DIGIT_DEFAULT,
      overflowContinue: (config.overflowContinue as number) || CONSTANTS.ENABLED,
      initialValue: (config.startValue as number) || START_VALUE_DEFAULT,
      resetCycle: (config.resetCycle as string) || AUTO_CODE_SEQUENCE_DEFAULT_CONFIG.resetCycle,
      resetOnInitialChange: (config.resetOnInitialChange as number) || CONSTANTS.ENABLED,
      rules: otherRules
    };
  }

  // 没有 SEQUENCE 规则时，使用默认配置
  return {
    ...AUTO_CODE_SEQUENCE_DEFAULT_CONFIG,
    rules: otherRules
  };
};

/**
 * 根据字段ID找到完整的路径
 * @param fieldId 字段ID
 * @param fields 字段选项数据
 * @returns 完整路径数组
 */
export const findFieldPath = (fieldId: string, fields: unknown[]): string[] => {
  if (!fieldId || !fields || fields.length === 0) {
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

  console.log('findFieldPath: no match found for fieldId', fieldId);
  return [];
};

/**
 * 将 AutoNumberRule 对象格式转换为自动编号组件数组格式
 * @param autoNumberRule AutoNumberRule 对象格式
 * @param fields 字段选项数据（用于查找字段路径）
 * @returns 转换后的自动编号组件数组格式
 */
export const convertAutoNumberRuleToAutoCodeComp = (
  autoNumberRule: AutoNumberRule,
  fields?: unknown[]
): AutoCodeRule[] => {
  if (!autoNumberRule) {
    return [];
  }

  const rules: AutoCodeRule[] = [];

  // 将主配置转换为 SEQUENCE 规则
  const sequenceRule: AutoCodeRule = {
    id: Date.now().toString(),
    itemType: AUTO_CODE_RULE_TYPE.SEQUENCE,
    config: {
      ...autoNumberRule,
      startValue: autoNumberRule.initialValue
    }
  };
  rules.push(sequenceRule);

  // 将 rules 数组转换为其他类型的规则
  autoNumberRule?.rules?.forEach((rule, index) => {
    const autoCodeRule: AutoCodeRule = {
      id: rule.id || `rule_${index + 1}`,
      itemType: rule.itemType,
      config: {}
    };

    switch (rule.itemType) {
      case AUTO_CODE_RULE_TYPE.DATE:
        autoCodeRule.config = { dateFormat: rule.format || DATE_FORMAT_DEFAULT, fixedText: rule.textValue || '' };
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

    rules.push(autoCodeRule);
  });

  return rules;
};

const arrayMoveMutate = (array: unknown[], from: number, to: number) => {
  const startIndex = to < 0 ? array.length + to : to;

  if (startIndex >= 0 && startIndex < array.length) {
    const item = array.splice(from, 1)[0];
    array.splice(startIndex, 0, item);
  }
};

export const arrayMove = (array: unknown[], from: number, to: number) => {
  array = [...array];
  arrayMoveMutate(array, from, to);

  return array.map((item) => {
    if (item && typeof item === 'object') {
      return { ...item };
    }
    return item;
  });
};

// 用于计算自定义字段index
export const systemFieldsLength = 10;
