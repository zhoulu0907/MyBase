import { nanoid } from 'nanoid';

import iconCase from '@/assets/flow/icon-case.png';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '../../const';
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
    icon: iconCase,
    description: '满足条件时尝试执行分支'
  },
  canAdd: () => false,
  canDelete: (ctx, node) => node.parent!.blocks.length >= 2,
  onAdd(ctx, from) {
    return {
      id: `Catch_${nanoid(5)}`,
      type: 'catchBlock',
      data: {
        title: `Catch Block ${id++}`,
      }
    };
  },
  formMeta
};
