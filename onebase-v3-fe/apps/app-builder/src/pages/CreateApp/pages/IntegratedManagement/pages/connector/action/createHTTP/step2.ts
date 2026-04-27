import type { ISchema } from '@formily/react';

/** HTTP 动作创建 - 第二步：参考 Postman/Apifox：Params / Headers / Body */
export const step2Schema: ISchema = {
  type: 'object',
  properties: {
    requestTitle: {
      type: 'void',
      title: '请求定义',
      'x-component': 'SectionTitle'
    },
    url: {
      title: '请求路径',
      type: 'string',
      required: true,
      'x-decorator': 'FormItem',
      'x-component': 'Input',
      'x-component-props': { placeholder: '类似: /api/v1/users' },
      description: '请求的相对路径，如 /api/v1/users'
    },
    method: {
      title: '请求方法',
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
    tabs: {
      type: 'object',
      'x-component': 'Tabs',
      'x-component-props': { type: 'card-gutter' },
      properties: {
        headers: {
          type: 'object',
          title: 'Headers',
          properties: {
            headerTitle: {
              type: 'void',
              title: 'Request Headers',
              'x-component': 'SectionTitle'
            },
            requestHeaders: {
              type: 'array',
              'x-component': 'ParamArrayTable'
            }
          }
        },
        body: {
          type: 'object',
          title: 'Body',
          properties: {
            bodyMode: {
              type: 'string',
              default: 'json',
              enum: [
                { label: 'none', value: 'none' },
                { label: 'form-data', value: 'kv' },
                { label: 'raw(JSON)', value: 'json' }
              ],
              'x-decorator': 'FormItem',
              'x-decorator-props': { labelCol: { span: 0 }, wrapperCol: { span: 24 } },
              'x-component': 'Radio.Group',
              'x-component-props': { type: 'button' }
            },
            kvBody: {
              type: 'object',
              'x-visible': "{{$form.values.tabs?.body?.bodyMode === 'kv'}}",
              properties: {
                bodyKvTitle: {
                  type: 'void',
                  title: 'Body (Key-Value)',
                  'x-component': 'SectionTitle'
                },
                requestBody: {
                  type: 'array',
                  'x-component': 'ParamArrayTable'
                }
              }
            },
            jsonBody: {
              type: 'object',
              'x-visible': "{{$form.values.tabs?.body?.bodyMode === 'json'}}",
              properties: {
                bodyJsonTitle: {
                  type: 'void',
                  title: 'Body (JSON)',
                  'x-component': 'SectionTitle'
                },
                requestBodyJson: {
                  type: 'string',
                  'x-component': 'JsonEditor'
                }
              }
            }
          }
        },
        params: {
          type: 'object',
          title: 'Params',
          properties: {
            queryTitle: {
              type: 'void',
              title: 'Query Params',
              'x-component': 'SectionTitle'
            },
            queryParams: {
              type: 'array',
              'x-component': 'ParamArrayTable'
            },
            pathTitle: {
              type: 'void',
              title: 'Path Params',
              'x-component': 'SectionTitle'
            },
            pathParams: {
              type: 'array',
              'x-component': 'ParamArrayTable'
            }
          }
        }
      }
    },
    responseTitle: {
      type: 'void',
      title: '响应定义',
      'x-component': 'SectionTitle'
    },
    successCondition: {
      type: 'object',
      properties: {
        conditionTitle: {
          type: 'void',
          title: '调用成功规范',
          'x-component': 'SectionTitle'
        },
        successConditions: {
          type: 'array',
          'x-component': 'SuccessConditionTable'
        },
        errorMessagePathWrapper: {
          type: 'void',
          'x-component': 'div',
          'x-component-props': {
            style: { marginTop: 20 }
          },
          properties: {
            errorMessagePath: {
              type: 'string',
              title: '错误消息路径',
              'x-decorator': 'FormItem',
              'x-decorator-props': { labelCol: { span: 6 }, wrapperCol: { span: 18 } },
              'x-component': 'Input',
              'x-component-props': { placeholder: 'e.g. $.message' },
              description: '失败时提取错误消息的 JSON Path'
            }
          }
        }
      }
    },
    responseTabs: {
      type: 'object',
      'x-component': 'Tabs',
      'x-component-props': { type: 'card-gutter' },
      properties: {
        responseBodyTab: {
          type: 'object',
          title: 'Body',
          properties: {
            responseBodyMode: {
              type: 'string',
              default: 'json',
              enum: [
                { label: 'Text', value: 'text' },
                { label: 'JSON', value: 'json' }
              ],
              'x-decorator': 'FormItem',
              'x-decorator-props': { labelCol: { span: 0 }, wrapperCol: { span: 24 } },
              'x-component': 'Radio.Group',
              'x-component-props': { type: 'button' }
            },
            responseBodyTextWrapper: {
              type: 'object',
              'x-visible': "{{$form.values.responseTabs?.responseBodyTab?.responseBodyMode === 'text'}}",
              properties: {
                responseBodyTextTitle: {
                  type: 'void',
                  title: 'Body (Text)',
                  'x-component': 'SectionTitle'
                },
                responseBodyText: {
                  type: 'string',
                  'x-component': 'TextAreaEditor'
                }
              }
            },
            responseBodyJsonWrapper: {
              type: 'object',
              'x-visible': "{{$form.values.responseTabs?.responseBodyTab?.responseBodyMode === 'json'}}",
              properties: {
                responseBodyJsonTitle: {
                  type: 'void',
                  title: 'Body (JSON)',
                  'x-component': 'SectionTitle'
                },
                responseBodyJson: {
                  type: 'string',
                  'x-component': 'JsonEditor'
                }
              }
            },
            responseSchema: {
              type: 'object',
              'x-display': 'none'  // 隐藏字段，仅用于存储 schema 数据
            }
          }
        },
        responseHeaders: {
          type: 'array',
          title: 'Headers',
          'x-component': 'OutputParamArrayTable'
        }
      }
    }
  }
};
