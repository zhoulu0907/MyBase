import { ETLNodeType } from '@onebase/common';
import { type FlowNodeRegistry } from '../../../typings';
import { formMeta } from './form-meta';

export const OutputNodeRegistry: FlowNodeRegistry = {
  type: ETLNodeType.OUTPUT_NODE,
  title: '输出节点',
  meta: {
    isStart: false,
    draggable: true,
    deleteDisable: false,
    selectable: true,
    defaultPorts: [{ type: 'input' }]
  },
  /**
   * Render node via formMeta
   */
  formMeta
};
