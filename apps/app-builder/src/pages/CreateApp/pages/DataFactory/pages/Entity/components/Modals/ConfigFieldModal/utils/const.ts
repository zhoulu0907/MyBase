// 自动编号规则类型
export const AUTO_CODE_RULE_TYPE = {
  SEQUENCE: 'SEQUENCE',
  DATE: 'DATE',
  TEXT: 'TEXT',
  FIELD_REF: 'FIELD_REF'
} as const;

// 自动编号方式
export const AUTO_CODE_NUMBER_MODE = {
  NATURAL: 'NATURAL',
  FIXED_DIGITS: 'FIXED_DIGITS'
} as const;

// 自动编号重置周期
export const AUTO_CODE_RESET_CYCLE = {
  NONE: 'NONE',
  DAILY: 'DAILY',
  MONTHLY: 'MONTHLY',
  YEARLY: 'YEARLY'
} as const;

export const DIGIT_DEFAULT = 4;
export const DATE_FORMAT_DEFAULT = '年月日';

// TODO 待确认后补充
export const RULE_ENABLED = {
  ENABLE: 1,
  DISABLE: 0
} as const;
