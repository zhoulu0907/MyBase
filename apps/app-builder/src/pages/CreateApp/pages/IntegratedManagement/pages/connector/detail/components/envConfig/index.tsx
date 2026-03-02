import { Button, Message, Radio, Select, Spin } from '@arco-design/web-react';
import { createForm } from '@formily/core';
import { createSchemaField, FormProvider, type ISchema } from '@formily/react';
import {
  createConnectorEnv,
  enableConnectorEnvironment,
  getConnectgorEnvironmentConfig,
  getConnectorEnvList,
  getEnableConnectorEnvironment,
  getEnvConfigTemplate,
  updateEnvironmentConfig,
  type ConnectInstance
} from '@onebase/app';
import React, { useEffect, useMemo, useState } from 'react';
import { AuthSettingsCard } from '../../../../../../../../../components/DynamicForm/AuthComponents';
import { componentMap, FormilyFormItem } from '../../../../../../../../../components/DynamicForm/componentMapper';

import styles from './index.module.less';

interface ConnectorEnvConfigProps {
  baseInfo: ConnectInstance;
  onNext: () => void;
  onPrev: () => void;
}

const SchemaField = createSchemaField({
  components: {
    ...componentMap,
    FormItem: FormilyFormItem,
    AuthSettingsCard: AuthSettingsCard
  }
});

const ConnectorEnvConfig: React.FC<ConnectorEnvConfigProps> = ({ baseInfo, onNext, onPrev }) => {
  const [configType, setConfigType] = useState<'existing' | 'create'>('existing');
  const [loading, setLoading] = useState(false);
  const [envList, setEnvList] = useState<{ label: string; value: string }[]>([]);
  const [selectedEnv, setSelectedEnv] = useState<string | undefined>(baseInfo.environment); // For the outer selector
  const [envDetail, setEnvDetail] = useState<any>(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [envConfigTemplateSchema, setEnvConfigTemplateSchema] = useState<ISchema | null>(null);
  const [templateLoading, setTemplateLoading] = useState(false);
  const [saveLoading, setSaveLoading] = useState(false);

  const form = useMemo(
    () =>
      createForm({
        values: {
          envMode: 'create',
          authType: 'none'
        }
      }),
    []
  );

  // 创建环境信息时使用的表单（根据接口返回的 schema 渲染）
  const createEnvForm = useMemo(
    () =>
      createForm({
        initialValues: { envMode: 'create' }
      }),
    []
  );

  const loadDefaultEnv = async () => {
    try {
      const res = await getEnableConnectorEnvironment(baseInfo.id);
      console.log(res);
      const envName = res;
      if (envName) {
        setSelectedEnv(envName);
        setConfigType('existing');
        await fetchEnvDetail(envName);
      }
    } catch (error) {
      console.error('Failed to get default connector environment:', error);
    }
  };

  // 进入页面时先获取默认启用环境信息
  useEffect(() => {
    if (!baseInfo?.id) return;

    loadDefaultEnv();
  }, [baseInfo?.id]);

  // 选择已有环境时拉取环境列表
  useEffect(() => {
    if (configType === 'existing') {
      fetchEnvList();
    }
  }, [configType]);

  // 挂载时加载环境配置模板 schema，供「创建」与「选择已有环境」详情共用动态表单
  useEffect(() => {
    if (baseInfo?.id) {
      handleGetEnvConfigTemplate();
    }
  }, [baseInfo?.id]);

  const fetchEnvList = async () => {
    setLoading(true);
    try {
      const res = await getConnectorEnvList(baseInfo.id);
      if (res) {
        const list = res.data || res.list || (Array.isArray(res) ? res : []);
        setEnvList(
          list.map((item: any) => ({
            label: item.envName,
            value: item.envName
          }))
        );
      }
    } catch (error) {
      console.error('Failed to fetch environment list:', error);
      Message.error('获取环境列表失败');
      // Mock data
      setEnvList([
        { label: 'Development Environment', value: 'dev' },
        { label: 'Testing Environment', value: 'test' },
        { label: 'Production Environment', value: 'prod' }
      ]);
    } finally {
      setLoading(false);
    }
  };

  const fetchEnvDetail = async (envName: string) => {
    setDetailLoading(true);
    try {
      const res = await getConnectgorEnvironmentConfig(baseInfo.id, envName);
      const data = res?.data ?? res;

      if (data && (data.schema || data.envCode != null)) {
        setEnvDetail(data);
        // 按接口结构 schema.envMode、schema.envConfig 填入动态表单（与创建环境表单结构一致）
        form.setValues({
          envMode: data.schema?.envMode ?? 'create',
          envConfig: data.schema?.envConfig ?? {}
        });
      } else {
        setEnvDetail(null);
      }
    } catch (error) {
      console.error('Failed to fetch environment detail:', error);
      Message.error('获取环境详情失败');
    } finally {
      setDetailLoading(false);
    }
  };

  const handleEnvChange = async (envName: string) => {
    setSelectedEnv(envName);
    if (envName) {
      await fetchEnvDetail(envName);
      try {
        await enableConnectorEnvironment(baseInfo.id, envName);
      } catch (error) {
        console.error('Failed to enable connector environment:', error);
        Message.error('启用环境失败');
      }
    } else {
      setEnvDetail(null);
      form.reset();
    }
  };

  const handleGetEnvConfigTemplate = async () => {
    setTemplateLoading(true);
    try {
      const res = await getEnvConfigTemplate(baseInfo.id);
      console.log('res: ', res);

      const raw = res && (res as any).data !== undefined ? (res as any).data : res;

      //   console.log('raw: ', raw.schema);
      //   if (raw && raw.schema && typeof raw.schema === 'object') {
      if (raw && typeof raw === 'object') {
        const mockSchema = {
          type: 'object',
          title: 'HTTP连接器环境配置',
          description: '配置HTTP连接器的环境信息，包括基础URL、超时设置、认证方式等',
          'x-component': 'Card',
          properties: {
            // envMode: {
            //   type: 'string',
            //   title: '环境模式',
            //   default: 'create',
            //   'x-decorator': 'FormItem',
            //   'x-component': 'Radio.Group',
            //   enum: [
            //     { label: '创建新环境', value: 'create' },
            //     { label: '选择已有环境', value: 'select' }
            //   ]
            // },
            // EnvId: {
            //   title: '环境配置',
            //   type: 'string',
            //   placeholder: '请选择环境配置',
            //   'x-decorator': 'FormItem',
            //   'x-component': 'Input'
            // },
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
                      required: true,
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
                      // 'ui:hidden': "{{$form.values.envConfig.authInfo.authType !== 'basic'}}",
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
                      // 'ui:hidden': "{{$form.values.envConfig.authInfo.authType !== 'bearer'}}",
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
                      // 'ui:hidden': "{{$form.values.envConfig.authInfo.authType !== 'apikey'}}",
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
                      // 'ui:hidden': "{{$form.values.envConfig.authInfo.authType !== 'oauth2'}}",
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
                      // 'ui:hidden': "{{$form.values.envConfig.authInfo.authType !== 'custom'}}",
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

        console.log(mockSchema);

        setEnvConfigTemplateSchema(mockSchema as ISchema);
      } else {
        setEnvConfigTemplateSchema(null);
      }
    } catch (error) {
      console.error('Failed to fetch env config template:', error);
      Message.error('获取环境配置模板失败');
      setEnvConfigTemplateSchema(null);
    } finally {
      setTemplateLoading(false);
    }
  };

  const handleSave = async () => {
    if (!envDetail) {
      Message.warning('请先选择环境');
      return;
    }
    try {
      //   await form.validate();
      const values = form.values as { envMode?: string; envConfig?: Record<string, unknown> };
      await updateEnvironmentConfig(baseInfo.id, {
        config: {
          envMode: values.envMode,
          envConfig: values.envConfig
        }
      });
      Message.success('保存成功');
    } catch (error) {
      console.error('Failed to save environment:', error);
      Message.error((error as any).message || '保存失败');
    }
  };

  const handleCreateEnvSave = async () => {
    try {
      // TODO(好心人): 需要验证表单是否符合要求:)
      //   await createEnvForm.validate();
      const values = createEnvForm.values;
      console.log(values);
      setSaveLoading(true);
      await createConnectorEnv(baseInfo.id, {
        config: values
      });
      Message.success('保存成功');
      fetchEnvList();
    } catch (error) {
      console.error('Failed to create connector env:', error);
      Message.error((error as any).message || '保存失败');
    } finally {
      setSaveLoading(false);
    }
  };

  const handleNext = () => {
    if (configType === 'existing' && !selectedEnv) {
      Message.warning('请选择环境信息');
      return;
    }
    onNext();
  };

  return (
    <div className={styles.container}>
      <div className={styles.header}>环境配置</div>

      <div className={styles.formItem}>
        <div className={styles.label}>认证方式</div>
        <Radio.Group value={configType} onChange={setConfigType} type="button">
          <Radio value="existing">选择已有环境信息</Radio>
          <Radio value="create">创建环境信息</Radio>
        </Radio.Group>
      </div>

      {configType === 'existing' ? (
        <div className={styles.formItem}>
          <div className={styles.label}>环境信息</div>
          <Select
            placeholder="请选择环境信息"
            style={{ width: 480 }}
            value={selectedEnv}
            onChange={handleEnvChange}
            loading={loading}
          >
            {envList.map((option) => (
              <Select.Option key={option.value} value={option.value}>
                {option.label}
              </Select.Option>
            ))}
          </Select>

          {envDetail && (
            <Spin loading={detailLoading} style={{ width: '100%', marginTop: 20 }}>
              <div className={styles.envDetail}>
                {envConfigTemplateSchema ? (
                  <>
                    <FormProvider form={form}>
                      <SchemaField schema={envConfigTemplateSchema} />
                    </FormProvider>
                    <div className={styles.saveButton} style={{ marginTop: 24 }}>
                      <Button type="primary" onClick={handleSave}>
                        保存
                      </Button>
                    </div>
                  </>
                ) : (
                  <Spin loading={templateLoading} style={{ display: 'block', margin: '20px auto' }} />
                )}
              </div>
            </Spin>
          )}
        </div>
      ) : (
        <div className={styles.formItem}>
          <div
            style={{
              padding: '20px',
              border: '1px dashed var(--color-border-3)',
              textAlign: 'center',
              color: 'var(--color-text-3)'
            }}
          >
            {templateLoading ? (
              <Spin loading />
            ) : envConfigTemplateSchema ? (
              <>
                <FormProvider form={createEnvForm}>
                  <SchemaField schema={envConfigTemplateSchema} />
                </FormProvider>
                <div className={styles.saveButton} style={{ marginTop: 24 }}>
                  <Button type="primary" loading={saveLoading} onClick={handleCreateEnvSave}>
                    保存
                  </Button>
                </div>
              </>
            ) : (
              '创建环境信息功能开发中...'
            )}
          </div>
        </div>
      )}

      <div className={styles.footer}>
        <Button onClick={onPrev}>上一步</Button>
        <Button type="primary" onClick={handleNext}>
          下一步
        </Button>
      </div>
    </div>
  );
};

export default ConnectorEnvConfig;
