import {
  Button,
  DatePicker,
  Form,
  Grid,
  Input,
  InputNumber,
  Select,
  Switch,
  TimePicker,
  type FormInstance
} from '@arco-design/web-react';
import { IconDelete, IconPlus } from '@arco-design/web-react/icon';
import { ENTITY_FIELD_TYPE } from '@onebase/ui-kit';
import { useEffect } from 'react';

interface SchemaFormProps {
  form: FormInstance;
  schema: any[];
  fieldPrefix: string;
}
const SchemaForm: React.FC<SchemaFormProps> = ({ form, schema, fieldPrefix = 'schemaForm' }) => {
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
  const fieldList = Form.useWatch(fieldPrefix, form);

  // 如果对象为空，自动添加一个字段
  useEffect(() => {
    const currentValue = form.getFieldValue(fieldPrefix);
    if (!currentValue || currentValue.length === 0) {
      // 初始化一个默认字段
      form.setFieldValue(fieldPrefix, [
        {
          name: '',
          type: 'TEXT'
        }
      ]);
    }
  }, [fieldPrefix, form]);

  const renderDefault = (field: any, index: number) => {
    const fieldType = fieldList[index].type;
    switch (fieldType) {
      case ENTITY_FIELD_TYPE.NUMBER.VALUE:
        return (
          <Form.Item field={field.field + '.value'}>
            <InputNumber placeholder="默认值" />
          </Form.Item>
        );
      case ENTITY_FIELD_TYPE.DATE.VALUE:
        return (
          <Form.Item field={field.field + '.value'}>
            <DatePicker format="YYYY-MM-DD" style={{ width: '100%' }} placeholder="默认值" />
          </Form.Item>
        );
      case ENTITY_FIELD_TYPE.DATETIME.VALUE:
        return (
          <Form.Item field={field.field + '.value'}>
            <DatePicker showTime format="YYYY-MM-DD HH:mm:ss" style={{ width: '100%' }} placeholder="默认值" />
          </Form.Item>
        );
      case ENTITY_FIELD_TYPE.TIME.VALUE:
        return (
          <Form.Item field={field.field + '.value'}>
            <TimePicker format="HH:mm:ss" style={{ width: '100%' }} placeholder="默认值" />
          </Form.Item>
        );
      case ENTITY_FIELD_TYPE.BOOLEAN.VALUE:
        return (
          <Form.Item field={field.field + '.value'}>
            <Switch />
          </Form.Item>
        );
      default:
        return (
          <Form.Item field={field.field + '.value'}>
            <Input placeholder="默认值" />
          </Form.Item>
        );
    }
  };

  return (
    <>
      <Grid.Row gutter={8} style={{ marginBottom: '8px' }}>
        <Grid.Col span={7}>
          <div style={{ padding: '4px 0', color: '#86909c' }}>字段名称</div>
        </Grid.Col>
        <Grid.Col span={7}>
          <div style={{ padding: '4px 0', color: '#86909c' }}>字段类型</div>
        </Grid.Col>
        <Grid.Col span={7}>
          <div style={{ padding: '4px 0', color: '#86909c' }}>默认值</div>
        </Grid.Col>
        <Grid.Col span={3}>
          <div style={{ padding: '4px 0', color: '#86909c' }}>操作</div>
        </Grid.Col>
      </Grid.Row>
      <Form.List field={fieldPrefix}>
        {(fields, { add, remove }) => (
          <>
            {fields.map((field, index) => (
              <Grid.Row key={field.key} gutter={8}>
                <Grid.Col span={7}>
                  <Form.Item field={field.field + '.name'}>
                    <Input placeholder="字段名称" />
                  </Form.Item>
                </Grid.Col>
                <Grid.Col span={7}>
                  <Form.Item field={field.field + '.type'}>
                    <Select
                      placeholder="字段类型"
                      options={FIELD_TYPES}
                      onChange={() => {
                        form.clearFields(field.field + '.value');
                      }}
                    ></Select>
                  </Form.Item>
                </Grid.Col>
                <Grid.Col span={7}>{renderDefault(field, index)}</Grid.Col>
                <Grid.Col span={3}>
                  <Button
                    type="text"
                    icon={<IconPlus />}
                    style={{ marginRight: '4px' }}
                    onClick={() => {
                      // 在当前行的下一行插入新字段
                      add({ name: '', type: 'TEXT' }, index + 1);
                    }}
                    title="添加字段"
                  ></Button>
                  <Button
                    type="text"
                    status="danger"
                    icon={<IconDelete />}
                    onClick={() => remove(index)}
                    title="删除字段"
                  />
                </Grid.Col>
              </Grid.Row>
            ))}
          </>
        )}
      </Form.List>
    </>
  );
};

export default SchemaForm;
