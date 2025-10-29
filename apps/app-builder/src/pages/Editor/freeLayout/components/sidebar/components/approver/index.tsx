/**
 * 审批人
 */
import { Radio } from '@arco-design/web-react';
import styles from './index.module.less';
import { useState } from 'react';
import Header from '../../../header';
import BottomBtn from '../../../bottomBtn';
import ApproverConfig from './approverConfig/index';
import ApproverBtnConfig from './btnConfig/index';
import FieldConfig from './fieldConfig/index';
import { ApproveDrawerTab } from './constant';
// import { approverConfigVar } from './constant';

const RadioGroup = Radio.Group;
// todo(gjc):
// 1. approverConfigData类型不要写any
const approverConfigData: any = {
  approverConfig: {
    approvalMode: 'any_sign'
  },
  buttonConfigs: [],
  // fieldPermConfig: {}

  fieldPermConfig: {
    useNodeConfig: true,
    fieldConfigs: [
      {
        fieldId: '1',
        fieldName: '申请人姓名',
        fieldPermType: 'read'
      },
      {
        fieldId: '2',
        fieldName: '所属部门',
        fieldPermType: 'read'
      },
      {
        fieldId: '3',
        fieldName: '申请事由',
        fieldPermType: 'read'
      },
      {
        fieldId: '4',
        fieldName: '申请金额',
        fieldPermType: 'read'
      },
      {
        fieldId: '5',
        fieldName: '审批备注',
        fieldPermType: 'write'
      }
    ]
  }
};

function setApprovalConfigData(key: string, data: Object) {
  if (key === 'buttonConfigs') {
    approverConfigData.buttonConfigs = data;
  } else {
    approverConfigData[key] = {
      ...approverConfigData[key],
      ...data
    };
  }
}

export default function ApproveDreawer({ handleConfigSubmit }: any) {
  const [useApprover, setApprover] = useState<string>('approver');

  const renderContent = () => {
    switch (useApprover) {
      case ApproveDrawerTab.APPROVER:
        return <ApproverConfig setApprovalConfigData={setApprovalConfigData} />;
      case ApproveDrawerTab.APPROVER_BTN:
        return <ApproverBtnConfig setApprovalConfigData={setApprovalConfigData} />;
      case ApproveDrawerTab.FIELD_PERMISSIONS:
        return <FieldConfig setApprovalConfigData={setApprovalConfigData} />;
      case ApproveDrawerTab.ADVANCED_SETTINGS:
        return <div>高级设置</div>;
      default:
        return <div>审批人</div>;
    }
  };

  function handleSubmit() {
    console.log('approverConfigData ===', approverConfigData);
    handleConfigSubmit && handleConfigSubmit(approverConfigData, []);
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
      <BottomBtn handleSubmit={handleSubmit} />
    </>
  );
}
