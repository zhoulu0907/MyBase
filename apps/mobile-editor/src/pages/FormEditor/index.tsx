import EditorWorkspace from '@/components/workspace/Workspace';
import { EditorPanel } from '@onebase/ui-kit/';
import React from 'react';
import styles from './index.module.less';

const FormEditor: React.FC = () => {
  return (
    <div className={styles.formEditorPage}>
      <EditorPanel />
      <EditorWorkspace />
    </div>
  );
};

export { FormEditor };
