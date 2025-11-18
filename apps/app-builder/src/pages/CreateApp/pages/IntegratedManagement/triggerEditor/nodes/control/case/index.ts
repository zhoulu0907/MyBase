import iconControl from '@/assets/flow/nodes/switch.svg';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '@onebase/common';
import { generateNodeId } from '../../utils';
import { formMeta } from './form-meta';

let id = 2;
export const CaseNodeRegistry: FlowNodeRegistry = {
  type: NodeType.CASE,
  title: '分支节点',
  category: 'control',
  /**
   * 分支节点需要继承自 block
   * Branch nodes need to inherit from 'block'
   */
  extend: 'block',
  meta: {
    copyDisable: true,
    addDisable: true,
    expandable: false // disable expanded
  },
  info: {
    icon: iconControl,
    description: '满足条件时执行分支。'
  },
  canDelete: (ctx, node) => node.parent!.blocks.length >= 3,
  onAdd(ctx, from) {
    return {
      id: generateNodeId(NodeType.CASE),
      type: NodeType.CASE,
      data: {
        title: `分支_${id++}`
      }
    };
  },
  formMeta
};
