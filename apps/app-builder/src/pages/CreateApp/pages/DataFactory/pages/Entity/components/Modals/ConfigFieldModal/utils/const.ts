import { ENTITY_FIELD_TYPE } from '@onebase/ui-kit';

// 需要额外配置的字段类型
export const FIELD_TYPES_NEED_CONFIG = [
  ENTITY_FIELD_TYPE.SELECT.VALUE,
  ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE,
  ENTITY_FIELD_TYPE.AUTO_CODE.VALUE
];

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
export const START_VALUE_DEFAULT = 1;
export const DATE_FORMAT_DEFAULT = '年月日';
export const CHECK_CONST = { IS_TRUE: 1, IS_FALSE: 0 };
export const CONSTANTS = {
  ENABLED: 1,
  DISABLED: 0
} as const;

// 自动编号默认配置
export const AUTO_CODE_NUMBER_DEFAULT_CONFIG = {
  isEnabled: CONSTANTS.ENABLED,
  numberMode: AUTO_CODE_NUMBER_MODE.FIXED_DIGITS,
  digitWidth: DIGIT_DEFAULT,
  overflowContinue: CONSTANTS.DISABLED,
  resetOnInitialChange: CONSTANTS.DISABLED,
  initialValue: START_VALUE_DEFAULT,
  resetCycle: AUTO_CODE_RESET_CYCLE.NONE
} as const;
