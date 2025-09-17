import { nanoid } from 'nanoid';

import iconBreak from '@/assets/flow/icon-break.svg';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '../../const';
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
    icon: iconBreak,
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
      if (from.parent.flowNodeType === 'loop') return true;
      from = from.parent;
    }
    return false;
  },
  onAdd(ctx, from) {
    return {
      id: `break_${nanoid()}`,
      type: 'breakLoop',
      data: {
        title: '中断循环'
      }
    };
  }
};
