/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */
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
      <div>结束</div>
    </div>
  </>
);

export const formMeta: FormMeta<FlowNodeJSON> = {
  render: renderForm
};
