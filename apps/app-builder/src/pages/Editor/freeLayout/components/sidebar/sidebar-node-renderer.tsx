/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import { useNodeRender, FlowNodeEntity } from '@flowgram.ai/free-layout-editor';
import { useState } from 'react';
import { NodeRenderContext } from '../../context';
import type { FormInstance } from '@arco-design/web-react';
import Header from '../../components/header';
import BottomBtn from '../../components/bottomBtn';
import ApproveDreawer from './components/approver';

export function SidebarNodeRenderer(props: { node: FlowNodeEntity }) {
  const { node } = props;
  const nodeRender = useNodeRender(node);
  const [configForm, setconfigFormForm] = useState<FormInstance>();
  const contextValue = {
    ...nodeRender,
    configForm: configForm,
    setconfigFormForm: (form: FormInstance) => setconfigFormForm(form)
  };

  const handleSubmit = () => {
    const newData = Object.assign({}, nodeRender.data, { type: 123 });
    nodeRender.updateData(newData);
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
        {/* <Header />
        <Approver />
        <BottomBtn /> */}
        {/* <div>抽屉内容</div> */}
        {nodeRender?.type === 'approver' && <ApproveDreawer />}
      </div>
    </NodeRenderContext.Provider>
  );
}
