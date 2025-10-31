import type { EditorProps } from '@/common/props';
import EditorWorkspace from '@/components/workspace/Workspace';
import { EditorPanel } from '@onebase/ui-kit/';
import React from 'react';
import styles from './index.module.less';

interface FormEditorProps {
  props: EditorProps;
}

const FormEditor: React.FC<FormEditorProps> = ({ props }) => {
  //   useEffect(() => {
  //     console.log('form editor props', eidtProps.value);
  //   }, [eidtProps.value]);

  return (
    <div className={styles.formEditorPage}>
      <EditorPanel />
      {props.editMode && <EditorWorkspace props={props} />}
    </div>
  );
};

export { FormEditor };
