import iconStart from '@/assets/flow/icon-start.jpg';
import { type FlowNodeRegistry } from '../../../typings';
import { formMeta } from './form-meta';

export const DataDeleteNodeRegistry: FlowNodeRegistry = {
  type: 'dataDelete',
  title: '数据删除节点',
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
    '这是数据删除节点，用于删除数据。',
  },
  /**
   * Render node via formMeta
   */
  formMeta,
};
