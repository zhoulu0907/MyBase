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
  LIST_SELECT = 'listSelect',
  LIST_SELECT_MULTIPLE = 'listSelectMultiple',
  USER_SELECT = 'userSelect',
  USER_SELECT_MULTIPLE = 'userSelectMultiple',
  DEPARTMENT_SELECT = 'departmentSelect',
  DEPARTMENT_CONTAINS = 'departmentMoreSelect',
  DEPARTMENT_SELECT_MULTIPLE = 'departmentSelectMultiple'
}

export enum DataOperator {
  EQUALS = 'EQUALS',
  LATER_THAN = 'LATER_THAN',
  LATER_RANGE = 'LATER_RANGE',
  DATETIME_EARLIER_THAN = 'DATETIME_EARLIER_THAN',
  DATE_EQUALS = 'DATE_EQUALS',
  DATE_LATER_THAN = 'DATE_LATER_THAN',
  DATE_EARLIER_THAN = 'DATE_EARLIER_THAN',
  DATE_RANGE = 'DATE_RANGE'
}

interface ComplexInfoItem {
  type: ElementType;
  options: DataOperator | Array<{ label: string; value: string | boolean }>;
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
  | 'DATA_SELECTION_RESULT_EQUALS'
  | 'DATA_SELECTION_RESULT_NOT_EQUALS'
  | 'DATA_SELECTION_RESULT_CONTAINS'
  | 'DATA_SELECTION_RESULT_NOT_CONTAINS'
  | 'DATA_SELECTION_RESULT_EXISTS_IN'
  | 'DATA_SELECTION_RESULT_NOT_EXISTS_IN'
  | 'BOOLEAN_EQUALS',
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
  NUMBER_EQUALS = 'NUMBER_EQUALS',
  NUMBER_NOT_EQUALS = 'NUMBER_NOT_EQUALS',
  NUMBER_GREATER_THAN = 'NUMBER_GREATER_THAN',
  NUMBER_GREATER_EQUALS = 'NUMBER_GREATER_EQUALS',
  NUMBER_LESS_THAN = 'NUMBER_LESS_THAN',
  NUMBER_LESS_EQUALS = 'NUMBER_LESS_EQUALS',
  AGGREGATE_EQUALS = 'AGGREGATE_EQUALS',
  AGGREGATE_NOT_EQUALS = 'AGGREGATE_NOT_EQUALS',
  AGGREGATE_GREATER_THAN = 'AGGREGATE_GREATER_THAN',
  AGGREGATE_GREATER_EQUALS = 'AGGREGATE_GREATER_EQUALS',
  AGGREGATE_LESS_THAN = 'AGGREGATE_LESS_THAN',
  AGGREGATE_LESS_EQUALS = 'AGGREGATE_LESS_EQUALS',
  ID_EQUALS = 'ID_EQUALS',
  ID_NOT_EQUALS = 'ID_NOT_EQUALS'
} // 数字输入框

export enum ScopeKeyType {
  AGGREGATE_RANGE = 'AGGREGATE_RANGE'
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
  [FieldType.NUMBER]: [FieldType.NUMBER, FieldType.AGGREGATE],
  [FieldType.DATE]: [FieldType.DATE],
  [FieldType.DATETIME]: [FieldType.DATETIME],
  [FieldType.USER]: [FieldType.USER]
};

export const ComplexInfo: ComplexInfo = {
  // 日期时间_等于
  DATETIME_EQUALS: {
    type: ElementType.DATE,
    options: DataOperator.EQUALS
  },
  // 日期时间_晚于
  DATETIME_LATER_THAN: {
    type: ElementType.DATE,
    options: DataOperator.LATER_THAN
  },
  // 日期时间_范围
  DATETIME_RANGE: {
    type: ElementType.DATE_RANGE,
    options: DataOperator.LATER_RANGE
  },
  // 日期时间_早于
  DATETIME_EARLIER_THAN: {
    type: ElementType.DATE,
    options: DataOperator.DATETIME_EARLIER_THAN
  },
  // 日期_等于
  DATE_EQUALS: {
    type: ElementType.DATE,
    options: DataOperator.DATE_EQUALS
  },
  // 日期_晚于
  DATE_LATER_THAN: {
    type: ElementType.DATE,
    options: DataOperator.DATE_LATER_THAN
  },
  // 日期_早于
  DATE_EARLIER_THAN: {
    type: ElementType.DATE,
    options: DataOperator.DATE_EARLIER_THAN
  },
  // 日期_范围
  DATE_RANGE: {
    type: ElementType.DATE_RANGE,
    options: DataOperator.DATE_RANGE
  },

  // 审批结果_等于
  DATA_SELECTION_RESULT_EQUALS: {
    type: ElementType.SELECT,
    options: approvalResultOptions
  },
  // 审批结果_不等于
  DATA_SELECTION_RESULT_NOT_EQUALS: {
    type: ElementType.SELECT,
    options: approvalResultOptions
  },
  // 审批结果_包含
  DATA_SELECTION_RESULT_CONTAINS: {
    type: ElementType.SELECT_MULTIPLE,
    options: approvalResultOptions
  },
  // 审批结果_不包含
  DATA_SELECTION_RESULT_NOT_CONTAINS: {
    type: ElementType.SELECT_MULTIPLE,
    options: approvalResultOptions
  },

  // 审批结果_存在
  DATA_SELECTION_RESULT_EXISTS_IN: {
    type: ElementType.SELECT_MULTIPLE,
    options: approvalResultOptions
  },
  // 审批结果_不存在
  DATA_SELECTION_RESULT_NOT_EXISTS_IN: {
    type: ElementType.SELECT_MULTIPLE,
    options: approvalResultOptions
  },

  // 数据单选_等于
  DATA_SELECTION_EQUALS: {
    type: ElementType.SELECT,
    options: []
  },
  // 数据单选_不等于
  DATA_SELECTION_NOT_EQUALS: {
    type: ElementType.SELECT,
    options: []
  },
  // 数据单选_包含
  DATA_SELECTION_CONTAINS: {
    type: ElementType.SELECT_MULTIPLE,
    options: []
  },
  // 数据单选_不包含
  DATA_SELECTION_NOT_CONTAINS: {
    type: ElementType.SELECT_MULTIPLE,
    options: []
  },
  // 数据单选_存在
  DATA_SELECTION_EXISTS_IN: {
    type: ElementType.SELECT_MULTIPLE,
    options: []
  },
  // 数据单选_不存在
  DATA_SELECTION_NOT_EXISTS_IN: {
    type: ElementType.SELECT_MULTIPLE,
    options: []
  },

  // 单选列表_等于
  SELECT_EQUALS: {
    type: ElementType.LIST_SELECT,
    options: []
  },
  // 单选列表_不等于
  SELECT_NOT_EQUALS: {
    type: ElementType.LIST_SELECT,
    options: []
  },
  // 单选列表_包含
  SELECT_CONTAINS: {
    type: ElementType.LIST_SELECT_MULTIPLE,
    options: []
  },
  // 单选列表_不包含
  SELECT_NOT_CONTAINS: {
    type: ElementType.LIST_SELECT_MULTIPLE,
    options: []
  },
  // 单选列表_存在
  SELECT_EXISTS_IN: {
    type: ElementType.LIST_SELECT_MULTIPLE,
    options: []
  },
  // 单选列表_不存在
  SELECT_NOT_EXISTS_IN: {
    type: ElementType.LIST_SELECT_MULTIPLE,
    options: []
  },

  // 多选列表_等于
  MULTI_SELECT_EQUALS: {
    type: ElementType.LIST_SELECT_MULTIPLE,
    options: []
  },
  // 多选列表_包含全部
  MULTI_CONTAINS_ALL: {
    type: ElementType.LIST_SELECT_MULTIPLE,
    options: []
  },
  // 多选列表_不包含全部
  MULTI_NOT_CONTAINS_ALL: {
    type: ElementType.LIST_SELECT_MULTIPLE,
    options: []
  },
  // 多选列表_包含任意
  MULTI_CONTAINS_ANY: {
    type: ElementType.LIST_SELECT_MULTIPLE,
    options: []
  },
  // 多选列表_不包含任意
  MULTI_NOT_CONTAINS_ANY: {
    type: ElementType.LIST_SELECT_MULTIPLE,
    options: []
  },

  // 用户单选_等于
  USER_EQUALS: {
    type: ElementType.USER_SELECT,
    options: []
  },
  // 用户单选_不等于
  USER_NOT_EQUALS: {
    type: ElementType.USER_SELECT,
    options: []
  },
  // 用户单选_包含
  USER_CONTAINS: {
    type: ElementType.USER_SELECT_MULTIPLE,
    options: []
  },
  // 用户单选_不包含
  USER_NOT_CONTAINS: {
    type: ElementType.USER_SELECT_MULTIPLE,
    options: []
  },
  // 用户单选_存在
  USER_EXISTS_IN: {
    type: ElementType.USER_SELECT_MULTIPLE,
    options: []
  },
  // 用户单选_不存在
  USER_NOT_EXISTS_IN: {
    type: ElementType.USER_SELECT_MULTIPLE,
    options: []
  },
  // 用户多选_等于
  MULTI_USER_EQUALS: {
    type: ElementType.USER_SELECT_MULTIPLE,
    options: []
  },
  // 用户多选_不等于
  MULTI_USER_NOT_EQUALS: {
    type: ElementType.USER_SELECT_MULTIPLE,
    options: []
  },
  // 用户多选_包含
  MULTI_USER_CONTAINS: {
    type: ElementType.USER_SELECT_MULTIPLE,
    options: []
  },
  // 用户多选_不包含
  MULTI_USER_NOT_CONTAINS: {
    type: ElementType.USER_SELECT_MULTIPLE,
    options: []
  },
  // 用户多选_存在
  MULTI_USER_EXISTS_IN: {
    type: ElementType.USER_SELECT_MULTIPLE,
    options: []
  },
  // 用户多选_不存在
  MULTI_USER_NOT_EXISTS_IN: {
    type: ElementType.USER_SELECT_MULTIPLE,
    options: []
  },

  // 部门单选_等于
  DEPARTMENT_EQUALS: {
    type: ElementType.DEPARTMENT_SELECT,
    options: []
  },
  // 部门单选_不等于
  DEPARTMENT_NOT_EQUALS: {
    type: ElementType.DEPARTMENT_SELECT,
    options: []
  },
  // 部门单选_包含
  DEPARTMENT_CONTAINS: {
    type: ElementType.DEPARTMENT_SELECT_MULTIPLE,
    options: []
  },
  // 部门单选_不包含
  DEPARTMENT_NOT_CONTAINS: {
    type: ElementType.DEPARTMENT_SELECT_MULTIPLE,
    options: []
  },
  // 部门单选_存在
  DEPARTMENT_EXISTS_IN: {
    type: ElementType.DEPARTMENT_SELECT_MULTIPLE,
    options: []
  },
  // 部门单选_不存在
  DEPARTMENT_NOT_EXISTS_IN: {
    type: ElementType.DEPARTMENT_SELECT_MULTIPLE,
    options: []
  },
  // 部门多选_等于
  MULTI_DEPARTMENT_EQUALS: {
    type: ElementType.DEPARTMENT_SELECT_MULTIPLE,
    options: []
  },
  // 部门多选_不等于
  MULTI_DEPARTMENT_NOT_EQUALS: {
    type: ElementType.DEPARTMENT_SELECT_MULTIPLE,
    options: []
  },
  // 部门多选_包含
  MULTI_DEPARTMENT_CONTAINS: {
    type: ElementType.DEPARTMENT_SELECT_MULTIPLE,
    options: []
  },
  // 部门多选_不包含
  MULTI_DEPARTMENT_NOT_CONTAINS: {
    type: ElementType.DEPARTMENT_SELECT_MULTIPLE,
    options: []
  },
  // 部门多选_存在
  MULTI_DEPARTMENT_EXISTS_IN: {
    type: ElementType.DEPARTMENT_SELECT_MULTIPLE,
    options: []
  },
  // 部门多选_不存在
  MULTI_DEPARTMENT_NOT_EXISTS_IN: {
    type: ElementType.DEPARTMENT_SELECT_MULTIPLE,
    options: []
  },

  // 数据多选_等于
  MULTI_DATA_SELECTION_EQUALS: {
    type: ElementType.SELECT_MULTIPLE,
    options: []
  },
  // 数据多选_不等于
  MULTI_DATA_SELECTION_NOT_EQUALS: {
    type: ElementType.SELECT_MULTIPLE,
    options: []
  },
  // 数据多选_包含
  MULTI_DATA_SELECTION_CONTAINS: {
    type: ElementType.SELECT_MULTIPLE,
    options: []
  },
  // 数据多选_不包含
  MULTI_DATA_SELECTION_NOT_CONTAINS: {
    type: ElementType.SELECT_MULTIPLE,
    options: []
  },
  // 数据多选_存在
  MULTI_DATA_SELECTION_EXISTS_IN: {
    type: ElementType.SELECT_MULTIPLE,
    options: []
  },
  // 数据多选_不存在
  MULTI_DATA_SELECTION_NOT_EXISTS_IN: {
    type: ElementType.SELECT_MULTIPLE,
    options: []
  },
  BOOLEAN_EQUALS: {
    type: ElementType.SELECT,
    options: [
      {
        value: true,
        label: '是'
      },
      {
        value: false,
        label: '否'
      }
    ]
  }
};
