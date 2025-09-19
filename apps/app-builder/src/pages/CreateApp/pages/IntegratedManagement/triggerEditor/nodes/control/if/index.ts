import { FlowNodeSplitType } from '@flowgram.ai/fixed-layout-editor';
import { nanoid } from 'nanoid';

import iconIf from '@/assets/flow/icon-if.png';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '../../const';
import { formMeta } from './form-meta';

export const IFNodeRegistry: FlowNodeRegistry = {
  extend: FlowNodeSplitType.STATIC_SPLIT,
  type: NodeType.IF,
  title: '条件节点',
  category: 'control',
  info: {
    icon: iconIf,
    description: '只会执行满足设定条件相应的分支。'
  },
  meta: {
    expandable: false // disable expanded
  },
  formMeta: formMeta,
  onAdd() {
    return {
      id: `if_${nanoid(5)}`,
      type: NodeType.IF,
      data: {
        title: '条件节点'
      },
      blocks: [
        {
          id: nanoid(5),
          type: 'ifBlock',
          data: {
            title: '通过'
          },
          blocks: []
        },
        {
          id: nanoid(5),
          type: 'ifBlock',
          data: {
            title: '不通过'
          }
        }
      ]
    };
  }
};
