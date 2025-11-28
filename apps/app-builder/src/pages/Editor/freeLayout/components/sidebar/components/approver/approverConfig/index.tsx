/**
 * 审批人
 */
import { Radio, Divider, Tooltip } from '@arco-design/web-react';
import { IconQuestionCircle } from '@arco-design/web-react/icon';
import styles from './index.module.less';
import { useEffect, useState } from 'react';
import SimpleMode from './SimpleMode';
import ConditionMode from './ConditionMode';
import { approverConfigVar, type ApproverConfig } from '../constant';

const RadioGroup = Radio.Group;

export default function Approver({ setApprovalConfigData, approverConfig }: ApproverConfig) {
  const initMode = approverConfig.approvalMode || approverConfigVar.approvalMode['a'];
  const [configMode, setConfigMode] = useState<string>('simple');
  const [approvalMode, setApprovalMode] = useState(initMode);

  function changeApprovalMode(val: string) {
    setApprovalMode(val);
  }
  useEffect(() => {
    setApprovalConfigData('approverConfig', { approvalMode });
  }, [approvalMode]);

  return (
    <div className={styles.approverConfig}>
      <div className={styles.configTitle}>审批人设置</div>
      <div className={styles.configMode} style={{ display: 'none' }}>
        <span>配置模式：</span>
        <RadioGroup value={configMode} onChange={setConfigMode}>
          <Radio value="simple">简易模式</Radio>
          <Radio value="condition">
            条件模式
            <IconQuestionCircle />
          </Radio>
        </RadioGroup>
      </div>
      <Divider />
      {configMode === 'simple' && (
        <SimpleMode setApprovalConfigData={setApprovalConfigData} approverConfig={approverConfig} />
      )}
      {configMode === 'condition' && <ConditionMode />}
      <div className={styles.configTitle} style={{ paddingTop: '8px' }}>
        多人审批方式
      </div>
      <RadioGroup direction="vertical" value={approvalMode} onChange={changeApprovalMode}>
        <Radio value={approverConfigVar.approvalMode['a']}>会签（所有人同意才通过）</Radio>
        <Radio value={approverConfigVar.approvalMode['b']}>
          或签（一人同意即通过）
          <Tooltip
            position="tl"
            trigger="hover"
            content="当或签时，多个审批人会同时收到审批通知，第一个提交审批结果的审批人会决定最终的审批结
果。若同意即通过，若拒绝则终止。"
          >
            <IconQuestionCircle style={{ fontSize: '15px', marginLeft: '4px', color: '#AAAEB3' }} />
          </Tooltip>
        </Radio>
        <Radio value={approverConfigVar.approvalMode['c']}>依次审批（按顺序依次审批）</Radio>
        <Radio value={approverConfigVar.approvalMode['d']}>投票（按投票比例决定是否通过）</Radio>
      </RadioGroup>
    </div>
  );
}
