import type { ISchema } from '@formily/react';

/** HTTP 动作创建 - 第四步：动作调试（接口地址 + 接口方法 + 执行动作入参填写，数据来自 step2 的 tabs，仅字段值可编辑） */
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
      enum: ['GET', 'POST', 'PUT', 'DELETE'],
      'x-decorator': 'FormItem',
      'x-component': 'Select',
      'x-component-props': { placeholder: '请选择' }
    },
    debugParamsTabs: {
      type: 'object',
      'x-component': 'Tabs',
      'x-component-props': { type: 'card-gutter' },
      properties: {
        requestHeaders: {
          type: 'array',
          title: 'HTTP 请求头',
          'x-component': 'DebugParamReadOnlyTable'
        },
        requestBody: {
          type: 'array',
          title: 'HTTP 请求体',
          'x-component': 'DebugParamReadOnlyTable'
        },
        queryParams: {
          type: 'array',
          title: 'URL 查询参数',
          'x-component': 'DebugParamReadOnlyTable'
        },
        pathParams: {
          type: 'array',
          title: 'URL 路径参数',
          'x-component': 'DebugParamReadOnlyTable'
        }
      }
    }
  }
};
