/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import { nanoid } from 'nanoid';

import iconLoop from '@/assets/flow/icon-loop.svg';
import { type FlowNodeRegistry } from '../../typings';
import { formMeta } from './form-meta';

export const LoopNodeRegistry: FlowNodeRegistry = {
  type: 'loop',
  info: {
    icon: iconLoop,
    description:
      'Used to repeatedly execute a series of tasks by setting the number of iterations and logic',
  },
  meta: {
    expandable: false, // disable expanded
  },
  formMeta,
  onAdd() {
    return {
      id: `loop_${nanoid(5)}`,
      type: 'loop',
      data: {
        title: 'Loop',
      },
    };
  },
};
