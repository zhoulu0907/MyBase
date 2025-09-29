import iconControl from '@/assets/flow/nodes/loop.svg';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '../../const';
import { generateNodeId } from '../../utils';
import { formMeta } from './form-meta';

export const LoopNodeRegistry: FlowNodeRegistry = {
  type: NodeType.LOOP,
  title: '循环节点',
  category: 'control',
  info: {
    icon: iconControl,
    description: '用于通过设置迭代次数和逻辑来重复执行一系列任务'
  },
  meta: {
    expandable: false // disable expanded
  },
  formMeta,
  onAdd() {
    return {
      id: generateNodeId(NodeType.LOOP),
      type: NodeType.LOOP,
      data: {
        title: '循环节点'
      }
    };
  }
};
