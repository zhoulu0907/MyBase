import React from 'react';
import { WorkbenchConfiger, WorkbenchPanel, WorkbenchWorkspace } from '../../workbench';

import styles from './index.module.less';

/**
 * 工作台编辑器
 * 集成面板、工作区、配置区
 */

const WorkbenchEditor: React.FC = () => {
  return (
    <div className={styles.workbenchEditorPage}>
      <WorkbenchPanel />
      <WorkbenchWorkspace />
      <WorkbenchConfiger />
    </div>
  );
};

export default WorkbenchEditor;
