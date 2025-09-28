import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { triggerNodeOutputSignal } from '@/store/singals/trigger_node_output';
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
import { IconDelete } from '@arco-design/web-react/icon';
import {
  FieldType,
  VALIDATION_TYPE,
  type ConfitionField,
  type EntityFieldValidationTypes,
  type ValidationTypeItem
} from '@onebase/app';
import { ENTITY_FIELD_TYPE } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useEffect } from 'react';
import { NodeType } from '../../nodes/const';
import { getPrecedingNodes } from '../../nodes/utils';
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
  nodeId: string;
  label: string;
  required: boolean;
  fields: ConfitionField[];
  entityFieldValidationTypes: EntityFieldValidationTypes[];
  form: FormInstance;
  // 可选变量下拉选项， 如果不传默认从节点id中计算后获取
  variableOptions?: TreeSelectDataType[];
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
  variableOptions
}) => {
  useSignals();

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
      form.setFieldValue('filterCondition', filterCondition);
    }
  }, []);

  useEffect(() => {
    // console.log('entityFieldValidationTypes:  ', entityFieldValidationTypes);
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

  const getVariableOptions = (nodeId: string): TreeSelectDataType[] => {
    if (nodeId == undefined || nodeId == '') {
      if (variableOptions) {
        return variableOptions;
      }

      return [];
    }

    const nodeTypes = [
      NodeType.DATA_QUERY,
      NodeType.DATA_QUERY_MULTIPLE,
      NodeType.DATA_UPDATE,
      NodeType.DATA_ADD,
      NodeType.START_FORM,
      NodeType.START_ENTITY,
      NodeType.START_TIME,
      NodeType.START_DATE_FIELD,
      NodeType.START_API,
      NodeType.START_BPM,
      NodeType.LOOP
    ];

    const nodes = getPrecedingNodes(nodeId, triggerEditorSignal.nodes.value, nodeTypes);
    // console.log('nodes: ', nodes);

    const options: TreeSelectDataType[] = [];

    nodes.forEach((node) => {
      const nodeOutput = triggerNodeOutputSignal.getTriggerNodeOutput(node.id);

      //   console.log('nodeOutput: ', nodeOutput);

      const treeNode = {
        key: node.id,
        title: node.data?.title,
        disabled: true,
        // TODO(mickey): add icon
        children: [] as TreeSelectDataType[]
      };

      switch (node.type) {
        case NodeType.START_FORM:
          const startFormFields = nodeOutput.conditionFields;

          startFormFields &&
            startFormFields.forEach((field: any) => {
              treeNode.children.push({
                key: `${node.id}.${field.value}`,
                title: field.label
              });
            });

          if (treeNode.children.length > 0) {
            options.push(treeNode);
          }

          break;
        case NodeType.START_ENTITY:
          const startEntityFields = nodeOutput.conditionFields;

          startEntityFields &&
            startEntityFields.forEach((field: any) => {
              treeNode.children.push({
                key: `${node.id}.${field.value}`,
                title: field.label
              });
            });

          if (treeNode.children.length > 0) {
            options.push(treeNode);
          }

          break;
        case NodeType.START_TIME:
          break;
        case NodeType.START_DATE_FIELD:
          const startDateFields = nodeOutput.conditionFields;

          startDateFields &&
            startDateFields.forEach((field: any) => {
              treeNode.children.push({
                key: `${node.id}.${field.value}`,
                title: field.label
              });
            });

          if (treeNode.children.length > 0) {
            options.push(treeNode);
          }

          break;
        case NodeType.START_API:
          break;
        case NodeType.START_BPM:
          break;
        case NodeType.DATA_ADD:
          const dataAddFields = nodeOutput.conditionFields;
          dataAddFields &&
            dataAddFields.forEach((field: any) => {
              treeNode.children.push({
                key: `${node.id}.${field.value}`,
                title: field.label
              });
            });

          if (treeNode.children.length > 0) {
            options.push(treeNode);
          }

          break;
        case NodeType.DATA_DELETE:
          break;
        case NodeType.DATA_QUERY:
          const dataQueryFields = nodeOutput.conditionFields;
          dataQueryFields &&
            dataQueryFields.forEach((field: any) => {
              treeNode.children.push({
                key: `${node.id}.${field.value}`,
                title: field.label
              });
            });

          if (treeNode.children.length > 0) {
            options.push(treeNode);
          }
          break;
        case NodeType.DATA_QUERY_MULTIPLE:
          const dataQueryMultipleFields = nodeOutput.conditionFields;
          dataQueryMultipleFields &&
            dataQueryMultipleFields.forEach((field: any) => {
              treeNode.children.push({
                key: `${node.id}.${field.value}`,
                title: field.label
              });
            });

          if (treeNode.children.length > 0) {
            options.push(treeNode);
          }
          break;
        case NodeType.DATA_UPDATE:
          const dataUpdateFields = nodeOutput.conditionFields;
          dataUpdateFields &&
            dataUpdateFields.forEach((field: any) => {
              treeNode.children.push({
                key: `${node.id}.${field.value}`,
                title: field.label
              });
            });

          if (treeNode.children.length > 0) {
            options.push(treeNode);
          }
          break;
        case NodeType.DATA_CALC:
          break;
        case NodeType.LOOP:
          const loopFields = nodeOutput.conditionFields;
          loopFields &&
            loopFields.forEach((field: any) => {
              treeNode.children.push({
                key: `${node.id}.${field.value}`,
                title: field.label
              });
            });

          if (treeNode.children.length > 0) {
            options.push(treeNode);
          }
          break;
      }
    });

    return options;
  };

  const showTriggerElement = (params: any, options: TreeSelectDataType[]) => {
    // console.log(params.value);

    if (params.value) {
      const parentId = params.value.split('.')[0];
      const parentNode = options.find((item) => item.key == parentId);

      const childrenName = parentNode?.children?.find((item) => item.key == params.value)?.title;
      return `${parentNode?.title} - ${childrenName}`;
    }

    return '';
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
                                                  <TreeSelect
                                                    treeData={getVariableOptions(nodeId)}
                                                    triggerElement={(params) => {
                                                      return (
                                                        <Input
                                                          readOnly
                                                          value={showTriggerElement(params, getVariableOptions(nodeId))}
                                                        ></Input>
                                                      );
                                                    }}
                                                  />
                                                </Form.Item>
                                              )}

                                              {form.getFieldValue(item.field + '.operatorType') ==
                                                FieldType.FORMULA && (
                                                <Form.Item field={item.field + '.value'}>
                                                  <Input placeholder="请输入公式" />
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
