import iconStart from '@/assets/flow/icon-start.jpg';
import { nanoid } from 'nanoid';
import { type FlowNodeRegistry } from '../../../typings';
import { formMeta } from './form-meta';

export const DataQueryNodeRegistry: FlowNodeRegistry = {
  type: 'dataQuery',
  title: '数据查询节点(单条)',
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
    description: '这是数据查询节点(单条)，用于查询数据。'
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
      id: `dataQuery_${nanoid()}`,
      type: 'dataQuery',
      data: {
        title: '数据查询节点(单条)'
      }
    };
  }
};
