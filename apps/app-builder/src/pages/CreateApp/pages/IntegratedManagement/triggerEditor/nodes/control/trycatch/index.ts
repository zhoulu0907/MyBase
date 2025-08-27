import { nanoid } from 'nanoid';

import iconTryCatch from '@/assets/flow/icon-trycatch.svg';
import { type FlowNodeRegistry } from '../../../typings';
import { formMeta } from './form-meta';

export const TryCatchNodeRegistry: FlowNodeRegistry = {
  type: 'tryCatch',
  title: '异常处理节点',
  category: 'control',
  info: {
    icon: iconTryCatch,
    description: 'try catch.',
  },
  meta: {
    expandable: false, // disable expanded
  },
  formMeta,
  onAdd() {
    return {
      id: `tryCatch${nanoid(5)}`,
      type: 'tryCatch',
      data: {
        title: '异常处理',
      },
      blocks: [
        {
          id: `tryBlock${nanoid(5)}`,
          type: 'tryBlock',
          blocks: [],
        },
        {
          id: `catchBlock${nanoid(5)}`,
          type: 'catchBlock',
          blocks: [],
          data: {
            title: '异常捕获_1',
            inputsValues: {
              condition: '',
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
        },
      ],
    };
  },
};
