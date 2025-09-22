import iconStart from '@/assets/flow/icon-start.jpg';
import { nanoid } from 'nanoid';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '../../const';
import { formMeta } from './form-meta';

export const DataDeleteNodeRegistry: FlowNodeRegistry = {
  type: NodeType.DATA_DELETE,
  title: '数据删除节点',
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
    description: '这是数据删除节点，用于删除数据。'
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
      id: `dataDelete_${nanoid()}`,
      type: NodeType.DATA_DELETE,
      data: {
        title: '数据删除节点'
      }
    };
  }
};
