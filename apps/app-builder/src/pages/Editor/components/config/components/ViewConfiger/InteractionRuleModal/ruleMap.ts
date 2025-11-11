import { FORM_COMPONENT_TYPES } from '@onebase/ui-kit';

// 枚举所有操作符，涵盖常见的字段判断操作
export const OPERATOR_OPTIONS_EQUAL = [
  {
    label: '等于',
    value: 'equal'
  },
  {
    label: '不等于',
    value: 'not_equal'
  }
];
export const OPERATOR_OPTIONS_CONTAINS = [
  {
    label: '包含',
    value: 'contains'
  },
  {
    label: '不包含',
    value: 'not_contains'
  }
];
export const OPERATOR_OPTIONS_IN = [
  {
    label: '存在于',
    value: 'in'
  },
  {
    label: '不存在于',
    value: 'not_in'
  }
];
export const OPERATOR_OPTIONS_NULL = [
  {
    label: '为空',
    value: 'is_null'
  },
  {
    label: '不为空',
    value: 'is_not_null'
  }
];

export const OPERATOR_OPTIONS_NUMBER_COMPARE = [
  {
    label: '大于',
    value: 'greater_than'
  },
  {
    label: '大于等于',
    value: 'greater_than_or_equal'
  },
  {
    label: '小于',
    value: 'less_than'
  },
  {
    label: '小于等于',
    value: 'less_than_or_equal'
  }
];

export const OPERATOR_OPTIONS_RANGE = [
  {
    label: '范围',
    value: 'range'
  }
];

export const OPERATOR_OPTIONS_DATE_TIME_COMPARE = [
  {
    label: '早于',
    value: 'before'
  },
  {
    label: '晚于',
    value: 'after'
  }
];

export const OPERATOR_OPTIONS_MULTI_SELECT = [
  {
    label: '等于',
    value: 'equal'
  },
  {
    label: '包含全部',
    value: 'contains_all'
  },
  {
    label: '不包含全部',
    value: 'not_contains_all'
  },
  {
    label: '包含任一',
    value: 'contains_any'
  },
  {
    label: '不包含任一',
    value: 'not_contains_any'
  },
  ...OPERATOR_OPTIONS_NULL
];

export const OPERATOR_OPTIONS_COMMON = [
  ...OPERATOR_OPTIONS_EQUAL,
  ...OPERATOR_OPTIONS_CONTAINS,
  ...OPERATOR_OPTIONS_IN,
  ...OPERATOR_OPTIONS_NULL
];

export const getOperatorOptions = (cpType: string): { label: string; value: string }[] => {
  switch (cpType) {
    case FORM_COMPONENT_TYPES.INPUT_TEXT:
    case FORM_COMPONENT_TYPES.INPUT_TEXTAREA:
    case FORM_COMPONENT_TYPES.INPUT_EMAIL:
    case FORM_COMPONENT_TYPES.INPUT_PHONE:
    case FORM_COMPONENT_TYPES.SELECT_ONE:
    case FORM_COMPONENT_TYPES.AUTO_CODE:
    case FORM_COMPONENT_TYPES.USER_SELECT:
    case FORM_COMPONENT_TYPES.DATA_SELECT:
    case FORM_COMPONENT_TYPES.DEPT_SELECT:
      return OPERATOR_OPTIONS_COMMON;
    case FORM_COMPONENT_TYPES.INPUT_NUMBER:
      return [
        ...OPERATOR_OPTIONS_NUMBER_COMPARE,
        ...OPERATOR_OPTIONS_EQUAL,
        ...OPERATOR_OPTIONS_NULL,
        ...OPERATOR_OPTIONS_RANGE
      ];
    case FORM_COMPONENT_TYPES.DATE_PICKER:
    case FORM_COMPONENT_TYPES.DATE_TIME_PICKER:
    case FORM_COMPONENT_TYPES.DATE_RANGE_PICKER:
      return [
        ...OPERATOR_OPTIONS_DATE_TIME_COMPARE,
        ...OPERATOR_OPTIONS_EQUAL,
        ...OPERATOR_OPTIONS_NULL,
        ...OPERATOR_OPTIONS_RANGE
      ];
    case FORM_COMPONENT_TYPES.SWITCH:
      return OPERATOR_OPTIONS_EQUAL;
    case FORM_COMPONENT_TYPES.SELECT_MUTIPLE:
      return OPERATOR_OPTIONS_MULTI_SELECT;
    default:
      return OPERATOR_OPTIONS_COMMON;
  }
};
