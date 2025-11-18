import iconControl from '@workflow/nodes/ifCase.svg';
import { FlowNodeSplitType } from '@flowgram.ai/fixed-layout-editor';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '@onebase/common';
import { generateNodeId } from '../../utils';
import { formMeta } from './form-meta';

export const IFNodeRegistry: FlowNodeRegistry = {
  extend: FlowNodeSplitType.STATIC_SPLIT,
  type: NodeType.IF,
  title: '条件节点',
  category: 'control',
  info: {
    icon: iconControl,
    description: '只会执行满足设定条件相应的分支。'
  },
  meta: {
    expandable: false // disable expanded
  },
  formMeta: formMeta,
  onAdd() {
    return {
      id: generateNodeId(NodeType.IF),
      type: NodeType.IF,
      data: {
        title: '条件节点'
      },
      blocks: [
        {
          id: generateNodeId(NodeType.IF_BLOCK),
          type: NodeType.IF_BLOCK,
          data: {
            title: '通过',
            initialData: {
              value: true
            }
          },
          blocks: []
        },
        {
          id: generateNodeId(NodeType.IF_BLOCK),
          type: NodeType.IF_BLOCK,
          data: {
            title: '不通过',
            initialData: {
              value: false
            }
          }
        }
      ]
    };
  }
};
