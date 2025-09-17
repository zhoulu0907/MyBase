import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { type FlowNodeJSON } from '../../../typings';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { Form } from '@arco-design/web-react';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import ConditionEditor from '../../../components/condition-editor';
import { type ConfitionField, type EntityFieldValidationTypes } from '@onebase/app';
import { useEffect, useState } from 'react';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();
  const [payloadForm] = Form.useForm();
  const [validationTypes, setValidationTypes] = useState<EntityFieldValidationTypes[]>([]);
  const [conditionFields, setConditionFields] = useState<ConfitionField[]>([]);

  const onValuesChange = (changeValue: any, values: any) => {
    console.log('onValuesChange: ', changeValue, values);
    triggerEditorSignal.setNodeData(node.id, values);
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
            layout="vertical"
          >
            <Form.Item field="filterCondition" label="条件" required>
              <ConditionEditor
                data={triggerEditorSignal.nodeData.value[node.id]?.filterCondition || []}
                fields={conditionFields}
                entityFieldValidationTypes={validationTypes}
              />
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

export const formMeta: FormMeta<FlowNodeJSON['data']> = {
  render: renderForm
};