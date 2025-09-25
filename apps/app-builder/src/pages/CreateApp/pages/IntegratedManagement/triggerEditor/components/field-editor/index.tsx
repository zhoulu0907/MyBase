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
import { FieldType, type AppEntityField } from '@onebase/app';
import { ENTITY_FIELD_TYPE } from '@onebase/ui-kit';
import React, { useEffect, useState } from 'react';
import { NodeType } from '../../nodes/const';
import { getBeforeCurQueryNodes } from '../../nodes/utils';
import styles from './index.module.less';

export interface FieldEditorProps {
  nodeId: string;
  form: FormInstance;
  fieldList: AppEntityField[];
}

const valueTypeOptions = [
  { label: '值', value: FieldType.VALUE },
  { label: '变量', value: FieldType.VARIABLES }
];

const FieldEditor: React.FC<FieldEditorProps> = ({ fieldList, form, nodeId }) => {
  const [selectedFields, setSelectedFields] = useState<any[]>();

  const fields = Form.useWatch('fields', form);

  useEffect(() => {
    setSelectedFields(form.getFieldValue('fields'));
  }, [form, fieldList]);

  useEffect(() => {}, [fields]);

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
    const nodeTypes = [NodeType.DATA_QUERY, NodeType.START_ENTITY, NodeType.START_FORM];

    const nodes = getBeforeCurQueryNodes(nodeId, triggerEditorSignal.nodes.value, nodeTypes);
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
      }
    });

    return options;
  };

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

                      <Grid.Col span={2}>
                        <div style={{ lineHeight: '32px' }}>的值设为</div>
                      </Grid.Col>

                      <Grid.Col span={5}>
                        <Form.Item field={item.field + '.operatorType'}>
                          <Select
                            disabled={form.getFieldValue(item.field + '.fieldId') == undefined}
                            options={valueTypeOptions}
                          />
                        </Form.Item>
                      </Grid.Col>

                      <Grid.Col span={8}>
                        {form.getFieldValue(item.field + '.operatorType') == undefined && (
                          <Form.Item field={item.field + '.fieldValue'}>
                            <Input placeholder="请输入" disabled />
                          </Form.Item>
                        )}

                        {form.getFieldValue(item.field + '.operatorType') == FieldType.VALUE &&
                          StaticValueComponent(item.field + '.fieldValue', form.getFieldValue(item.field + '.fieldId'))}

                        {form.getFieldValue(item.field + '.operatorType') == FieldType.VARIABLES && (
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
