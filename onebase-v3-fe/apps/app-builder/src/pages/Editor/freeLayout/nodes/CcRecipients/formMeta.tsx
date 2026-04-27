import { type FormRenderProps, Field, type FormMeta } from '@flowgram.ai/free-layout-editor';
import { FormContent, FormHeader } from '../../form-components/index';
import { Input } from '@arco-design/web-react';

export const CarbonCopyFormRender = ({ form }: FormRenderProps) => {
  return (
    <>
      <FormHeader />
    </>
  );
};

export const formMeta: FormMeta = {
  validateTrigger: 'onChange',
  validate: {},
  render: CarbonCopyFormRender
};
