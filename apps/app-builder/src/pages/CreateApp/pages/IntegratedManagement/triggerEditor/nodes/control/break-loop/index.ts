import iconControl from '@/assets/flow/nodes/end.svg';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '@onebase/common';
import { generateNodeId } from '../../utils';
import { formMeta } from './form-meta';
/**
 * Break 节点用于在 loop 中根据条件终止并跳出
 */
export const BreakLoopNodeRegistry: FlowNodeRegistry = {
  type: NodeType.BREAK_LOOP,
  title: '循环结束节点',
  category: 'control',
  extend: 'end',
  info: {
    icon: iconControl,
    description: '中断当前循环'
  },
  meta: {
    style: {
      width: 240
    }
  },
  /**
   * Render node via formMeta
   */
  formMeta,
  canAdd(ctx, from) {
    while (from.parent) {
      if (from.parent.flowNodeType === NodeType.LOOP) return true;
      from = from.parent;
    }
    return false;
  },
  onAdd(ctx, from) {
    return {
      id: generateNodeId(NodeType.BREAK_LOOP),
      type: NodeType.BREAK_LOOP,
      data: {
        title: '中断循环'
      }
    };
  }
};
