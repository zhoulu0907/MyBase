import type { ISchema } from '@formily/react';

/** HTTP 动作创建 - 第三步：出参配置（响应头 / 响应体），每个 Tab 内用表格展示并支持动态增减 */
export const step3Schema: ISchema = {
  type: 'object',
  properties: {
    tabs: {
      type: 'object',
      'x-component': 'Tabs',
      'x-component-props': { type: 'card-gutter' },
      properties: {
        responseHeaders: {
          type: 'array',
          title: '响应头',
          'x-component': 'OutputParamArrayTable'
        },
        responseBody: {
          type: 'array',
          title: '响应体',
          'x-component': 'OutputParamArrayTable'
        }
      }
    }
  }
};
