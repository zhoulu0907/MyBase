import iconStart from '@/assets/flow/icon-start.jpg';
import { type FlowNodeRegistry } from '../../../typings';
import { formMeta } from './form-meta';

export const DataCalcNodeRegistry: FlowNodeRegistry = {
  type: 'dataCalc',
  title: '数据计算节点',
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
    '这是数据计算节点，用于计算数据。',
  },
  /**
   * Render node via formMeta
   */
  formMeta,
};
