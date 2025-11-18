import iconControl from '@/assets/flow/nodes/ifCase.svg';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '@onebase/common';
import { formMeta } from './form-meta';

export const IFBlockNodeRegistry: FlowNodeRegistry = {
  type: NodeType.IF_BLOCK,
  title: '条件分支节点',
  category: 'control',
  /**
   * 分支节点需要继承自 block
   * Branch nodes need to inherit from 'block'
   */
  extend: 'block',
  meta: {
    copyDisable: true,
    addDisable: true,
    sidebarDisable: true,
    defaultExpanded: false,
    style: {
      width: 'auto',
      height: 32,
    }
  },
  info: {
    icon: iconControl,
    description: ''
  },
  canAdd: () => false,
  canDelete: (ctx, node) => false,
  formMeta
};
