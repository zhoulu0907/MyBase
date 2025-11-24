/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import { useNodeRender, type WorkflowLineEntity } from '@flowgram.ai/free-layout-editor';
import { useState } from 'react';
import { NodeRenderContext } from '../../context';
import type { FormInstance } from '@arco-design/web-react';
import ApproveDreawer from './components/approver';
import Launch from './components/launch';
import CcRecipientsDreawer from './components/ccRecipients/index';
import Conditional from './components/conditional';
import Parallel from './components/parallel';
import Sink from './components/sink';
import { WorkflowNodeType } from '../../nodes/constants';
export function SidebarLineRenderer(props: { line: WorkflowLineEntity }) {
  const { line } = props;
  return (
    // <NodeRenderContext.Provider value={contextValue}>
    <div
      style={{
        borderRadius: 8,
        border: '1px solid rgba(82,100,154, 0.13)',
        margin: 0,
        height: 'calc(100% - 2px)',
        overflow: 'hidden',
        background: '#fff'
      }}
    >
      线段弹窗
    </div>
    // </NodeRenderContext.Provider>
  );
}
