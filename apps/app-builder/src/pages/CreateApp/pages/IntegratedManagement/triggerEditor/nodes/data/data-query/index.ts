import iconStart from '@/assets/flow/icon-start.jpg';
import { type FlowNodeRegistry } from '../../../typings';
import { formMeta } from './form-meta';

export const DataQueryNodeRegistry: FlowNodeRegistry = {
  type: 'dataQuery',
  title: '数据查询节点',
  category: 'data',
  meta: {
    isStart: false,
    deleteDisable: false,
    selectable: true,
    copyDisable: false,
    expandable: false,
    addDisable: false,
  },
  info: {
    icon: iconStart,
    description:
    '这是数据查询节点，用于查询数据。',
  },
  /**
   * Render node via formMeta
   */
  formMeta,
};
