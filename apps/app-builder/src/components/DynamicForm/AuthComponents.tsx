import React from 'react';


// 认证设置容器组件 (灰色背景盒子)
export const AuthSettingsCard: React.FC<any> = ({ children, title }) => {
    return (
        <div style={{
            background: 'var(--color-fill-2)',
            padding: '16px',
            borderRadius: '4px',
            marginTop: '8px'
        }}>
            {title && <div style={{ marginBottom: '16px', fontWeight: 500, fontSize: '14px' }}>{title}</div>}
            {children}
        </div>
    );
};



import { Button, Input, Popconfirm, Grid, Space } from '@arco-design/web-react';
import { IconDelete, IconPlus } from '@arco-design/web-react/icon';

const { Row, Col } = Grid;

// Key-Value 列表组件，支持添加、删除、清空
export const KeyValueList: React.FC<any> = ({ value = [], onChange }) => {
    // value format: [{ key: '', value: '' }]

    const handleAdd = () => {
        const newValue = [...value, { key: '', value: '' }];
        onChange?.(newValue);
    };

    const handleDelete = (index: number) => {
        const newValue = [...value];
        newValue.splice(index, 1);
        onChange?.(newValue);
    };

    const handleClear = () => {
        onChange?.([]);
    };

    const handleChange = (index: number, field: string, val: string) => {
        const newValue = [...value];
        newValue[index] = { ...newValue[index], [field]: val };
        onChange?.(newValue);
    };

    return (
        <div style={{ width: '100%' }}>
            {/* Header Row - Only show if there are items or just to show structure */}
            <div style={{ padding: '0 0 8px 0', borderBottom: '1px dashed var(--color-border-2)', marginBottom: 12 }}>
                <Row gutter={12}>
                    <Col span={10} style={{ color: 'var(--color-text-3)', fontSize: 12 }}>KEY</Col>
                    <Col span={10} style={{ color: 'var(--color-text-3)', fontSize: 12 }}>VALUE</Col>
                    <Col span={4}></Col>
                </Row>
            </div>

            {/* List Rows */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: 12, marginBottom: 12 }}>
                {value.map((item: any, index: number) => (
                    <Row key={index} gutter={12} align="center">
                        <Col span={10}>
                            <Input
                                placeholder="Key"
                                value={item.key}
                                onChange={(val) => handleChange(index, 'key', val)}
                            />
                        </Col>
                        <Col span={10}>
                            <Input
                                placeholder="Value"
                                value={item.value}
                                onChange={(val) => handleChange(index, 'value', val)}
                            />
                        </Col>
                        <Col span={4}>
                            <Button
                                type="text"
                                status="danger"
                                icon={<IconDelete />}
                                onClick={() => handleDelete(index)}
                                style={{ padding: '0 8px' }}
                            />
                        </Col>
                    </Row>
                ))}
            </div>

            {/* Actions */}
            <Space size={12}>
                <Button onClick={handleAdd} type="dashed" icon={<IconPlus />}>
                    添加参数
                </Button>

                {value.length > 0 && (
                    <Popconfirm
                        title="确定要清空所有字段吗？"
                        onOk={handleClear}
                    >
                        <Button type="text" status="danger" icon={<IconDelete />}>
                            清空
                        </Button>
                    </Popconfirm>
                )}
            </Space>
        </div>
    );
};
