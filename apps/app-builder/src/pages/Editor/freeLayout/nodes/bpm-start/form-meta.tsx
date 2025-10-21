import type { FormRenderProps, FormMeta } from '@flowgram.ai/free-layout-editor';

import { Avatar } from '@douyinfe/semi-ui';

import type { FlowNodeJSON } from '../../typings';
import iconStart from '../../assets/icon-start.jpg';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON>) => (
  <>
    <div
      style={{
        width: 73,
        height: 29,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: '#ccc'
      }}
    >
      <Avatar
        shape="circle"
        style={{
          width: 20,
          height: 20,
          borderRadius: '50%',
          cursor: 'move'
        }}
        alt="Icon"
        src={iconStart}
      />
    </div>
  </>
);

export const formMeta: FormMeta<FlowNodeJSON> = {
  render: renderForm
};
