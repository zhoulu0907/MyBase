/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */
import { useEffect, useRef } from 'react';
import {
  EditorRenderer,
  FreeLayoutEditorProvider,
  type FreeLayoutPluginContext
} from '@flowgram.ai/free-layout-editor';
import { Button } from '@douyinfe/semi-ui';

import '@flowgram.ai/free-layout-editor/index.css';
import './styles/index.css';
import { nodeRegistries } from './nodes';
import { initialData } from './initial-data';
import { useEditorProps } from './hooks';
import { DemoTools } from './components/tools';
import { SidebarProvider, SidebarRenderer } from './components/sidebar';
import LeftNavBar from './components/left-nav-bar/index';
export const Editor = () => {
  const ref = useRef<FreeLayoutPluginContext | undefined>();
  const editorProps = useEditorProps(initialData, nodeRegistries);

  const onSave = () => {
    const data = ref.current.document.toJSON();
  };
  return (
    <div className="doc-free-feature-overview">
      {/* <Button onClick={() => onSave()}>保存</Button> */}
      <FreeLayoutEditorProvider {...editorProps} ref={ref}>
        <SidebarProvider>
          <div className="demo-container">
            <EditorRenderer className="demo-editor" />
          </div>
          <DemoTools onSave={onSave} />
          <LeftNavBar></LeftNavBar>
          <SidebarRenderer />
        </SidebarProvider>
      </FreeLayoutEditorProvider>
    </div>
  );
};
