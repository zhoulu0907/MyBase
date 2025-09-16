import { FlowNodeSplitType } from '@flowgram.ai/fixed-layout-editor';
import { nanoid } from 'nanoid';

import iconCondition from '@/assets/flow/icon-condition.svg';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '../../const';
import { defaultFormMeta } from '../../default-form-meta';

export const SwitchNodeRegistry: FlowNodeRegistry = {
  extend: FlowNodeSplitType.DYNAMIC_SPLIT,
  type: NodeType.SWITCH,
  title: '分支节点',
  category: 'control',
  info: {
    icon: iconCondition,
    description: '连接多个下游分支。如果满足设定的条件，则仅执行相应的分支。'
  },
  meta: {
    expandable: false // disable expanded
  },
  formMeta: defaultFormMeta,
  onAdd() {
    return {
      id: `switch_${nanoid(5)}`,
      type: 'switch',
      data: {
        title: '分支节点'
      },
      blocks: [
        {
          id: nanoid(5),
          type: 'case',
          data: {
            title: '分支_0',
            inputsValues: {
              condition: { type: 'constant', content: '' }
            },
            inputs: {
              type: 'object',
              required: ['condition'],
              properties: {
                condition: {
                  type: 'boolean'
                }
              }
            }
          },
          blocks: []
        },
        {
          id: nanoid(5),
          type: 'case',
          data: {
            title: '分支_1',
            inputsValues: {
              condition: { type: 'constant', content: '' }
            },
            inputs: {
              type: 'object',
              required: ['condition'],
              properties: {
                condition: {
                  type: 'boolean'
                }
              }
            }
          }
        },
        {
          id: nanoid(5),
          type: 'caseDefault',
          data: {
            title: '默认分支'
          },
          blocks: []
        }
      ]
    };
  }
};
