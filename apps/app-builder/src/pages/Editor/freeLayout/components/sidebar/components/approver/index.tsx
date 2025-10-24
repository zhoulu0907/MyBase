/**
 * 审批人
 */
import { Radio, Divider } from '@arco-design/web-react';
import styles from './index.module.less';
import { useState } from 'react';
import ApproverConfig from './approverConfig/index';
const RadioGroup = Radio.Group;

export default function Approver() {
  const [useApprover, setApprover] = useState<string>('approver');
  const [configMode, setConfigMode] = useState<string>('simple');
  const renderContent = () => {
    switch (useApprover) {
      case 'approver':
        return <ApproverConfig />;
      case 'approverBtn':
        return <div>审批按钮</div>;
      case 'fieldPermissions':
        return <div>字段权限</div>;
      case 'advancedSettings':
        return <div>高级设置</div>;
      default:
        return <div>审批人</div>;
    }
  };
  return (
    <div className={styles.approver}>
      <RadioGroup
        className={styles.radioGroup}
        type="button"
        name="lang"
        value={useApprover}
        onChange={(value) => setApprover(value)}
      >
        <Radio value="approver">审批人</Radio>
        <Radio value="approverBtn">审批按钮</Radio>
        <Radio value="fieldPermissions">字段权限</Radio>
        <Radio value="advancedSettings">高级设置</Radio>
      </RadioGroup>
      <div className={styles.content}>{renderContent()}</div>
    </div>
  );
}
