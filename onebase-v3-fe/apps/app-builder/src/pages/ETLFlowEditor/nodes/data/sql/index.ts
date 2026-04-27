import { ETLNodeType } from '@onebase/common';
import { type FlowNodeRegistry } from '../../../typings';
import { formMeta } from './form-meta';

export const SQLNodeRegistry: FlowNodeRegistry = {
  type: ETLNodeType.SQL_NODE,
  title: 'SQL节点',
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
