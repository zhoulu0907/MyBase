import iconStart from '@/assets/flow/icon-start.jpg';
import { nanoid } from 'nanoid';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '../../const';
import { formMeta } from './form-meta';

export const IpaasNodeRegistry: FlowNodeRegistry = {
  type: NodeType.IPAAS,
  title: '连接器节点',
  category: 'other',
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
    description: '这是连接器节点，用于连接各类API'
  },
  /**
   * Render node via formMeta
   */
  formMeta,
  canDelete(ctx, node) {
    return node.parent !== ctx.document.root;
  },
  onAdd(ctx, from) {
    return {
      id: `ipaas_${nanoid()}`,
      type: 'ipaas',
      data: {
        title: '连接器节点'
      }
    };
  }
};
