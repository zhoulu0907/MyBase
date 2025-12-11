import { ENTITY_FIELD_TYPE } from '@onebase/ui-kit';

// 需要额外配置的字段类型
export const FIELD_TYPES_NEED_CONFIG = [
  ENTITY_FIELD_TYPE.SELECT.VALUE,
  ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE,
  ENTITY_FIELD_TYPE.AUTO_CODE.VALUE,
  ENTITY_FIELD_TYPE.DATA_SELECTION.VALUE
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

// 自动编号SEQUENCE类型默认配置
export const AUTO_CODE_SEQUENCE_DEFAULT_CONFIG = {
  isEnabled: CONSTANTS.ENABLED,
  numberMode: AUTO_CODE_NUMBER_MODE.FIXED_DIGITS,
  digitWidth: DIGIT_DEFAULT,
  overflowContinue: CONSTANTS.DISABLED,
  resetOnInitialChange: CONSTANTS.DISABLED,
  initialValue: START_VALUE_DEFAULT,
  resetCycle: AUTO_CODE_RESET_CYCLE.NONE
} as const;

// 自动编号初始规则
export const AUTO_CODE_INITIAL_RULES = [
  {
    id: 'rule-1',
    itemType: AUTO_CODE_RULE_TYPE.SEQUENCE,
    ...AUTO_CODE_SEQUENCE_DEFAULT_CONFIG
  }
];

// 日期格式值
export const DATE_FORMAT_VALUES = {
  YEAR_MONTH_DAY: '年月日',
  YEAR_MONTH: '年月',
  YEAR: '年',
  YEAR_MONTH_DAY_TIME: '年月日时分',
  YEAR_MONTH_DAY_TIME_SECOND: '年月日时分秒',
  CUSTOM: '自定义'
} as const;

// 日期格式选项
export const DATE_FORMAT_OPTIONS = Object.values(DATE_FORMAT_VALUES).map((value) => ({
  label: value,
  value: value
})) as { label: string; value: string }[];
