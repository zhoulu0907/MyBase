import { type FormMeta, type FormRenderProps } from '@flowgram.ai/free-layout-editor';
import { useEffect, useState } from 'react';
import { type FlowNodeJSON } from '../../../typings';
import styles from './index.module.less';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const [title, setTitle] = useState('追加合并');

  useEffect(() => {
    setTitle(form.getValueIn('title'));
  }, [form]);

  return <div className={styles.unionNode}>{title}</div>;
};

export const formMeta: FormMeta<FlowNodeJSON['data']> = {
  render: renderForm

  //   validateTrigger: ValidateTrigger.onChange,
  //   validate: {
  //     title: ({ value }: { value: string }) => (value ? undefined : 'Title is required')
  //   },
  //   effect: {
  //     title: syncVariableTitle,
  //     outputs: provideJsonSchemaOutputs
  //   }
};
