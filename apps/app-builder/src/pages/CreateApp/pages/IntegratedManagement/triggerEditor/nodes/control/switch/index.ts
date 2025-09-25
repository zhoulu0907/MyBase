import { FlowNodeSplitType } from '@flowgram.ai/fixed-layout-editor';

import iconCondition from '@/assets/flow/icon-condition.svg';
import { v4 as uuidv4 } from 'uuid';
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
      id: `switch_${uuidv4()}`,
      type: NodeType.SWITCH,
      data: {
        title: '分支节点'
      },
      blocks: [
        {
          id: uuidv4(),
          type: 'case',
          data: {
            title: '分支_0'
          },
          blocks: []
        },
        {
          id: uuidv4(),
          type: 'case',
          data: {
            title: '分支_1'
          }
        },
        {
          id: uuidv4(),
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
