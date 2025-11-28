import { FormulaEditor } from '@/components/FormulaEditor';
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
import { IconDelete, IconLaunch } from '@arco-design/web-react/icon';
import {
  FieldType,
  getFieldCheckTypeApi,
  VALIDATION_TYPE,
  type ConditionField,
  type EntityFieldValidationTypes,
  type ValidationTypeItem
} from '@onebase/app';
import { NodeType } from '@onebase/common';
import { ENTITY_FIELD_TYPE } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { getPrecedingNodes } from '../../nodes/utils';
import styles from './index.module.less';

const Option = Select.Option;

const ALLOW_NODE_TYPES = [
  NodeType.DATA_QUERY,
  NodeType.DATA_ADD,
  NodeType.DATA_CALC,
  NodeType.START_FORM,
  NodeType.START_ENTITY,
  NodeType.START_DATE_FIELD,
  NodeType.LOOP,
  NodeType.IF,
  NodeType.IF_BLOCK,
  NodeType.CASE,
  NodeType.CASE_DEFAULT,
  NodeType.MODAL,
  NodeType.IPAAS
];

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

const commonOperatorOptions = [
  {
    name: '等于',
    value: VALIDATION_TYPE.EQUALS
  },

  {
    name: '不等于',
    value: VALIDATION_TYPE.NOT_EQUALS
  },

  {
    name: '包含',
    value: VALIDATION_TYPE.CONTAINS
  },
  {
    name: '不包含',
    value: VALIDATION_TYPE.NOT_CONTAINS
  },
  {
    name: '为空',
    value: VALIDATION_TYPE.IS_EMPTY
  },
  {
    name: '不为空',
    value: VALIDATION_TYPE.IS_NOT_EMPTY
  }
];

/**
 * ConditionEditor 组件的 props 类型定义
 */
export interface ConditionEditorProps {
  nodeId: string;
  label: string;
  required: boolean;
  form: FormInstance;
}

/**
 * 条件编辑器组件初始化
 */
const IfNodeConditionEditor: React.FC<ConditionEditorProps> = ({ nodeId, form, label, required }) => {
  useSignals();

  const filterCondition = Form.useWatch('filterCondition', form);

  const [fieldOptions, setFieldOptions] = useState<TreeSelectDataType[]>([]);
  const [entityFieldValidationTypes, setEntityFieldValidationTypes] = useState<EntityFieldValidationTypes[]>([]);

  const [formulaVisible, setFormulaVisible] = useState<boolean>(false);
  const [formulaFieldKey, setFormulaFieldKey] = useState<string>('');
  const [formulaData, setFormulaData] = useState<string>('');

  // 过滤为空的条件和field字段不存在的条件
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
    getFieldOptions(nodeId);
  }, []);

  const getFieldOptions = async (nodeId: string) => {
    const newFieldOptions = getVariableOptions(nodeId);
    setFieldOptions(newFieldOptions);

    const fieldIds = getEntityFieldValidationTypes(nodeId);

    if (fieldIds?.length) {
      const newValidationTypes = await getFieldCheckTypeApi(fieldIds);
      setEntityFieldValidationTypes(newValidationTypes);
    }
  };

  const StaticValueComponent = (fieldName: string, fieldId: string, op: string) => {
    const fieldValidationType = entityFieldValidationTypes.find((cc) => cc.fieldId == getSplitFieldId(fieldId));

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
                        callback();
                        return;
                      }
                    }
                    callback();
                  }
                }
              ]}
            >
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
          <DatePicker showTime placeholder="请输入静态值" style={{ width: '100%' }} />
        </Form.Item>
      );
    }

    return (
      <Form.Item field={fieldName}>
        <Input placeholder="请输入静态值" />
      </Form.Item>
    );
  };

  const getEntityFieldValidationTypes = (nodeId: string): string[] => {
    const nodes = getPrecedingNodes(nodeId, triggerEditorSignal.nodes.value, ALLOW_NODE_TYPES);

    const fieldIds: string[] = [];

    nodes.forEach((node) => {
      const nodeOutput = triggerNodeOutputSignal.getTriggerNodeOutput(node.id);

      switch (node.type) {
        case NodeType.START_FORM:
          const startFormFields = nodeOutput.conditionFields;

          startFormFields &&
            startFormFields.forEach((field: any) => {
              fieldIds.push(field.value);
            });

          break;
        case NodeType.START_ENTITY:
          const startEntityFields = nodeOutput.conditionFields;

          startEntityFields &&
            startEntityFields.forEach((field: any) => {
              fieldIds.push(field.value);
            });

          break;
        case NodeType.START_TIME:
          break;
        case NodeType.START_DATE_FIELD:
          const startDateFields = nodeOutput.conditionFields;

          startDateFields &&
            startDateFields.forEach((field: any) => {
              fieldIds.push(field.value);
            });

          break;
        case NodeType.START_API:
          break;
        case NodeType.START_BPM:
          break;
        case NodeType.DATA_ADD:
          const dataAddFields = nodeOutput.conditionFields;
          dataAddFields &&
            dataAddFields.forEach((field: any) => {
              fieldIds.push(field.value);
            });

          break;
        case NodeType.DATA_DELETE:
          break;
        case NodeType.DATA_QUERY:
          const dataQueryFields = nodeOutput.conditionFields;
          dataQueryFields &&
            dataQueryFields.forEach((field: any) => {
              fieldIds.push(field.value);
            });

          break;
        case NodeType.DATA_QUERY_MULTIPLE:
          const dataQueryMultipleFields = nodeOutput.conditionFields;
          dataQueryMultipleFields &&
            dataQueryMultipleFields.forEach((field: any) => {
              fieldIds.push(field.value);
            });

          break;
        case NodeType.DATA_UPDATE:
          const dataUpdateFields = nodeOutput.conditionFields;
          dataUpdateFields &&
            dataUpdateFields.forEach((field: any) => {
              fieldIds.push(field.value);
            });

          break;
        case NodeType.DATA_CALC:
          break;
        case NodeType.LOOP:
          const loopFields = nodeOutput.conditionFields;
          loopFields &&
            loopFields.forEach((field: any) => {
              fieldIds.push(field.value);
            });

          break;
        case NodeType.MODAL:
          const modalFields = nodeOutput.conditionFields;
          modalFields &&
            modalFields.forEach((field: any) => {
              fieldIds.push(field.value);
            });

          break;
      }
    });

    return fieldIds;
  };

  // 提取公共的字段处理逻辑
  const processConditionFields = (
    nodeId: string,
    conditionFields: ConditionField[],
    children: TreeSelectDataType[],
    fieldType?: string
  ): void => {
    if (!conditionFields) return;

    conditionFields.forEach((field: ConditionField) => {
      if (!fieldType || !field.fieldType) {
        children.push({
          key: `${nodeId}.${field.value}`,
          title: field.label,
          fieldType: field.fieldType
        });
      } else if (
        field?.fieldType === fieldType ||
        ([ENTITY_FIELD_TYPE.NUMBER.VALUE, ENTITY_FIELD_TYPE.ID.VALUE].includes(field?.fieldType) &&
          [ENTITY_FIELD_TYPE.NUMBER.VALUE, ENTITY_FIELD_TYPE.ID.VALUE].includes(fieldType))
      ) {
        children.push({
          key: `${nodeId}.${field.value}`,
          title: field.label
        });
      }
    });
  };

  // 使用 useMemo 缓存节点类型集合，避免重复创建
  const nodesWithConditionFields = useMemo(
    () =>
      new Set([
        NodeType.START_FORM,
        NodeType.START_ENTITY,
        NodeType.START_DATE_FIELD,
        NodeType.DATA_ADD,
        NodeType.DATA_QUERY,
        NodeType.DATA_QUERY_MULTIPLE,
        NodeType.DATA_UPDATE,
        NodeType.DATA_CALC,
        NodeType.LOOP,
        NodeType.MODAL,
        NodeType.IPAAS
      ]),
    []
  );

  // 使用 useCallback 缓存函数，避免不必要的重新创建
  const getVariableOptions = useCallback(
    (nodeId: string, item?: any): TreeSelectDataType[] => {
      const nodes = getPrecedingNodes(nodeId, triggerEditorSignal.nodes.value, ALLOW_NODE_TYPES);

      const options: TreeSelectDataType[] = [];
      let fieldType = '';
      if (item) {
        const fieldId = form.getFieldValue(item.field + '.fieldId');
        for (let ele of fieldOptions) {
          const targetField = ele.children?.find((e) => e.key?.indexOf(fieldId) !== -1);
          if (targetField) {
            fieldType = targetField?.fieldType;
            break;
          }
        }
      }

      nodes.forEach((node) => {
        const nodeOutput = triggerNodeOutputSignal.getTriggerNodeOutput(node.id);

        // 只处理有 conditionFields 的节点类型
        if (!node.type || !nodesWithConditionFields.has(node.type as NodeType)) {
          return;
        }

        const treeNode: TreeSelectDataType = {
          key: node.id,
          title: node.data?.title,
          disabled: true,
          children: []
        };

        // 统一处理 conditionFields
        if (nodeOutput.conditionFields && treeNode.children) {
          processConditionFields(node.id, nodeOutput.conditionFields, treeNode.children, fieldType);
        }
        // 只有当有子字段时才添加到选项中
        if (treeNode.children && treeNode.children.length > 0) {
          options.push(treeNode);
        }
      });

      return options;
    },
    [nodesWithConditionFields, fieldOptions]
  );

  const showTriggerElement = (params: any, options: TreeSelectDataType[]) => {
    if (params.value) {
      const parentId = params.value.split('.')[0];
      const parentNode = options.find((item) => item.key == parentId);
      const childrenName = parentNode?.children?.find((item) => item.key == params.value)?.title;
      return `${parentNode?.title} - ${childrenName}`;
    }

    return '';
  };

  const getSplitFieldId = (fieldId: string) => {
    if (fieldId) {
      const splits = fieldId.split('.');
      if (splits.length > 1) {
        return splits[1];
      }

      return '';
    }

    return '';
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
                                {condition.map((item, childIndex) => {
                                  return (
                                    // 字段id
                                    <Grid.Row key={item.key} gutter={8} align="center">
                                      <Grid.Col span={8}>
                                        <Form.Item field={item.field + '.fieldId'}>
                                          <TreeSelect
                                            className={styles.itemSelect}
                                            treeData={fieldOptions}
                                            triggerElement={(params) => {
                                              return (
                                                <Input
                                                  readOnly
                                                  value={showTriggerElement(params, getVariableOptions(nodeId))}
                                                ></Input>
                                              );
                                            }}
                                            onChange={(_value) => {
                                              form.setFieldValue(item.field + '.op', undefined);
                                              form.setFieldValue(item.field + '.operatorType', undefined);
                                              form.setFieldValue(item.field + '.value', undefined);
                                            }}
                                          />
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
                                            {(() => {
                                              const fieldId = form.getFieldValue(item.field)?.fieldId;
                                              let options: ValidationTypeItem[] | undefined;
                                              if (fieldId && entityFieldValidationTypes) {
                                                const found = entityFieldValidationTypes.find(
                                                  (cc) => cc.fieldId == getSplitFieldId(fieldId)
                                                );
                                                options = found?.validationTypes;
                                              }
                                              if (options && options.length > 0) {
                                                return options.map((operator: ValidationTypeItem) => (
                                                  <Option key={operator.code} value={operator.code}>
                                                    {operator.name}
                                                  </Option>
                                                ));
                                              } else {
                                                return commonOperatorOptions.map((operator) => (
                                                  <Option key={operator.value} value={operator.value}>
                                                    {operator.name}
                                                  </Option>
                                                ));
                                              }
                                            })()}
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
                                                  onChange={() => {
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

                                            <Grid.Col span={8}>
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
                                                    treeData={getVariableOptions(nodeId, item)}
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

export default IfNodeConditionEditor;
