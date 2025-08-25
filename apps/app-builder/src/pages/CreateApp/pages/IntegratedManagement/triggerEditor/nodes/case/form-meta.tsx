import { type FormMeta, type FormRenderProps, ValidateTrigger } from '@flowgram.ai/fixed-layout-editor';

import { FormContent, FormHeader, FormInputs, FormOutputs } from '../../form-components';
import { type FlowNodeJSON } from '../../typings';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => (
  <>
    <FormHeader />
    <FormContent>
      <FormInputs />
      <FormOutputs />
    </FormContent>
  </>
);

export const formMeta: FormMeta<FlowNodeJSON['data']> = {
  render: renderForm,
  validateTrigger: ValidateTrigger.onChange,
  validate: {
    'inputsValues.*': ({ value, context, formValues, name }) => {
      const valuePropetyKey = name.replace(/^inputsValues\./, '');
      const required = formValues.inputs?.required || [];
      if (required.includes(valuePropetyKey) && (value === '' || value === undefined || value?.content === '')) {
        return `${valuePropetyKey} is required`;
      }
      return undefined;
    }
  }
};
