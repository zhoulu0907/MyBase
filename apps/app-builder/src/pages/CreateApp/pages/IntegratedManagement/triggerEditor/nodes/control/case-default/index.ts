import iconControl from '@/assets/flow/nodes/switch.svg';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '@onebase/common';
import { defaultFormMeta } from '../../default-form-meta';

export const CaseDefaultNodeRegistry: FlowNodeRegistry = {
  type: NodeType.CASE_DEFAULT,
  title: '默认分支节点',
  category: 'control',
  /**
   * 分支节点需要继承自 block
   * Branch nodes need to inherit from 'block'
   */
  extend: NodeType.CASE,
  meta: {
    copyDisable: true,
    addDisable: true,
    /**
     * caseDefault 永远在最后一个分支，所以不允许拖拽排序
     * "caseDefault" is always in the last branch, so dragging and sorting is not allowed.
     */
    draggable: false,
    deleteDisable: true,
  },
  info: {
    icon: iconControl,
    description: '默认分支'
  },
  canDelete: (ctx, node) => false,
  formMeta: defaultFormMeta,
};
