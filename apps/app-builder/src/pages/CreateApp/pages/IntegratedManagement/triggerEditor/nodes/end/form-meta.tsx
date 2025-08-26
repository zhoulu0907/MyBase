import { type FormMeta } from '@flowgram.ai/fixed-layout-editor';

import { FormContent, FormHeader } from '../../form-components';
import { useIsSidebar } from '../../hooks';
import { defaultFormMeta } from '../default-form-meta';

export const renderForm = () => {
  const isSidebar = useIsSidebar();
  if (isSidebar) {
    return (
      <>
        <FormHeader />
        <FormContent></FormContent>
      </>
    );
  }
  return (
    <>
      <FormHeader />
      <FormContent></FormContent>
    </>
  );
};

export const formMeta: FormMeta = {
  ...defaultFormMeta,
  render: renderForm
};
