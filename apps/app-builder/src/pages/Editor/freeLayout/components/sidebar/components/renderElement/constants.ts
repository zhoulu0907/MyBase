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
  SELECT_MULTIPLE = 'selectMultiple'
}



export enum DateOperator {
  EQUALS = 'EQUALS',
  LATER_THAN = 'LATER_THAN',
  LATER_RANGE = 'LATER_RANGE'
}

interface ComplexInfoItem {
  type: 'date' | 'dateRange' | 'select' | 'selectMultiple';
  options: DateOperator | Array<{ label: string; value: string }>;
}

interface ComplexInfo {
  DATETIME_EQUALS: ComplexInfoItem;
  DATETIME_LATER_THAN: ComplexInfoItem;
  DATETIME_RANGE: ComplexInfoItem;
  DATA_SELECTION_EQUALS: ComplexInfoItem;
  DATA_SELECTION_NOT_EQUALS: ComplexInfoItem;
  DATA_SELECTION_CONTAINS: ComplexInfoItem;
}
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
  [FieldType.TEXT]: [FieldType.TEXT, FieldType.LONG_TEXT, FieldType.EMAIL, FieldType.PHONE, FieldType.URL]
};

export const ComplexInfo: ComplexInfo = {
  DATETIME_EQUALS: {
    type: 'date',
    options: DateOperator.EQUALS
  },
  DATETIME_LATER_THAN: {
    type: 'date',
    options: DateOperator.LATER_THAN
  },
  DATETIME_RANGE: {
    type: 'dateRange',
    options: DateOperator.LATER_RANGE
  },
  DATA_SELECTION_EQUALS: {
    type: 'select',
    options: approvalResultOptions
  },
  DATA_SELECTION_NOT_EQUALS: {
    type: 'select',
    options: approvalResultOptions
  },
  DATA_SELECTION_CONTAINS: {
    type: 'selectMultiple',
    options: approvalResultOptions
  }
};
