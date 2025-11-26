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

// 对象类型字段项组件
interface ObjectFieldItemProps {
  field: any;
  index: number;
  form: FormInstance;
  level: number;
  onAdd: (index: number) => void;
  onRemove: (index: number) => void;
}

const ObjectFieldItem: React.FC<ObjectFieldItemProps> = ({ field, index, form, level, onAdd, onRemove }) => {
  const fieldName = field.field;
  const fieldType = Form.useWatch(`${fieldName}.type`, form);
  const fieldSchema = Form.useWatch(`${fieldName}.schema`, form) || { type: fieldType || 'string' };

  return (
    <div
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
        <Col span={4} style={{ display: 'flex', gap: '4px', justifyContent: 'flex-end' }}>
          <Button
            type="text"
            icon={<IconPlus />}
            onClick={() => onAdd(index)}
            style={{ padding: '4px 8px' }}
            title="添加字段"
          />
          <Button
            type="text"
            status="danger"
            icon={<IconDelete />}
            onClick={() => onRemove(index)}
            style={{ padding: '4px 8px' }}
            title="删除字段"
          />
        </Col>
        <Col span={4}></Col>
      </Row>

      {/* 递归渲染嵌套的表单 */}
      {(fieldType === 'object' || fieldType === 'array') && fieldType && (
        <div style={{ marginTop: 8, marginLeft: 20 }}>
          <SchemaForm form={form} schema={fieldSchema} fieldPrefix={`${fieldName}.children`} level={level + 1} />
        </div>
      )}
    </div>
  );
};

// 数组类型表单列表包装组件
interface ArrayFormListProps {
  fieldPrefix: string;
  form: FormInstance;
  level: number;
  itemsSchema: any;
}

const ArrayFormList: React.FC<ArrayFormListProps> = ({ fieldPrefix, form, level, itemsSchema }) => {
  const listFieldName = fieldPrefix;
  const initializedRef = React.useRef(false);

  // 如果数组为空，自动添加一个项
  React.useEffect(() => {
    if (!initializedRef.current) {
      const currentValue = form.getFieldValue(listFieldName);
      if (!currentValue || currentValue.length === 0) {
        // 初始化一个默认项
        form.setFieldValue(listFieldName, [
          {
            type: itemsSchema.type || 'string',
            schema: itemsSchema
          }
        ]);
      }
      initializedRef.current = true;
    }
  }, [listFieldName, itemsSchema, form]);

  return (
    <div style={{ marginLeft: level > 0 ? level * 20 : 0 }}>
      <Form.List field={listFieldName}>
        {(fields) => {
          return (
            <>
              {fields.map((field, index) => (
                <ArrayFieldItem
                  key={field.key}
                  field={field}
                  index={index}
                  form={form}
                  level={level}
                  itemsSchema={itemsSchema}
                  onRemove={() => {
                    // 数组类型不允许删除，至少需要保留1项
                    return;
                  }}
                />
              ))}
            </>
          );
        }}
      </Form.List>
    </div>
  );
};

// 数组类型字段项组件
interface ArrayFieldItemProps {
  field: any;
  index: number;
  form: FormInstance;
  level: number;
  itemsSchema: any;
  onRemove: (index: number) => void;
}

const ArrayFieldItem: React.FC<ArrayFieldItemProps> = ({ field, index, form, level, itemsSchema, onRemove }) => {
  const fieldName = field.field;
  const itemType = Form.useWatch(`${fieldName}.type`, form);
  const itemSchema = Form.useWatch(`${fieldName}.schema`, form) || itemsSchema || { type: itemType || 'string' };

  return (
    <div style={{ marginBottom: 16, paddingLeft: 12, borderLeft: '2px solid #e5e6eb' }}>
      <Row gutter={8}>
        <Col span={6}>
          <div style={{ padding: '4px 0', color: '#86909c' }}>字段 </div>
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
        <Col span={4} style={{ display: 'flex', gap: '4px', justifyContent: 'flex-end' }}>
          <Button
            type="text"
            status="danger"
            icon={<IconDelete />}
            onClick={() => onRemove(index)}
            disabled
            style={{ padding: '4px 8px', opacity: 0.5, cursor: 'not-allowed' }}
            title="数组类型至少需要保留1项"
          />
        </Col>
        <Col span={4}></Col>
      </Row>

      {/* 递归渲染嵌套的表单 */}
      {(itemType === 'object' || itemType === 'array') && itemType && (
        <div style={{ marginTop: 8, marginLeft: 20 }}>
          <SchemaForm form={form} schema={itemSchema} fieldPrefix={`${fieldName}.children`} level={level + 1} />
        </div>
      )}
    </div>
  );
};

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
    const initializedRef = React.useRef(false);

    // 如果对象为空，自动添加一个字段
    React.useEffect(() => {
      if (!initializedRef.current) {
        const currentValue = form.getFieldValue(listFieldName);
        if (!currentValue || currentValue.length === 0) {
          // 初始化一个默认字段
          form.setFieldValue(listFieldName, [
            {
              name: '',
              type: 'string',
              schema: { type: 'string' }
            }
          ]);
        }
        initializedRef.current = true;
      }
    }, [listFieldName, form]);

    return (
      <div style={{ marginLeft: level > 0 ? level * 20 : 0 }}>
        <Form.List field={listFieldName}>
          {(fields, { add, remove }) => {
            return (
              <>
                {fields.map((field, index) => (
                  <ObjectFieldItem
                    key={field.key}
                    field={field}
                    index={index}
                    form={form}
                    level={level}
                    onAdd={(currentIndex) => {
                      // 在当前行的下一行插入新字段
                      add(
                        {
                          name: '',
                          type: 'string',
                          schema: { type: 'string' }
                        },
                        currentIndex + 1
                      );
                    }}
                    onRemove={remove}
                  />
                ))}
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

    return <ArrayFormList fieldPrefix={fieldPrefix} form={form} level={level} itemsSchema={itemsSchema} />;
  }

  return null;
};

export default SchemaForm;
