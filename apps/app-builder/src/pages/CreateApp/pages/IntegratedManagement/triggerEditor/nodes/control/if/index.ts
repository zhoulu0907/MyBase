
import { FlowNodeSplitType } from '@flowgram.ai/fixed-layout-editor';
import { nanoid } from 'nanoid';

import iconIf from '@/assets/flow/icon-if.png';
import { type FlowNodeRegistry } from '../../../typings';
import { defaultFormMeta } from '../../default-form-meta';

export const IFNodeRegistry: FlowNodeRegistry = {
  extend: FlowNodeSplitType.STATIC_SPLIT,
  type: 'if',
  title: '条件节点',
  category: 'control',
  info: {
    icon: iconIf,
    description: 'Only the corresponding branch will be executed if the set conditions are met.',
  },
  meta: {
    expandable: false, // disable expanded
  },
  formMeta: defaultFormMeta,
  onAdd() {
    return {
      id: `if_${nanoid(5)}`,
      type: 'if',
      data: {
        title: '条件节点',
        inputsValues: {
          condition: { type: 'constant', content: true },
        },
        inputs: {
          type: 'object',
          required: ['condition'],
          properties: {
            condition: {
              type: 'boolean',
            },
          },
        },
      },
      blocks: [
        {
          id: nanoid(5),
          type: 'ifBlock',
          data: {
            title: 'true',
          },
          blocks: [],
        },
        {
          id: nanoid(5),
          type: 'ifBlock',
          data: {
            title: 'false',
          },
        },
      ],
    };
  },
};
