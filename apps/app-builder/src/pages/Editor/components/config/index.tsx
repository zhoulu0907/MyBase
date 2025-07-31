import { usePageEditorStore } from '@/hooks/useStore';
import MaterialConfiger from './components';
import styles from './index.module.less';

interface EditorConfigProps {

}

export default function EditorConfig({ }: EditorConfigProps) {
  const { curComponentID } = usePageEditorStore();


  return (
    <div className={styles.editorConfig}>
        <MaterialConfiger cpID={curComponentID}/>
    </div>
  );

}