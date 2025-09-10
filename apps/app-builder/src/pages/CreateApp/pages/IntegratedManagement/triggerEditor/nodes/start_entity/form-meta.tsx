import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';

import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Input, Select } from '@arco-design/web-react';
import {
  getEntityListByApp,
  getFlowMgmt,
  type Condition,
  type ConfitionField,
  type EntityFieldValidationTypes
} from '@onebase/app';
import { getHashQueryParam } from '@onebase/common';
import { useEffect, useState } from 'react';
import { FormContent, FormHeader, FormOutputs } from '../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../hooks';
import { type FlowNodeJSON } from '../../typings';

const Option = Select.Option;

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();

  const handlePropsOnChange = (values: any) => {
    triggerEditorSignal.setNodeData(node.id, values);
  };

  const [payloadForm] = Form.useForm();
  const triggerType = Form.useWatch('triggerType', payloadForm);

  const [conditionFields, setConditionFields] = useState<ConfitionField[]>([]);
  const [validationTypes, setValidationTypes] = useState<EntityFieldValidationTypes[]>([]);

  const [entityList, setEntityList] = useState<any[]>();

  useEffect(() => {
    const appId = getHashQueryParam('appId');
    if (appId) {
      handleGetEntityListByApp(appId);
    }

    const flowId = getHashQueryParam('flowId');
    if (flowId) {
      handleGetFlowInfo(flowId);
    }
  }, []);

  // 根据流程id获取流程详细信息
  const handleGetFlowInfo = async (flowId: string) => {
    const res = await getFlowMgmt(flowId);
    console.log('res: ', res);
    if (res && res.triggerConfig && res.triggerConfig.entityId) {
      payloadForm.setFieldValue('entityId', res.triggerConfig.entityId);
    }
  };

  const handleGetEntityListByApp = async (appId: string) => {
    const res = await getEntityListByApp(appId);
    setEntityList(res);
  };

  const onValuesChange = (changeValue: any, values: any) => {
    console.log('onValuesChange: ', changeValue, values);

    handlePropsOnChange(values);
  };

  const onConditionChange = (conditions: Condition[]) => {
    console.log(conditions);
    handlePropsOnChange({
      ...triggerEditorSignal.nodeData.value[node.id],
      filterConditions: conditions
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
            onValuesChange={onValuesChange}
          >
            <Form.Item label="节点ID" field="id" initialValue={node.id}>
              <Input disabled />
            </Form.Item>
            <Form.Item label="实体ID" field="entityId">
              <Select>
                {entityList?.map((item) => (
                  <Option key={item.entityId} value={item.entityId}>
                    {item.entityName}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          </Form>
          {/* {validationTypes && (
            <ConditionEditor
              onChange={onConditionChange}
              data={triggerEditorSignal.nodeData.value[node.id].filterConditions}
              fields={conditionFields}
              entityFieldValidationTypes={validationTypes}
            />
          )} */}
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
