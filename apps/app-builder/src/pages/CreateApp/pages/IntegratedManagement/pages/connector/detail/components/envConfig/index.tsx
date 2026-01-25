import { Button, Message, Radio, Select } from '@arco-design/web-react';
import { getConnectorEnvByType, type ConnectInstance } from '@onebase/app';
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

    useEffect(() => {
        if (configType === 'existing') {
            fetchEnvList();
        }
    }, [configType]);

    const fetchEnvList = async () => {
        setLoading(true);
        try {
            // Use the correct API with typeCode from baseInfo
            console.log('Fetching env list for typeCode:', baseInfo.typeCode);
            const res = await getConnectorEnvByType(baseInfo.typeCode);
            console.log('Env list response:', res);
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
            console.error('TypeCode was:', baseInfo.typeCode);
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
                        onChange={setSelectedEnv}
                        loading={loading}
                    >
                        {envList.map(option => (
                            <Select.Option key={option.value} value={option.value}>
                                {option.label}
                            </Select.Option>
                        ))}
                    </Select>
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
