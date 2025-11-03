/**
 * 发起
 */
import styles from './index.module.less';
import Header from '../../../header';
import BottomBtn from '../../../bottomBtn';
import { Switch } from '@arco-design/web-react';
import { useState } from 'react';
export default function ApproveDreawer({ handleConfigSubmit }: any) {
  const [switchChecked, setSwitchChecked] = useState(false);
  const [deptConfig, setDeptConfig] = useState({
    useCustomDept: switchChecked,
    deptId: '111',
    deptName: '部门A'
  });

  function handleSubmit() {
    handleConfigSubmit({ deptConfig });
  }
  return (
    <>
      <Header />
      <div className={styles.launch}>
        <div className={styles.configTitle}>发起部门选择</div>
        <div className={styles.configContent}>
          <Switch size="small" checked={switchChecked} onChange={(v) => setSwitchChecked(v)} />
          <span className={styles.switchTips}>
            开启后，若用户属于多个部门，可手动选择本次流程的发起部门；关闭则自动使用其主部门。
          </span>
        </div>
      </div>
      <BottomBtn handleSubmit={handleSubmit} />
    </>
  );
}
