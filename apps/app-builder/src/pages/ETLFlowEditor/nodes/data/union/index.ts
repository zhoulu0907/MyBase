import { ETLNodeType } from '@onebase/common';
import { type FlowNodeRegistry } from '../../../typings';
import { formMeta } from './form-meta';

export const UnionNodeRegistry: FlowNodeRegistry = {
  type: ETLNodeType.UNION_NODE,
  title: '追加合并节点',
  meta: {
    isStart: false,
    draggable: true,
    deleteDisable: false,
    selectable: true
  },
  /**
   * Render node via formMeta
   */
  formMeta
};
