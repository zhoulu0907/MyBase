/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import React from 'react';
import { type WorkflowLineEntity } from '@flowgram.ai/free-layout-editor';

export const SidebarContext = React.createContext<{
  visible: boolean;
  nodeId?: string;
  setNodeId: (node: string | undefined) => void;
  lineData?: WorkflowLineEntity;
  setLineData: (line: WorkflowLineEntity | undefined) => void;
}>({ visible: false, setNodeId: () => {}, setLineData: () => {} });

export const IsSidebarContext = React.createContext<boolean>(false);
