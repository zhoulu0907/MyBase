import { Form as ArcoForm } from '@arco-design/web-react';
import { createForm, type Form } from '@formily/core';
import { createSchemaField, FormProvider, type ISchema } from '@formily/react';
import { useMemo } from 'react';
import { componentMap, FormilyFormItem } from './componentMapper';

const SchemaField = createSchemaField({
  components: {
    ...componentMap,
    FormItem: FormilyFormItem
  }
});

interface DynamicFormProps {
  schema: ISchema;
  form?: Form;
}

const DynamicForm = ({ schema, form: propForm }: DynamicFormProps) => {
  const form = useMemo(() => propForm || createForm(), [propForm]);

  return (
    <FormProvider form={form}>
      <ArcoForm layout="vertical">
        <SchemaField schema={schema} />
      </ArcoForm>
    </FormProvider>
  );
};

export default DynamicForm;
