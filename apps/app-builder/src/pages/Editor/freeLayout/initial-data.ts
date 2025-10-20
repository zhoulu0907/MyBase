/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import { type FlowDocumentJSON } from './typings';

export const initialData: FlowDocumentJSON = {
  nodes: [
    {
      id: 'start_0',
      type: 'start',
      meta: {
        position: {
          x: 180,
          y: 100
        },
        defaultPorts: [{ type: 'output', location: 'bottom' }]
      },
      data: {
        title: '开始'
      }
    },
    {
      id: 'node_0',
      type: 'condition',
      meta: {
        position: {
          x: 180,
          y: 300
        },
        defaultPorts: [
          { type: 'output', location: 'bottom' },
          { type: 'input', location: 'top' }
        ]
      },
      data: {
        title: '抄送人'
      }
    },
    {
      id: 'end_0',
      type: 'end',
      meta: {
        position: {
          x: 180,
          y: 500
        },
        defaultPorts: [{ type: 'input', location: 'top' }]
      },
      data: {
        title: '结束'
      }
    }
  ],
  edges: [
    {
      sourceNodeID: 'start_0',
      targetNodeID: 'node_0'
    },

    {
      sourceNodeID: 'node_0',
      targetNodeID: 'end_0'
    }
  ]
};
