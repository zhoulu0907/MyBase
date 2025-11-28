import { FieldType, approvalResultOptions } from '../../constants';

export enum OperatorType {
  VALUE = 'value',
  VARIABLES = 'variables',
  FORMULA = 'formula'
}

export enum ElementType {
  INPUT = 'input',
  NUMBER = 'number',
  SCOPE = 'scope',
  DATE = 'date',
  DATE_RANGE = 'dateRange',
  SELECT = 'select',
  SELECT_MULTIPLE = 'selectMultiple',
  QUERY_SELECT1_ONE = 'querySelect1One',
  QUERY_SELECT1_MORE = 'querySelect1More',
  QUERY_SELECT2_MORE = 'querySelect2More',
  USER_EQUALS = 'userSelect',
  USER_CONTAINS = 'userContainsSelect',
  MULTI_USER_CONTAINS = 'multiUserContainSelect',
  DEPARTMENT_EQUALS = 'departmentSelect',
  DEPARTMENT_CONTAINS = 'departmentMoreSelect',
  MULTI_DEPARTMENT_EQUALS = 'multiDepartmentMoreSelect',
  MULTI_DATA_SELECTION_EQUALS = 'multiDataMoreSelect'
}

export enum DateOperator {
  EQUALS = 'EQUALS',
  LATER_THAN = 'LATER_THAN',
  LATER_RANGE = 'LATER_RANGE',
  DATETIME_EARLIER_THAN = 'DATETIME_EARLIER_THAN',
  DATE_EQUALS = 'DATE_EQUALS',
  DATE_LATER_THAN = 'DATE_LATER_THAN',
  DATE_EARLIER_THAN = 'DATE_EARLIER_THAN',
  DATE_RANGE = 'DATE_RANGE',
}

interface ComplexInfoItem {
  type: ElementType;
  options: DateOperator | Array<{ label: string; value: string }>;
}

type ComplexInfo = Record<
  | 'DATETIME_EQUALS'
  | 'DATETIME_LATER_THAN'
  | 'DATETIME_RANGE'
  | 'DATETIME_EARLIER_THAN'
  | 'DATA_SELECTION_EQUALS'
  | 'DATA_SELECTION_NOT_EQUALS'
  | 'DATA_SELECTION_CONTAINS'
  | 'DATE_EQUALS'
  | 'DATE_LATER_THAN'
  | 'DATE_EARLIER_THAN'
  | 'DATE_RANGE'
  | 'SELECT_EQUALS'
  | 'SELECT_NOT_EQUALS'
  | 'SELECT_CONTAINS'
  | 'SELECT_NOT_CONTAINS'
  | 'SELECT_EXISTS_IN'
  | 'SELECT_NOT_EXISTS_IN'
  | 'MULTI_SELECT_EQUALS'
  | 'MULTI_CONTAINS_ALL'
  | 'MULTI_NOT_CONTAINS_ALL'
  | 'MULTI_CONTAINS_ANY'
  | 'MULTI_NOT_CONTAINS_ANY'

  | 'USER_EQUALS'
  | 'USER_NOT_EQUALS'
  | 'USER_CONTAINS'
  | 'USER_NOT_CONTAINS'
  | 'USER_EXISTS_IN'
  | 'USER_NOT_EXISTS_IN'
  | 'MULTI_USER_EQUALS'
  | 'MULTI_USER_NOT_EQUALS'
  | 'MULTI_USER_CONTAINS'
  | 'MULTI_USER_NOT_CONTAINS'
  | 'MULTI_USER_EXISTS_IN'
  | 'MULTI_USER_NOT_EXISTS_IN'
  | 'DEPARTMENT_EQUALS'
  | 'DEPARTMENT_NOT_EQUALS'
  | 'DEPARTMENT_CONTAINS'
  | 'DEPARTMENT_NOT_CONTAINS'
  | 'DEPARTMENT_EXISTS_IN'
  | 'DEPARTMENT_NOT_EXISTS_IN'
  | 'MULTI_DEPARTMENT_EQUALS'
  | 'MULTI_DEPARTMENT_NOT_EQUALS'
  | 'MULTI_DEPARTMENT_CONTAINS'
  | 'MULTI_DEPARTMENT_NOT_CONTAINS'
  | 'MULTI_DEPARTMENT_EXISTS_IN'
  | 'MULTI_DEPARTMENT_NOT_EXISTS_IN'
  | 'DATA_SELECTION_EQUALS'
  | 'DATA_SELECTION_NOT_EQUALS'
  | 'DATA_SELECTION_CONTAINS'
  | 'DATA_SELECTION_NOT_CONTAINS'
  | 'DATA_SELECTION_EXISTS_IN'
  | 'DATA_SELECTION_NOT_EXISTS_IN'
  | 'MULTI_DATA_SELECTION_EQUALS'
  | 'MULTI_DATA_SELECTION_NOT_EQUALS'
  | 'MULTI_DATA_SELECTION_CONTAINS'
  | 'MULTI_DATA_SELECTION_NOT_CONTAINS'
  | 'MULTI_DATA_SELECTION_EXISTS_IN'
  | 'MULTI_DATA_SELECTION_NOT_EXISTS_IN'
  
  ,
  ComplexInfoItem
>;

export enum InputKeyType {
  TEXT_EQUALS = 'TEXT_EQUALS',
  TEXT_NOT_EQUALS = 'TEXT_NOT_EQUALS',
  TEXT_CONTAINS = 'TEXT_CONTAINS',
  TEXT_NOT_CONTAINS = 'TEXT_NOT_CONTAINS',
  TEXT_EXISTS_IN = 'TEXT_EXISTS_IN',
  TEXT_NOT_EXISTS_IN = 'TEXT_NOT_EXISTS_IN',
  LONG_TEXT_EQUALS = 'LONG_TEXT_EQUALS',
  LONG_TEXT_NOT_EQUALS = 'LONG_TEXT_NOT_EQUALS',
  LONG_TEXT_CONTAINS = 'LONG_TEXT_CONTAINS',
  LONG_TEXT_NOT_CONTAINS = 'LONG_TEXT_NOT_CONTAINS',
  EMAIL_EQUALS = 'EMAIL_EQUALS',
  EMAIL_NOT_EQUALS = 'EMAIL_NOT_EQUALS',
  EMAIL_CONTAINS = 'EMAIL_CONTAINS',
  EMAIL_NOT_CONTAINS = 'EMAIL_NOT_CONTAINS',
  EMAIL_EXISTS_IN = 'EMAIL_EXISTS_IN',
  EMAIL_NOT_EXISTS_IN = 'EMAIL_NOT_EXISTS_IN',
  PHONE_EQUALS = 'PHONE_EQUALS',
  PHONE_NOT_EQUALS = 'PHONE_NOT_EQUALS',
  PHONE_CONTAINS = 'PHONE_CONTAINS',
  PHONE_NOT_CONTAINS = 'PHONE_NOT_CONTAINS',
  PHONE_EXISTS_IN = 'PHONE_EXISTS_IN',
  PHONE_NOT_EXISTS_IN = 'PHONE_NOT_EXISTS_IN',
  URL_EQUALS = 'URL_EQUALS',
  URL_NOT_EQUALS = 'URL_NOT_EQUALS',
  URL_CONTAINS = 'URL_CONTAINS',
  URL_NOT_CONTAINS = 'URL_NOT_CONTAINS',
  ADDRESS_EQUALS = 'ADDRESS_EQUALS',
  ADDRESS_NOT_EQUALS = 'ADDRESS_NOT_EQUALS',
  ADDRESS_CONTAINS = 'ADDRESS_CONTAINS',
  ADDRESS_NOT_CONTAINS = 'ADDRESS_NOT_CONTAINS',
  AUTO_CODE_EQUALS = 'AUTO_CODE_EQUALS',
  AUTO_CODE_NOT_EQUALS = 'AUTO_CODE_NOT_EQUALS',
  AUTO_CODE_CONTAINS = 'AUTO_CODE_CONTAINS',
  AUTO_CODE_NOT_CONTAINS = 'AUTO_CODE_NOT_CONTAINS',
  AUTO_CODE_EXISTS_IN = 'AUTO_CODE_EXISTS_IN',
  AUTO_CODE_NOT_EXISTS_IN = 'AUTO_CODE_NOT_EXISTS_IN'
}

export enum NumberKeyType {
  XXX = 'XXX'
} // 数字输入框

export enum ScopeKeyType {
  XXXX = 'XXXX'
} // 数字范围

export const VARIABLE_MAP: Partial<Record<FieldType, FieldType[]>> = {
  [FieldType.TEXT]: [FieldType.TEXT, FieldType.LONG_TEXT, FieldType.EMAIL, FieldType.PHONE, FieldType.URL],
  [FieldType.LONG_TEXT]: [FieldType.TEXT, FieldType.LONG_TEXT, FieldType.EMAIL, FieldType.PHONE, FieldType.URL],
  [FieldType.EMAIL]: [FieldType.TEXT, FieldType.LONG_TEXT, FieldType.EMAIL],
  [FieldType.PHONE]: [FieldType.TEXT, FieldType.LONG_TEXT, FieldType.PHONE],
  [FieldType.URL]: [FieldType.TEXT, FieldType.LONG_TEXT, FieldType.URL],
  [FieldType.ADDRESS]: [FieldType.ADDRESS],
  [FieldType.BOOLEAN]: [FieldType.BOOLEAN],
  [FieldType.SELECT]: [FieldType.SELECT, FieldType.MULTI_SELECT, FieldType.TEXT, FieldType.LONG_TEXT],
  [FieldType.MULTI_SELECT]: [FieldType.SELECT, FieldType.MULTI_SELECT, FieldType.TEXT, FieldType.LONG_TEXT],
  [FieldType.AUTO_CODE]: [FieldType.AUTO_CODE, FieldType.TEXT, FieldType.LONG_TEXT],
  [FieldType.MULTI_USER]: [FieldType.USER, FieldType.MULTI_USER],
  [FieldType.MULTI_DEPARTMENT]: [FieldType.DEPARTMENT, FieldType.MULTI_DEPARTMENT],
  [FieldType.MULTI_DATA_SELECTION]: [FieldType.DATA_SELECTION, FieldType.MULTI_DATA_SELECTION],
  [FieldType.GEOGRAPHY]: [FieldType.GEOGRAPHY],
  [FieldType.ID]: [FieldType.ID, FieldType.NUMBER],
};

export const ComplexInfo: ComplexInfo = {
  DATETIME_EQUALS: {
    type: ElementType.DATE,
    options: DateOperator.EQUALS
  },
  DATETIME_LATER_THAN: {
    type: ElementType.DATE,
    options: DateOperator.LATER_THAN
  },
  DATETIME_RANGE: {
    type: ElementType.DATE_RANGE,
    options: DateOperator.LATER_RANGE
  },
  DATETIME_EARLIER_THAN: {
    type: ElementType.DATE,
    options: DateOperator.DATETIME_EARLIER_THAN
  },
  DATA_SELECTION_EQUALS: {
    type: ElementType.SELECT,
    options: approvalResultOptions
  },
  DATA_SELECTION_NOT_EQUALS: {
    type: ElementType.SELECT,
    options: approvalResultOptions
  },
  DATA_SELECTION_CONTAINS: {
    type: ElementType.SELECT_MULTIPLE,
    options: approvalResultOptions
  },
  DATE_EQUALS: {
    type: ElementType.DATE,
    options: DateOperator.DATE_EQUALS
  },
  DATE_LATER_THAN: {
    type: ElementType.DATE,
    options: DateOperator.DATE_LATER_THAN
  },
  DATE_EARLIER_THAN: {
    type: ElementType.DATE,
    options: DateOperator.DATE_EARLIER_THAN
  },
  DATE_RANGE: {
    type: ElementType.DATE,
    options: DateOperator.DATE_RANGE
  },
  SELECT_EQUALS: {
    type: ElementType.QUERY_SELECT1_ONE,
    options: []
  },
  SELECT_NOT_EQUALS: {
    type: ElementType.QUERY_SELECT1_ONE,
    options: []
  },
  SELECT_CONTAINS: {
    type: ElementType.QUERY_SELECT1_MORE,
    options: []
  },
  SELECT_NOT_CONTAINS: {
    type: ElementType.QUERY_SELECT1_MORE,
    options: []
  },
  SELECT_EXISTS_IN: {
    type: ElementType.QUERY_SELECT1_MORE,
    options: []
  },
  SELECT_NOT_EXISTS_IN: {
    type: ElementType.QUERY_SELECT1_MORE,
    options: []
  },
  MULTI_SELECT_EQUALS: {
    type: ElementType.QUERY_SELECT2_MORE,
    options: []
  },
  MULTI_CONTAINS_ALL: {
    type: ElementType.QUERY_SELECT2_MORE,
    options: []
  },
  MULTI_NOT_CONTAINS_ALL: {
    type: ElementType.QUERY_SELECT2_MORE,
    options: []
  },
  MULTI_CONTAINS_ANY: {
    type: ElementType.QUERY_SELECT2_MORE,
    options: []
  },
  MULTI_NOT_CONTAINS_ANY: {
    type: ElementType.QUERY_SELECT2_MORE,
    options: []
  },
  // user 1
  USER_EQUALS: {
    type: ElementType.USER_EQUALS,
    options: []
  },
  USER_NOT_EQUALS: {
    type: ElementType.USER_EQUALS,
    options: []
  },
  USER_CONTAINS: {
    type: ElementType.USER_CONTAINS,
    options: []
  },
  USER_NOT_CONTAINS: {
    type: ElementType.USER_CONTAINS,
    options: []
  },
  USER_EXISTS_IN: {
    type: ElementType.USER_CONTAINS,
    options: []
  },
  USER_NOT_EXISTS_IN: {
    type: ElementType.USER_CONTAINS,
    options: []
  },
  // user 2
  MULTI_USER_EQUALS: {
    type: ElementType.MULTI_USER_CONTAINS,
    options: []
  },
  MULTI_USER_NOT_EQUALS: {
    type: ElementType.MULTI_USER_CONTAINS,
    options: []
  },
  MULTI_USER_CONTAINS: {
    type: ElementType.MULTI_USER_CONTAINS,
    options: []
  },
  MULTI_USER_NOT_CONTAINS: {
    type: ElementType.MULTI_USER_CONTAINS,
    options: []
  },
  MULTI_USER_EXISTS_IN: {
    type: ElementType.MULTI_USER_CONTAINS,
    options: []
  },
  MULTI_USER_NOT_EXISTS_IN: {
    type: ElementType.MULTI_USER_CONTAINS,
    options: []
  },
  // user 3
  DEPARTMENT_EQUALS: {
    type: ElementType.DEPARTMENT_EQUALS,
    options: []
  },
  DEPARTMENT_NOT_EQUALS: {
    type: ElementType.DEPARTMENT_EQUALS,
    options: []
  },
  DEPARTMENT_CONTAINS: {
    type: ElementType.DEPARTMENT_CONTAINS,
    options: []
  },
  DEPARTMENT_NOT_CONTAINS: {
    type: ElementType.DEPARTMENT_CONTAINS,
    options: []
  },
  DEPARTMENT_EXISTS_IN: {
    type: ElementType.DEPARTMENT_CONTAINS,
    options: []
  },
  DEPARTMENT_NOT_EXISTS_IN: {
    type: ElementType.DEPARTMENT_CONTAINS,
    options: []
  },
  // user 4
  MULTI_DEPARTMENT_EQUALS: {
    type: ElementType.MULTI_DEPARTMENT_EQUALS,
    options: []
  },
  MULTI_DEPARTMENT_NOT_EQUALS: {
    type: ElementType.MULTI_DEPARTMENT_EQUALS,
    options: []
  },
  MULTI_DEPARTMENT_CONTAINS: {
    type: ElementType.MULTI_DEPARTMENT_EQUALS,
    options: []
  },
  MULTI_DEPARTMENT_NOT_CONTAINS: {
    type: ElementType.MULTI_DEPARTMENT_EQUALS,
    options: []
  },
  MULTI_DEPARTMENT_EXISTS_IN: {
    type: ElementType.MULTI_DEPARTMENT_EQUALS,
    options: []
  },
  MULTI_DEPARTMENT_NOT_EXISTS_IN: {
    type: ElementType.MULTI_DEPARTMENT_EQUALS,
    options: []
  },
  // user 5
  // DATA_SELECTION_EQUALS: {
  //   type: ElementType.SELECT_MULTIPLE,
  //   options: []
  // },
  // DATA_SELECTION_NOT_EQUALS: {
  //   type: ElementType.SELECT_MULTIPLE,
  //   options: []
  // },
  // DATA_SELECTION_CONTAINS: {
  //   type: ElementType.SELECT_MULTIPLE,
  //   options: []
  // },
  DATA_SELECTION_NOT_CONTAINS: {
    type: ElementType.SELECT_MULTIPLE,
    options: []
  },
  DATA_SELECTION_EXISTS_IN: {
    type: ElementType.SELECT_MULTIPLE,
    options: []
  },
  DATA_SELECTION_NOT_EXISTS_IN: {
    type: ElementType.SELECT_MULTIPLE,
    options: []
  },
  // user 6
  MULTI_DATA_SELECTION_EQUALS: {
    type: ElementType.MULTI_DATA_SELECTION_EQUALS,
    options: []
  },
  MULTI_DATA_SELECTION_NOT_EQUALS: {
    type: ElementType.MULTI_DATA_SELECTION_EQUALS,
    options: []
  },
  MULTI_DATA_SELECTION_CONTAINS: {
    type: ElementType.MULTI_DATA_SELECTION_EQUALS,
    options: []
  },
  MULTI_DATA_SELECTION_NOT_CONTAINS: {
    type: ElementType.MULTI_DATA_SELECTION_EQUALS,
    options: []
  },
  MULTI_DATA_SELECTION_EXISTS_IN: {
    type: ElementType.MULTI_DATA_SELECTION_EQUALS,
    options: []
  },
  MULTI_DATA_SELECTION_NOT_EXISTS_IN: {
    type: ElementType.MULTI_DATA_SELECTION_EQUALS,
    options: []
  }
};
