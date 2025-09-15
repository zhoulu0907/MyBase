import { Button, Form, Grid, Input, Select } from '@arco-design/web-react';
import { IconDelete, IconPlus } from '@arco-design/web-react/icon';
import type { AppEntityField } from '@onebase/app';
import React from 'react';
import styles from './index.module.less';

export interface FieldEditorProps {
  fieldList: AppEntityField[];
}

const valueTypeOptions = [
  { label: '值', value: 'value' },
  { label: '变量', value: 'variable' }
];

const FieldEditor: React.FC<FieldEditorProps> = ({ fieldList }) => {
  return (
    <div className={styles.conditionWrapper}>
      <Form.Item>
        <Form.List field="fieldList">
          {(fields, { add, remove }) => {
            return (
              <>
                {fields.map((item: any, index: number) => {
                  return (
                    <Grid.Row gutter={8} key={item.key}>
                      <Grid.Col span={6}>
                        <Form.Item field={item.field + '.fieldId'}>
                          <Select
                            options={fieldList.map((item) => ({ label: item.displayName, value: item.fieldId }))}
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
                        <Button type="text" icon={<IconDelete />} onClick={() => remove(index)} />
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
