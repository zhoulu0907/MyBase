import React from 'react';
import EditorConfig from '../Editor/components/config';
import EditorPanel from '../Editor/components/panel/Panel';
import EditorWorkspace from '../Editor/components/workspace/Workspace';
import styles from './index.module.less';

const ListEditor: React.FC = () => {
  return (
    <div className={styles.listEditorPage}>
      <EditorPanel />
      <EditorWorkspace />
      <EditorConfig />
    </div>
  );
};

export { ListEditor };
