import { EditorRenderer, FreeLayoutEditorProvider } from '@flowgram.ai/free-layout-editor';
import '@flowgram.ai/free-layout-editor/index.css';
import React from 'react';
import NodeAddPanel from './components/addPanel';
import { SidebarRenderer } from './components/sidebar';
import { SidebarProvider } from './components/sidebar/sidebar-provider';
import { Tools } from './components/tools';
import { useEditorProps } from './hooks';
import styles from './index.module.less';
import { initialData } from './initial-data';
import { nodeRegistries } from './node';

/**
 * 触发器编辑器组件初始化
 * 该组件用于后续集成管理页面的触发器编辑功能
 */
const TriggerEditor: React.FC = () => {
  const editorProps = useEditorProps(initialData, nodeRegistries);

  return (
    <div className={styles.triggerEditor}>
      <FreeLayoutEditorProvider {...editorProps}>
        <SidebarProvider>
          <div className={styles.container}>
            <div className={styles.layout}>
              <NodeAddPanel />
              <EditorRenderer className={styles.editor} />
            </div>
            <Tools />
            <SidebarRenderer />
          </div>
        </SidebarProvider>
      </FreeLayoutEditorProvider>
    </div>
  );
};

export default TriggerEditor;
