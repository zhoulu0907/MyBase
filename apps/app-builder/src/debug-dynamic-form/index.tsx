import React, { useState, useMemo, useEffect } from 'react';
import { createForm } from '@formily/core';
import { Card, Grid, Input, Button, Message, Space, Typography, Select } from '@arco-design/web-react';
import DynamicForm from '@/components/DynamicForm';
import { DEFAULT_SCHEMA } from './schemas/default';
import { MULTI_STEP_SCHEMA } from './schemas/multi-step';

const { Row, Col } = Grid;
const { Option } = Select;

const PRESET_SCHEMAS: Record<string, { label: string; schema: any }> = {
  default: {
    label: '基础示例',
    schema: DEFAULT_SCHEMA,
  },
  multi: {
    label: '多阶段表单',
    schema: MULTI_STEP_SCHEMA,
  },
};

const DynamicFormDebug = () => {
  const [activePreset, setActivePreset] = useState<'default' | 'multi'>('default');
  const [jsonStr, setJsonStr] = useState(JSON.stringify(PRESET_SCHEMAS.default.schema, null, 2));
  const [schema, setSchema] = useState(PRESET_SCHEMAS.default.schema);
  const [error, setError] = useState('');

  const form = useMemo(() => createForm(), []);

  const handlePresetChange = (value: string) => {
    const preset = PRESET_SCHEMAS[value];
    if (!preset) {
      return;
    }
    setActivePreset(value as any);
    setSchema(preset.schema);
    setJsonStr(JSON.stringify(preset.schema, null, 2));
    setError('');
  };

  const handleJsonChange = (value: string) => {
    setJsonStr(value);
    try {
      const parsed = JSON.parse(value);
      setSchema(parsed);
      setError('');
    } catch (e: any) {
      setError(e.message);
    }
  };

  const handleReset = () => {
      form.reset();
  };

  const handleSubmit = async () => {
      try {
          const values = await form.submit();
          console.log(values);
          Message.success('提交成功，请查看控制台输出');
      } catch (e) {
          console.error(e);
          Message.error('表单校验失败');
      }
  };

  return (
    <div style={{ padding: 20, height: '100vh', boxSizing: 'border-box', background: '#f0f2f5' }}>
      <Row gutter={20} style={{ height: '100%' }}>
        <Col span={12} style={{ height: '100%' }}>
          <Card 
            title="Schema JSON 编辑" 
            style={{ height: '100%', display: 'flex', flexDirection: 'column' }}
            bodyStyle={{ flex: 1, display: 'flex', flexDirection: 'column', padding: 0 }}
          >
            <div style={{ padding: '8px 12px', borderBottom: '1px solid #f0f0f0' }}>
              <Space size="small">
                <Typography.Text>选择示例</Typography.Text>
                <Select
                  size="small"
                  style={{ width: 220 }}
                  value={activePreset}
                  onChange={handlePresetChange}
                >
                  {Object.entries(PRESET_SCHEMAS).map(([key, item]) => (
                    <Option key={key} value={key}>
                      {item.label}
                    </Option>
                  ))}
                </Select>
              </Space>
            </div>
            <div style={{ flex: 1, position: 'relative' }}>
                <Input.TextArea
                    value={jsonStr}
                    onChange={handleJsonChange}
                    style={{ 
                        width: '100%', 
                        height: '100%', 
                        resize: 'none', 
                        border: 'none',
                        padding: 12,
                        fontFamily: 'monospace',
                        fontSize: 14,
                        backgroundColor: '#fafafa'
                    }}
                />
            </div>
            {error && (
                <div style={{ padding: '8px 16px', color: 'red', borderTop: '1px solid #eee' }}>
                    JSON 解析错误: {error}
                </div>
            )}
          </Card>
        </Col>
        <Col span={12} style={{ height: '100%' }}>
          <Card 
            title="动态表单预览" 
            style={{ height: '100%', display: 'flex', flexDirection: 'column' }}
            extra={
                <Space>
                    <Button onClick={handleReset}>重置表单</Button>
                    <Button type="primary" onClick={handleSubmit}>获取数据</Button>
                </Space>
            }
            bodyStyle={{ flex: 1, overflowY: 'auto' }}
          >
            <DynamicForm schema={schema} form={form} />
            
            <div style={{ marginTop: 24, padding: 12, background: '#f5f5f5', borderRadius: 4 }}>
                <Typography.Text type="secondary">当前表单数据 (实时):</Typography.Text>
                <FormValuesDisplay form={form} />
            </div>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

const FormValuesDisplay = ({ form }: { form: any }) => {
  const [values, setValues] = useState({});

  useEffect(() => {
    const id = form.subscribe(() => {
      setValues({ ...form.values });
    });
    return () => {
      form.unsubscribe(id);
    };
  }, [form]);

  return (
    <pre style={{ margin: '8px 0 0', fontSize: 12 }}>
      {JSON.stringify(values, null, 2)}
    </pre>
  );
};

export default DynamicFormDebug;
