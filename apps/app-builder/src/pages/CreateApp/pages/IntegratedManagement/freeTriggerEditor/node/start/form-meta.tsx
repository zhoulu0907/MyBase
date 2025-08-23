import { Field, ValidateTrigger, type FormMeta, type FormRenderProps } from '@flowgram.ai/free-layout-editor';
import { useIsSidebar } from '../../hooks';
import type { FlowNodeJSON } from '../../typings';
import styles from '../index.module.less';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON>) => {
  const isSidebar = useIsSidebar();

  if (isSidebar) {
    return (
      <>
        <div className={styles.startNodeContainer}>
          <Field<string> name="title">
            {({ field }) => <div className={styles.startNodeTitle}>{field.value}</div>}
          </Field>
        </div>
      </>
    );
  }

  return (
    <>
      <div
        className={styles.startNodeContainer}
        onClick={() => {
          console.log(form);
        }}
      >
        <Field<string> name="title">{({ field }) => <div className={styles.startNodeTitle}>{field.value}</div>}</Field>
      </div>
    </>
  );
};

export const formMeta: FormMeta<FlowNodeJSON> = {
  render: renderForm,
  validateTrigger: ValidateTrigger.onChange,
  validate: {
    title: ({ value }: { value: string }) => (value ? undefined : 'Title is required')
  }
  //   effect: {
  //     title: syncVariableTitle,
  //     outputs: provideJsonSchemaOutputs
  //   }
};
