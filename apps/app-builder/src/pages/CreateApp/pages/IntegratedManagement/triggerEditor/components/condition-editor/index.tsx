import {
  Button,
  DatePicker,
  Divider,
  Form,
  Grid,
  Input,
  InputNumber,
  Select,
  Switch,
  type FormInstance
} from '@arco-design/web-react';
import { IconDelete } from '@arco-design/web-react/icon';
import {
  FieldType,
  VALIDATION_TYPE,
  type ConfitionField,
  type EntityFieldValidationTypes,
  type ValidationTypeItem
} from '@onebase/app';
import { ENTITY_FIELD_TYPE } from '@onebase/ui-kit';
import React, { useEffect } from 'react';
import styles from './index.module.less';

const Option = Select.Option;

const opCodeOptions = [
  {
    label: '公式',
    value: FieldType.FORMULA
  },
  {
    label: '静态值',
    value: FieldType.VALUE
  },
  {
    label: '变量',
    value: FieldType.VARIABLES
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

  // 过滤为空的条件
  useEffect(() => {
    console.log('filterCondition:  ', filterCondition);
    if (Array.isArray(filterCondition)) {
      filterCondition.forEach((item: any, index: number) => {
        if (Array.isArray(item.conditions)) {
          item.conditions = item.conditions.filter((condition: any) => condition != undefined);
          if (item.conditions.length === 0) {
            filterCondition.splice(index, 1);
          }
        }
      });
    }
    form.setFieldValue('filterCondition', filterCondition);
  }, []);

  useEffect(() => {
    console.log('entityFieldValidationTypes:  ', entityFieldValidationTypes);
  }, [entityFieldValidationTypes]);

  useEffect(() => {
    // console.log('filterCondition:  ', filterCondition);
  }, [filterCondition]);

  const StaticValueComponent = (fieldName: string, fieldId: string, op: string) => {
    const fieldValidationType = entityFieldValidationTypes.find((cc) => cc.fieldId == fieldId);

    if (
      fieldValidationType?.fieldTypeCode == ENTITY_FIELD_TYPE.TEXT.VALUE ||
      fieldValidationType?.fieldTypeCode == ENTITY_FIELD_TYPE.LONG_TEXT.VALUE ||
      fieldValidationType?.fieldTypeCode == ENTITY_FIELD_TYPE.EMAIL.VALUE ||
      fieldValidationType?.fieldTypeCode == ENTITY_FIELD_TYPE.PHONE.VALUE ||
      fieldValidationType?.fieldTypeCode == ENTITY_FIELD_TYPE.URL.VALUE ||
      fieldValidationType?.fieldTypeCode == ENTITY_FIELD_TYPE.ADDRESS.VALUE
    ) {
      return (
        <Form.Item field={fieldName}>
          <Input placeholder="请输入静态值" />
        </Form.Item>
      );
    }

    if (fieldValidationType?.fieldTypeCode == ENTITY_FIELD_TYPE.NUMBER.VALUE) {
      // 范围
      if (op == VALIDATION_TYPE.RANGE) {
        return (
          <Form.Item style={{ height: '12px' }}>
            <Form.List
              field={fieldName}
              rules={[
                {
                  validator: (list: any[] | undefined, callback: (msg?: string) => void) => {
                    if (Array.isArray(list) && list.length === 2) {
                      const [first, second] = list;
                      // 只校验有值的情况
                      if (
                        first !== undefined &&
                        second !== undefined &&
                        first !== null &&
                        second !== null &&
                        second <= first
                      ) {
                        // callback('第二个值必须大于第一个值');
                        callback();
                        return;
                      }
                    }
                    callback();
                  }
                }
              ]}
            >
              {(list, {}) => {
                return (
                  <div className={styles.inputNumberWrapper}>
                    {list.map((item, index) => {
                      return (
                        <Form.Item key={item.key} field={item.field}>
                          <InputNumber style={{ width: '100%' }} />
                        </Form.Item>
                      );
                    })}
                  </div>
                );
              }}
            </Form.List>
          </Form.Item>
        );
      }

      return (
        <Form.Item field={fieldName}>
          <InputNumber placeholder="请输入静态值" />
        </Form.Item>
      );
    }

    if (fieldValidationType?.fieldTypeCode == ENTITY_FIELD_TYPE.BOOLEAN.VALUE) {
      return (
        <Form.Item field={fieldName} triggerPropName="checked">
          <Switch />
        </Form.Item>
      );
    }

    if (fieldValidationType?.fieldTypeCode == ENTITY_FIELD_TYPE.DATE.VALUE) {
      if (op == VALIDATION_TYPE.RANGE) {
        return (
          <Form.Item
            field={fieldName}
            normalize={(value) => {
              return {
                begin: value && value[0],
                end: value && value[1]
              };
            }}
            formatter={(value) => {
              return value && value.begin ? [value.begin, value.end] : [];
            }}
          >
            <DatePicker.RangePicker />
          </Form.Item>
        );
      }
      return (
        <Form.Item field={fieldName}>
          <DatePicker placeholder="请输入静态值" />
        </Form.Item>
      );
    }

    if (fieldValidationType?.fieldTypeCode == ENTITY_FIELD_TYPE.DATETIME.VALUE) {
      if (op == VALIDATION_TYPE.RANGE) {
        return (
          <Form.Item
            field={fieldName}
            normalize={(value) => {
              return {
                begin: value && value[0],
                end: value && value[1]
              };
            }}
            formatter={(value) => {
              return value && value.begin ? [value.begin, value.end] : [];
            }}
          >
            <DatePicker.RangePicker showTime />
          </Form.Item>
        );
      }

      return (
        <Form.Item field={fieldName}>
          <DatePicker showTime placeholder="请输入静态值" />
        </Form.Item>
      );
    }

    return (
      <Form.Item field={fieldName}>
        <Input placeholder="请输入静态值" />
      </Form.Item>
    );
  };

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
                                    // 字段id
                                    <Grid.Row key={item.key} gutter={8} align="center">
                                      <Grid.Col span={5}>
                                        <Form.Item field={item.field + '.fieldId'}>
                                          <Select
                                            className={styles.itemSelect}
                                            onChange={(_value) => {
                                              form.setFieldValue(item.field + '.op', undefined);
                                              form.setFieldValue(item.field + '.operatorType', undefined);
                                              form.setFieldValue(item.field + '.value', undefined);
                                            }}
                                          >
                                            {fields.map((field) => (
                                              <Option key={field.value} value={field.value}>
                                                {field.label}
                                              </Option>
                                            ))}
                                          </Select>
                                        </Form.Item>
                                      </Grid.Col>

                                      {/* 操作符 */}
                                      <Grid.Col span={4}>
                                        <Form.Item field={item.field + '.op'}>
                                          <Select
                                            className={styles.itemSelect}
                                            disabled={form.getFieldValue(item.field + '.fieldId') == undefined}
                                            onChange={(_value) => {
                                              form.setFieldValue(item.field + '.operatorType', undefined);
                                              form.setFieldValue(item.field + '.value', undefined);
                                            }}
                                          >
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

                                      {/* 不为空和为空不需要选择操作类型 */}
                                      {form.getFieldValue(item.field + '.op') != VALIDATION_TYPE.IS_EMPTY &&
                                        form.getFieldValue(item.field + '.op') != VALIDATION_TYPE.IS_NOT_EMPTY && (
                                          <>
                                            <Grid.Col span={3}>
                                              <Form.Item field={item.field + '.operatorType'}>
                                                <Select
                                                  className={styles.itemSelect}
                                                  disabled={form.getFieldValue(item.field + '.op') == undefined}
                                                  options={opCodeOptions}
                                                  onChange={(value) => {
                                                    form.setFieldValue(item.field + '.value', undefined);
                                                    // 如果是范围类型 需要用数组兜底
                                                    if (
                                                      form.getFieldValue(item.field + '.op') == VALIDATION_TYPE.RANGE
                                                    ) {
                                                      form.setFieldValue(item.field + '.value', [undefined, undefined]);
                                                    }
                                                  }}
                                                ></Select>
                                              </Form.Item>
                                            </Grid.Col>

                                            <Grid.Col span={11}>
                                              {form.getFieldValue(item.field + '.operatorType') == undefined && (
                                                <Form.Item field={item.field + '.value'}>
                                                  <Input placeholder="请输入" disabled />
                                                </Form.Item>
                                              )}
                                              {form.getFieldValue(item.field + '.operatorType') == FieldType.VALUE &&
                                                StaticValueComponent(
                                                  item.field + '.value',
                                                  form.getFieldValue(item.field + '.fieldId'),
                                                  form.getFieldValue(item.field + '.op')
                                                )}

                                              {form.getFieldValue(item.field + '.operatorType') ==
                                                FieldType.VARIABLES && (
                                                <Form.Item field={item.field + '.value'}>
                                                  <Select placeholder="请选择变量"></Select>
                                                </Form.Item>
                                              )}

                                              {form.getFieldValue(item.field + '.operatorType') ==
                                              (
                                                <Form.Item field={item.field + '.value'}>
                                                  FieldType.FORMULA && <Input placeholder="请输入公式" />
                                                </Form.Item>
                                              )}
                                            </Grid.Col>
                                          </>
                                        )}

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
                      conditions: [undefined]
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
