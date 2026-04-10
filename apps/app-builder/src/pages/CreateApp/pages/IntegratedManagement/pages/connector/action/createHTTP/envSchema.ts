import type { ISchema } from '@formily/react';

/** HTTP 连接器环境配置 Schema */
export const httpEnvSchema: ISchema = {
  type: 'object',
  title: 'HTTP连接器环境配置',
  description: '配置HTTP连接器的环境信息，包括基础URL、超时设置、认证方式等',
  'x-component': 'Card',
  properties: {
    envConfig: {
      title: '环境配置',
      type: 'object',
      properties: {
        basicInfo: {
          title: '基础配置',
          type: 'object',
          properties: {
            envName: {
              title: '环境名称',
              type: 'string',
              placeholder: '如：开发环境、测试环境',
              'x-decorator': 'FormItem',
              'x-component': 'Input'
            },
            envCode: {
              title: '环境编码',
              type: 'string',
              placeholder: '如：DEV、TEST、PROD',
              pattern: '^[A-Z0-9_]+$',
              message: {
                pattern: '只能包含大写字母、数字和下划线'
              },
              required: true,
              'x-decorator': 'FormItem',
              'x-component': 'Input'
            },
            baseUrl: {
              title: '基础URL',
              type: 'string',
              placeholder: 'https://api.example.com',
              required: true,
              'x-decorator': 'FormItem',
              'x-component': 'Input'
            },
            timeout: {
              title: '超时时间(ms)',
              type: 'number',
              default: 30000,
              minimum: 1000,
              maximum: 300000,
              'x-decorator': 'FormItem',
              'x-component': 'InputNumber'
            },
            retryTimes: {
              title: '重试次数',
              type: 'number',
              default: 3,
              minimum: 0,
              maximum: 5,
              'x-decorator': 'FormItem',
              'x-component': 'InputNumber'
            },
            enableLog: {
              title: '启用日志',
              type: 'boolean',
              default: true,
              'x-decorator': 'FormItem',
              'x-component': 'Switch'
            }
          }
        },
        authInfo: {
          title: '认证配置',
          type: 'object',
          properties: {
            authType: {
              title: '认证方式',
              type: 'string',
              default: 'none',
              enum: ['none', 'basic', 'bearer', 'apikey', 'oauth2', 'custom'],
              enumNames: ['无认证', 'Basic Auth', 'Bearer Token', 'API Key', 'OAuth 2.0', '自定义签名'],
              'ui:width': '50%',
              'x-decorator': 'FormItem',
              'x-component': 'Select'
            },
            basicAuthConfig: {
              title: 'Basic Auth配置',
              type: 'object',
              'x-visible': "{{$form.values.envConfig?.authInfo?.authType === 'basic'}}",
              properties: {
                username: {
                  title: '用户名',
                  type: 'string',
                  placeholder: '请输入用户名',
                  'ui:width': '50%',
                  required: true,
                  'x-decorator': 'FormItem',
                  'x-component': 'Input'
                },
                password: {
                  title: '密码',
                  type: 'string',
                  placeholder: '请输入密码',
                  'ui:width': '50%',
                  required: true,
                  'x-decorator': 'FormItem',
                  'x-component': 'Input.Password'
                }
              }
            },
            bearerAuthConfig: {
              title: 'Bearer Token配置',
              type: 'object',
              'x-visible': "{{$form.values.envConfig?.authInfo?.authType === 'bearer'}}",
              properties: {
                tokenType: {
                  title: 'Token类型',
                  type: 'string',
                  default: 'Bearer',
                  enum: ['Bearer', 'Token'],
                  'ui:width': '33.33%',
                  'x-decorator': 'FormItem',
                  'x-component': 'Select'
                },
                headerName: {
                  title: '请求头名称',
                  type: 'string',
                  default: 'Authorization',
                  placeholder: 'Authorization',
                  'ui:width': '33.33%',
                  'x-decorator': 'FormItem',
                  'x-component': 'Input'
                },
                tokenValue: {
                  title: 'Token值',
                  type: 'string',
                  placeholder: '请输入Token值',
                  'ui:width': '33.33%',
                  required: true,
                  'x-decorator': 'FormItem',
                  'x-component': 'Input'
                }
              }
            },
            apiKeyAuthConfig: {
              title: 'API Key配置',
              type: 'object',
              'x-visible': "{{$form.values.envConfig?.authInfo?.authType === 'apikey'}}",
              properties: {
                headerName: {
                  title: '请求头名称',
                  type: 'string',
                  default: 'X-API-Key',
                  placeholder: 'X-API-Key',
                  'ui:width': '50%',
                  'x-decorator': 'FormItem',
                  'x-component': 'Input'
                },
                keyLocation: {
                  title: 'Key位置',
                  type: 'string',
                  default: 'header',
                  enum: ['header', 'query'],
                  enumNames: ['请求头', '查询参数'],
                  'ui:width': '50%',
                  'x-decorator': 'FormItem',
                  'x-component': 'Select'
                },
                keyValue: {
                  title: 'API Key值',
                  type: 'string',
                  placeholder: '请输入API Key',
                  'ui:width': '50%',
                  required: true,
                  'x-decorator': 'FormItem',
                  'x-component': 'Input'
                },
                prefix: {
                  title: '前缀',
                  type: 'string',
                  placeholder: '如：Bearer ',
                  'ui:width': '50%',
                  'x-decorator': 'FormItem',
                  'x-component': 'Input'
                }
              }
            },
            oauth2AuthConfig: {
              title: 'OAuth 2.0配置',
              type: 'object',
              'x-visible': "{{$form.values.envConfig?.authInfo?.authType === 'oauth2'}}",
              properties: {
                authUrl: {
                  title: '授权URL',
                  type: 'string',
                  placeholder: 'https://auth.example.com/authorize',
                  'ui:width': '100%',
                  required: true,
                  'x-decorator': 'FormItem',
                  'x-component': 'Input'
                },
                tokenUrl: {
                  title: 'Token URL',
                  type: 'string',
                  placeholder: 'https://auth.example.com/token',
                  'ui:width': '100%',
                  required: true,
                  'x-decorator': 'FormItem',
                  'x-component': 'Input'
                },
                clientId: {
                  title: '客户端ID',
                  type: 'string',
                  placeholder: '请输入客户端ID',
                  'ui:width': '50%',
                  required: true,
                  'x-decorator': 'FormItem',
                  'x-component': 'Input'
                },
                clientSecret: {
                  title: '客户端密钥',
                  type: 'string',
                  placeholder: '请输入客户端密钥',
                  'ui:width': '50%',
                  required: true,
                  'x-decorator': 'FormItem',
                  'x-component': 'Input.Password'
                },
                scope: {
                  title: '授权范围',
                  type: 'string',
                  placeholder: 'read write',
                  'ui:width': '100%',
                  'x-decorator': 'FormItem',
                  'x-component': 'Input'
                },
                refreshStrategy: {
                  title: '刷新策略',
                  type: 'string',
                  default: 'auto',
                  enum: ['auto', 'manual', 'none'],
                  enumNames: ['自动刷新', '手动刷新', '不刷新'],
                  'ui:width': '33.33%',
                  'x-decorator': 'FormItem',
                  'x-component': 'Select'
                },
                tokenHeader: {
                  title: 'Token请求头',
                  type: 'string',
                  default: 'Authorization',
                  placeholder: 'Authorization',
                  'ui:width': '33.33%',
                  'x-decorator': 'FormItem',
                  'x-component': 'Input'
                },
                tokenPrefix: {
                  title: 'Token前缀',
                  type: 'string',
                  default: 'Bearer',
                  placeholder: 'Bearer',
                  'ui:width': '33.33%',
                  'x-decorator': 'FormItem',
                  'x-component': 'Input'
                }
              }
            },
            customAuthConfig: {
              title: '自定义签名配置',
              type: 'object',
              'x-visible': "{{$form.values.envConfig?.authInfo?.authType === 'custom'}}",
              properties: {
                locationType: {
                  title: '签名位置',
                  type: 'string',
                  default: 'header',
                  enum: ['header', 'query', 'body'],
                  enumNames: ['请求头', '查询参数', '请求体'],
                  'ui:width': '50%',
                  'x-decorator': 'FormItem',
                  'x-component': 'Select'
                },
                signatureParams: {
                  title: '签名参数',
                  type: 'array',
                  'ui:width': '100%',
                  'x-decorator': 'FormItem',
                  'x-component': 'ArrayItems',
                  items: {
                    type: 'object',
                    properties: {
                      paramName: {
                        title: '参数名称',
                        type: 'string',
                        placeholder: '参数名',
                        'ui:width': '25%',
                        'x-decorator': 'FormItem',
                        'x-component': 'Input'
                      },
                      paramValue: {
                        title: '参数值',
                        type: 'string',
                        placeholder: '参数值',
                        'ui:width': '25%',
                        'x-decorator': 'FormItem',
                        'x-component': 'Input'
                      },
                      paramType: {
                        title: '类型',
                        type: 'string',
                        default: 'static',
                        enum: ['static', 'dynamic'],
                        enumNames: ['静态值', '动态变量'],
                        'ui:width': '25%',
                        'x-decorator': 'FormItem',
                        'x-component': 'Select'
                      },
                      required: {
                        title: '必填',
                        type: 'boolean',
                        default: false,
                        'ui:width': '25%',
                        'x-decorator': 'FormItem',
                        'x-component': 'Switch'
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
};
