import iconControl from '@/assets/flow/nodes/control.svg';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '../../const';
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
      width: 66,
      height: 20,
      borderRadius: 4
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
