import { usePageEditorStore } from '@/hooks/useStore';
import { Button } from '@arco-design/web-react';
import { IconLeft, IconRight } from '@arco-design/web-react/icon';
import { useState } from 'react';
import MaterialConfiger from './components';
import styles from './index.module.less';

interface EditorConfigProps {

}

export default function EditorConfig({ }: EditorConfigProps) {
  const { curComponentID } = usePageEditorStore();


  const [showDrawer, setShowDrawer] = useState(true);


  return (

    <div className={styles.editorConfig}
        style={{
            width: showDrawer ? "310px" : "0px",
            transition: "width 0.3s cubic-bezier(0.4, 0, 0.2, 1)",
        }}
    >
        <Button
            size="mini"
            className={styles.drawerButton}
            icon={showDrawer ? <IconRight /> : <IconLeft />}
            type="default"
            onClick={() => setShowDrawer(!showDrawer)}
        />
        <MaterialConfiger cpID={curComponentID}/>
    </div>
  );

}