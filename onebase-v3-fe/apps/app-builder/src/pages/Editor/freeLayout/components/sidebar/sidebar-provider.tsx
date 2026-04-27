/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import { useState } from 'react';
import { type WorkflowLineEntity } from '@flowgram.ai/free-layout-editor';
import { SidebarContext } from '../../context';

export function SidebarProvider({ children }: { children: React.ReactNode }) {
  const [nodeId, setNodeId] = useState<string | undefined>();
  const [lineData, setLineData] = useState<WorkflowLineEntity | undefined>();
  return (
    <SidebarContext.Provider value={{ visible: !!nodeId, nodeId, lineData, setNodeId, setLineData }}>
      {children}
    </SidebarContext.Provider>
  );
}
