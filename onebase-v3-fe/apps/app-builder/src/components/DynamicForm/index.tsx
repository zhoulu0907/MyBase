import { Form as ArcoForm } from '@arco-design/web-react';
import { createForm, type Form } from '@formily/core';
import { createSchemaField, FormProvider, type ISchema } from '@formily/react';
import React, { useMemo } from 'react';
import { componentMap, FormilyFormItem } from './componentMapper';

interface DynamicFormProps {
  schema: ISchema;
  form?: Form;
  /** 额外组件，会与默认 componentMap 合并，用于 schema 中 x-component 引用 */
  components?: Record<string, React.ComponentType<any>>;
}

const DynamicForm = ({ schema, form: propForm, components: extraComponents }: DynamicFormProps) => {
  const form = useMemo(() => propForm || createForm(), [propForm]);
  const SchemaField = useMemo(
    () =>
      createSchemaField({
        components: {
          ...componentMap,
          FormItem: FormilyFormItem,
          ...extraComponents
        }
      }),
    [extraComponents]
  );

  return (
    <FormProvider form={form}>
      <ArcoForm layout="vertical">
        <SchemaField schema={schema} />
      </ArcoForm>
    </FormProvider>
  );
};

export default DynamicForm;
