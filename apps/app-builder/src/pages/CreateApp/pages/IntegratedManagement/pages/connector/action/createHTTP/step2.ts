import type { ISchema } from '@formily/react';

/** HTTP 动作创建 - 第二步：4 个 Tab（请求头 / 请求体 / 查询参数 / 路径参数），每个 Tab 内用表格展示并支持动态增减 */
export const step2Schema: ISchema = {
  type: 'object',
  properties: {
    tabs: {
      type: 'void',
      'x-component': 'Tabs',
      'x-component-props': { type: 'card-gutter' },
      properties: {
        requestHeaders: {
          type: 'array',
          title: 'HTTP 请求头',
          'x-component': 'ParamArrayTable'
        },
        requestBody: {
          type: 'array',
          title: 'HTTP 请求体',
          'x-component': 'ParamArrayTable'
        },
        queryParams: {
          type: 'array',
          title: 'URL 查询参数',
          'x-component': 'ParamArrayTable'
        },
        pathParams: {
          type: 'array',
          title: 'URL 路径参数',
          'x-component': 'ParamArrayTable'
        }
      }
    }
  }
};
