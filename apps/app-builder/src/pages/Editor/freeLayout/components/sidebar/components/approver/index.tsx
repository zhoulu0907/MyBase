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
import type {
  ApproverConfigDataType,
  ApproverConfigType,
  ButtonConfigType,
  FieldPermConfigType,
  ApproveDrawerProps
} from './constant';

const RadioGroup = Radio.Group;

export default function ApproveDreawer({ handleConfigSubmit, configData }: ApproveDrawerProps) {
  const [useApprover, setApprover] = useState<string>('approver');
  const [approverConfigData, setApproverConfigData] = useState<ApproverConfigDataType>(
    configData || {
      approverConfig: {
        approvalMode: 'any_sign'
      },
      buttonConfigs: [],
      fieldPermConfig: {}
    }
  );
  console.log(approverConfigData);

  function setApprovalConfigData<T extends keyof ApproverConfigDataType>(
    key: T,
    data: T extends 'buttonConfigs' ? ButtonConfigType[] : ApproverConfigType | FieldPermConfigType
  ) {
    setApproverConfigData((prev) => {
      const newData = { ...prev };
      if (key === 'buttonConfigs') {
        newData.buttonConfigs = data as ButtonConfigType[];
      } else if (key === 'approverConfig') {
        newData.approverConfig = Object.assign({}, newData.approverConfig, data) as ApproverConfigType;
      } else if (key === 'fieldPermConfig') {
        newData.fieldPermConfig = {
          ...newData.fieldPermConfig,
          ...data
        } as FieldPermConfigType;
      }
      return newData;
    });
  }
  const { approverConfig, buttonConfigs, fieldPermConfig } = approverConfigData;

  const renderContent = () => {
    switch (useApprover) {
      case ApproveDrawerTab.APPROVER:
        return <ApproverConfig setApprovalConfigData={setApprovalConfigData} approverConfig={approverConfig || {}} />;
      case ApproveDrawerTab.APPROVER_BTN:
        return <ApproverBtnConfig setApprovalConfigData={setApprovalConfigData} buttonConfigs={buttonConfigs || []} />;
      case ApproveDrawerTab.FIELD_PERMISSIONS:
        return <FieldConfig setApprovalConfigData={setApprovalConfigData} fieldPermConfig={fieldPermConfig || {}} />;
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
