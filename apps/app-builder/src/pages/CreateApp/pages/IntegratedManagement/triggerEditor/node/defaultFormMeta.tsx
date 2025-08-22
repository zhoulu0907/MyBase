import { Field, type FormRenderProps } from '@flowgram.ai/free-layout-editor';
import type { FlowNodeJSON } from '../typings';
import styles from './index.module.less';

export const defaultFormMeta = ({ form }: FormRenderProps<FlowNodeJSON>) => {
  return (
    <>
      <div className={styles.defaultNodeContainer}>
        <Field<string> name="title">
          {({ field }) => <div className={styles.defaultNodeTitle}>{field.value}</div>}
        </Field>
      </div>
    </>
  );
};

export default defaultFormMeta;
