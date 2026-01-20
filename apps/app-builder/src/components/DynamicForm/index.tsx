import { createForm } from '@formily/core';
import { createSchemaField, FormProvider, type ISchema } from '@formily/react';
import { componentMap, FormilyFormItem } from './componentMapper';

const SchemaField = createSchemaField({
  components: {
    ...componentMap,
    FormItem: FormilyFormItem,
  },
})

interface DynamicFormProps {
  schema: ISchema;
}

const DynamicForm = ({ schema }: DynamicFormProps) => {
  const form = createForm();

  return (
    <FormProvider form={form}>
      <SchemaField schema={schema} />
    </FormProvider>
  );
}



export default DynamicForm;