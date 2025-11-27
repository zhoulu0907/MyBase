import { Button, Form, Grid, Input, InputNumber, Select, Switch, type FormInstance } from '@arco-design/web-react';
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
 * 根据新类型转换值
 * @param value 原始值
 * @param newType 新类型
 * @param oldType 旧类型
 * @returns 转换后的值
 */
const convertValueByType = (value: any, newType: string, oldType: string): any => {
  // 如果值已经是 null 或 undefined，直接返回
  if (value === null || value === undefined) {
    return null;
  }

  // 如果新旧类型相同，直接返回原值
  if (newType === oldType) {
    return value;
  }

  // 如果新类型是 object 或 array，返回 null（因为这些类型不需要 value 字段）
  if (newType === 'object' || newType === 'array') {
    return undefined; // 返回 undefined 表示不设置 value
  }

  // 如果旧类型是 object 或 array，新类型是基本类型，返回 null
  if (oldType === 'object' || oldType === 'array') {
    return null;
  }

  // 类型转换逻辑
  try {
    switch (newType) {
      case 'string':
        return String(value);
      case 'number':
        // 尝试转换为数字
        const num = Number(value);
        return isNaN(num) ? null : num;
      case 'boolean':
        // 转换布尔值
        if (typeof value === 'string') {
          return value.toLowerCase() === 'true' || value === '1';
        }
        return Boolean(value);
      case 'null':
        return null;
      default:
        return value;
    }
  } catch (e) {
    return null;
  }
};

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
        <Col span={6}>
          <Form.Item
            field={`${fieldName}.name`}
            rules={[{ required: true, message: '请输入字段名称' }]}
            style={{ marginBottom: 0 }}
          >
            <Input placeholder="字段名称" />
          </Form.Item>
        </Col>
        <Col span={5}>
          <Form.Item
            field={`${fieldName}.type`}
            rules={[{ required: true, message: '请选择字段类型' }]}
            style={{ marginBottom: 0 }}
          >
            <Select
              placeholder="字段类型"
              options={FIELD_TYPES}
              onChange={(value) => {
                // 获取当前字段的完整数据
                const currentFieldData = form.getFieldValue(fieldName);
                const oldType = currentFieldData?.type || 'string';
                const oldValue = currentFieldData?.value;

                const newSchema = {
                  type: value,
                  ...(value === 'object' ? { properties: {} } : {}),
                  ...(value === 'array' ? { items: {} } : {})
                };
                form.setFieldValue(`${fieldName}.schema`, newSchema);
                form.setFieldValue(`${fieldName}.type`, value);

                // 如果新类型是 object 或 array，清空子字段和 value
                if (value === 'object' || value === 'array') {
                  form.setFieldValue(`${fieldName}.children`, undefined);
                  form.setFieldValue(`${fieldName}.value`, undefined);
                } else {
                  // 如果新类型是基本类型，尝试转换并保留值
                  form.setFieldValue(`${fieldName}.children`, undefined);
                  const convertedValue = convertValueByType(oldValue, value, oldType);
                  // 只有当转换后的值不为 undefined 时才设置（undefined 表示不设置 value）
                  if (convertedValue !== undefined) {
                    form.setFieldValue(`${fieldName}.value`, convertedValue);
                  } else {
                    form.setFieldValue(`${fieldName}.value`, null);
                  }
                }
              }}
            />
          </Form.Item>
        </Col>
        <Col span={9}>
          {/* 根据字段类型显示不同的默认值输入控件 */}
          {(fieldType === 'string' || !fieldType) && (
            <Form.Item field={`${fieldName}.value`} style={{ marginBottom: 0 }}>
              <Input placeholder="默认值（字符串）" />
            </Form.Item>
          )}
          {fieldType === 'number' && (
            <Form.Item field={`${fieldName}.value`} style={{ marginBottom: 0 }}>
              <InputNumber placeholder="默认值（数字）" style={{ width: '100%' }} />
            </Form.Item>
          )}
          {fieldType === 'boolean' && (
            <Form.Item field={`${fieldName}.value`} triggerPropName="checked" style={{ marginBottom: 0 }}>
              <Switch checkedText="true" uncheckedText="false" />
            </Form.Item>
          )}
          {(fieldType === 'object' || fieldType === 'array') && (
            <div style={{ padding: '4px 0', color: '#86909c', fontSize: '12px' }}>
              {fieldType === 'object' ? '对象类型无需设置默认值' : '数组类型无需设置默认值'}
            </div>
          )}
          {fieldType === 'null' && <div style={{ padding: '4px 0', color: '#86909c', fontSize: '12px' }}>null</div>}
        </Col>
        <Col span={2} style={{ display: 'flex', gap: '4px', justifyContent: 'flex-end' }}>
          <Button
            type="text"
            icon={<IconPlus />}
            onClick={() => onAdd(index)}
            style={{ padding: '4px 8px' }}
            title="添加字段"
          />
        </Col>
        <Col span={2} style={{ display: 'flex', gap: '4px', justifyContent: 'flex-start' }}>
          <Button
            type="text"
            status="danger"
            icon={<IconDelete />}
            onClick={() => onRemove(index)}
            style={{ padding: '4px 8px' }}
            title="删除字段"
          />
        </Col>
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
        <Col span={5}>
          <Form.Item
            field={`${fieldName}.type`}
            rules={[{ required: true, message: '请选择字段类型' }]}
            style={{ marginBottom: 0 }}
          >
            <Select
              placeholder="字段类型"
              options={FIELD_TYPES}
              onChange={(value) => {
                // 获取当前数组项的完整数据
                const currentItemData = form.getFieldValue(fieldName);
                const oldType = currentItemData?.type || 'string';
                const oldValue = currentItemData?.value;

                const newSchema = {
                  type: value,
                  ...(value === 'object' ? { properties: {} } : {}),
                  ...(value === 'array' ? { items: {} } : {})
                };
                form.setFieldValue(`${fieldName}.schema`, newSchema);
                form.setFieldValue(`${fieldName}.type`, value);

                // 如果新类型是 object 或 array，清空子字段和 value
                if (value === 'object' || value === 'array') {
                  form.setFieldValue(`${fieldName}.children`, undefined);
                  form.setFieldValue(`${fieldName}.value`, undefined);
                } else {
                  // 如果新类型是基本类型，尝试转换并保留值
                  form.setFieldValue(`${fieldName}.children`, undefined);
                  const convertedValue = convertValueByType(oldValue, value, oldType);
                  // 只有当转换后的值不为 undefined 时才设置（undefined 表示不设置 value）
                  if (convertedValue !== undefined) {
                    form.setFieldValue(`${fieldName}.value`, convertedValue);
                  } else {
                    form.setFieldValue(`${fieldName}.value`, null);
                  }
                }
              }}
            />
          </Form.Item>
        </Col>
        <Col span={9}>
          {/* 根据字段类型显示不同的默认值输入控件 */}
          {(itemType === 'string' || !itemType) && (
            <Form.Item field={`${fieldName}.value`} style={{ marginBottom: 0 }}>
              <Input placeholder="默认值（字符串）" />
            </Form.Item>
          )}
          {itemType === 'number' && (
            <Form.Item field={`${fieldName}.value`} style={{ marginBottom: 0 }}>
              <InputNumber placeholder="默认值（数字）" style={{ width: '100%' }} />
            </Form.Item>
          )}
          {itemType === 'boolean' && (
            <Form.Item field={`${fieldName}.value`} triggerPropName="checked" style={{ marginBottom: 0 }}>
              <Switch checkedText="true" uncheckedText="false" />
            </Form.Item>
          )}
          {(itemType === 'object' || itemType === 'array') && (
            <div style={{ padding: '4px 0', color: '#86909c', fontSize: '12px' }}>
              {itemType === 'object' ? '对象类型无需设置默认值' : '数组类型无需设置默认值'}
            </div>
          )}
          {itemType === 'null' && <div style={{ padding: '4px 0', color: '#86909c', fontSize: '12px' }}>null</div>}
        </Col>
        <Col span={2} style={{ display: 'flex', gap: '4px', justifyContent: 'flex-end' }}>
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
        <Col span={2}></Col>
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
