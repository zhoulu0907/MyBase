import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Grid, Input } from '@arco-design/web-react';
import { type FormMeta, type FormRenderProps } from '@flowgram.ai/free-layout-editor';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect } from 'react';
import IfNodeConditionEditor from '../../../components/if-node-condition-editor';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { validateNodeForm } from '../../utils';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  useSignals();

  const isSidebar = useIsSidebar();

  const { node } = useNodeRenderContext();
  const [payloadForm] = Form.useForm();

  useEffect(() => {
    payloadForm && validateNodeForm(form, payloadForm, true);
  }, [payloadForm]);

  useEffect(() => {
    init();
  }, []);

  const init = async () => {};

  // 表单内容改变
  const handlePropsOnChange = (values: any) => {
    triggerEditorSignal.setNodeData(node.id, values);
  };

  const onValuesChange = async (changeValue: any, values: any) => {
    // 校验表单
    // validateNodeForm(form, payloadForm, false);

    // handlePropsOnChange(values);
  };

  const getInitData = () => {
    return { ...triggerEditorSignal.nodeData.value[node.id] };
  };

  return (
    <>
      <FormHeader />
      {isSidebar ? (
        <FormContent>
          <Form
            form={payloadForm}
            initialValues={getInitData()}
            onValuesChange={onValuesChange}
            layout="vertical"
            requiredSymbol={{ position: 'end' }}
          >
            <Grid.Row>
              <Form.Item label="节点ID" field="id" initialValue={node.id} rules={[{ required: true }]}>
                <Input disabled />
              </Form.Item>
            </Grid.Row>

            <Grid.Row>
              <IfNodeConditionEditor nodeId={node.id} label="条件" required form={payloadForm} />
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
};
