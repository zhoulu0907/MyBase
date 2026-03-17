import type { ISchema } from '@formily/react';

/** HTTP 动作创建 - 第四步：动作调试（调用方只填写动作入参） */
export const step4Schema: ISchema = {
  type: 'object',
  properties: {
    url: {
      title: '接口地址',
      type: 'string',
      required: true,
      'x-decorator': 'FormItem',
      'x-component': 'Input',
      'x-component-props': { placeholder: 'https://api.example.com/xxx' }
    },
    method: {
      title: '接口方法',
      type: 'string',
      default: 'GET',
      enum: [
        { label: 'GET', value: 'GET' },
        { label: 'POST', value: 'POST' },
        { label: 'POST(JSON)', value: 'POST_JSON' },
        { label: 'PUT', value: 'PUT' },
        { label: 'DELETE', value: 'DELETE' }
      ],
      'x-decorator': 'FormItem',
      'x-component': 'Select',
      'x-component-props': { placeholder: '请选择' }
    },
    debugInputs: {
      type: 'array',
      title: '动作入参',
      'x-decorator': 'FormItem',
      'x-decorator-props': { labelCol: { span: 4 }, wrapperCol: { span: 20 } },
      'x-component': 'DebugParamReadOnlyTable'
    }
  }
};
