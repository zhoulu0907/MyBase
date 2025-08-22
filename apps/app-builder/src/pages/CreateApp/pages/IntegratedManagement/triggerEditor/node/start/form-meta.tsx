import {
  DisplayOutputs,
  JsonSchemaEditor,
  provideJsonSchemaOutputs,
  syncVariableTitle
} from '@flowgram.ai/form-materials';
import {
  Field,
  type FieldRenderProps,
  type FormMeta,
  type FormRenderProps,
  ValidateTrigger
} from '@flowgram.ai/free-layout-editor';

// import { FormContent, FormHeader } from '../../form-components';
import { useIsSidebar } from '../../hooks';
import { type FlowNodeJSON, type JsonSchema } from '../../typings';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON>) => {
  const isSidebar = useIsSidebar();
  if (isSidebar) {
    return (
      <>
        {/* <FormHeader />
        <FormContent> */}
        <Field
          name="outputs"
          render={({ field: { value, onChange } }: FieldRenderProps<JsonSchema>) => (
            <>
              <JsonSchemaEditor value={value} onChange={(value) => onChange(value as JsonSchema)} />
            </>
          )}
        />
        {/* </FormContent> */}
      </>
    );
  }
  return (
    <>
      {/* <FormHeader />
      <FormContent> */}
      <DisplayOutputs displayFromScope />
      {/* </FormContent> */}
    </>
  );
};

export const formMeta: FormMeta<FlowNodeJSON> = {
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
