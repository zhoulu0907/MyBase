import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';

import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Input, Select } from '@arco-design/web-react';
import ConditionEditor from '../../components/condition-editor';
import { FormContent, FormHeader, FormOutputs } from '../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../hooks';
import { type FlowNodeJSON } from '../../typings';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();

  const handlePropsOnChange = (key: string, value: any) => {
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      [key]: value
    });
  };

  const [payloadForm] = Form.useForm();
  const triggerType = Form.useWatch('triggerType', payloadForm);

  return (
    <>
      <FormHeader />
      {isSidebar ? (
        <FormContent>
          <Form form={payloadForm} initialValues={{ ...triggerEditorSignal.nodeData.value[node.id] }}>
            <Form.Item label="节点ID" field="id" initialValue={node.id}>
              <Input disabled />
            </Form.Item>
            <Form.Item label="节点名称" field="title">
              <Input onChange={(e) => handlePropsOnChange('title', e)} />
            </Form.Item>
            <Form.Item label="触发类型" field="triggerType">
              <Select
                onChange={(value) => handlePropsOnChange('triggerType', value)}
                allowClear={true}
                options={[
                  { label: '界面交互触发', value: 'interaction_trigger' },
                  { label: '表单数据触发', value: 'form_trigger' },
                  { label: '定时触发', value: 'time_trigger' },
                  { label: '日期字段触发', value: 'date_trigger' },
                  { label: '流程触发', value: 'flow_trigger' }
                ]}
              />
            </Form.Item>

            {triggerType == 'interaction_trigger' && (
              <>
                <Form.Item label="触发范围" field="triggerScope">
                  <Select onChange={(value) => handlePropsOnChange('triggerScope', value)} />
                </Form.Item>
                <Form.Item label="触发事件" field="triggerEvent">
                  <Select
                    onChange={(value) => handlePropsOnChange('triggerEvent', value)}
                    options={[
                      { label: '点击', value: 'click' },
                      { label: '提交', value: 'submit' }
                    ]}
                  />
                </Form.Item>
                <Form.Item label="过滤条件" field="filterCondition">
                  <Input />
                </Form.Item>
              </>
            )}
          </Form>
          <ConditionEditor onChange={() => {}} fields={[]} fieldOperatorMapping={{}} />
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
