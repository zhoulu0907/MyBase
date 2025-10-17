import type { AUTO_CODE_RULE_TYPE } from './const';

// 创建自动编号规则返回值
export interface AutoNumberRuleResponce {
  mode?: string;
  digitWidth: number;
  overflowContinue?: number;
  resetCycle?: string;
  nextRecordStartValue?: number;
}
// 创建自动编号规则
export interface AutoNumberRule {
  isEnabled: number;
  numberMode: string;
  digitWidth: number;
  overflowContinue: number;
  initialValue: number;
  resetCycle: string;
  nextRecordStartValue?: number;
  startValue?: number;
  rules: AutoNumberRuleItem[];
}

export interface AutoNumberRuleItem {
  id?: string;
  itemType:
    | typeof AUTO_CODE_RULE_TYPE.SEQUENCE
    | typeof AUTO_CODE_RULE_TYPE.DATE
    | typeof AUTO_CODE_RULE_TYPE.TEXT
    | typeof AUTO_CODE_RULE_TYPE.FIELD_REF;
  itemOrder?: number;
  isEnabled?: number;
  format?: string;
  textValue?: string;
}

export interface AutoCodeRule {
  id?: string;
  itemType:
    | typeof AUTO_CODE_RULE_TYPE.SEQUENCE
    | typeof AUTO_CODE_RULE_TYPE.DATE
    | typeof AUTO_CODE_RULE_TYPE.TEXT
    | typeof AUTO_CODE_RULE_TYPE.FIELD_REF;
  config: Record<string, unknown>;
}

export interface AutoCodeRules {
  rules: AutoCodeRule[];
}
