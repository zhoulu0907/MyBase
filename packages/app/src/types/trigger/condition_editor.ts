export interface ConditionField {
  // 显示标签
  label: string;
  // 字段值
  value: string;
  // 字段类型
  fieldType: string;

  // 子表
  children?: ConditionField[];
}

export enum VALIDATION_TYPE {
  // 等于
  EQUALS = 'EQUALS',
  // 不等于
  NOT_EQUALS = 'NOT_EQUALS',

  // 大于
  GREATER_THAN = 'GREATER_THAN',
  // 大于等于
  GREATER_EQUALS = 'GREATER_EQUALS',

  // 小于
  LESS_THAN = 'LESS_THAN',
  // 小于等于
  LESS_EQUALS = 'LESS_EQUALS',

  // 为空
  IS_EMPTY = 'IS_EMPTY',
  // 不为空
  IS_NOT_EMPTY = 'IS_NOT_EMPTY',

  // 存在于
  EXISTS_IN = 'EXISTS_IN',
  // 不存在于
  NOT_EXISTS_IN = 'NOT_EXISTS_IN',

  // 晚于
  LATER_THAN = 'LATER_THAN',
  // 早于
  EARLIER_THAN = 'EARLIER_THAN',
  // 范围
  RANGE = 'RANGE',

  // 包含
  CONTAINS = 'CONTAINS',
  // 不包含
  NOT_CONTAINS = 'NOT_CONTAINS'
}

export const OPERATOR_MAP: Record<string, string> = {
  [VALIDATION_TYPE.EQUALS]: '==',
  [VALIDATION_TYPE.NOT_EQUALS]: '!=',
  [VALIDATION_TYPE.GREATER_THAN]: '>',
  [VALIDATION_TYPE.GREATER_EQUALS]: '>=',
  [VALIDATION_TYPE.LESS_THAN]: '<',
  [VALIDATION_TYPE.LESS_EQUALS]: '<=',
  [VALIDATION_TYPE.IS_EMPTY]: 'is empty',
  [VALIDATION_TYPE.IS_NOT_EMPTY]: 'is not empty'
};
