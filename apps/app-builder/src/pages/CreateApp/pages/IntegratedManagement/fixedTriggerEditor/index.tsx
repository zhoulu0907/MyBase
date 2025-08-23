import { EditorRenderer, FixedLayoutEditorProvider } from '@flowgram.ai/fixed-layout-editor';

import { DemoTools } from './components';
import { SidebarProvider, SidebarRenderer } from './components/sidebar';
import { useEditorProps } from './hooks/use-editor-props';
import { initialData } from './initial-data';
import { FlowNodeRegistries } from './nodes';

import '@flowgram.ai/fixed-layout-editor/index.css';

const TriggerEditor = () => {
  /**
   * Editor Config
   */
  const editorProps = useEditorProps(initialData, FlowNodeRegistries);

  return (
    <div className="doc-feature-overview">
      <FixedLayoutEditorProvider {...editorProps}>
        <SidebarProvider>
          <EditorRenderer />
          <DemoTools />
          <SidebarRenderer />
        </SidebarProvider>
      </FixedLayoutEditorProvider>
    </div>
  );
};

export default TriggerEditor;
