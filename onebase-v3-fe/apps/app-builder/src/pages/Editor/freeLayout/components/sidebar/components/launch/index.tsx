/**
 * 发起
 */
import styles from './index.module.less';
import Header from '../../../header';
import BottomBtn from '../../../bottomBtn';
import { Switch } from '@arco-design/web-react';
import { useState } from 'react';
export default function ApproveDreawer({ handleConfigSubmit, configData }: any) {
  const [deptConfig, setDeptConfig] = useState(
    (configData && configData.deptConfig) || {
      useCustomDept: false,
      deptId: '111',
      deptName: '部门A'
    }
  );
  const [editValue, setEditValue] = useState('');
  function handleSubmit() {
    handleConfigSubmit({ deptConfig }, editValue);
  }
  return (
    <>
      <Header changeName={(name) => setEditValue(name)} />
      <div className={styles.launch}>
        <div className={styles.configTitle}>发起部门选择</div>
        <div className={styles.configContent}>
          <Switch
            size="small"
            checked={deptConfig.useCustomDept}
            onChange={(v) => {
              setDeptConfig({ ...deptConfig, useCustomDept: v });
            }}
          />
          <span className={styles.switchTips}>
            开启后，若用户属于多个部门，可手动选择本次流程的发起部门；关闭则自动使用其主部门。
          </span>
        </div>
      </div>
      <BottomBtn handleSubmit={handleSubmit} />
    </>
  );
}
