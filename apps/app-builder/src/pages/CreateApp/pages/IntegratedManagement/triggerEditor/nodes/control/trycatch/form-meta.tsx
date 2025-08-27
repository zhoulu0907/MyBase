import { type FormMeta, type FormRenderProps, ValidateTrigger } from '@flowgram.ai/fixed-layout-editor';

import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { type FlowNodeJSON } from '../../../typings';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => (
  <>
    <FormHeader />
    <FormContent>
      <FormOutputs />
    </FormContent>
  </>
);

export const formMeta: FormMeta<FlowNodeJSON['data']> = {
  render: renderForm,
  validateTrigger: ValidateTrigger.onChange,
  validate: {
    title: ({ value }: { value: string }) => (value ? undefined : 'Title is required')
  }
};
