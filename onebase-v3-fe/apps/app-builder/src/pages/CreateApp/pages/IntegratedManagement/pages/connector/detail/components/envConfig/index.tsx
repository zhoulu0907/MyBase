import { Button, Message, Radio, Select, Spin } from '@arco-design/web-react';
import { createForm } from '@formily/core';
import { createSchemaField, FormProvider, type ISchema } from '@formily/react';
import {
  createConnectorEnv,
  enableConnectorEnvironment,
  getConnectgorEnvironmentConfig,
  getConnectorEnvList,
  getEnableConnectorEnvironment,
  updateEnvironmentConfig,
  type ConnectInstance,
  type FlowConnectorEnvLiteVO
} from '@onebase/app';
import React, { useEffect, useMemo, useState } from 'react';
import { AuthSettingsCard } from '../../../../../../../../../components/DynamicForm/AuthComponents';
import { componentMap, FormilyFormItem } from '../../../../../../../../../components/DynamicForm/componentMapper';
import { httpEnvSchema } from '../../../action/createHTTP/envSchema';

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

const genRandomEnvCode = () => {
  const ts = Date.now().toString(36).toUpperCase();
  const rand = Math.random().toString(36).slice(2, 8).toUpperCase();
  return `ENV_${ts}_${rand}`;
};

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
          envConfig: {
            authInfo: {
              authType: 'none'
            }
          }
        }
      }),
    []
  );

  // 创建环境信息时使用的表单（根据接口返回的 schema 渲染）
  const createEnvForm = useMemo(
    () =>
      createForm({
        initialValues: {
          envMode: 'create',
          envConfig: {
            basicInfo: {
              envName: baseInfo?.connectorName ? `${baseInfo.connectorName}-环境` : '',
              envCode: genRandomEnvCode()
            },
            authInfo: {
              authType: 'none'
            }
          }
        }
      }),
    []
  );

  const loadDefaultEnv = async () => {
    try {
      // 获取启用的环境信息
      const res = await getEnableConnectorEnvironment(baseInfo.id);
      console.log('enabled env res:', res);
      // http.get 已解包 response.data.data，res 就是 FlowConnectorEnvLiteVO 或 null
      const envData = res as FlowConnectorEnvLiteVO | null;

      if (envData?.envCode) {
        setSelectedEnv(envData.envCode);
        setConfigType('existing');
        await fetchEnvDetail(envData.envCode);
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
      // 新接口直接返回 FlowConnectorEnvLiteVO[] 数组
      const list: FlowConnectorEnvLiteVO[] = Array.isArray(res) ? res : (res as any)?.data || [];
      setEnvList(
        list.map((item) => ({
          label: item.envName,
          value: item.envCode  // 使用 envCode 作为 value
        }))
      );
    } catch (error) {
      console.error('Failed to fetch environment list:', error);
      Message.error('获取环境列表失败');
      setEnvList([]);
    } finally {
      setLoading(false);
    }
  };

  const fetchEnvDetail = async (envCode: string) => {
    setDetailLoading(true);
    try {
      const res = await getConnectgorEnvironmentConfig(baseInfo.id, envCode);
      // 新接口返回 EnvironmentConfigVO: { schema, envCode, typeCode }
      const data = res as any;
      console.log('env detail res:', data);

      if (data && data.schema) {
        setEnvDetail(data);
        // 新接口的 schema 直接就是 Formily Schema，用于渲染表单
        // 配置值可能直接在 schema.envConfig 下，或者是整个 schema
        let formValues: any = {};

        // 优先从 schema.envConfig 提取配置值
        if (data.schema.envConfig) {
          formValues.envConfig = data.schema.envConfig;
        } else {
          // 如果 schema 没有 envConfig，直接使用 schema
          formValues = data.schema;
        }

        // 确保 envConfig.basicInfo.envCode 存在（用于更新时标识环境）
        if (!formValues.envConfig) {
          formValues.envConfig = {};
        }
        if (!formValues.envConfig.basicInfo) {
          formValues.envConfig.basicInfo = {};
        }
        if (!formValues.envConfig.basicInfo.envCode) {
          formValues.envConfig.basicInfo.envCode = envCode;
        }

        console.log('formValues:', formValues);
        form.setValues(formValues);
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

  const handleEnvChange = async (envCode: string) => {
    setSelectedEnv(envCode);
    if (envCode) {
      await fetchEnvDetail(envCode);
      try {
        await enableConnectorEnvironment(baseInfo.id, envCode);
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
      // 直接使用 HTTP 连接器的内置 schema
      setEnvConfigTemplateSchema(httpEnvSchema);
    } catch (error) {
      console.error('Failed to set env config template:', error);
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
      <div className={styles.header}>认证配置</div>

      <div className={styles.formItem}>
        <div className={styles.label}>配置方式</div>
        <Radio.Group value={configType} onChange={setConfigType} type="button">
          <Radio value="existing">选择已有认证配置</Radio>
          <Radio value="create">创建认证配置</Radio>
        </Radio.Group>
      </div>

      {configType === 'existing' ? (
        <div className={styles.formItem}>
          <div className={styles.label}>认证配置</div>
          <Select
            placeholder="请选择认证配置"
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
