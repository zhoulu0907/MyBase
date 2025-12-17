import { VALIDATION_TYPE } from '@onebase/app';
import { FORM_COMPONENT_TYPES } from '@onebase/ui-kit';

// 枚举所有操作符，涵盖常见的字段判断操作
export const OPERATOR_OPTIONS_EQUAL = [
  {
    label: '等于',
    value: VALIDATION_TYPE.EQUALS
  },
  {
    label: '不等于',
    value: VALIDATION_TYPE.NOT_EQUALS
  }
];
export const OPERATOR_OPTIONS_CONTAINS = [
  {
    label: '包含',
    value: VALIDATION_TYPE.CONTAINS
  },
  {
    label: '不包含',
    value: VALIDATION_TYPE.NOT_CONTAINS
  }
];
export const OPERATOR_OPTIONS_IN = [
  {
    label: '存在于',
    value: VALIDATION_TYPE.EXISTS_IN
  },
  {
    label: '不存在于',
    value: VALIDATION_TYPE.NOT_EXISTS_IN
  }
];
export const OPERATOR_OPTIONS_NULL = [
  {
    label: '为空',
    value: VALIDATION_TYPE.IS_EMPTY
  },
  {
    label: '不为空',
    value: VALIDATION_TYPE.IS_NOT_EMPTY
  }
];

export const OPERATOR_OPTIONS_NUMBER_COMPARE = [
  {
    label: '大于',
    value: VALIDATION_TYPE.GREATER_THAN
  },
  {
    label: '大于等于',
    value: VALIDATION_TYPE.GREATER_EQUALS
  },
  {
    label: '小于',
    value: VALIDATION_TYPE.LESS_THAN
  },
  {
    label: '小于等于',
    value: VALIDATION_TYPE.LESS_EQUALS
  }
];

export const OPERATOR_OPTIONS_RANGE = [
  {
    label: '范围',
    value: VALIDATION_TYPE.RANGE
  }
];

export const OPERATOR_OPTIONS_DATE_TIME_COMPARE = [
  {
    label: '早于',
    value: VALIDATION_TYPE.EARLIER_THAN
  },
  {
    label: '晚于',
    value: VALIDATION_TYPE.LATER_THAN
  }
];

export const OPERATOR_OPTIONS_MULTI_SELECT = [
  {
    label: '等于',
    value: VALIDATION_TYPE.EQUALS
  },
  {
    label: '包含全部',
    value: VALIDATION_TYPE.CONTAINS
  },
  {
    label: '不包含全部',
    value: VALIDATION_TYPE.NOT_CONTAINS
  },
  {
    label: '包含任一',
    value: VALIDATION_TYPE.CONTAINS
  },
  {
    label: '不包含任一',
    value: VALIDATION_TYPE.NOT_CONTAINS
  },
  ...OPERATOR_OPTIONS_NULL
];

export const OPERATOR_OPTIONS_COMMON = [
  ...OPERATOR_OPTIONS_EQUAL,
  ...OPERATOR_OPTIONS_CONTAINS,
  ...OPERATOR_OPTIONS_IN,
  ...OPERATOR_OPTIONS_NULL
];

export const getOperatorOptions = (components: any[], cpId: string): { label: string; value: string }[] => {
  const cpType = components.find((item: any) => item?.id == cpId)?.type;

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
