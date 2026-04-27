import ResizableTable from '@/components/ResizableTable';
import React from 'react';
import { Input, Select, Tabs, Button, Space, Grid, Checkbox, Switch } from '@arco-design/web-react';
import { useForm } from '@formily/react';
import { KeyValueList } from './AuthComponents';

const { Row, Col } = Grid;
const TabPane = Tabs.TabPane;

export const TokenAuthPanel: React.FC = () => {
    const form = useForm();

    // Field names mapping
    const FIELDS = {
        METHOD: 'token_method',
        URL: 'token_url',
        PARAMS_HEADER: 'token_params_header',
        PARAMS_BODY: 'token_params_body',
        PARAMS_QUERY: 'token_params_query',
        PARAMS_PATH: 'token_params_path',
        TOKEN_PATH: 'token_result_path',
        REFRESH_POLICY: 'token_refresh_policy'
    };

    // Helper to get/set form values
    const useFieldValue = (path: string, defaultValue: any) => {
        const val = form.values[path];
        return val !== undefined ? val : defaultValue;
    };

    // Mock Data for the Response Table
    const tableData = [
        {
            key: '1',
            fieldKey: 'result',
            fieldType: 'object',
            token: false,
            children: [
                {
                    key: '1-1',
                    fieldKey: 'token',
                    fieldType: 'string',
                    token: true,
                },
            ],
        },
    ];

    const columns = [
        {
            title: '字段Key',
            dataIndex: 'fieldKey',
            render: (col: any) => (
                <Input value={col} style={{ width: '100%' }} size="small" readOnly />
            ),
        },
        {
            title: '字段类型',
            dataIndex: 'fieldType',
            render: (col: any) => (
                <Select value={col} size="small" style={{ width: 100 }}>
                    <Select.Option value="object">object</Select.Option>
                    <Select.Option value="string">string</Select.Option>
                    <Select.Option value="number">number</Select.Option>
                </Select>
            ),
        },
        {
            title: '操作',
            dataIndex: 'op',
            render: (_: any, item: any) => (
                <Space size={4} style={{ fontSize: 12 }}>
                    <span style={{ color: 'var(--color-primary-6)', cursor: 'pointer' }}>添加</span>
                    {item.key === '1-1' && (
                        <span style={{ color: 'var(--color-primary-6)', cursor: 'pointer' }}>删除</span>
                    )}
                </Space>
            ),
        },
        {
            title: 'token',
            dataIndex: 'token',
            render: (col: any) => (
                <Checkbox checked={col} disabled={!col} />
            ),
        },
    ];

    return (
        <div style={{ padding: '0 4px' }}>
            {/* 1. Interface Address */}
            <div style={{ marginBottom: 24 }}>
                <div style={{ marginBottom: 8, color: 'var(--color-text-2)' }}>接口地址</div>
                <Input.Group style={{ width: '100%' }}>
                    <Select
                        style={{ width: '20%' }}
                        defaultValue="GET"
                        value={useFieldValue(FIELDS.METHOD, 'GET')}
                        onChange={v => form.setValuesIn(FIELDS.METHOD, v)}
                    >
                        <Select.Option value="GET">GET</Select.Option>
                        <Select.Option value="POST">POST</Select.Option>
                    </Select>
                    <Input
                        style={{ width: '80%' }}
                        placeholder="请输入接口地址"
                        value={useFieldValue(FIELDS.URL, '')}
                        onChange={v => form.setValuesIn(FIELDS.URL, v)}
                    />
                </Input.Group>
            </div>

            {/* 2. Set Parameters */}
            <div style={{ marginBottom: 24 }}>
                <div style={{ marginBottom: 12, color: 'var(--color-text-2)' }}>设置参数</div>
                <Tabs defaultActiveTab="query">
                    <TabPane key="header" title="HTTP请求头">
                        <KeyValueList
                            value={useFieldValue(FIELDS.PARAMS_HEADER, [])}
                            onChange={(v: any) => form.setValuesIn(FIELDS.PARAMS_HEADER, v)}
                        />
                    </TabPane>
                    <TabPane key="body" title="HTTP请求体">
                        <KeyValueList
                            value={useFieldValue(FIELDS.PARAMS_BODY, [])}
                            onChange={(v: any) => form.setValuesIn(FIELDS.PARAMS_BODY, v)}
                        />
                    </TabPane>
                    <TabPane key="query" title="URL查询参数">
                        <KeyValueList
                            value={useFieldValue(FIELDS.PARAMS_QUERY, [])}
                            onChange={(v: any) => form.setValuesIn(FIELDS.PARAMS_QUERY, v)}
                        />
                    </TabPane>
                    <TabPane key="path" title="URL路径参数">
                        <KeyValueList
                            value={useFieldValue(FIELDS.PARAMS_PATH, [])}
                            onChange={(v: any) => form.setValuesIn(FIELDS.PARAMS_PATH, v)}
                        />
                    </TabPane>
                </Tabs>
            </div>

            {/* 3. Response Result Area */}
            <div style={{ marginBottom: 24 }}>
                {/* Tab Header Style */}
                <div style={{
                    borderBottom: '2px solid var(--color-primary-6)',
                    display: 'inline-block',
                    padding: '8px 16px',
                    fontWeight: 500,
                    marginBottom: 1
                }}>
                    响应结果
                </div>
                <div style={{ borderTop: '1px solid var(--color-border-2)', paddingTop: 16 }}>

                    {/* Toolbar */}
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16, alignItems: 'center' }}>
                        <Button size="small">复制</Button>
                        <Space>
                            <span style={{ fontSize: 12, color: 'var(--color-text-3)' }}>Json模式</span>
                            <Switch size="small" />
                        </Space>
                    </div>

                    {/* Tree Table */}
                    <div style={{ background: 'var(--color-fill-1)', padding: 16, borderRadius: 4 }}>
                        <ResizableTable
                            columns={columns}
                            data={tableData}
                            pagination={false}
                            border={false}
                            defaultExpandAllRows={true}
                            rowKey="key"
                        />
                    </div>

                    {/* Token & Refresh Settings */}
                    <div style={{ marginTop: 24 }}>
                        <Row gutter={24}>
                            <Col span={12}>
                                <div style={{ marginBottom: 8, color: 'var(--color-text-2)' }}>Token</div>
                                <Input
                                    placeholder="$.result.token"
                                    disabled
                                    value={useFieldValue(FIELDS.TOKEN_PATH, '')}
                                    style={{ background: 'var(--color-fill-2)' }}
                                    suffix={<span style={{ color: 'var(--color-text-3)' }}>icon</span>}
                                />
                            </Col>
                            <Col span={12}>
                                <div style={{ marginBottom: 8, color: 'var(--color-text-2)' }}>Token刷新设置</div>
                                <Select
                                    placeholder="每次请求"
                                    value={useFieldValue(FIELDS.REFRESH_POLICY, 'always')}
                                    onChange={v => form.setValuesIn(FIELDS.REFRESH_POLICY, v)}
                                >
                                    <Select.Option value="always">每次请求</Select.Option>
                                    <Select.Option value="expire">过期刷新</Select.Option>
                                </Select>
                            </Col>
                        </Row>
                    </div>
                </div>
            </div>

            {/* 4. Verification Button */}
            <div style={{ textAlign: 'center', marginBottom: 24 }}>
                <Button type="default">认证验证</Button>
            </div>

            {/* 5. Debug Info Area */}
            <div style={{ background: 'var(--color-fill-1)', padding: 16, borderRadius: 4 }}>
                <div style={{ color: 'var(--color-primary-6)', marginBottom: 12, fontSize: 13, fontWeight: 500 }}>
                    已认证，请自行检查返回结果是否符合预期
                </div>
                <Tabs type="line">
                    <TabPane key="req" title="Token请求参数">
                        <Input.TextArea
                            autoSize={{ minRows: 6 }}
                            style={{ border: 'none', background: 'white' }}
                        />
                    </TabPane>
                    <TabPane key="res" title="Token返回结果">
                        <Input.TextArea
                            autoSize={{ minRows: 6 }}
                            style={{ border: 'none', background: 'white' }}
                        />
                    </TabPane>
                </Tabs>
            </div>
        </div>
    );
};
