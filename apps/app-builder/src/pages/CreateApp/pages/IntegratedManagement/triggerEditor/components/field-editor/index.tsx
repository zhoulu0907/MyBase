import { Button, Form, Grid, Input, Select, type FormInstance } from '@arco-design/web-react';
import { IconDelete, IconPlus } from '@arco-design/web-react/icon';
import type { AppEntityField } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';

export interface FieldEditorProps {
  form: FormInstance;
  fieldList: AppEntityField[];
}

const valueTypeOptions = [
  { label: '值', value: 'value' },
  { label: '变量', value: 'variable' }
];

const FieldEditor: React.FC<FieldEditorProps> = ({ fieldList, form }) => {
  const [selectedFields, setSelectedFields] = useState<any[]>();

  useEffect(() => {
    setSelectedFields(form.getFieldValue('fields'));
  }, [form, fieldList]);

  return (
    <div className={styles.conditionWrapper}>
      <Form.Item validateTrigger={['onChange']}>
        <Form.List field="fields">
          {(fields, { add, remove }) => {
            return (
              <>
                {fields.map((item: any, index: number) => {
                  return (
                    <Grid.Row gutter={8} key={item.key}>
                      <Grid.Col span={6}>
                        <Form.Item field={item.field + '.fieldId'} rules={[{ required: true, message: '请选择字段' }]}>
                          <Select
                            options={fieldList.map((field) => ({
                              label: field.displayName,
                              value: field.fieldId,
                              disabled: selectedFields?.some((f) => f?.fieldId === field.fieldId)
                            }))}
                            onChange={(_value) => {
                              setSelectedFields(form.getFieldValue('fields'));
                            }}
                          />
                        </Form.Item>
                      </Grid.Col>
                      <Grid.Col span={3}>
                        <div style={{ lineHeight: '32px' }}>的值设为</div>
                      </Grid.Col>
                      <Grid.Col span={5}>
                        <Form.Item field={item.field + '.fieldType'}>
                          <Select options={valueTypeOptions} />
                        </Form.Item>
                      </Grid.Col>
                      <Grid.Col span={8}>
                        <Form.Item field={item.field + '.fieldValue'}>
                          <Input />
                        </Form.Item>
                      </Grid.Col>
                      <Grid.Col span={2}>
                        <Button
                          type="text"
                          icon={<IconDelete />}
                          onClick={() => {
                            remove(index);
                            setSelectedFields(form.getFieldValue('fields'));
                          }}
                        />
                      </Grid.Col>
                    </Grid.Row>
                  );
                })}

                <Grid.Row>
                  <Button
                    type="dashed"
                    icon={<IconPlus />}
                    onClick={() => {
                      add();
                    }}
                    disabled={(selectedFields || [])?.length >= fieldList?.length}
                  >
                    添加字段
                  </Button>
                </Grid.Row>
              </>
            );
          }}
        </Form.List>
      </Form.Item>
    </div>
  );
};

export default FieldEditor;
