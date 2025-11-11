/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import { type FlowDocumentJSON } from '../../freeLayout/typings';

export const initialData: FlowDocumentJSON = {
  nodes: [
    {
      id: 'start_0',
      type: 'start',
      meta: {
        position: {
          x: 180,
          y: 100
        }
      },
      data: {
        name: '开始',
        status: 'completed'
      }
    },
    {
      id: 'start_1',
      type: 'initiation',
      meta: {
        position: {
          x: 180,
          y: 300
        }
      },
      data: {
        name: '发起',
        status: 'completed'
      }
    },
    {
      id: 'end_0',
      type: 'end',
      meta: {
        position: {
          x: 180,
          y: 500
        }
      },
      data: {
        name: '结束',
        status: 'pending'
      }
    },
    {
      id: 'executor_zIEdi',
      type: 'approver',
      meta: {
        position: {
          x: 180,
          y: 390
        }
      },
      data: {
        status: 'processing',
        approverConfig: {
          approverType: 'user',
          users: [
            {
              userId: '130010051773104128',
              name: '杨新玉'
            },
            {
              userId: '101802183959412736',
              name: '高国清'
            }
          ],
          approvalMode: 'counter_sign'
        },
        buttonConfigs: [
          {
            buttonName: '同意',
            buttonType: 'approve',
            displayName: '同意',
            defaultApprovalComment: '同意',
            approvalCommentRequired: false,
            enabled: true,
            batchApproval: false
          },
          {
            buttonName: '拒绝',
            buttonType: 'reject',
            displayName: '拒绝',
            defaultApprovalComment: '拒绝',
            approvalCommentRequired: false,
            enabled: true,
            batchApproval: false
          }
        ],
        fieldPermConfig: {
          useNodeConfig: false
        },
        name: '审批人'
      }
    }
  ],
  edges: [
    {
      sourceNodeID: 'start_0',
      targetNodeID: 'start_1',
      status: 'pass'
    },
    {
      sourceNodeID: 'start_1',
      targetNodeID: 'executor_zIEdi',
      status: 'pass'
    },
    {
      sourceNodeID: 'executor_zIEdi',
      targetNodeID: 'end_0'
    }
  ]
};
