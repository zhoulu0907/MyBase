import { type FlowNodeRegistry } from '../../typings';
import { formMeta } from './form-meta';
import { nanoid } from 'nanoid';
import approver from '../../assets/bpmLogo/approver.png';

export const ApproverNodeRegistry: FlowNodeRegistry = {
  type: 'approver',
  name: '审批人',
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
    icon: approver,
    description: '这是审批人节点，用于编辑审批人。'
  },
  /**
   * Render node via formMeta
   */
  formMeta,
  canDelete(ctx, node) {
    return node.parent !== ctx.document.root;
  },
  onAdd() {
    return {
      id: `executor_${nanoid(5)}`,
      type: 'executor',
      data: {
        name: '审批人',
        errorMsg: '节点缺少审批人',
        buttonConfigs: [
          {
            key: '1',
            buttonType: 'approve',
            buttonName: '同意',
            displayName: '同意',
            name: '同意',
            defaultApprovalComment: '同意',
            approvalCommentRequired: false,
            batchApproval: false,
            enabled: true
          },
          {
            key: '2',
            buttonType: 'reject',
            buttonName: '拒绝',
            displayName: '拒绝',
            name: '拒绝',
            defaultApprovalComment: '拒绝',
            approvalCommentRequired: true,
            batchApproval: false,
            enabled: true
          },
          {
            key: '3',
            buttonType: 'save',
            buttonName: '保存',
            displayName: '保存',
            name: '保存',
            approvalCommentRequired: false,
            batchApproval: false,
            enabled: false
          },
          {
            key: '4',
            buttonType: 'transfer',
            buttonName: '转交',
            displayName: '转交',
            name: '转交',
            defaultApprovalComment: '转交',
            approvalCommentRequired: false,
            batchApproval: false,
            enabled: false
          },
          {
            key: '5',
            buttonType: 'add_sign',
            buttonName: '加签',
            displayName: '加签',
            name: '加签',
            defaultApprovalComment: '加签',
            approvalCommentRequired: false,
            batchApproval: false,
            enabled: false
          },
          {
            key: '6',
            buttonType: 'return',
            buttonName: '退回',
            displayName: '退回',
            name: '退回',
            defaultApprovalComment: '退回',
            approvalCommentRequired: true,
            batchApproval: false,
            enabled: false
          },
          {
            key: '7',
            buttonType: 'withdraw',
            buttonName: '撤回',
            displayName: '撤回',
            name: '撤回',
            defaultApprovalComment: '撤回',
            approvalCommentRequired: false,
            batchApproval: false,
            enabled: false
          },
          {
            key: '8',
            buttonType: 'abstain',
            buttonName: '弃权',
            displayName: '弃权',
            name: '弃权',
            defaultApprovalComment: '弃权',
            approvalCommentRequired: true,
            batchApproval: false,
            enabled: false
          }
        ],
        approverConfig: {
          approverType: 'user',
          approvalMode: 'counter_sign'
        }
      }
    };
  }
};
