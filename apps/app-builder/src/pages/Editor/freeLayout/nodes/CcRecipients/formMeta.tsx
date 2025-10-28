import { type FormRenderProps, Field, type FormMeta } from '@flowgram.ai/free-layout-editor';
import { FormContent, FormHeader } from '../../form-components/index';
import { Input } from '@arco-design/web-react';

export const CarbonCopyFormRender = ({ form }: FormRenderProps) => {
  return (
    <>
      <FormHeader />
      <FormContent>
        <div>
          <Field name="test">
            {({ field, fieldState }) => (
              <div>
                <Input
                  placeholder="请选择抄送人"
                  value={field.value || ''}
                  onChange={field.onChange}
                //   status={fieldState.invalid ? 'error' : undefined}
                  style={{
                    width: '100%'
                  }}
                />
              </div>
            )}
          </Field>
        </div>
      </FormContent>
    </>
  );
};

export const formMeta: FormMeta = {
  validateTrigger: 'onChange',
  validate: {
    test: ({ value }) => {
      if (!value || value.trim() === '') {
        return '抄送人不能为空';
      }
      return undefined;
    }
  },
  render: CarbonCopyFormRender
};
