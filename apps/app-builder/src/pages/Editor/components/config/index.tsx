import { EDITOR_TYPES, usePageEditorSignal } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import MaterialConfiger from './components/MaterialConfiger';
import ViewConfiger from './components/ViewConfiger';
import WorkbenchConfiger from '../../workbench/editor-components/wb-configer';
import styles from './index.module.less';

interface EditorConfigProps {}

export default function EditorConfig({}: EditorConfigProps) {
  useSignals();

  const { curComponentID } = usePageEditorSignal();

  const hash = window.location.hash;
  const isFormEditor = hash.includes(EDITOR_TYPES.FORM_EDITOR);
  const isWorkbenchEditor = hash.includes(EDITOR_TYPES.WORKBENCH_EDITOR);

  return (
    <div
      className={styles.editorConfig}
      style={{
        // width: showDrawer ? '310px' : '0px',
        transition: 'width 0.3s cubic-bezier(0.4, 0, 0.2, 1)'
      }}
    >
      {curComponentID ? (
        isWorkbenchEditor ? (
          <WorkbenchConfiger cpID={curComponentID} />
        ) : (
          <MaterialConfiger cpID={curComponentID} />
        )
      ) : (
        //   只有表单设计时允许显示视图配置
        isFormEditor && <ViewConfiger />
      )}
    </div>
  );
}
