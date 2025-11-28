// 校验类型常量
export const VALIDATION_TYPES = {
  REQUIRED: 'REQUIRED',
  UNIQUE: 'UNIQUE',
  LENGTH: 'LENGTH',
  RANGE: 'RANGE',
  FORMAT: 'FORMAT',
  CHILD_NOT_EMPTY: 'CHILD_NOT_EMPTY',
  SELF_DEFINED: 'SELF_DEFINED'
} as const;

// 校验类型选项
export const validationTypeMap: Record<string, string> = {
  [VALIDATION_TYPES.REQUIRED]: '必填校验',
  [VALIDATION_TYPES.UNIQUE]: '唯一校验',
  [VALIDATION_TYPES.LENGTH]: '长度校验',
  [VALIDATION_TYPES.RANGE]: '范围校验',
  [VALIDATION_TYPES.FORMAT]: '格式校验',
  [VALIDATION_TYPES.CHILD_NOT_EMPTY]: '子表空行校验',
  [VALIDATION_TYPES.SELF_DEFINED]: '自定义校验'
};

export const validationTypeList = Object.entries(validationTypeMap).map(([key, value]) => ({
  label: value,
  value: key
}));

// 格式校验类型选项
export const formatValidationTypeOptions = [
  { label: '正则校验', value: 'pattern' },
  { label: '邮箱格式', value: 'email' },
  { label: '电话格式', value: 'phone' },
  { label: 'URL格式', value: 'url' },
  { label: '日期格式', value: 'date' }
];

// 提示语
export const ruleTip: Record<string, string> = {
  REQUIRED: '请输入校验不通过后的弹窗提示语，例如“XXX必填”',
  UNIQUE: '请输入校验不通过后的弹窗提示语，例如“XXX不可重复”',
  LENGTH: '请输入校验不通过后的弹窗提示语，例如“XXX长度范围需在XX~XX之间”',
  RANGE: '请输入校验不通过后的弹窗提示语，例如“XXX范围需在XX~XX之间”',
  FORMAT: '请输入校验不通过后的弹窗提示语，例如“请输入有效的手机号码”',
  CHILD_NOT_EMPTY: '请输入校验不通过后的弹窗提示语，例如“子表存在空行”'
};

// 正则校验选项
export const REGEX_LIST = [
  { label: '手机号码', value: '^1[3-9]\\d{9}$' },
  { label: '电话号码', value: '^(\\d{3,4}-)?\\d{7,8}$' },
  { label: '邮箱格式', value: "^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$" },
  { label: '大写字母', value: '[A-Z]+' },
  { label: '小写字母', value: '[a-z]+' },
  { label: '8位字母数字', value: '^[a-zA-Z0-9]{8}$' },
  { label: '8位数字', value: '^\\d{8}$' },
  { label: '数字', value: '^\\d+$' },
  { label: '邮编', value: '^\\d{6}$' },
  { label: '身份证号（18位）', value: '^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$' }
] as const;
