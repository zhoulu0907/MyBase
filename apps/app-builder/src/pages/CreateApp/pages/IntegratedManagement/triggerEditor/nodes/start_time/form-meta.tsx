import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';

import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Grid, Input, InputNumber } from '@arco-design/web-react';
import TimeEditor from '../../components/time-editor';
import { FormContent, FormHeader, FormOutputs } from '../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../hooks';
import { type FlowNodeJSON } from '../../typings';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();

  const [payloadForm] = Form.useForm();

  const handlePropsOnChange = (values: any) => {
    triggerEditorSignal.setNodeData(node.id, values);
  };

  const onValuesChange = (changeValue: any, values: any) => {
    console.log('onValuesChange: ', changeValue, values);

    handlePropsOnChange(values);
  };

  return (
    <>
      <FormHeader />
      {isSidebar ? (
        <FormContent>
          <Form
            form={payloadForm}
            initialValues={{ ...triggerEditorSignal.nodeData.value[node.id] }}
            onValuesChange={onValuesChange}
          >
            <Grid.Row>
              <Grid.Col span={12}>
                <Form.Item label="节点ID" field="id" initialValue={node.id}>
                  <Input disabled />
                </Form.Item>
              </Grid.Col>
            </Grid.Row>

            <TimeEditor />

            <Grid.Row>
              <Grid.Col span={12}>
                <Form.Item
                  label="延迟秒数"
                  layout="vertical"
                  field="delaySeconds"
                  rules={[
                    { required: true, message: '请输入延迟秒数' },
                    { type: 'number', min: 0, message: '延迟秒数不能小于0' },
                    { type: 'number', max: 100, message: '延迟秒数不能大于100' }
                  ]}
                >
                  <InputNumber mode="button" suffix="秒" />
                </Form.Item>
              </Grid.Col>
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
