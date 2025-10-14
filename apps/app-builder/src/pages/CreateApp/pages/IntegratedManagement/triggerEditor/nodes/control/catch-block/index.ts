import iconControl from '@/assets/flow/nodes/tryCatch.svg';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '@onebase/common';
import { generateNodeId } from '../../utils';
import { formMeta } from './form-meta';

let id = 3;
export const CatchBlockNodeRegistry: FlowNodeRegistry = {
  type: NodeType.CATCH_BLOCK,
  title: '异常捕获节点',
  category: 'control',
  meta: {
    copyDisable: true,
    addDisable: true,
    expandable: false // disable expanded
  },
  info: {
    icon: iconControl,
    description: '满足条件时尝试执行分支'
  },
  canAdd: () => false,
  canDelete: (ctx, node) => node.parent!.blocks.length >= 2,
  onAdd(ctx, from) {
    return {
      id: generateNodeId(NodeType.CATCH_BLOCK),
      type: NodeType.CATCH_BLOCK,
      data: {
        title: `Catch Block ${id++}`
      }
    };
  },
  formMeta
};
