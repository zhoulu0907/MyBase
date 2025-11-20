import { ETLNodeType } from '@onebase/common';
import { type FlowNodeRegistry } from '../../../typings';
import { formMeta } from './form-meta';

export const JoinNodeRegistry: FlowNodeRegistry = {
  type: ETLNodeType.JOIN_NODE,
  title: '横向连接节点',
  meta: {
    isStart: false,
    draggable: true,
    deleteDisable: false,
    selectable: true
  },
  canAddLine: (fromPort, toPort, lines) => {
    // 控制线条添加数目
    if (toPort.availableLines.length >= 2) {
      return false
    }
    return true;
  },
  /**
   * Render node via formMeta
   */
  formMeta
};
