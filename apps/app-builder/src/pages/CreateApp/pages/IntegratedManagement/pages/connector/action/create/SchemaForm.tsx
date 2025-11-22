import { Button, Form, Grid, Input, Select, type FormInstance } from '@arco-design/web-react';
import { IconDelete, IconPlus } from '@arco-design/web-react/icon';
import React from 'react';

const Row = Grid.Row;
const Col = Grid.Col;

interface SchemaFormProps {
  form: FormInstance;
  schema: any;
  fieldPrefix?: string;
  level?: number;
}

const FIELD_TYPES = [
  { label: '字符串', value: 'string' },
  { label: '数字', value: 'number' },
  { label: '布尔值', value: 'boolean' },
  { label: '对象', value: 'object' },
  { label: '数组', value: 'array' },
  { label: '空值', value: 'null' }
];

/**
 * 基于 JSON Schema 动态生成递归表单
 * 支持动态添加、删除字段，支持递归处理嵌套结构
 * 每行显示：字段名称、字段类型、操作（添加、删除）
 */
const SchemaForm: React.FC<SchemaFormProps> = ({ form, schema, fieldPrefix = 'schemaForm', level = 0 }) => {
  if (!schema || !schema.type) {
    return null;
  }

  // 处理 object 类型
  if (schema.type === 'object') {
    const listFieldName = fieldPrefix;

    return (
      <div style={{ marginLeft: level > 0 ? level * 20 : 0 }}>
        <Form.List field={listFieldName}>
          {(fields, { add, remove }) => {
            return (
              <>
                {fields.map((field, index) => {
                  const fieldName = field.field;
                  const fieldType = Form.useWatch(`${fieldName}.type`, form);
                  const fieldSchema = Form.useWatch(`${fieldName}.schema`, form) || { type: fieldType || 'string' };

                  return (
                    <div
                      key={field.key}
                      style={{
                        marginBottom: 16,
                        paddingLeft: level > 0 ? 12 : 0,
                        borderLeft: level > 0 ? '2px solid #e5e6eb' : 'none'
                      }}
                    >
                      <Row gutter={8}>
                        <Col span={8}>
                          <Form.Item
                            field={`${fieldName}.name`}
                            rules={[{ required: true, message: '请输入字段名称' }]}
                            style={{ marginBottom: 0 }}
                          >
                            <Input placeholder="字段名称" />
                          </Form.Item>
                        </Col>
                        <Col span={8}>
                          <Form.Item
                            field={`${fieldName}.type`}
                            rules={[{ required: true, message: '请选择字段类型' }]}
                            style={{ marginBottom: 0 }}
                          >
                            <Select
                              placeholder="字段类型"
                              options={FIELD_TYPES}
                              onChange={(value) => {
                                const newSchema = {
                                  type: value,
                                  ...(value === 'object' ? { properties: {} } : {}),
                                  ...(value === 'array' ? { items: {} } : {})
                                };
                                form.setFieldValue(`${fieldName}.schema`, newSchema);
                                // 清空子字段数据
                                form.setFieldValue(`${fieldName}.children`, undefined);
                              }}
                            />
                          </Form.Item>
                        </Col>
                        <Col span={6}>
                          <Button
                            type="text"
                            status="danger"
                            icon={<IconDelete />}
                            onClick={() => remove(index)}
                            style={{ padding: '4px 8px' }}
                          />
                        </Col>
                        <Col span={2}></Col>
                      </Row>

                      {/* 递归渲染嵌套的表单 */}
                      {(fieldType === 'object' || fieldType === 'array') && fieldType && (
                        <div style={{ marginTop: 8, marginLeft: 20 }}>
                          <SchemaForm
                            form={form}
                            schema={fieldSchema}
                            fieldPrefix={`${fieldName}.children`}
                            level={level + 1}
                          />
                        </div>
                      )}
                    </div>
                  );
                })}

                <Row>
                  <Col span={24}>
                    <Button
                      type="dashed"
                      icon={<IconPlus />}
                      onClick={() => {
                        add({
                          name: '',
                          type: 'string',
                          schema: { type: 'string' }
                        });
                      }}
                      style={{ width: '100%', marginTop: 8 }}
                    >
                      添加字段
                    </Button>
                  </Col>
                </Row>
              </>
            );
          }}
        </Form.List>
      </div>
    );
  }

  // 处理 array 类型
  if (schema.type === 'array') {
    const itemsSchema = schema.items || {};
    const listFieldName = fieldPrefix;

    return (
      <div style={{ marginLeft: level > 0 ? level * 20 : 0 }}>
        <Form.List field={listFieldName}>
          {(fields, { add, remove }) => {
            return (
              <>
                {fields.map((field, index) => {
                  const fieldName = field.field;
                  const itemType = Form.useWatch(`${fieldName}.type`, form);
                  const itemSchema = Form.useWatch(`${fieldName}.schema`, form) ||
                    itemsSchema || { type: itemType || 'string' };

                  return (
                    <div key={field.key} style={{ marginBottom: 16, paddingLeft: 12, borderLeft: '2px solid #e5e6eb' }}>
                      <Row gutter={8}>
                        <Col span={6}>
                          <div style={{ padding: '4px 0', color: '#86909c' }}>项 {index + 1}</div>
                        </Col>
                        <Col span={10}>
                          <Form.Item
                            field={`${fieldName}.type`}
                            rules={[{ required: true, message: '请选择字段类型' }]}
                            style={{ marginBottom: 0 }}
                          >
                            <Select
                              placeholder="字段类型"
                              options={FIELD_TYPES}
                              onChange={(value) => {
                                const newSchema = {
                                  type: value,
                                  ...(value === 'object' ? { properties: {} } : {}),
                                  ...(value === 'array' ? { items: {} } : {})
                                };
                                form.setFieldValue(`${fieldName}.schema`, newSchema);
                                // 清空子字段数据
                                form.setFieldValue(`${fieldName}.children`, undefined);
                              }}
                            />
                          </Form.Item>
                        </Col>
                        <Col span={6}>
                          <Button
                            type="text"
                            status="danger"
                            icon={<IconDelete />}
                            onClick={() => remove(index)}
                            style={{ padding: '4px 8px' }}
                          />
                        </Col>
                        <Col span={2}></Col>
                      </Row>

                      {/* 递归渲染嵌套的表单 */}
                      {(itemType === 'object' || itemType === 'array') && itemType && (
                        <div style={{ marginTop: 8, marginLeft: 20 }}>
                          <SchemaForm
                            form={form}
                            schema={itemSchema}
                            fieldPrefix={`${fieldName}.children`}
                            level={level + 1}
                          />
                        </div>
                      )}
                    </div>
                  );
                })}

                <Row>
                  <Col span={24}>
                    <Button
                      type="dashed"
                      icon={<IconPlus />}
                      onClick={() => {
                        add({
                          type: itemsSchema.type || 'string',
                          schema: itemsSchema
                        });
                      }}
                      style={{ width: '100%', marginTop: 8 }}
                    >
                      添加项
                    </Button>
                  </Col>
                </Row>
              </>
            );
          }}
        </Form.List>
      </div>
    );
  }

  return null;
};

export default SchemaForm;
