import { EDITOR_TYPES } from '@onebase/ui-kit';
import React from 'react';
import { Route, Routes } from 'react-router-dom';
import EditorHeader from './components/header/Header';
import styles from './index.module.less';
import { FormEditor } from './pages/FromEditor';
import { ListEditor } from './pages/ListEditor';
import FlowEditorPage from './pages/FlowEditor/index';
const EditorPage: React.FC = () => {
  return (
    <div className={styles.editorPage}>
      <EditorHeader />
      <div className={styles.editorContent}>
        <Routes>
          <Route path={EDITOR_TYPES.FORM_EDITOR} element={<FormEditor />} />
          <Route path={EDITOR_TYPES.LIST_EDITOR} element={<ListEditor />} />
          <Route path={EDITOR_TYPES.FLOW_EDITOR} element={<FlowEditorPage />} />
        </Routes>
      </div>
    </div>
  );
};

export { EditorPage };
