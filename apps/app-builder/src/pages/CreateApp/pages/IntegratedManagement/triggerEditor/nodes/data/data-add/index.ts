import iconStart from '@/assets/flow/icon-start.jpg';
import { type FlowNodeRegistry } from '../../../typings';
import { formMeta } from './form-meta';

export const DataAddNodeRegistry: FlowNodeRegistry = {
  type: 'dataAdd',
  title: '数据新增节点',
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
    '这是数据新增节点，用于新增数据。',
  },
  /**
   * Render node via formMeta
   */
  formMeta,
};
