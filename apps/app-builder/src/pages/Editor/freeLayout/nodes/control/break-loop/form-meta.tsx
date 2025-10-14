import { type FormMeta } from '@flowgram.ai/free-layout-editor';

import { FormHeader } from '../../../form-components';

export const renderForm = () => (
  <>
    <FormHeader />
  </>
);

export const formMeta: FormMeta = {
  render: renderForm
};
