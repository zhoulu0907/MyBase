import React from 'react';
import EditorConfig from '../../components/config';
import EditorPanel from '../../components/panel/Panel';
import EditorWorkspace from '../../components/workspace/Workspace';

import styles from './index.module.less';

/**
 * 工作台编辑器
 * 集成面板、工作区、配置区
 */

const WorkbenchEditor: React.FC = () => {
  return (
    <div className={styles.workbenchEditorPage}>
      <EditorPanel />
      <EditorWorkspace />
      <EditorConfig />
    </div>
  );
};

export default WorkbenchEditor;
