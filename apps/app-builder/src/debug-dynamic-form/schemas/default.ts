export const DEFAULT_SCHEMA = {
  type: 'object',
  properties: {
    username: {
      type: 'string',
      title: '用户名',
      required: true,
      'x-decorator': 'FormItem',
      'x-component': 'Input',
      'x-component-props': {
        placeholder: '请输入用户名',
      },
    },
    age: {
      type: 'number',
      title: '年龄',
      'x-decorator': 'FormItem',
      'x-component': 'InputNumber',
      'x-component-props': {
        min: 0,
        max: 150,
      },
    },
    role: {
      type: 'string',
      title: '角色',
      enum: [
        { label: '管理员', value: 'admin' },
        { label: '普通用户', value: 'user' },
        { label: '访客', value: 'guest' },
      ],
      'x-decorator': 'FormItem',
      'x-component': 'Select',
    },
    enableNotification: {
      type: 'boolean',
      title: '开启通知',
      'x-decorator': 'FormItem',
      'x-component': 'Switch',
    },
    gender: {
      type: 'string',
      title: '性别',
      enum: [
        { label: '男', value: 'male' },
        { label: '女', value: 'female' },
      ],
      'x-decorator': 'FormItem',
      'x-component': 'Radio.Group',
    },
  },
};

