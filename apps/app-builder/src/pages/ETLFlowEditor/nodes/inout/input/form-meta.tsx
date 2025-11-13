import InputIcon from '@/assets/images/etl/node_input.svg';
import { type FormMeta, type FormRenderProps } from '@flowgram.ai/free-layout-editor';
import { etlEditorSignal } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import { type FlowNodeJSON } from '../../../typings';
import styles from './index.module.less';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  useSignals();

  const { nodeData } = etlEditorSignal;

  return (
    <div className={styles.inputNode}>
      <img src={InputIcon} alt="input" />

      {nodeData.value[form.getValueIn('id')]?.title || '输入节点'}
    </div>
  );
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
