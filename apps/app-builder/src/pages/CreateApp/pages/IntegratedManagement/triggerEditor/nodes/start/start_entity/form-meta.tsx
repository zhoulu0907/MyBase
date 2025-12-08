import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';

import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Checkbox, Form, Grid, Input, Radio, Select } from '@arco-design/web-react';
import type { TreeSelectDataType } from '@arco-design/web-react/es/TreeSelect/interface';
import {
  getEntityFieldsWithChildren,
  getEntityListByApp,
  getFieldCheckTypeApi,
  type AppEntityField,
  type ConditionField,
  type EntityFieldValidationTypes,
  type MetadataEntityPair
} from '@onebase/app';
import { getHashQueryParam } from '@onebase/common';
import { useEffect, useState } from 'react';
import ConditionEditor from '../../../components/condition-editor';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { updateStartEntityOutputs } from './output';

const Option = Select.Option;
const CheckboxGroup = Checkbox.Group;
const RadioGroup = Radio.Group;

const beforeTriggerEvents = [
  {
    label: '创建前',
    value: 'beforeCreate'
  },
  {
    label: '修改前',
    value: 'beforeUpdate'
  },
  {
    label: '删除前',
    value: 'beforeDelete'
  }
];

const afterTriggerEvents = [
  {
    label: '创建后',
    value: 'afterCreate'
  },
  {
    label: '修改后',
    value: 'afterUpdate'
  },
  {
    label: '删除后',
    value: 'afterDelete'
  }
];

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();

  const handlePropsOnChange = (values: any) => {
    triggerEditorSignal.setNodeData(node.id, values);
  };

  const [payloadForm] = Form.useForm();

  const [conditionFields, setConditionFields] = useState<TreeSelectDataType[]>([]);
  const [validationTypes, setValidationTypes] = useState<EntityFieldValidationTypes[]>([]);

  const [entityList, setEntityList] = useState<MetadataEntityPair[]>([]);

  const tableName = Form.useWatch('tableName', payloadForm);
  const triggerType = Form.useWatch('triggerType', payloadForm);

  useEffect(() => {
    const appId = getHashQueryParam('appId');
    if (appId) {
      handleGetEntityListByApp(appId);
    }
  }, []);

  useEffect(() => {
    if (entityList.length > 0 && tableName) {
      const eid = entityList.find((item) => item.tableName === tableName)?.entityId;
      if (eid) {
        setNodeData(eid);
      }
    }
  }, [entityList, tableName]);

  const handleGetEntityListByApp = async (appId: string) => {
    const res = await getEntityListByApp(appId);
    setEntityList(res);
  };

  const setNodeData = async (eid: string) => {
    const res = await getEntityFieldsWithChildren(eid);

    if (res && res.parentFields) {
      console.log(res);

      const conditions: ConditionField[] = [];
      const fieldIds: string[] = [];

      res.parentFields.forEach((item: AppEntityField) => {
        fieldIds.push(item.fieldId);

        conditions.push({
          label: item.displayName,
          value: `${res.tableName}.${item.fieldName}`,
          fieldType: item.fieldType
        });
      });

      if (fieldIds?.length) {
        const newValidationTypes = await getFieldCheckTypeApi(fieldIds);

        //   TODO(mickey): 需要卞老师补充fieldName字段
        newValidationTypes.forEach((item: EntityFieldValidationTypes) => {
          const fieldName =
            [...res.parentFields].find((field: AppEntityField) => field.fieldId == item.fieldId)?.fieldName || '';
          item.fieldKey = `${res.tableName}.${fieldName}`;

          if (!fieldName) {
            for (const subEntity of res.childEntities) {
              const foundField = subEntity.childFields.find((field: AppEntityField) => field.fieldId == item.fieldId);
              if (foundField) {
                // 同时返回字段名和子表tableName
                item.fieldKey = `${subEntity.childTableName}.${foundField.fieldName}`;
              }
            }
          }
        });

        setValidationTypes(newValidationTypes);
      }

      setConditionFields([
        {
          key: res.tableName,
          title: res.entityName,
          children: conditions.map((item) => {
            return {
              key: item.value,
              title: item.label,
              fieldType: item.fieldType
            };
          })
        }
      ]);

      // 更新节点输出配置
      updateStartEntityOutputs(node.id, conditions);
    }
  };

  // 触发类型
  const handleTriggerTypeChange = () => {
    payloadForm.clearFields('triggerEvents');
    handlePropsOnChange({
      ...triggerEditorSignal.nodeData.value[node.id],
      triggerEvents: []
    });
  };

  return (
    <>
      <FormHeader />
      {isSidebar ? (
        <FormContent>
          <Form
            form={payloadForm}
            initialValues={{ ...triggerEditorSignal.nodeData.value[node.id] }}
            layout="vertical"
            requiredSymbol={{ position: 'end' }}
          >
            <Grid.Row>
              <Form.Item label="节点ID" field="id" initialValue={node.id} rules={[{ required: true }]}>
                <Input disabled />
              </Form.Item>
            </Grid.Row>

            <Grid.Row>
              <Form.Item label="实体" field="tableName">
                <Select disabled={true}>
                  {entityList?.map((item) => (
                    <Option key={item.entityUuid} value={item.tableName}>
                      {item.entityName}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Grid.Row>

            <Grid.Row>
              <Form.Item
                label="触发类型"
                field="triggerType"
                layout="vertical"
                rules={[{ required: true, message: '请选择触发类型' }]}
              >
                <RadioGroup
                  onChange={handleTriggerTypeChange}
                  direction="horizontal"
                  options={[
                    {
                      label: '前置',
                      value: 'before'
                    },
                    {
                      label: '后置',
                      value: 'after'
                    }
                  ]}
                />
              </Form.Item>
            </Grid.Row>

            {triggerType && (
              <Grid.Row align="end">
                <Form.Item
                  label="事件类型"
                  field="triggerEvents"
                  layout="vertical"
                  rules={[{ required: true, message: '请选择触发类型' }]}
                >
                  <CheckboxGroup
                    direction="horizontal"
                    options={triggerType === 'before' ? beforeTriggerEvents : afterTriggerEvents}
                  />
                </Form.Item>
              </Grid.Row>
            )}

            <Grid.Row>
              <ConditionEditor
                nodeId={node.id}
                label="过滤条件"
                required
                fields={conditionFields}
                entityFieldValidationTypes={validationTypes}
                form={payloadForm}
              />
            </Grid.Row>
          </Form>
        </FormContent>
      ) : (
        <FormContent>
          <FormOutputs />
        </FormContent>
      )}
    </>
  );
};

export const formMeta: FormMeta<FlowNodeJSON['data']> = {
  render: renderForm
  //   validateTrigger: ValidateTrigger.onChange,
  //   validate: {
  //     title: ({ value }: { value: string }) => (value ? undefined : 'Title is required')
  //   },
  //   effect: {
  //     title: syncVariableTitle,
  //     outputs: provideJsonSchemaOutputs
  //   }
};
