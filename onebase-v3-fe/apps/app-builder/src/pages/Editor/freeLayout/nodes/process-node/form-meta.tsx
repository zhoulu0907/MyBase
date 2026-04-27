import type { FormMeta } from '@flowgram.ai/free-layout-editor';

import { defaultFormMeta } from '../default-form-meta';
import { FormProcess } from '../../form-components';

export const renderForm = () => {
  return (
    <>
      <FormProcess />
    </>
  );
};

export const formMeta: FormMeta = {
  ...defaultFormMeta,
  render: renderForm
};
