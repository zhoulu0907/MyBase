import iconStart from '@/assets/flow/icon-start.jpg';
import { nanoid } from 'nanoid';
import { type FlowNodeRegistry } from '../../../typings';
import { formMeta } from './form-meta';

export const DataUpdateNodeRegistry: FlowNodeRegistry = {
  type: 'dataUpdate',
  title: '数据更新节点',
  category: 'data',
  meta: {
    isStart: false,
    deleteDisable: false,
    selectable: true,
    copyDisable: false,
    expandable: false,
    addDisable: false
  },
  info: {
    icon: iconStart,
    description: '这是数据更新节点，用于更新数据。'
  },
  /**
   * Render node via formMeta
   */
  formMeta,
  canDelete(ctx, node) {
    return true;
  },
  onAdd(ctx, from) {
    return {
      id: `dataUpdate_${nanoid()}`,
      type: 'dataUpdate',
      data: {
        title: '数据更新节点'
      }
    };
  }
};
