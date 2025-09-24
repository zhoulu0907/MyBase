import type { AutoNumberRule, AutoNumberRuleItem, AutoCodeRule } from './types';

/**
 * 将自动编号组件数组格式转换为 AutoNumberRule 对象格式
 * @param autoCodeRules 自动编号组件数组格式
 * @returns 转换后的 AutoNumberRule 对象格式
 */
export const convertAutoCodeCompoToAutoNumberRule = (autoCodeRules: AutoCodeRule[]): AutoNumberRule => {
  if (!autoCodeRules || autoCodeRules.length === 0) {
    // 返回默认配置
    return {
      isEnabled: 1,
      numberMode: 'FIXED_DIGITS',
      digitWidth: 4,
      overflowContinue: 1,
      initialValue: 1,
      resetCycle: 'NONE',
      rules: []
    };
  }

  // 找到第一个 SEQUENCE 类型的规则作为主配置
  const sequenceRule = autoCodeRules.find((rule) => rule.itemType === 'SEQUENCE');

  // 其他规则转换为 rules 数组
  const otherRules: AutoNumberRuleItem[] = autoCodeRules
    .filter((rule) => rule.itemType !== 'SEQUENCE')
    .map((rule, index) => ({
      id: rule.id?.startsWith('rule-') ? '' : rule.id,
      itemType: rule.itemType,
      itemOrder: index + 1,
      isEnabled: 1,
      format: rule.config?.dateFormat || rule.config?.fixedText || rule.config?.fieldName || ''
    }));

  // 如果有 SEQUENCE 规则，使用其配置；否则使用默认配置
  if (sequenceRule && sequenceRule.config) {
    const config = sequenceRule.config;
    return {
      isEnabled: 1,
      numberMode: (config.numberMode as string) || 'FIXED_DIGITS',
      digitWidth: (config.digitWidth as number) || 4,
      overflowContinue: config.continueIncrement ? 1 : 0,
      initialValue: (config.startValue as number) || 1,
      resetCycle: config.resetCycle === 'NONE' ? 'NONE' : (config.resetCycle as string) || 'NONE',
      nextRecordStartValue: (config.nextRecordStartValue as number) || undefined,
      rules: otherRules
    };
  }

  // 没有 SEQUENCE 规则时，使用默认配置
  return {
    isEnabled: 1,
    numberMode: 'FIXED_DIGITS',
    digitWidth: 4,
    overflowContinue: 1,
    initialValue: 1,
    resetCycle: 'NONE',
    rules: otherRules
  };
};

/**
 * 将 AutoNumberRule 对象格式转换为自动编号组件数组格式
 * @param autoNumberRule AutoNumberRule 对象格式
 * @returns 转换后的自动编号组件数组格式
 */
export const convertAutoNumberRuleToAutoCodeComp = (autoNumberRule: AutoNumberRule): AutoCodeRule[] => {
  if (!autoNumberRule) {
    return [];
  }

  const rules: AutoCodeRule[] = [];

  // 将主配置转换为 SEQUENCE 规则
  const sequenceRule: AutoCodeRule = {
    id: Date.now().toString(),
    itemType: 'SEQUENCE',
    config: {
      numberMode: autoNumberRule.numberMode,
      digitWidth: autoNumberRule.digitWidth,
      continueIncrement: autoNumberRule.overflowContinue === 1,
      startValue: autoNumberRule.initialValue,
      nextRecordStartValue: autoNumberRule.nextRecordStartValue,
      resetCycle: autoNumberRule.resetCycle === 'NONE' ? 'NONE' : autoNumberRule.resetCycle
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
      case 'DATE':
        autoCodeRule.config = { dateFormat: rule.format || '年月日' };
        break;
      case 'TEXT':
        autoCodeRule.config = { fixedText: rule.format || '' };
        break;
      case 'FIELD_REF':
        autoCodeRule.config = { fieldName: rule.format || '' };
        break;
      default:
        autoCodeRule.config = {};
    }

    rules.push(autoCodeRule);
  });

  return rules;
};

const arrayMoveMutate = (array: any[], from: number, to: number) => {
  const startIndex = to < 0 ? array.length + to : to;

  if (startIndex >= 0 && startIndex < array.length) {
    const item = array.splice(from, 1)[0];
    array.splice(startIndex, 0, item);
  }
};

export const arrayMove = (array: any[], from: number, to: number) => {
  array = [...array];
  arrayMoveMutate(array, from, to);

  return array.map((item, index) => ({
    ...item
  }));
};
