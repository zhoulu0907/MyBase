import { Button, Form, Grid, Input, InputNumber, Select, Switch, type FormInstance } from '@arco-design/web-react';
import { IconDelete, IconPlus } from '@arco-design/web-react/icon';
import { ENTITY_FIELD_TYPE } from '@onebase/ui-kit';
import React from 'react';

const Row = Grid.Row;
const Col = Grid.Col;

interface SchemaFormProps {
  form: FormInstance;
  schema: any;
  fieldPrefix?: string;
  level?: number;
}

// 从 ENTITY_FIELD_TYPE 中提取需要的字段类型
const FIELD_TYPES = [
  ENTITY_FIELD_TYPE.ID,
  ENTITY_FIELD_TYPE.EMAIL,
  ENTITY_FIELD_TYPE.PHONE,
  ENTITY_FIELD_TYPE.URL,
  ENTITY_FIELD_TYPE.ADDRESS,
  ENTITY_FIELD_TYPE.NUMBER,
  ENTITY_FIELD_TYPE.DATE,
  ENTITY_FIELD_TYPE.DATETIME,
  ENTITY_FIELD_TYPE.LONG_TEXT,
  ENTITY_FIELD_TYPE.TEXT,
  ENTITY_FIELD_TYPE.BOOLEAN
].map((field) => ({
  label: field.LABEL,
  value: field.VALUE
}));

/**
 * 判断类型是否为字符串类型
 */
const isStringType = (type: string): boolean => {
  return ['ID', 'TEXT', 'EMAIL', 'PHONE', 'URL', 'ADDRESS', 'LONG_TEXT'].includes(type);
};

/**
 * 判断类型是否为数字类型
 */
const isNumberType = (type: string): boolean => {
  return ['NUMBER'].includes(type);
};

/**
 * 判断类型是否为日期类型
 */
const isDateType = (type: string): boolean => {
  return ['DATE', 'DATETIME'].includes(type);
};

/**
 * 判断类型是否为布尔类型
 */
const isBooleanType = (type: string): boolean => {
  return type === 'BOOLEAN';
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
  const fieldSchema = Form.useWatch(`${fieldName}.schema`, form) || { type: fieldType || 'TEXT' };

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

                const newSchema = {
                  type: value,
                  ...(value === 'object' ? { properties: {} } : {}),
                  ...(value === 'array' ? { items: {} } : {})
                };

                // 构建新的字段数据
                const updatedFieldData: any = {
                  schema: newSchema,
                  type: value,
                  children: undefined
                };

                updatedFieldData.value = undefined;

                // 保留字段名称
                if (currentFieldData?.name !== undefined) {
                  updatedFieldData.name = currentFieldData.name;
                }

                // 一次性更新整个字段数据
                form.setFieldValue(fieldName, updatedFieldData);
              }}
            />
          </Form.Item>
        </Col>
        {/* 默认值 */}
        <Col span={9}>
          {/* 根据字段类型显示不同的默认值输入控件 */}
          {(!fieldType || isStringType(fieldType) || isDateType(fieldType)) && (
            <Form.Item field={`${fieldName}.value`} style={{ marginBottom: 0 }}>
              <Input placeholder="默认值" />
            </Form.Item>
          )}
          {isNumberType(fieldType) && (
            <Form.Item field={`${fieldName}.value`} style={{ marginBottom: 0 }}>
              <InputNumber placeholder="默认值（数字）" style={{ width: '100%' }} />
            </Form.Item>
          )}
          {isBooleanType(fieldType) && (
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
        const itemsType = itemsSchema.type || 'TEXT';
        form.setFieldValue(listFieldName, [
          {
            type: itemsType,
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
  let itemType = Form.useWatch(`${fieldName}.type`, form);
  const itemSchema = Form.useWatch(`${fieldName}.schema`, form) || itemsSchema || { type: itemType || 'TEXT' };

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
                const oldType = currentItemData?.type;

                const newSchema = {
                  type: value,
                  ...(value === 'object' ? { properties: {} } : {}),
                  ...(value === 'array' ? { items: {} } : {})
                };

                // 只要类型值发生变化（包括 FIELD_TYPES 中的类型变化，如 ADDRESS 转 URL），就清空默认值
                const typeChanged = oldType !== value;

                // 构建新的字段数据
                const updatedItemData: any = {
                  schema: newSchema,
                  type: value,
                  children: undefined
                };

                // 如果类型变化了，清空默认值；如果新类型是 object 或 array，也清空 value
                if (typeChanged || value === 'object' || value === 'array') {
                  updatedItemData.value = undefined;
                  // 显式清空表单中的 value 字段
                  form.setFieldValue(`${fieldName}.value`, undefined);
                } else {
                  // 类型没变化，保留原有的 value
                  updatedItemData.value = currentItemData?.value;
                }

                // 一次性更新整个字段数据
                form.setFieldValue(fieldName, updatedItemData);
              }}
            />
          </Form.Item>
        </Col>
        <Col span={9}>
          {/* 根据字段类型显示不同的默认值输入控件 */}
          {(!itemType || isStringType(itemType) || isDateType(itemType)) && (
            <Form.Item field={`${fieldName}.value`} style={{ marginBottom: 0 }}>
              <Input placeholder="默认值" />
            </Form.Item>
          )}
          {isNumberType(itemType) && (
            <Form.Item field={`${fieldName}.value`} style={{ marginBottom: 0 }}>
              <InputNumber placeholder="默认值（数字）" style={{ width: '100%' }} />
            </Form.Item>
          )}
          {isBooleanType(itemType) && (
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
              type: 'TEXT',
              schema: { type: 'TEXT' }
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
                          type: 'TEXT',
                          schema: { type: 'TEXT' }
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
