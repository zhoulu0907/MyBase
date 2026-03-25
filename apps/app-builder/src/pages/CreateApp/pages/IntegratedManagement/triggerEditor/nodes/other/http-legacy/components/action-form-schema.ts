import type { ISchema } from '@formily/react';

/**
 * 默认动作表单 schema，与 actionConfigToFormValues 产出的结构一致，供 DynamicForm 渲染。
 * 调试参数使用 DebugParamReadOnlyTable，仅字段值可编辑。
 */
export function getDefaultActionSchema(): ISchema {
  return {
    type: 'object',
    properties: {
      url: {
        title: '接口地址',
        type: 'string',
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
          { label: 'PUT', value: 'PUT' },
          { label: 'DELETE', value: 'DELETE' }
        ],
        'x-decorator': 'FormItem',
        'x-component': 'Select',
        'x-component-props': { placeholder: '请选择' }
      },
      debugParamsTabs: {
        type: 'object',
        title: '调试参数',
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
}
