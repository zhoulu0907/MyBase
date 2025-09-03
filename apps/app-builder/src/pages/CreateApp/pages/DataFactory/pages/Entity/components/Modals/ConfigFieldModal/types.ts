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
  rules: AutoNumberRuleItem[];
}

export interface AutoNumberRuleItem {
  id?: string;
  itemType: 'SEQUENCE' | 'DATE' | 'TEXT' | 'FIELD_REF';
  itemOrder?: number;
  isEnabled?: number;
  format?: string;
  textValue?: string;
}

export interface AutoCodeRule {
  id?: string;
  itemType: 'SEQUENCE' | 'DATE' | 'TEXT' | 'FIELD_REF';
  config: Record<string, unknown>;
}

export interface AutoCodeRules {
  rules: AutoCodeRule[];
}
