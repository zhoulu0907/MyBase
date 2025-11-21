import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { defaultFormMeta } from '../../default-form-meta';
import { Form, Input } from '@arco-design/web-react';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { validateNodeForm } from '../../utils';
import { type FlowNodeJSON } from '../../../typings';
import { useEffect } from 'react';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();

  const { node } = useNodeRenderContext();
  const [payloadForm] = Form.useForm();

  useEffect(() => {
    payloadForm && validateNodeForm(form, payloadForm, true);
  }, [payloadForm]);

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
            <Form.Item label="节点ID" field="id" initialValue={node.id} rules={[{ required: true }]}>
              <Input disabled />
            </Form.Item>
            <Form.Item label="用户提示语" field="prompt">
              <Input placeholder="用于提示用户流程已被终结，不超过30个字" maxLength={30} />
            </Form.Item>
            <Form.Item label="状态码" field="statusCode">
              <Input placeholder="请输入" />
            </Form.Item>
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

export const formMeta: FormMeta = {
  ...defaultFormMeta,
  render: renderForm
};
