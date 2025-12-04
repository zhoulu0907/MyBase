import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { triggerNodeOutputSignal } from '@/store/singals/trigger_node_output';
import {
  Button,
  DatePicker,
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
import { IconDelete, IconPlus } from '@arco-design/web-react/icon';
import { FieldType, type AppEntityField, type ConditionField } from '@onebase/app';
import { NodeType } from '@onebase/common';
import { ENTITY_FIELD_TYPE } from '@onebase/ui-kit';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { getPrecedingNodes } from '../../nodes/utils';
import styles from './index.module.less';

export interface FieldEditorProps {
  nodeId: string;
  form: FormInstance;
  fieldList: AppEntityField[];
  dataNodeId?: string;
}

const valueTypeOptions = [
  { label: '值', value: FieldType.VALUE },
  { label: '变量', value: FieldType.VARIABLES }
];

const FieldEditor: React.FC<FieldEditorProps> = ({ fieldList, form, nodeId, dataNodeId }) => {
  const [selectedFields, setSelectedFields] = useState<any[]>();

  const fields = Form.useWatch('fields', form);

  useEffect(() => {
    setSelectedFields(fields);
  }, [form, fieldList]);

  const StaticValueComponent = (fieldName: string, fieldId: string) => {
    const targetField = fieldList.find((cc) => cc.fieldId == fieldId);

    if (
      targetField?.fieldType == ENTITY_FIELD_TYPE.TEXT.VALUE ||
      targetField?.fieldType == ENTITY_FIELD_TYPE.LONG_TEXT.VALUE ||
      targetField?.fieldType == ENTITY_FIELD_TYPE.EMAIL.VALUE ||
      targetField?.fieldType == ENTITY_FIELD_TYPE.PHONE.VALUE ||
      targetField?.fieldType == ENTITY_FIELD_TYPE.URL.VALUE ||
      targetField?.fieldType == ENTITY_FIELD_TYPE.ADDRESS.VALUE
    ) {
      return (
        <Form.Item field={fieldName}>
          <Input placeholder="请输入静态值" />
        </Form.Item>
      );
    }

    if (targetField?.fieldType == ENTITY_FIELD_TYPE.NUMBER.VALUE) {
      return (
        <Form.Item field={fieldName}>
          <InputNumber placeholder="请输入静态值" />
        </Form.Item>
      );
    }

    if (targetField?.fieldType == ENTITY_FIELD_TYPE.BOOLEAN.VALUE) {
      return (
        <Form.Item field={fieldName} triggerPropName="checked">
          <Switch />
        </Form.Item>
      );
    }

    if (targetField?.fieldType == ENTITY_FIELD_TYPE.DATE.VALUE) {
      return (
        <Form.Item field={fieldName}>
          <DatePicker placeholder="请输入静态值" />
        </Form.Item>
      );
    }

    if (targetField?.fieldType == ENTITY_FIELD_TYPE.DATETIME.VALUE) {
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
          title: field.label
        });
      } else if (
        field?.fieldType === fieldType ||
        field?.fieldType === FieldType.VALUE ||
        field?.fieldType === FieldType.FORMULA ||
        ([ENTITY_FIELD_TYPE.NUMBER.VALUE, ENTITY_FIELD_TYPE.ID.VALUE].includes(field?.fieldType) &&
          [ENTITY_FIELD_TYPE.NUMBER.VALUE, ENTITY_FIELD_TYPE.ID.VALUE].includes(fieldType))
      ) {
        //  FieldType 计算节点类型
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
        NodeType.MODAL
      ]),
    []
  );

  // 使用 useCallback 缓存函数，避免不必要的重新创建
  const getVariableOptions = useCallback(
    (nodeId: string, dataNodeId?: string, item?: any): TreeSelectDataType[] => {
      const nodeTypes = [
        NodeType.DATA_QUERY,
        NodeType.START_ENTITY,
        NodeType.START_FORM,
        NodeType.DATA_CALC,
        NodeType.MODAL
      ];

      const fieldId = form.getFieldValue(item.field + '.fieldId');
      const targetField = fieldList.find((ele) => ele.fieldId == fieldId);
      const fieldType = targetField?.fieldType;

      let nodes = getPrecedingNodes(nodeId, triggerEditorSignal.nodes.value, nodeTypes);

      if (dataNodeId) {
        nodes = triggerEditorSignal.nodes.value.filter((node) => node.id == dataNodeId);
      }

      const options: TreeSelectDataType[] = [];

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
    [nodesWithConditionFields, fieldList]
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

  return (
    <div className={styles.conditionWrapper}>
      <Form.Item validateTrigger={['onChange']}>
        <Form.List field="fields">
          {(fields, { add, remove }) => {
            return (
              <>
                {fields.map((item: any, index: number) => {
                  return (
                    <Grid.Row gutter={8} key={item.key} align="center">
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
                              form.clearFields(item.field + '.value');
                            }}
                          />
                        </Form.Item>
                      </Grid.Col>

                      <Grid.Col span={2}>
                        <div style={{ marginBottom: '15px' }}>的值设为</div>
                      </Grid.Col>

                      <Grid.Col span={5}>
                        <Form.Item field={item.field + '.operatorType'}>
                          <Select
                            disabled={form.getFieldValue(item.field + '.fieldId') == undefined}
                            options={valueTypeOptions}
                            onChange={() => {
                              form.setFieldValue(item.field + '.value', undefined);
                            }}
                          />
                        </Form.Item>
                      </Grid.Col>

                      <Grid.Col span={8}>
                        {form.getFieldValue(item.field + '.operatorType') == undefined && (
                          <Form.Item field={item.field + '.value'}>
                            <Input placeholder="请输入" disabled />
                          </Form.Item>
                        )}

                        {form.getFieldValue(item.field + '.operatorType') == FieldType.VALUE &&
                          StaticValueComponent(item.field + '.value', form.getFieldValue(item.field + '.fieldId'))}

                        {form.getFieldValue(item.field + '.operatorType') == FieldType.VARIABLES && (
                          <Form.Item field={item.field + '.value'}>
                            <TreeSelect
                              treeData={getVariableOptions(nodeId, dataNodeId, item)}
                              triggerElement={(params) => {
                                return (
                                  <Input
                                    readOnly
                                    value={showTriggerElement(params, getVariableOptions(nodeId, dataNodeId, item))}
                                  ></Input>
                                );
                              }}
                            />
                          </Form.Item>
                        )}
                      </Grid.Col>

                      <Grid.Col span={2}>
                        <IconDelete
                          style={{ fontSize: '15px', color: '#4E5969', marginBottom: '15px' }}
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
