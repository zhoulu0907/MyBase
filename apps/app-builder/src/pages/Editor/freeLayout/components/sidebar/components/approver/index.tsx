/**
 * 审批人
 */
import { Radio } from '@arco-design/web-react';
import styles from './index.module.less';
import { useState, useEffect } from 'react';
import Header from '../../../header';
import BottomBtn from '../../../bottomBtn';
import ApproverConfig from './approverConfig/index';
import ApproverBtnConfig from './btnConfig/index';
import FieldConfig from './fieldConfig/index';
import AdvancedConfig from './advancedConfig/index'
import { ApproveDrawerTab } from './constant';
import { useLocation } from 'react-router-dom';
import type {
  ApproverConfigDataType,
  ApproverConfigType,
  ButtonConfigType,
  FieldPermConfigType,
  ApproveDrawerProps
} from './constant';
import { getEntityFieldsWithChildren, getPageSetMetaData } from '@onebase/app';

const RadioGroup = Radio.Group;

export default function ApproveDreawer({ handleConfigSubmit, configData }: ApproveDrawerProps) {
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const pageSetId = searchParams.get('pageSetId') || '';
  const [ckOptions, setCkOptions] = useState([]);
  const [useApprover, setApprover] = useState<string>('approver');
  const [approverConfigData, setApproverConfigData] = useState<ApproverConfigDataType>(
    configData || {
      approverConfig: {
        approvalMode: 'any_sign'
      },
      buttonConfigs: [],
      fieldPermConfig: {
        useNodeConfig: false
      }
    }
  );
  const [editValue, setEditValue] = useState('');
  const { approverConfig, buttonConfigs, fieldPermConfig } = approverConfigData;

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
  const getMainMetaData = async () => {
    const mainMetaData = await getPageSetMetaData({ pageSetId: pageSetId });
    const { parentFields } = await getEntityFieldsWithChildren(mainMetaData);
    setCkOptions(parentFields);
  };

  useEffect(() => {
    getMainMetaData();
  }, []);
  const renderContent = () => {
    switch (useApprover) {
      case ApproveDrawerTab.APPROVER:
        return <ApproverConfig setApprovalConfigData={setApprovalConfigData} approverConfig={approverConfig || {}} />;
      case ApproveDrawerTab.APPROVER_BTN:
        return <ApproverBtnConfig setApprovalConfigData={setApprovalConfigData} buttonConfigs={buttonConfigs || []} />;
      case ApproveDrawerTab.FIELD_PERMISSIONS:
        return (
          <FieldConfig
            setApprovalConfigData={setApprovalConfigData}
            fieldPermConfig={fieldPermConfig || {}}
            ckOptions={ckOptions}
          />
        );
      case ApproveDrawerTab.ADVANCED_SETTINGS:
        return <AdvancedConfig />;
      default:
        return <div>审批人</div>;
    }
  };

  function handleSubmit() {
    console.log('approverConfigData ===', approverConfigData);
    handleConfigSubmit && handleConfigSubmit(approverConfigData, editValue);
  }

  return (
    <>
      <Header changeName={(name) => setEditValue(name)} />
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
