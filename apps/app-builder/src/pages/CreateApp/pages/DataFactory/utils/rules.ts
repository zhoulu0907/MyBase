// 由小写字母、数字、下划线组成，须以字母开头
export const nameRegex = /^[a-z][a-z0-9_]*$/;

// 实体表单验证规则
export const createEntityRules = {
  tableName: [
    { required: true, message: '请输入数据资产名称' },
    { max: 40, message: '数据资产名称不能超过40个字符' },
    {
      validator: (value: string | undefined, cb: (error?: React.ReactNode) => void) => {
        if (value && !nameRegex.test(value)) {
          cb('请输入符合规范的数据资产名称');
        } else {
          cb();
        }
      }
    }
  ],
  displayName: [
    { required: true, message: '请输入业务展示名称' },
    { max: 50, message: '业务展示名称不能超过50个字符' }
  ],
  description: [{ max: 500, message: '数据资产描述不能超过500个字符' }]
} as const;

// 字段名称验证规则
export const createFieldRules = {
  fieldName: [
    { required: true, message: '请输入字段名称' },
    { max: 40, message: '不能超过40个字符' },
    {
      validator: (value: string | undefined, cb: (error?: React.ReactNode) => void) => {
        if (value && !nameRegex.test(value)) {
          cb('不符合规范');
        } else {
          cb();
        }
      }
    }
  ],
  displayName: [
    { required: true, message: '请输入字段展示名称' },
    { max: 40, message: '不能超过40个字符' }
  ]
} as const;
