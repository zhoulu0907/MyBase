import { Button } from '@arco-design/web-react';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import React from 'react';
import styles from './index.module.less';

export function FormFooter({ nodeInfo }: { nodeInfo?: React.ReactNode }) {
  const saveNode = () => {};

  debugger;

  return <div className={styles.formFooter}>
    <Button type="outline" style={{marginRight:'20px'}} onClick={saveNode}>
      取消
    </Button>
    <Button type="primary" onClick={saveNode}>
      保存
    </Button>
  </div>
}
