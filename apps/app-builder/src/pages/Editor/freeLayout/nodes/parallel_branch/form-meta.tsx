import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { FormHeader } from '../../form-components';
import { type FlowNodeJSON } from '../../typings';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  return (
    <>
      <FormHeader />
    </>
  );
};

export const formMeta: FormMeta<FlowNodeJSON['data']> = {
  render: renderForm
};
