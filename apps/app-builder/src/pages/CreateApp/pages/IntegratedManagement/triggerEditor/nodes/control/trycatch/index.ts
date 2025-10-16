import iconControl from '@/assets/flow/nodes/tryCatch.svg';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '@onebase/common';
import { generateNodeId } from '../../utils';
import { formMeta } from './form-meta';

export const TryCatchNodeRegistry: FlowNodeRegistry = {
  type: NodeType.TRY_CATCH,
  title: '异常处理节点',
  category: 'control',
  info: {
    icon: iconControl,
    description: '异常捕获'
  },
  meta: {
    expandable: false // disable expanded
  },
  formMeta,
  onAdd() {
    return {
      id: generateNodeId(NodeType.TRY_CATCH),
      type: NodeType.TRY_CATCH,
      data: {
        title: '异常处理'
      },
      blocks: [
        {
          id: generateNodeId(NodeType.TRY_BLOCK),
          type: NodeType.TRY_BLOCK,
          blocks: []
        },
        {
          id: generateNodeId(NodeType.CATCH_BLOCK),
          type: NodeType.CATCH_BLOCK,
          blocks: [],
          data: {
            title: '异常捕获_1'
          }
        }
      ]
    };
  }
};
