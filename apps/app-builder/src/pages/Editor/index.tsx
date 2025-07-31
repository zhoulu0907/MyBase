import React from 'react';
import { Route, Routes } from 'react-router-dom';
import { FormEditor } from '../FromEditor';
import { ListEditor } from '../ListEditor';
import EditorHeader from './components/header/Header';
import styles from './index.module.less';

const EditorPage: React.FC = () => {
  return <div className={styles.editorPage}>
    <EditorHeader />

    <div className={styles.editorContent}>
        <Routes>
            <Route path="form_editor" element={<FormEditor />} />
            <Route path="list_editor" element={<ListEditor />} />
        </Routes>
    </div>
  </div>;
};

export { EditorPage };
