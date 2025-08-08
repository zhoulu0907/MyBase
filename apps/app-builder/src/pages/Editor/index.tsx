import React from 'react';
import { Route, Routes } from 'react-router-dom';
import { FormEditor } from '../FromEditor';
import { ListEditor } from '../ListEditor';
import { EDITOR_TYPES } from './components/const';
import EditorHeader from './components/header/Header';
import styles from './index.module.less';

const EditorPage: React.FC = () => {
  return (
    <div className={styles.editorPage}>
      <EditorHeader />

      <div className={styles.editorContent}>
        <Routes>
          <Route path={EDITOR_TYPES.FORM_EDITOR} element={<FormEditor />} />
          <Route path={EDITOR_TYPES.LIST_EDITOR} element={<ListEditor />} />
        </Routes>
      </div>
    </div>
  );
};

export { EditorPage };
