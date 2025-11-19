import { type FlowNodeRegistry } from '../../typings';
import { nanoid } from 'nanoid';
import { formMeta } from './formMeta';
import ccto from '../../assets/bpmLogo/ccto.png';


export const CcRecipientsNodeRegistry: FlowNodeRegistry = {
  type: 'cc',
  name: '抄送人',
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
    icon: ccto,
    description: '这是抄送人节点，用于编辑抄送人。'
  },
  formMeta,

  canDelete(ctx, node) {
    return node.parent !== ctx.document.root;
  },
  onAdd(ctx, from) {
    return {
      id: `ccRecipients_${nanoid(5)}`,
      type: 'ccRecipients',
      data: {
        name: '抄送人'
      }
    };
  }
};
