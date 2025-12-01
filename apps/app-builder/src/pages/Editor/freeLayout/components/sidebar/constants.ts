/*********************
   *TEXT: '常规文本',  [等于
不等于
包含
不包含
存在于
不存在于
为空
不为空]
    LONG_TEXT: '长文本', [等于
不等于
包含
不包含
为空
不为空]
    EMAIL: '邮箱地址',[等于
不等于
包含
不包含
存在于
不存在于
为空
不为空]
    PHONE: '电话号码',[等于
不等于
包含
不包含
存在于
不存在于
为空
不为空]
    URL: '网址链接',[等于
不等于
包含
不包含
为空
不为空]
    ADDRESS: '详细地址',[等于
不等于
包含
不包含
为空
不为空]
    NUMBER: '通用数字',[等于
不等于
大于
大于等于
小于
小于等于
范围
为空
不为空]
    DATE: '日期',[等于
晚于
早于
范围
为空
不为空]
    DATETIME: '日期时间',[等于
晚于
早于
范围
为空
不为空]
    BOOLEAN: '布尔值',[等于]

    SELECT: '单选列表',[等于
包含全部
不包含全部
包含任一
不包含任一
为空
不为空]
    MULTI_SELECT: '多选列表',[等于
    包含全部
不包含全部
包含任一
不包含任一
为空
不为空]
    AUTO_CODE: '自动编码',[等于
不等于
包含
不包含
存在于
不存在于
为空
不为空]
    USER: '用户单选',[等于
不等于
包含
不包含
存在于
不存在于
为空
不为空]
    MULTI_USER: '用户多选',[等于
包含全部
不包含全部
包含任一
不包含任一
为空
不为空]
    DEPARTMENT: '部门单选',[等于
不等于
包含
不包含
存在于
不存在于
为空
不为空]
    MULTI_DEPARTMENT: '部门多选',[等于
包含全部
不包含全部
包含任一
不包含任一
为空
不为空]
    DATA_SELECTION: '数据单选',[等于
不等于
包含
不包含
存在于
不存在于
为空
不为空]
    MULTI_DATA_SELECTION: '数据多选',[等于
包含全部
不包含全部
包含任一
不包含任一
为空
不为空]
    FILE: '文件',[为空
不为空]
    IMAGE: '图片',[为空
不为空]
    GEOGRAPHY: '地理位置',[等于
不等于
为空
不为空]
    PASSWORD: '密码',[为空
不为空]
    ENCRYPTED: '加密字段',[为空
不为空]
    AGGREGATE: '聚合统计',[等于
不等于
大于
大于等于
小于
小于等于
范围
为空
不为空]
    ID: '数据标识'[等于
不等于
为空
不为空]
   */
export const enum FieldType {
  TEXT = 'TEXT',
  LONG_TEXT = 'LONG_TEXT',
  EMAIL = 'EMAIL',
  PHONE = 'PHONE',
  URL = 'URL',
  ADDRESS = 'ADDRESS',
  NUMBER = 'NUMBER',
  DATE = 'DATE',
  DATETIME = 'DATETIME',
  BOOLEAN = 'BOOLEAN',
  SELECT = 'SELECT',
  MULTI_SELECT = 'MULTI_SELECT',
  AUTO_CODE = 'AUTO_CODE',
  USER = 'USER',
  MULTI_USER = 'MULTI_USER',
  DEPARTMENT = 'DEPARTMENT',
  MULTI_DEPARTMENT = 'MULTI_DEPARTMENT',
  DATA_SELECTION = 'DATA_SELECTION',
  MULTI_DATA_SELECTION = 'MULTI_DATA_SELECTION',
  FILE = 'FILE',
  IMAGE = 'IMAGE',
  GEOGRAPHY = 'GEOGRAPHY',
  PASSWORD = 'PASSWORD',
  ENCRYPTED = 'ENCRYPTED',
  AGGREGATE = 'AGGREGATE',
  ID = 'ID',
  DATA_SELECTION_RESULT = 'DATA_SELECTION_RESULT'
}

export const enum PreNode {
  APPROVAL_RESULT = 'approvalResult',
  APPROVER_ID = 'approverId',
  APPROVAL_TIME = 'approvalTime',
  APPROVER_DEPT_ID = 'approverDeptId'
}

export const enum Instance {
  BPM_TITLE = 'bpmTitle',
  INITIATOR_ID = 'initiatorId',
  INITIATOR_DEPT_ID = 'initiatorDeptId',
  SUBMIT_TIME = 'submitTime',
  CREATE_TIME = 'createTime',
  UPDATE_TIME = 'updateTime'
}
export const enum Operator {
  EQUALS = 'EQUALS', // 等于
  NOT_EQUALS = 'NOT_EQUALS', // 不等于
  CONTAINS = 'CONTAINS', // 包含
  NOT_CONTAINS = 'NOT_CONTAINS', // 不包含
  EXISTS_IN = 'EXISTS_IN', // 存在于
  NOT_EXISTS_IN = 'NOT_EXISTS_IN', // 不存在于
  GREATER_THAN = 'GREATER_THAN', // 大于
  GREATER_EQUALS = 'GREATER_EQUALS', // 大于等于
  LESS_THAN = 'LESS_THAN', // 小于
  LESS_EQUALS = 'LESS_EQUALS', // 小于等于
  LATER_THAN = 'LATER_THAN', // 晚于
  EARLIER_THAN = 'EARLIER_THAN', // 早于
  RANGE = 'RANGE', // 范围
  IS_EMPTY = 'IS_EMPTY', // 为空
  IS_NOT_EMPTY = 'IS_NOT_EMPTY', // 不为空
  CONTAINS_ALL = 'CONTAINS_ALL', // 包含全部
  NOT_CONTAINS_ALL = 'NOT_CONTAINS_ALL', // 不包含全部
  CONTAINS_ANY = 'CONTAINS_ANY', // 包含任一
  NOT_CONTAINS_ANY = 'NOT_CONTAINS_ANY' // 不包含任一
}

export interface FieldOption {
  label: string;
  value: string;
  type: FieldType; // 添加 type 属性
}

export interface OpOptions {
  label: string;
  value: string;
}

export interface ConditionRule {
  fieldScope: string;
  fieldId: string;
  op: string;
  operatorType: string;
  value: any;
  fieldType: string;
}

export const datetimeOpOption = [
  { label: '等于', value: Operator.EQUALS },
  { label: '晚于', value: Operator.LATER_THAN },
  { label: '早于', value: Operator.EARLIER_THAN },
  { label: '范围', value: Operator.RANGE },
  { label: '为空', value: Operator.IS_EMPTY },
  { label: '不为空', value: Operator.IS_NOT_EMPTY }
];

export const textOpOption = [
  { label: '等于', value: Operator.EQUALS },
  { label: '不等于', value: Operator.NOT_EQUALS },
  { label: '包含', value: Operator.CONTAINS },
  { label: '不包含', value: Operator.NOT_CONTAINS },
  { label: '存在于', value: Operator.EXISTS_IN },
  { label: '不存在于', value: Operator.NOT_EXISTS_IN },
  { label: '为空', value: Operator.IS_EMPTY },
  { label: '不为空', value: Operator.IS_NOT_EMPTY }
];

export const longTextOpOption = [
  { label: '等于', value: Operator.EQUALS },
  { label: '不等于', value: Operator.NOT_EQUALS },
  { label: '包含', value: Operator.CONTAINS },
  { label: '不包含', value: Operator.NOT_CONTAINS },
  { label: '为空', value: Operator.IS_EMPTY },
  { label: '不为空', value: Operator.IS_NOT_EMPTY }
];

export const emailOpOption = [
  { label: '等于', value: Operator.EQUALS },
  { label: '不等于', value: Operator.NOT_EQUALS },
  { label: '包含', value: Operator.CONTAINS },
  { label: '不包含', value: Operator.NOT_CONTAINS },
  { label: '存在于', value: Operator.EXISTS_IN },
  { label: '不存在于', value: Operator.NOT_EXISTS_IN },
  { label: '为空', value: Operator.IS_EMPTY },
  { label: '不为空', value: Operator.IS_NOT_EMPTY }
];

export const phoneOpOption = emailOpOption;
export const urlOpOption = [
  { label: '等于', value: Operator.EQUALS },
  { label: '不等于', value: Operator.NOT_EQUALS },
  { label: '包含', value: Operator.CONTAINS },
  { label: '不包含', value: Operator.NOT_CONTAINS },
  { label: '为空', value: Operator.IS_EMPTY },
  { label: '不为空', value: Operator.IS_NOT_EMPTY }
];

export const addressOpOption = urlOpOption;
export const numberOpOption = [
  { label: '等于', value: Operator.EQUALS },
  { label: '不等于', value: Operator.NOT_EQUALS },
  { label: '大于', value: Operator.GREATER_THAN },
  { label: '大于等于', value: Operator.GREATER_EQUALS },
  { label: '小于', value: Operator.LESS_THAN },
  { label: '小于等于', value: Operator.LESS_EQUALS },
  { label: '范围', value: Operator.RANGE },
  { label: '为空', value: Operator.IS_EMPTY },
  { label: '不为空', value: Operator.IS_NOT_EMPTY }
];

export const dateOpOption = [
  { label: '等于', value: Operator.EQUALS },
  { label: '晚于', value: Operator.LATER_THAN },
  { label: '早于', value: Operator.EARLIER_THAN },
  { label: '范围', value: Operator.RANGE },
  { label: '为空', value: Operator.IS_EMPTY },
  { label: '不为空', value: Operator.IS_NOT_EMPTY }
];

export const booleanOpOption = [{ label: '等于', value: Operator.EQUALS }];

export const selectOpOption = [
  { label: '等于', value: Operator.EQUALS },
  { label: '包含全部', value: Operator.CONTAINS_ALL },
  { label: '不包含全部', value: Operator.NOT_CONTAINS_ALL },
  { label: '包含任一', value: Operator.CONTAINS_ANY },
  { label: '不包含任一', value: Operator.NOT_CONTAINS_ANY },
  { label: '为空', value: Operator.IS_EMPTY },
  { label: '不为空', value: Operator.IS_NOT_EMPTY }
];

export const multiSelectOpOption = selectOpOption;
export const autoCodeOpOption = emailOpOption;
export const userOpOption = emailOpOption;
export const multiUserOpOption = selectOpOption;
export const departmentOpOption = emailOpOption;
export const multiDepartmentOpOption = selectOpOption;
export const dataSelectionOpOption = emailOpOption;
export const multiDataSelectionOpOption = selectOpOption;

export const fileOpOption = [
  { label: '为空', value: Operator.IS_EMPTY },
  { label: '不为空', value: Operator.IS_NOT_EMPTY }
];

export const imageOpOption = fileOpOption;
export const geographyOpOption = [
  { label: '等于', value: Operator.EQUALS },
  { label: '不等于', value: Operator.NOT_EQUALS },
  { label: '为空', value: Operator.IS_EMPTY },
  { label: '不为空', value: Operator.IS_NOT_EMPTY }
];

export const passwordOpOption = fileOpOption;
export const encryptedOpOption = fileOpOption;
export const aggregateOpOption = numberOpOption;
export const idOpOption = [
  { label: '等于', value: Operator.EQUALS },
  { label: '不等于', value: Operator.NOT_EQUALS },
  { label: '为空', value: Operator.IS_EMPTY },
  { label: '不为空', value: Operator.IS_NOT_EMPTY }
];

export const approvalResultOptions = [
  {
    label: '待审批',
    value: 'pre_approval'
  },
  {
    label: '待执行',
    value: 'pre_exec'
  },
  {
    label: '自动抄送',
    value: 'pre_auto_cc'
  },
  {
    label: '待提交',
    value: 'curr_pending_submit'
  },
  {
    label: '审批中',
    value: 'curr_in_approval'
  },
  {
    label: '执行中',
    value: 'curr_in_exec'
  },
  {
    label: '已提交',
    value: 'post_submitted'
  },
  {
    label: '已同意',
    value: 'post_approved'
  },
  {
    label: '已拒绝',
    value: 'post_rejected'
  },
  {
    label: '已转交',
    value: 'post_transferred'
  },
  {
    label: '已加签',
    value: 'post_add_signer'
  },
  {
    label: '已退回',
    value: 'post_returned'
  },
  {
    label: '已弃权',
    value: 'post_abstained'
  },
  {
    label: '已撤回',
    value: 'post_withdrawn'
  },
  {
    label: '自动通过',
    value: 'post_auto_approved'
  },
  {
    label: '自动拒绝',
    value: 'post_auto_rejected'
  },
  {
    label: '自动转交',
    value: 'post_auto_transferred'
  },
  {
    label: '自动跳过',
    value: 'post_auto_skipped'
  }
  // {
  //   label: '自动抄送',
  //   value: 'post_auto_cc'
  // }
];
