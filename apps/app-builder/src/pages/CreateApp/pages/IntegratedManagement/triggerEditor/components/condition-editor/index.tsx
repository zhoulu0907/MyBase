import { Button, Divider, Form, Grid, Input, Select, type FormInstance } from '@arco-design/web-react';
import { IconDelete } from '@arco-design/web-react/icon';
import type { ConfitionField, EntityFieldValidationTypes, ValidationTypeItem } from '@onebase/app';
import React, { useEffect } from 'react';
import styles from './index.module.less';

const Option = Select.Option;

const opCodeOptions = [
  {
    label: '公式',
    value: 'formula'
  },
  {
    label: '静态值',
    value: 'value'
  },
  {
    label: '变量',
    value: 'variable'
  }
];

/**
 * ConditionEditor 组件的 props 类型定义
 */
export interface ConditionEditorProps {
  // 可以下拉选择的字段列表
  label: string;
  required: boolean;
  fields: ConfitionField[];
  entityFieldValidationTypes: EntityFieldValidationTypes[];
  form: FormInstance;
}

/**
 * 条件编辑器组件初始化
 */
const ConditionEditor: React.FC<ConditionEditorProps> = ({
  fields,
  entityFieldValidationTypes,
  form,
  label,
  required
}) => {
  const filterCondition = Form.useWatch('filterCondition', form);

  useEffect(() => {
    console.log('filterCondition:  ', filterCondition);
  }, [filterCondition]);

  return (
    <div className={styles.conditionWrapper}>
      <Form.Item label={label} required={required}>
        <Form.List field="filterCondition">
          {(conditions, { add, remove, move }) => {
            return (
              <div>
                {conditions.map((item, index) => {
                  return (
                    <div key={item.key}>
                      <div className={styles.items}>
                        <div className={styles.tag}>且</div>
                        <Form.List field={item.field + '.conditions'}>
                          {(condition, { add: childAdd, remove: childRemove, move: childMove }) => {
                            return (
                              <div style={{ width: '100%' }}>
                                {condition.map((item, childIndex) => {
                                  return (
                                    <Grid.Row key={item.key} gutter={8} align="center">
                                      <Grid.Col span={6}>
                                        <Form.Item field={item.field + '.fieldId'}>
                                          <Select
                                            className={styles.itemSelect}
                                            // onChange={(value) => {
                                            //   console.log(value);
                                            //   console.log(item);
                                            // }}
                                          >
                                            {fields.map((field) => (
                                              <Option key={field.value} value={field.value}>
                                                {field.label}
                                              </Option>
                                            ))}
                                          </Select>
                                        </Form.Item>
                                      </Grid.Col>

                                      <Grid.Col span={5}>
                                        <Form.Item field={item.field + '.op'}>
                                          <Select className={styles.itemSelect}>
                                            {form.getFieldValue(item.field)?.fieldId &&
                                              entityFieldValidationTypes &&
                                              entityFieldValidationTypes
                                                .find((cc) => cc.fieldId == form.getFieldValue(item.field).fieldId)
                                                ?.validationTypes.map((operator: ValidationTypeItem) => (
                                                  <Option key={operator.code} value={operator.code}>
                                                    {operator.name}
                                                  </Option>
                                                ))}
                                          </Select>
                                        </Form.Item>
                                      </Grid.Col>

                                      <Grid.Col span={6}>
                                        <Form.Item field={item.field + '.operatorType'}>
                                          <Select className={styles.itemSelect} options={opCodeOptions}></Select>
                                        </Form.Item>
                                      </Grid.Col>

                                      <Grid.Col span={6}>
                                        <Form.Item field={item.field + '.value'}>
                                          <Input placeholder="请输入" />
                                        </Form.Item>
                                      </Grid.Col>

                                      <Grid.Col span={1}>
                                        <IconDelete
                                          style={{ fontSize: '15px', color: '#4E5969', marginBottom: '15px' }}
                                          onClick={() => {
                                            childRemove(childIndex);
                                            if (condition.length === 1) {
                                              remove(index);
                                            }
                                          }}
                                        />
                                      </Grid.Col>
                                    </Grid.Row>
                                  );
                                })}

                                <Button type="text" size="small" onClick={() => childAdd()}>
                                  + 添加且条件
                                </Button>
                              </div>
                            );
                          }}
                        </Form.List>
                      </div>

                      {index !== (conditions || [])?.length - 1 && (
                        <Divider
                          orientation="center"
                          style={{
                            marginTop: '10px',
                            marginBottom: '10px',
                            marginLeft: '10px',
                            marginRight: '10px'
                          }}
                        >
                          <div className={styles.dividerText}>或</div>
                        </Divider>
                      )}
                    </div>
                  );
                })}
                <Button
                  type="text"
                  onClick={() => {
                    add({
                      conditions: [
                        {
                          fieldId: '',
                          op: '',
                          operatorType: '',
                          value: ['']
                        }
                      ]
                    });
                  }}
                >
                  + 添加或条件
                </Button>
              </div>
            );
          }}
        </Form.List>
      </Form.Item>
    </div>
  );
};

export default ConditionEditor;
