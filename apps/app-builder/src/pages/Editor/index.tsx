import { EDITOR_TYPES } from '@onebase/ui-kit';
import { currentEditorSignal } from '@onebase/ui-kit/src/signals/current_editor';
import { useSignals } from '@preact/signals-react/runtime';
import React from 'react';
import { Route, Routes } from 'react-router-dom';
import EditorHeader from './components/header/Header';
import styles from './index.module.less';
import FlowEditorPage from './pages/FlowEditor/index';
import { FormEditor } from './pages/FromEditor';
import { ListEditor } from './pages/ListEditor';
import WorkbenchEditor from './pages/WorkbenchEditor';

const EditorPage: React.FC = () => {
  useSignals();

  // 区分移动端还是手机端
  const { editMode } = currentEditorSignal;

  return (
    <div className={styles.editorPage}>
      <EditorHeader />
      <div className={styles.editorContent}>
        <Routes>
          <Route path={EDITOR_TYPES.FORM_EDITOR} element={<FormEditor editMode={editMode.value} />} />
          <Route path={EDITOR_TYPES.LIST_EDITOR} element={<ListEditor editMode={editMode.value} />} />
          <Route path={EDITOR_TYPES.FLOW_EDITOR} element={<FlowEditorPage />} />
          <Route path={EDITOR_TYPES.WORKBENCH_EDITOR} element={<WorkbenchEditor editMode={editMode.value} />} />
        </Routes>
      </div>
    </div>
  );
};

export { EditorPage };
