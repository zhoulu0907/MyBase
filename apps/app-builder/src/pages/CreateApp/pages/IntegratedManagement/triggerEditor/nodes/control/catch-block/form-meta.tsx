import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Grid } from '@arco-design/web-react';
import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { type ConfitionField, type EntityFieldValidationTypes } from '@onebase/app';
import { useState } from 'react';
import ConditionEditor from '../../../components/condition-editor';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();
  const [payloadForm] = Form.useForm();
  const [validationTypes, setValidationTypes] = useState<EntityFieldValidationTypes[]>([]);
  const [conditionFields, setConditionFields] = useState<ConfitionField[]>([]);

  const onValuesChange = (changeValue: any, values: any) => {
    handlePropsOnChange(values);
  };

  const handlePropsOnChange = (values: any) => {
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
            <Grid.Row>
              <ConditionEditor
                label="条件"
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
};
