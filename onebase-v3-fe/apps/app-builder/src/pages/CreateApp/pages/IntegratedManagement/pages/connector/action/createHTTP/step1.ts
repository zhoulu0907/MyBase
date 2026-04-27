import type { ISchema } from '@formily/react';

/** HTTP 动作创建 - 第一步：基础配置 schema */
export const step1Schema: ISchema = {
  type: 'object',
  properties: {
    basic: {
      type: 'object',
      title: '基础配置',
      properties: {
        actionName: {
          title: '动作名称',
          type: 'string',
          required: true,
          'x-decorator': 'FormItem',
          'x-component': 'Input',
          'x-component-props': { placeholder: '请输入动作名称' }
        },
        description: {
          title: '描述',
          type: 'string',
          'x-decorator': 'FormItem',
          'x-component': 'Input.TextArea',
          'x-component-props': { placeholder: '选填' }
        }
      }
    }
  }
};
