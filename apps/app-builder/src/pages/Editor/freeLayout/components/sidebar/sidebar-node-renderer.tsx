/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import { useNodeRender, FlowNodeEntity } from '@flowgram.ai/free-layout-editor';
import { useState } from 'react';
import { NodeRenderContext } from '../../context';
import type { FormInstance } from '@arco-design/web-react';
import ApproveDreawer from './components/approver';
import Launch from './components/launch';
import Conditional from './components/conditional';
import Parallel from './components/parallel';
import Sink from './components/sink'
import { WorkflowNodeType } from '../../nodes/constants';
export function SidebarNodeRenderer(props: { node: FlowNodeEntity }) {
  const { node } = props;
  const nodeRender = useNodeRender(node);
  const [configForm, setconfigFormForm] = useState<FormInstance>();
  const contextValue = {
    ...nodeRender,
    configForm: configForm,
    setconfigFormForm: (form: FormInstance) => setconfigFormForm(form)
  };

  const handleSubmit = (data: any, nodeName: string) => {
    nodeName && (data.name = nodeName);
    nodeRender.updateData(Object.assign({}, nodeRender.data, data));
    console.log(nodeRender.data, '更新后的data');
  };

  return (
    <NodeRenderContext.Provider value={contextValue}>
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
        {nodeRender?.type === WorkflowNodeType.APPROVER && (
          <ApproveDreawer handleConfigSubmit={handleSubmit} configData={nodeRender.data} />
        )}
        {nodeRender?.type === WorkflowNodeType.INITIATION && (
          <Launch handleConfigSubmit={handleSubmit} configData={nodeRender.data} />
        )}
        {nodeRender?.type === WorkflowNodeType.CONDITIONAL_BRANCH && <Conditional />}
        {nodeRender?.type === WorkflowNodeType.PARALLEL_BRANCH && <Parallel />}
         {nodeRender?.type === WorkflowNodeType.SINK_NODE_BRANCH && <Sink />}
      </div>
    </NodeRenderContext.Provider>
  );
}
