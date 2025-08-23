import { Field, type FormMeta } from '@flowgram.ai/fixed-layout-editor';
import {
  createInferInputsPlugin,
  DisplayInputsValues,
  type IFlowValue,
  InputsValues
} from '@flowgram.ai/form-materials';

import { FormContent, FormHeader } from '../../form-components';
import { useIsSidebar } from '../../hooks';
import { defaultFormMeta } from '../default-form-meta';

export const renderForm = () => {
  const isSidebar = useIsSidebar();
  if (isSidebar) {
    return (
      <>
        <FormHeader />
        <FormContent>
          <Field<Record<string, IFlowValue | undefined> | undefined> name="inputsValues">
            {({ field: { value, onChange } }) => (
              <>
                <InputsValues value={value} onChange={(_v) => onChange(_v)} />
              </>
            )}
          </Field>
        </FormContent>
      </>
    );
  }
  return (
    <>
      <FormHeader />
      <FormContent>
        <Field<Record<string, IFlowValue | undefined> | undefined> name="inputsValues">
          {({ field: { value } }) => (
            <>
              <DisplayInputsValues value={value} />
            </>
          )}
        </Field>
      </FormContent>
    </>
  );
};

export const formMeta: FormMeta = {
  ...defaultFormMeta,
  render: renderForm,
  plugins: [
    createInferInputsPlugin({
      sourceKey: 'inputsValues',
      targetKey: 'inputs'
    })
  ]
};
