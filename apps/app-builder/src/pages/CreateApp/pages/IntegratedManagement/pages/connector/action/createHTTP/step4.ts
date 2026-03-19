import type { ISchema } from '@formily/react';

/** HTTP 动作创建 - 第四步：动作调试 */
export const step4Schema: ISchema = {
  type: 'object',
  properties: {
    // 隐藏字段
    baseUrl: {
      type: 'string',
      'x-component': 'Input',
      'x-decorator': 'FormItem',
      'x-decorator-props': { style: { display: 'none' } }
    },
    authType: {
      type: 'string',
      'x-component': 'Input',
      'x-decorator': 'FormItem',
      'x-decorator-props': { style: { display: 'none' } }
    },

    // 显示区域
    urlDisplay: {
      type: 'string',
      title: '接口地址',
      'x-decorator': 'FormItem',
      'x-component': 'DebugUrlText'
    },
    authTypeDisplay: {
      type: 'string',
      title: '认证方式',
      'x-decorator': 'FormItem',
      'x-component': 'DebugAuthTypeText'
    },
    methodDisplay: {
      type: 'string',
      title: '请求方法',
      'x-decorator': 'FormItem',
      'x-component': 'DebugMethodText'
    },

    // 分隔线
    divider1: {
      type: 'void',
      'x-component': 'div',
      'x-component-props': {
        style: { height: 1, background: '#e5e6eb', margin: '16px 0' }
      }
    },
    debugHeaders: {
      type: 'array',
      title: '请求头',
      'x-decorator': 'FormItem',
      'x-decorator-props': { style: { marginBottom: 16 } },
      'x-component': 'DebugHeadersTable'
    },

    // 分隔线
    divider2: {
      type: 'void',
      'x-component': 'div',
      'x-component-props': {
        style: { height: 1, background: '#e5e6eb', margin: '16px 0' }
      }
    },
    debugBody: {
      title: '请求体 (Body)',
      type: 'string',
      'x-component': 'JsonEditor'
    }
  }
};