// rule.ts
export const fieldOperatorMapping: { [key: string]: string[] } = {
  常规短文本: ['equals', 'not_equals', '包含', '不包含', '存在于', '不存在于'],
  长文本内容: ['equals', 'not_equals', '包含', '不包含', '存在于', '不存在于'],
  邮箱地址: ['equals', 'not_equals', '包含', '不包含', '存在于', '不存在于'],
  电话号码: ['equals', 'not_equals', '包含', '不包含', '存在于', '不存在于'],
  网址链接: ['equals', 'not_equals', '包含', '不包含', '存在于', '不存在于'],
  详细地址: ['equals', 'not_equals', '包含', '不包含', '存在于', '不存在于'],
  通用数字: ['大于', '大于等于', '小于', '小于等于', '等于', 'not_equals'],
  货币金额: ['大于', '大于等于', '小于', '小于等于', '等于', 'not_equals'],
  日期: ['早于', '晚于'],
  日期时间: ['早于', '晚于'],
  布尔值: ['equals', 'not_equals'],
  单选列表: ['equals', 'not_equals'],
  多选列表: ['包含', '不包含', '包含全部', '包含任意', '不包含任意'],
  自动编号: ['equals', 'not_equals'],
  用户引用: ['equals', 'not_equals'],
  部门引用: ['equals', 'not_equals'],
  数据选择: ['equals', 'not_equals'],
  关联关系: ['equals', 'not_equals'],
  结构化对象: ['equals', 'not_equals'],
  数组列表: ['包含', '不包含', '包含全部', '包含任意', '不包含任意'],
  文件: [],
  图片: [],
  地理位置: [],
  密码: [],
  加密字段: [],
  聚合统计: []
};

export const operatorMapping = {
  等于: 'equals',
  不等于: 'not_equals',
  包含: 'contains',
  不包含: 'not_contains',
  存在于: 'exists',
  不存在于: 'not_exists',
  大于: 'greater_than',
  大于等于: 'greater_equal',
  小于: 'less_than',
  小于等于: 'less_equal',
  晚于: 'later_than',
  早于: 'earlier_than',
  包含全部: 'contains_all',
  不包含全部: 'not_contains_all',
  包含任一: 'contains_any',
  不包含任一: 'not_contains_any'
};

// 操作符选项
export const operatorOptions = [
  { label: '等于', value: 'equals' },
  { label: '不等于', value: 'not_equals' },
  { label: '包含', value: 'contains' },
  { label: '不包含', value: 'not_contains' },
  { label: '存在于', value: 'exists' },
  { label: '不存在于', value: 'not_exists' },
  { label: '大于', value: 'greater_than' },
  { label: '大于等于', value: 'greater_equal' },
  { label: '小于', value: 'less_than' },
  { label: '小于等于', value: 'less_equal' },
  { label: '晚于', value: 'later_than' },
  { label: '早于', value: '' },
  { label: '包含全部', value: 'contains_all' },
  { label: '不包含全部', value: 'not_contains_all' },
  { label: '包含任一', value: 'contains_any' },
  { label: '不包含任一', value: 'not_contains_any' }
];

// 值类型选项
export const valueTypeOptions = [
  { label: '静态值 ', value: 'custom' },
  { label: '变量', value: 'fieldId' }
  // { label: '系统字段', value: 'fixed' } //  后期放开
];

// 校验类型常量
export const VALIDATION_TYPES = {
  REQUIRED: 'REQUIRED',
  UNIQUE: 'UNIQUE',
  LENGTH: 'LENGTH',
  RANGE: 'RANGE',
  FORMAT: 'FORMAT',
  SUBTABLE_EMPTY: 'SUBTABLE_EMPTY',
  CUSTOM: 'CUSTOM'
} as const;

// 校验类型选项
export const validationTypeMap: Record<string, string> = {
  [VALIDATION_TYPES.REQUIRED]: '必填校验',
  [VALIDATION_TYPES.UNIQUE]: '唯一校验',
  [VALIDATION_TYPES.LENGTH]: '长度校验',
  [VALIDATION_TYPES.RANGE]: '范围校验',
  [VALIDATION_TYPES.FORMAT]: '格式校验',
  [VALIDATION_TYPES.SUBTABLE_EMPTY]: '子表空行校验',
  [VALIDATION_TYPES.CUSTOM]: '自定义校验'
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
  SUBTABLE_EMPTY: '请输入校验不通过后的弹窗提示语，例如“子表存在空行”'
};
