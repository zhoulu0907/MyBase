import { FlowNodeSplitType } from '@flowgram.ai/fixed-layout-editor';
import iconControl from '@/assets/flow/nodes/switch.svg';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '@onebase/common';
import { defaultFormMeta } from '../../default-form-meta';
import { generateNodeId } from '../../utils';

export const SwitchNodeRegistry: FlowNodeRegistry = {
  extend: FlowNodeSplitType.DYNAMIC_SPLIT,
  type: NodeType.SWITCH,
  title: '分支节点',
  category: 'control',
  info: {
    icon: iconControl,
    description: '连接多个下游分支。如果满足设定的条件，则仅执行相应的分支。'
  },
  meta: {
    expandable: false // disable expanded
  },
  formMeta: defaultFormMeta,
  onAdd() {
    return {
      id: generateNodeId(NodeType.SWITCH),
      type: NodeType.SWITCH,
      data: {
        title: '分支节点'
      },
      blocks: [
        {
          id: generateNodeId(NodeType.CASE),
          type: NodeType.CASE,
          data: {
            title: '分支_0'
          },
          blocks: []
        },
        {
          id: generateNodeId(NodeType.CASE),
          type: NodeType.CASE,
          data: {
            title: '分支_1'
          }
        },
        {
          id: generateNodeId(NodeType.CASE_DEFAULT),
          type: NodeType.CASE_DEFAULT,
          data: {
            title: '默认分支'
          },
          blocks: []
        }
      ]
    };
  }
};
