import {
  Field,
  type FieldRenderProps,
  type FormMeta,
  type FormRenderProps,
  ValidateTrigger
} from '@flowgram.ai/fixed-layout-editor';
import { JsonSchemaEditor, provideJsonSchemaOutputs, syncVariableTitle } from '@flowgram.ai/form-materials';

import { FormContent, FormHeader, FormOutputs } from '../../form-components';
import { useIsSidebar } from '../../hooks';
import { type FlowNodeJSON, type JsonSchema } from '../../typings';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  if (isSidebar) {
    return (
      <>
        <FormHeader />
        <FormContent>
          <Field
            name="outputs"
            render={({ field: { value, onChange } }: FieldRenderProps<JsonSchema>) => (
              <>
                <JsonSchemaEditor value={value} onChange={(value) => onChange(value as JsonSchema)} />
              </>
            )}
          />
        </FormContent>
      </>
    );
  }
  return (
    <>
      <FormHeader />
      <FormContent>
        <FormOutputs />
      </FormContent>
    </>
  );
};

export const formMeta: FormMeta<FlowNodeJSON['data']> = {
  render: renderForm,
  validateTrigger: ValidateTrigger.onChange,
  validate: {
    title: ({ value }: { value: string }) => (value ? undefined : 'Title is required')
  },
  effect: {
    title: syncVariableTitle,
    outputs: provideJsonSchemaOutputs
  }
};
