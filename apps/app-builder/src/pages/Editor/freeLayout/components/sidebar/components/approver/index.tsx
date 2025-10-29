/**
 * 审批人
 */
import { Radio } from '@arco-design/web-react';
import styles from './index.module.less';
import { useState } from 'react';
import Header from '../../../header'
import BottomBtn from '../../../bottomBtn';
import ApproverConfig from './approverConfig/index';
import ApproverBtnConfig from './btnConfig/index'
import FieldConfig from './fieldConfig/index'

const RadioGroup = Radio.Group;

export default function ApproveDreawer() {
  const [useApprover, setApprover] = useState<string>('approver');
  const renderContent = () => {
    switch (useApprover) {
      case 'approver':
        return <ApproverConfig />;
      case 'approverBtn':
        return <ApproverBtnConfig />;
      case 'fieldPermissions':
        return <FieldConfig />;
      case 'advancedSettings':
        return <div>高级设置</div>;
      default:
        return <div>审批人</div>;
    }
  };

  function handleSubmit() {
    console.log('999 000 ===')
  }

  return (
    <>
      <Header />
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
      <BottomBtn handleSubmit={handleSubmit}/>
    </>
  );
}
