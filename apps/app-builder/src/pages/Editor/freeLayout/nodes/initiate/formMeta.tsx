import type { FormMeta } from '@flowgram.ai/free-layout-editor';

import { defaultFormMeta } from '../default-form-meta';
import { FormHeader } from '../../form-components/index';

export const renderForm = () => {
  return (
    <>
      <FormHeader />
    </>
  );
};

export const formMeta: FormMeta = {
  ...defaultFormMeta,
  render: renderForm
};
