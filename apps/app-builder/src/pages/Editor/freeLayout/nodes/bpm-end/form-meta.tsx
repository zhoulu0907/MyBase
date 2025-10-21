/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */
import type { FormRenderProps, FormMeta } from '@flowgram.ai/free-layout-editor';
import { Avatar } from '@douyinfe/semi-ui';

import type { FlowNodeJSON } from '../../typings';
import iconEnd from '../../assets/icon-end.jpg';

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
        borderRadius: '15px'
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
        src={iconEnd}
      />
    </div>
  </>
);

export const formMeta: FormMeta<FlowNodeJSON> = {
  render: renderForm
};
