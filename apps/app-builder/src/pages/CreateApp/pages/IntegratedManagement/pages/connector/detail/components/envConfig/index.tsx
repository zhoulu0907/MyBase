import { Button, Input, Message, Radio, Select } from '@arco-design/web-react';
import { getConnectorEnvByType, getConnectorEnvDetail, updateConnectorEnv, type ConnectInstance } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';

interface ConnectorEnvConfigProps {
    baseInfo: ConnectInstance;
    onNext: () => void;
    onPrev: () => void;
}

const ConnectorEnvConfig: React.FC<ConnectorEnvConfigProps> = ({ baseInfo, onNext, onPrev }) => {
    const [configType, setConfigType] = useState<'existing' | 'create'>('existing');
    const [loading, setLoading] = useState(false);
    const [envList, setEnvList] = useState<{ label: string; value: string }[]>([]);
    const [selectedEnv, setSelectedEnv] = useState<string | undefined>(baseInfo.environment);
    const [envDetail, setEnvDetail] = useState<any>(null);
    const [envName, setEnvName] = useState('');
    const [envUrl, setEnvUrl] = useState('');
    const [authType, setAuthType] = useState('none');
    const [detailLoading, setDetailLoading] = useState(false);

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
                // 后端返回格式：{ code: 0, data: [...], msg: "" }
                const list = res.data || res.list || (Array.isArray(res) ? res : []);

                setEnvList(list.map((item: any) => ({
                    label: item.envName,
                    value: item.id
                })));
            }
        } catch (error) {
            console.error('Failed to fetch environment list:', error);
            Message.error('获取环境列表失败，请检查网络连接或联系管理员');
            // Mock data fallback
            setEnvList([
                { label: 'Development Environment', value: 'dev' },
                { label: 'Testing Environment', value: 'test' },
                { label: 'Production Environment', value: 'prod' }
            ]);
        } finally {
            setLoading(false);
        }
    };

    // 获取环境详情
    const fetchEnvDetail = async (envId: string) => {
        setDetailLoading(true);
        try {
            const res = await getConnectorEnvDetail(envId);

            if (res && res.data) {
                setEnvDetail(res.data);
                setEnvName(res.data.envName || '');
                setEnvUrl(res.data.envUrl || '');
                setAuthType(res.data.authType || 'none');
            } else {
                // 尝试检查res本身是否就是data
                if (res && res.id) {
                    setEnvDetail(res);
                    setEnvName(res.envName || '');
                    setEnvUrl(res.envUrl || '');
                    setAuthType(res.authType || 'none');
                }
            }
        } catch (error) {
            console.error('Failed to fetch environment detail:', error);
            Message.error('获取环境详情失败');
        } finally {
            setDetailLoading(false);
        }
    };

    // 处理环境选择变化
    const handleEnvChange = (envId: string) => {
        setSelectedEnv(envId);
        if (envId) {
            fetchEnvDetail(envId);
        } else {
            setEnvDetail(null);
        }
    };

    // 保存环境配置
    const handleSave = async () => {
        if (!envDetail) {
            Message.warning('请先选择环境');
            return;
        }
        try {
            await updateConnectorEnv({
                id: envDetail.id,
                envName,
                envUrl,
                authType
            });
            Message.success('保存成功');
        } catch (error) {
            console.error('Failed to save environment:', error);
            Message.error('保存失败');
        }
    };

    const handleNext = () => {
        if (configType === 'existing' && !selectedEnv) {
            Message.warning('请选择环境信息');
            return;
        }
        // detailed save logic can be added here
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

                    {/* 环境详情显示区域 */}
                    {envDetail && (
                        <div className={styles.envDetail}>
                            <div className={styles.formItem}>
                                <div className={styles.label}>环境名称</div>
                                <Input
                                    value={envName}
                                    onChange={setEnvName}
                                    placeholder="测试环境1"
                                    style={{ width: 480 }}
                                />
                            </div>

                            <div className={styles.formItem}>
                                <div className={styles.label}>URL</div>
                                <Input
                                    value={envUrl}
                                    onChange={setEnvUrl}
                                    placeholder="http://test.com"
                                    style={{ width: 480 }}
                                />
                            </div>

                            <div className={styles.formItem}>
                                <div className={styles.label}>选择认证类型</div>
                                <Select
                                    value={authType}
                                    onChange={setAuthType}
                                    style={{ width: 480 }}
                                >
                                    <Select.Option value="none">无认证</Select.Option>
                                    <Select.Option value="Basic">Basic Auth</Select.Option>
                                    <Select.Option value="OAuth">OAuth</Select.Option>
                                </Select>
                            </div>

                            <div className={styles.saveButton}>
                                <Button type="primary" onClick={handleSave}>
                                    保存
                                </Button>
                            </div>
                        </div>
                    )}
                </div>
            ) : (
                <div className={styles.formItem}>
                    {/* Placeholder for Create New Environment */}
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
