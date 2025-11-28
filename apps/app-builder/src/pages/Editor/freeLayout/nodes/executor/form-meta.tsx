import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { FormContent, FormHeader } from '../../form-components';
import { useIsSidebar } from '../../hooks';
import { type FlowNodeJSON } from '../../typings';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  return (
    <>
      <FormHeader />
    </>
  );
};

export const formMeta: FormMeta<FlowNodeJSON['data']> = {
  render: renderForm
};
