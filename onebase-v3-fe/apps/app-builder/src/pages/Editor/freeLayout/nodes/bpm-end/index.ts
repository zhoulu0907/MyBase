/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import { type FlowNodeRegistry } from '../../typings';
import { formMeta } from './form-meta';
import { WorkflowNodeType } from '../constants';

export const BpmEndNodeRegistry: FlowNodeRegistry = {
  type: WorkflowNodeType.END,
  meta: {
    isNodeEnd: true,
    deleteDisable: true,
    copyDisable: true,
    sidebarDisabled: true,
    nodePanelVisible: false,
    size: {
      width: 74,
      height: 30
    },
    wrapperStyle: {
      minWidth: 'unset',
      width: '100%',
      borderWidth: 0,
      borderRadius: 15,
      cursor: 'move'
    },
    defaultPorts: [{ type: 'input', location: 'top' }]
  },
  info: {
    icon: '',
    description: 'The final node of the block.'
  },
  /**
   * Render node via formMeta
   */
  formMeta,
  /**
   * Start Node cannot be added
   */
  canAdd() {
    return false;
  }
};
