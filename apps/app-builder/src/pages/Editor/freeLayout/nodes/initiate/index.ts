import { type FlowNodeRegistry } from '../../typings';
import { nanoid } from 'nanoid';
import initiate from '../../assets/bpmLogo/initiate.png';
import { formMeta } from './formMeta';

export const InitiateNodeRegistry: FlowNodeRegistry = {
  type: 'initiate',
  id:'initiate',
  title: '发起',
  category: 'interaction',
  meta: {
    isStart: false,
    deleteDisable: false,
    selectable: true,
    copyDisable: false,
    expandable: false,
    addDisable: false,
    defaultPorts: [
      { type: 'output', location: 'bottom' },
      { type: 'input', location: 'top' }
    ]
  },
  info: {
    icon: initiate,
    description: '这是发起节点'
  },
  formMeta
};
