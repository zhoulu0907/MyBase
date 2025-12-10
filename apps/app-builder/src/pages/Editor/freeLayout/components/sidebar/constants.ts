export const enum FieldType {
  TEXT = 'TEXT', // 常规文本
  LONG_TEXT = 'LONG_TEXT', // 长文本
  EMAIL = 'EMAIL', // 邮箱地址
  PHONE = 'PHONE', // 电话号码
  URL = 'URL', // 网址链接
  ADDRESS = 'ADDRESS', // 详细地址
  NUMBER = 'NUMBER', // 通用数字
  DATE = 'DATE', // 日期
  DATETIME = 'DATETIME', // 日期时间
  BOOLEAN = 'BOOLEAN', // 布尔值
  SELECT = 'SELECT', // 单选列表
  MULTI_SELECT = 'MULTI_SELECT', // 多选列表
  AUTO_CODE = 'AUTO_CODE', // 自动编码
  USER = 'USER',
  MULTI_USER = 'MULTI_USER', // 用户多选
  DEPARTMENT = 'DEPARTMENT',
  MULTI_DEPARTMENT = 'MULTI_DEPARTMENT', // 部门多选
  DATA_SELECTION = 'DATA_SELECTION',
  MULTI_DATA_SELECTION = 'MULTI_DATA_SELECTION', // 数据多选
  FILE = 'FILE',
  IMAGE = 'IMAGE',
  GEOGRAPHY = 'GEOGRAPHY', // 地理位置
  PASSWORD = 'PASSWORD',
  ENCRYPTED = 'ENCRYPTED',
  AGGREGATE = 'AGGREGATE',
  ID = 'ID', // 数据标识
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
  fieldUuid: string;
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

export const preNodeOptions = [
  {
    label: '审批结果',
    value: PreNode.APPROVAL_RESULT,
    type: FieldType.DATA_SELECTION_RESULT
  },
  {
    label: '审批⼈',
    value: PreNode.APPROVER_ID,
    type: FieldType.USER
  },
  {
    label: '审批时间',
    value: PreNode.APPROVAL_TIME,
    type: FieldType.DATETIME
  },
  {
    label: '审批⼈部⻔',
    value: PreNode.APPROVER_DEPT_ID,
    type: FieldType.DEPARTMENT
  }
];

export const instanceOptions = [
  {
    label: '流程标题',
    value: Instance.BPM_TITLE,
    type: FieldType.TEXT
  },
  {
    label: '发起⼈',
    value: Instance.INITIATOR_ID,
    type: FieldType.USER
  },
  {
    label: '发起部⻔',
    value: Instance.INITIATOR_DEPT_ID,
    type: FieldType.DEPARTMENT
  },
  {
    label: '发起时间',
    value: Instance.SUBMIT_TIME,
    type: FieldType.DATETIME
  },
  {
    label: '创建时间',
    value: Instance.CREATE_TIME,
    type: FieldType.DATETIME
  },
  {
    label: '更新时间',
    value: Instance.UPDATE_TIME,
    type: FieldType.DATETIME
  }
];

export const entityOptions = [
  {
    label: '表单字段1',
    value: 'field1',
    type: FieldType.USER
  },
  {
    label: '表单字段2',
    value: 'field2',
    type: FieldType.DATE
  }
];
