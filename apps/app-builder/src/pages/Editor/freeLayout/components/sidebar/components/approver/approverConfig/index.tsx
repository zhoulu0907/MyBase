/**
 * 审批人
 */
import { Radio, Divider } from '@arco-design/web-react';
import {IconQuestionCircle} from '@arco-design/web-react/icon';
import styles from './index.module.less';
import { useEffect, useRef, useState } from 'react';
import SimpleMode from './SimpleMode';
import ConditionMode from './ConditionMode';
import {approverConfigVar} from '../constant'

const RadioGroup = Radio.Group;

export default function Approver({setApprovalConfigData}: any) {
  const [configMode, setConfigMode] = useState<string>('simple');
  let [approvalMode, setApprovalMode] = useState(approverConfigVar.approvalMode['a'])

  function changeApprovalMode(val: string) {
    setApprovalMode(val)
  }
  useEffect(() => {
    setApprovalConfigData('approverConfig', {approvalMode})
  }, [approvalMode])

  return (
    <div className={styles.approverConfig}>
      <div className={styles.configTitle}>审批人设置</div>
      <div className={styles.configMode} style={{display: 'none'}}>
        <span>配置模式：</span>
        <RadioGroup value={configMode} onChange={setConfigMode}>
          <Radio value="simple">简易模式</Radio>
          <Radio value="condition">条件模式<IconQuestionCircle /></Radio>
        </RadioGroup>
      </div>
      <Divider />
      {configMode === 'simple' && <SimpleMode setApprovalConfigData={setApprovalConfigData}/>}
      {configMode === 'condition' && <ConditionMode />}
      <div className={styles.configTitle} style={{paddingTop: '8px'}}>多人审批方式</div>
      <RadioGroup direction='vertical' value={approvalMode} onChange={changeApprovalMode}>
        <Radio value={approverConfigVar.approvalMode['a']}>会签（所有人同意才通过）<IconQuestionCircle /></Radio>
        <Radio value={approverConfigVar.approvalMode['b']}>或签（一人同意即通过）</Radio>
        <Radio value={approverConfigVar.approvalMode['c']}>依次审批（按顺序依次审批）</Radio>
        <Radio value={approverConfigVar.approvalMode['d']}>投票（按投票比例决定是否通过）</Radio>
      </RadioGroup>
    </div>
  );
}
