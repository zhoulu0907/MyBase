import { Button, Message, Radio, Select, Spin } from '@arco-design/web-react';
import { getConnectorEnvByType, getConnectorEnvDetail, updateConnectorEnv, type ConnectInstance } from '@onebase/app';
import React, { useEffect, useMemo, useState } from 'react';
import { createForm } from '@formily/core';
import { createSchemaField, FormProvider } from '@formily/react';
import { componentMap, FormilyFormItem } from '../../../../../../../../../components/DynamicForm/componentMapper';
import { AuthSettingsCard } from '../../../../../../../../../components/DynamicForm/AuthComponents';
import { mockConnConfig } from '../../../../../../../../../mocks/connectorSchemas';
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
    },
});

const ConnectorEnvConfig: React.FC<ConnectorEnvConfigProps> = ({ baseInfo, onNext, onPrev }) => {
    const [configType, setConfigType] = useState<'existing' | 'create'>('existing');
    const [loading, setLoading] = useState(false);
    const [envList, setEnvList] = useState<{ label: string; value: string }[]>([]);
    const [selectedEnv, setSelectedEnv] = useState<string | undefined>(baseInfo.environment); // For the outer selector
    const [envDetail, setEnvDetail] = useState<any>(null);
    const [detailLoading, setDetailLoading] = useState(false);

    // Filter the schema to only include relevant fields for editing
    const editSchema = useMemo(() => {
        const {
            envName, url, authType, authConfig,
            // Custom Auth split fields are already in authConfig
            // Token Auth new fields
            // Note: Since I added them under `authConfig.properties` in the schema file,
            // they are nested inside `authConfig`.
            // Wait, in `connectorSchemas.ts` I added them under `authConfig.properties`.
            // So `editSchema` just needs to include `authConfig`.
            // Previously I filtered `mockConnConfig.properties` which has `authConfig`.
            // So `editSchema` automatically includes the updated `authConfig` object with all its new properties.
            // NO CHANGE NEEDED HERE.
            // But I should verify that `mockConnConfig.properties` is what I am destructing.
            // Yes.
            // So this step is actually just verification.
            // I will update the code comment to reflect that dynamic sub-fields are included.
        } = mockConnConfig.properties;
        return {
            type: 'object',
            properties: {
                envName,
                url,
                authType,
                authConfig
            }
        };
    }, []);

    const form = useMemo(() => createForm({
        values: {
            envMode: 'create', // Force visibility of fields that depend on envMode='create'
            authType: 'none',
        }
    }), []);

    useEffect(() => {
        if (configType === 'existing') {
            fetchEnvList();
        }
    }, [configType]);

    const fetchEnvList = async () => {
        setLoading(true);
        try {
            const res = await getConnectorEnvByType(baseInfo.typeCode);
            if (res) {
                const list = res.data || res.list || (Array.isArray(res) ? res : []);
                setEnvList(list.map((item: any) => ({
                    label: item.envName,
                    value: item.id
                })));
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

    const fetchEnvDetail = async (envId: string) => {
        setDetailLoading(true);
        try {
            const res = await getConnectorEnvDetail(envId);
            const data = (res && res.data) ? res.data : (res && res.id ? res : null);

            if (data) {
                setEnvDetail(data);
                // Populate form
                form.setValues({
                    ...data,
                    envMode: 'create', // Ensure visibility
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

    const handleEnvChange = (envId: string) => {
        setSelectedEnv(envId);
        if (envId) {
            fetchEnvDetail(envId);
        } else {
            setEnvDetail(null);
            form.reset();
        }
    };

    const handleSave = async () => {
        if (!envDetail) {
            Message.warning('请先选择环境');
            return;
        }
        try {
            await form.validate();
            const values = form.values;
            await updateConnectorEnv({
                id: envDetail.id,
                ...values
            });
            Message.success('保存成功');
        } catch (error) {
            console.error('Failed to save environment:', error);
            Message.error((error as any).message || '保存失败');
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
                <Radio.Group
                    value={configType}
                    onChange={setConfigType}
                    type='button'
                >
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
                        {envList.map(option => (
                            <Select.Option key={option.value} value={option.value}>
                                {option.label}
                            </Select.Option>
                        ))}
                    </Select>

                    {envDetail && (
                        <Spin loading={detailLoading} style={{ width: '100%', marginTop: 20 }}>
                            <div className={styles.envDetail}>
                                <FormProvider form={form}>
                                    <SchemaField schema={editSchema} />
                                </FormProvider>

                                <div className={styles.saveButton} style={{ marginTop: 24 }}>
                                    <Button type="primary" onClick={handleSave}>
                                        保存
                                    </Button>
                                </div>
                            </div>
                        </Spin>
                    )}
                </div>
            ) : (
                <div className={styles.formItem}>
                    <div style={{ padding: '20px', border: '1px dashed var(--color-border-3)', textAlign: 'center', color: 'var(--color-text-3)' }}>
                        创建环境信息功能开发中...
                    </div>
                </div>
            )}

            <div className={styles.footer}>
                <Button onClick={onPrev}>上一步</Button>
                <Button type="primary" onClick={handleNext}>下一步</Button>
            </div>
        </div>
    );
};

export default ConnectorEnvConfig;
