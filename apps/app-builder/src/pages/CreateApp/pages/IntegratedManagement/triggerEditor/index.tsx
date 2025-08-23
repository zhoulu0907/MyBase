import { EditorRenderer, FixedLayoutEditorProvider } from '@flowgram.ai/fixed-layout-editor';

import '@flowgram.ai/fixed-layout-editor/index.css';
import { SidebarProvider, SidebarRenderer } from './components/sidebar';
import { useEditorProps } from './hooks/use-editor-props';
import styles from './index.module.less';
import { initialData } from './initial-data';
import { FlowNodeRegistries } from './nodes';

const TriggerEditor = () => {
  const editorProps = useEditorProps(initialData, FlowNodeRegistries);

  return (
    <div className={styles.triggerEditor}>
      <FixedLayoutEditorProvider {...editorProps}>
        <SidebarProvider>
          <div className={styles.container}>
            <div className={styles.layout}>
              <EditorRenderer className={styles.editor} />
            </div>
            <SidebarRenderer />
          </div>
        </SidebarProvider>
      </FixedLayoutEditorProvider>
    </div>
  );
};

export default TriggerEditor;
