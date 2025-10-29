import type { FormRenderProps, FormMeta } from '@flowgram.ai/free-layout-editor';

import type { FlowNodeJSON } from '../../typings';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON>) => (
  <>
    <div
      style={{
        width: 74,
        height: 30,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'rgba(78, 89, 105, 1)',
        borderRadius: '15px',
        color: '#fff'
      }}
    >
      <div>开始</div>
    </div>
  </>
);

export const formMeta: FormMeta<FlowNodeJSON> = {
  render: renderForm
};
