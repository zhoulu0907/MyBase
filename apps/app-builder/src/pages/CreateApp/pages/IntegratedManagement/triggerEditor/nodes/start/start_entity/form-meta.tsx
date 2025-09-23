import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';

import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { triggerNodeOutputSignal } from '@/store/singals/trigger_node_output';
import { Checkbox, Form, Grid, Input, Radio, Select } from '@arco-design/web-react';
import {
  getEntityFields,
  getEntityListByApp,
  getFieldCheckTypeApi,
  type ConfitionField,
  type EntityFieldValidationTypes,
  type MetadataEntityField
} from '@onebase/app';
import { getHashQueryParam } from '@onebase/common';
import { useEffect, useState } from 'react';
import ConditionEditor from '../../../components/condition-editor';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { validateNodeForm } from '../../utils';

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

  const updateTriggerEvents = Form.useWatch('updateTriggerEvents', payloadForm);

  const [conditionFields, setConditionFields] = useState<ConfitionField[]>([]);
  const [validationTypes, setValidationTypes] = useState<EntityFieldValidationTypes[]>([]);

  const [entityList, setEntityList] = useState<any[]>();
  const [triggerFieldList, setTriggerFieldList] = useState<any[]>();

  const entityId = Form.useWatch('entityId', payloadForm);
  const triggerType = Form.useWatch('triggerType', payloadForm);

  useEffect(() => {
    const appId = getHashQueryParam('appId');
    if (appId) {
      handleGetEntityListByApp(appId);
    }
  }, []);

  useEffect(() => {
    if (entityId) {
      handleGetEntityFieldList(entityId);
    }
  }, [entityId]);

  useEffect(() => {
    if (triggerType) {
      payloadForm.clearFields('triggerEvents');
      handlePropsOnChange({
        ...triggerEditorSignal.nodeData.value[node.id],
        triggerEvents: []
      });
    }
  }, [triggerType]);

  const handleGetEntityListByApp = async (appId: string) => {
    const res = await getEntityListByApp(appId);
    setEntityList(res);
  };

  const handleGetEntityFieldList = async (eId: string) => {
    const res = await getEntityFields({ entityId: eId });
    if (res) {
      console.log(res);
      const newConditionFields: ConfitionField[] = [];
      const filedIds: string[] = [];
      const fieldList: any[] = [];
      res.forEach((item: MetadataEntityField) => {
        filedIds.push(item.id);
        fieldList.push({
          label: item.displayName,
          value: item.id
        });

        newConditionFields.push({
          label: item.displayName,
          value: item.id,
          fieldType: item.fieldType
        });
      });

      if (filedIds?.length) {
        const newValidationTypes = await getFieldCheckTypeApi(filedIds);
        setValidationTypes(newValidationTypes);
      }

      setConditionFields(newConditionFields);
      setTriggerFieldList(fieldList);
    }
  };

  const onValuesChange = (changeValue: any, values: any) => {
    // 校验表单
    validateNodeForm(form, payloadForm, false);

    // 更新节点输出配置
    updateOutputs(values);

    handlePropsOnChange(values);
  };

  const updateOutputs = (values: any) => {
    const outputs = {
      entityId: values.entityId
    };

    triggerNodeOutputSignal.addTriggerNodeOutput(node.id, outputs);
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
            onValuesChange={onValuesChange}
          >
            <Grid.Row>
              <Form.Item
                label="节点ID"
                field="id"
                initialValue={node.id}
                rules={[{ required: true, message: '请选择' }]}
              >
                <Input disabled />
              </Form.Item>
            </Grid.Row>

            <Grid.Row>
              <Form.Item label="实体" field="entityId">
                <Select disabled={true}>
                  {entityList?.map((item) => (
                    <Option key={item.entityId} value={item.entityId}>
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

            {updateTriggerEvents && (
              <Grid.Row>
                <Form.Item label="触发字段" field="triggerFieldIds" layout="vertical">
                  <Select options={triggerFieldList} mode="multiple" />
                </Form.Item>
              </Grid.Row>
            )}

            <Grid.Row>
              <ConditionEditor
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
