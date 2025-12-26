import { FormulaEditor } from '@/components/FormulaEditor';
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
  TreeSelect,
  type FormInstance
} from '@arco-design/web-react';
import type { TreeSelectDataType } from '@arco-design/web-react/es/TreeSelect/interface';
import { IconDelete, IconLaunch } from '@arco-design/web-react/icon';
import { FieldType, VALIDATION_TYPE, type EntityFieldValidationTypes, type ValidationTypeItem } from '@onebase/app';
import { ENTITY_FIELD_TYPE } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useCallback, useEffect, useState } from 'react';
import styles from './ConditionEditor.module.less';

/**
 * 在condition-editor组件基础上修改了getVariableOptions和showTriggerElement
 */

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

const addRules = {
  fieldId: [{ required: true, message: '请选择字段' }],
  range: [
    {
      validator: (list: any[] | undefined, callback: (msg?: string) => void) => {
        if (Array.isArray(list) && list.length === 2) {
          const [first, second] = list;
          // 只校验有值的情况
          if (first !== undefined && second !== undefined && first !== null && second !== null && second <= first) {
            callback();
            return;
          }
        }
        callback();
      }
    }
  ],
  variables: [
    {
      required: true,
      message: '请选择变量'
    }
  ],
  op: [
    {
      required: true,
      message: '请选择操作符'
    }
  ],
  formula: [
    {
      required: true,
      message: '请设置公式'
    }
  ]
};

/**
 * ConditionEditor 组件的 props 类型定义
 */
export interface ConditionEditorProps {
  nodeId: string;
  label: string;
  required: boolean;
  fields: TreeSelectDataType[];
  entityFieldValidationTypes: EntityFieldValidationTypes[];
  form: FormInstance;
  // 可选变量下拉选项， 如果不传默认从节点id中计算后获取
  variableOptions?: TreeSelectDataType[];
  // 当字段或操作符变化时的回调函数，用于动态加载变量选项
  onFieldOrOperatorChange?: (fieldType: string, operator?: string) => void;
}

/**
 * 条件编辑器组件初始化
 */
const ConditionEditor: React.FC<ConditionEditorProps> = ({
  nodeId,
  fields,
  entityFieldValidationTypes,
  form,
  label,
  required,
  variableOptions,
  onFieldOrOperatorChange
}) => {
  useSignals();

  const filterCondition = Form.useWatch('filterCondition', form);

  const [formulaVisible, setFormulaVisible] = useState<boolean>(false);
  const [formulaFieldKey, setFormulaFieldKey] = useState<string>('');
  const [formulaData, setFormulaData] = useState<string>('');

  // 过滤为空的条件
  useEffect(() => {
    if (Array.isArray(filterCondition)) {
      filterCondition.forEach((item: any, index: number) => {
        if (Array.isArray(item.conditions)) {
          item.conditions = item.conditions.filter((condition: any) => condition != undefined);
          if (item.conditions.length === 0) {
            filterCondition.splice(index, 1);
          }
        }
      });
      form.setFieldValue('filterCondition', filterCondition);
    }
  }, []);

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
            <Form.List field={fieldName} rules={addRules.range}>
              {(list) => {
                return (
                  <div className={styles.inputNumberWrapper}>
                    {list.map((item) => {
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
      <Form.Item field={fieldName} style={{ marginBottom: 0 }}>
        <Input placeholder="请输入静态值" />
      </Form.Item>
    );
  };

  // 使用 useCallback 缓存函数，避免不必要的重新创建
  const getVariableOptions = useCallback(
    (nodeId: string, item: any): TreeSelectDataType[] => {
      if (variableOptions) {
        return variableOptions;
      }
    },
    [variableOptions, fields]
  );

  const showTriggerElement = (params: any, options: TreeSelectDataType[]) => {
    if (params.value) {
      for (const parent of options) {
        if (parent.children && Array.isArray(parent.children)) {
          const found = parent.children.find((child) => child.key === params.value);
          if (found) {
            return `${parent.title} - ${found.title}`;
          }
        }
      }
    }

    return '';
  };

  const showFieldTitle = (params: any): string => {
    let title = '';
    if (params.value) {
      for (const parent of fields) {
        if (parent.children && Array.isArray(parent.children)) {
          const found = parent.children.find((child) => child.key === params.value);
          if (found) {
            title = '' + parent.title + ' - ' + found.title;
            break;
          }
        }
      }
    }
    return title;
  };

  // 从 fields 中查找字段类型
  const findFieldType = (fieldId: string): string | undefined => {
    for (const parent of fields) {
      if (parent.children && Array.isArray(parent.children)) {
        const found = parent.children.find((child: any) => child.key === fieldId);
        if (found && found.fieldType) {
          return found.fieldType;
        }
      }
    }
    return undefined;
  };

  const handleFormulaConfirm = (formulaData: string, formattedFormula: string, params: any) => {
    setFormulaVisible(false);
    form.setFieldValue(formulaFieldKey, { formulaData: formulaData, formula: formattedFormula, parameters: params });
    setFormulaData('');
    setFormulaFieldKey('');
  };

  const openFormulaEditor = (fieldKey: string) => {
    setFormulaVisible(true);
    setFormulaData(form.getFieldValue(fieldKey)?.formulaData);
    setFormulaFieldKey(fieldKey);
  };

  return (
    <div className={styles.conditionWrapper}>
      <Form.Item label={label} required={required}>
        <Form.List field="filterCondition">
          {(conditions, { add, remove }) => {
            return (
              <div>
                {conditions.map((item, index) => {
                  return (
                    <div key={item.key}>
                      <div className={styles.items}>
                        <div className={styles.tag}>且</div>
                        <Form.List field={item.field + '.conditions'}>
                          {(condition, { add: childAdd, remove: childRemove }) => {
                            return (
                              <div style={{ width: '100%' }}>
                                {condition.map((item: any, childIndex) => {
                                  return (
                                    // 字段id
                                    <Grid.Row key={item.key} gutter={8} align="center">
                                      <Grid.Col span={8}>
                                        <Form.Item
                                          field={item.field + '.fieldId'}
                                          rules={required ? addRules.fieldId : []}
                                        >
                                          <TreeSelect
                                            treeData={fields}
                                            className={styles.itemSelect}
                                            onChange={(_value) => {
                                              form.setFieldValue(item.field + '.op', undefined);
                                              form.setFieldValue(item.field + '.operatorType', undefined);
                                              form.setFieldValue(item.field + '.value', undefined);
                                            }}
                                            triggerElement={(params) => {
                                              // 找到fields中，children中有params.value对应key的元素的父节点的title
                                              return <Input readOnly value={showFieldTitle(params)}></Input>;
                                            }}
                                          />
                                        </Form.Item>
                                      </Grid.Col>

                                      {/* 操作符 */}
                                      <Grid.Col span={4}>
                                        <Form.Item field={item.field + '.op'} rules={required ? addRules.op : []}>
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
                                              <Form.Item
                                                field={item.field + '.operatorType'}
                                                rules={
                                                  required
                                                    ? [
                                                        {
                                                          validator: (value, cb) => {
                                                            const currentOp = form.getFieldValue(item.field + '.op');
                                                            if (
                                                              currentOp === VALIDATION_TYPE.IS_EMPTY ||
                                                              currentOp === VALIDATION_TYPE.IS_NOT_EMPTY
                                                            ) {
                                                              cb();
                                                              return;
                                                            }
                                                            if (value === undefined || value === null) {
                                                              cb('请选择');
                                                              return;
                                                            }
                                                            cb();
                                                          }
                                                        }
                                                      ]
                                                    : []
                                                }
                                              >
                                                <Select
                                                  className={styles.itemSelect}
                                                  disabled={form.getFieldValue(item.field + '.op') == undefined}
                                                  options={opCodeOptions}
                                                  onChange={(_value) => {
                                                    form.setFieldValue(item.field + '.value', undefined);
                                                    // 如果是范围类型 需要用数组兜底
                                                    if (
                                                      form.getFieldValue(item.field + '.op') == VALIDATION_TYPE.RANGE
                                                    ) {
                                                      form.setFieldValue(item.field + '.value', [undefined, undefined]);
                                                    }
                                                    // // 选择变量时，触发变量选项更新
                                                    const fieldId = form.getFieldValue(item.field + '.fieldId');
                                                    const op = form.getFieldValue(item.field + '.op');
                                                    if (
                                                      onFieldOrOperatorChange &&
                                                      fieldId &&
                                                      op &&
                                                      _value === FieldType.VARIABLES
                                                    ) {
                                                      const fieldType = findFieldType(fieldId);
                                                      if (fieldType) {
                                                        onFieldOrOperatorChange(fieldType, op);
                                                      }
                                                    }
                                                  }}
                                                ></Select>
                                              </Form.Item>
                                            </Grid.Col>

                                            <Grid.Col span={8}>
                                              {form.getFieldValue(item.field + '.operatorType') == undefined && (
                                                <Form.Item field={item.field + '.value'}>
                                                  <Input placeholder="请输入" disabled />
                                                </Form.Item>
                                              )}
                                              {form.getFieldValue(item.field + '.operatorType') == FieldType.VALUE && (
                                                <Form.Item
                                                  field={item.field + '.value'}
                                                  rules={[
                                                    {
                                                      validator: (value, cb) => {
                                                        const op = form.getFieldValue(item.field + '.op');
                                                        if (op === VALIDATION_TYPE.RANGE) {
                                                          if (
                                                            Array.isArray(value) &&
                                                            value.length === 2 &&
                                                            value.every(
                                                              (val) => val !== undefined && val !== null && val !== ''
                                                            )
                                                          ) {
                                                            cb();
                                                            return;
                                                          }
                                                          cb('请输入完整的范围值');
                                                          return;
                                                        }
                                                        if (value === undefined || value === null || value === '') {
                                                          cb('请输入值');
                                                          return;
                                                        }
                                                        cb();
                                                      }
                                                    }
                                                  ]}
                                                >
                                                  {StaticValueComponent(
                                                    item.field + '.value',
                                                    form.getFieldValue(item.field + '.fieldId'),
                                                    form.getFieldValue(item.field + '.op')
                                                  )}
                                                </Form.Item>
                                              )}

                                              {form.getFieldValue(item.field + '.operatorType') ==
                                                FieldType.VARIABLES && (
                                                <Form.Item
                                                  field={item.field + '.value'}
                                                  rules={required ? addRules.variables : []}
                                                >
                                                  <TreeSelect
                                                    treeData={getVariableOptions(nodeId, item)}
                                                    triggerElement={(params) => {
                                                      return (
                                                        <Input
                                                          readOnly
                                                          value={showTriggerElement(
                                                            params,
                                                            getVariableOptions(nodeId, item)
                                                          )}
                                                        ></Input>
                                                      );
                                                    }}
                                                  />
                                                </Form.Item>
                                              )}

                                              {form.getFieldValue(item.field + '.operatorType') ==
                                                FieldType.FORMULA && (
                                                <Form.Item
                                                  field={item.field + '.value'}
                                                  rules={required ? addRules.formula : []}
                                                >
                                                  <Button onClick={() => openFormulaEditor(item.field + '.value')} long>
                                                    {form.getFieldValue(item.field + '.value')
                                                      ? '已设置公式'
                                                      : 'ƒx 编辑公式'}
                                                    {form.getFieldValue(item.field + '.value') ? <IconLaunch /> : ''}
                                                  </Button>
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

      <FormulaEditor
        initialFormula={formulaData}
        visible={formulaVisible}
        onCancel={() => setFormulaVisible(false)}
        onConfirm={handleFormulaConfirm}
      />
    </div>
  );
};

export default ConditionEditor;
