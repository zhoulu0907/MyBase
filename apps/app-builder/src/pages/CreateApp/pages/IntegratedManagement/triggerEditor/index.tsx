import { EditorRenderer, FixedLayoutEditorProvider } from '@flowgram.ai/fixed-layout-editor';

import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import '@flowgram.ai/fixed-layout-editor/index.css';
import { SidebarProvider, SidebarRenderer } from './components/sidebar';
import { Tools } from './components/tools';
import { useEditorProps } from './hooks/use-editor-props';
import styles from './index.module.less';
import { initialData } from './initial-data';
import { FlowNodeRegistries } from './nodes';

const TriggerEditor = () => {
  const editorProps = useEditorProps(initialData, FlowNodeRegistries);
  const { setNodeId } = triggerEditorSignal;

  return (
    <div className={styles.triggerEditor}>
      <FixedLayoutEditorProvider {...editorProps}>
        <SidebarProvider>
          <div
            className={styles.container}
            onClick={(e) => {
              let target = e.target as HTMLElement | null;
              if (target) {
                if (
                  target.classList &&
                  (target.classList.contains('gedit-playground-layer') ||
                    target.classList.contains('flow-canvas-adder'))
                ) {
                  setNodeId(undefined);
                }
              }
            }}
          >
            <div className={styles.layout}>
              <EditorRenderer className={styles.editor} />
              <Tools />
            </div>
            <SidebarRenderer />
          </div>
        </SidebarProvider>
      </FixedLayoutEditorProvider>
    </div>
  );
};

export default TriggerEditor;
