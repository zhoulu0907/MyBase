// 实体表单验证规则
export const createEntityRules = {
  tableName: [
    { required: true, message: '请输入业务实体名称' },
    { max: 40, message: '业务实体名称不能超过40个字符' },
    {
      validator: (value: string | undefined, cb: (error?: React.ReactNode) => void) => {
        if (value && !/^[a-z][a-z0-9_]*$/.test(value)) {
          cb('请输入符合规范的业务实体名称');
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
  description: [{ max: 500, message: '业务实体描述不能超过500个字符' }]
} as const;
