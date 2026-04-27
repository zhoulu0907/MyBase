import React from 'react';
import EditorConfig from '../../components/config';
import EditorPanel from '../../components/panel/Panel';
import EditorWorkspace from '../../components/workspace/Workspace';
import styles from './index.module.less';

const FormEditor: React.FC<{ editMode?: string }> = ({ editMode }) => {
  return (
    <div className={styles.formEditorPage}>
      {editMode !== 'mobile' && <EditorPanel />}
      <EditorWorkspace />
      <EditorConfig />
    </div>
  );
};

export { FormEditor };
