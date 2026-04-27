import {
  AUTO_CODE_SEQUENCE_DEFAULT_CONFIG,
  AUTO_CODE_RULE_TYPE,
  DATE_FORMAT_DEFAULT,
  CONSTANTS,
  AUTO_CODE_INITIAL_RULES
} from './const';
import type { AutoNumberRule, AutoNumberRuleItem } from '../types';

/**
 * 将自动编号组件数组中内容格式进行转换
 */
export const convertAutoCodeCompoToAutoNumberRule = (autoCodeRules: AutoNumberRuleItem[]) => {
  if (!autoCodeRules || autoCodeRules.length === 0) {
    // 返回默认配置
    return {
      rules: [...AUTO_CODE_INITIAL_RULES]
    };
  }

  const rules = autoCodeRules.map((rule, index) => ({
    ...rule,
    isEnabled: CONSTANTS.ENABLED,
    itemOrder: index + 1,
    id: rule.id?.startsWith('rule-') ? '' : rule.id
  }));

  return { rules };
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
 * 将 AutoNumberRule 对象数组格式转换为自动编号组件数组格式
 * @param autoNumberRule AutoNumberRule 对象格式
 * @param fields 字段选项数据（用于查找字段路径）
 */
export const convertAutoNumberRuleToAutoCodeComp = (
  autoNumberRule: AutoNumberRule,
  fields?: unknown[]
): AutoNumberRuleItem[] => {
  if (!autoNumberRule || autoNumberRule.rules.length === 0) {
    return [...AUTO_CODE_INITIAL_RULES];
  }

  return autoNumberRule.rules.map((rule, index) => {
    const baseRule: AutoNumberRuleItem = {
      ...rule,
      id: rule.id || `rule-${index + 1}`,
      itemOrder: rule.itemOrder || index + 1,
      isEnabled: rule.isEnabled ?? CONSTANTS.ENABLED
    };

    switch (rule.itemType) {
      case AUTO_CODE_RULE_TYPE.DATE:
        return {
          ...baseRule,
          format: rule.format || DATE_FORMAT_DEFAULT,
          textValue: rule.textValue || ''
        };
      case AUTO_CODE_RULE_TYPE.TEXT:
        return {
          ...baseRule,
          textValue: rule.textValue || ''
        };
      case AUTO_CODE_RULE_TYPE.FIELD_REF: {
        const fieldPath = fields
          ? findFieldPath(rule.refFieldUuid || '', fields)
          : rule.refFieldUuid
            ? [rule.refFieldUuid]
            : [];
        return {
          ...baseRule,
          fieldPath,
          format: rule.format || ''
        };
      }
      case AUTO_CODE_RULE_TYPE.SEQUENCE:
        return {
          ...AUTO_CODE_SEQUENCE_DEFAULT_CONFIG,
          ...baseRule,
          startValue: rule.startValue || rule.initialValue
        };
      default:
        return baseRule;
    }
  });
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
