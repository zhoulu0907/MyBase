import React from 'react';
import EditorConfig from '../../components/config';
import EditorPanel from '../../components/panel/Panel';
import EditorWorkspace from '../../components/workspace/Workspace';
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
