import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { type FlowNodeJSON } from '../../../typings';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  return <div style={{ width: '100px', height: '100px', backgroundColor: '#fff' }}>123123</div>;
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
