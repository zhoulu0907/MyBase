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
import AdvancedConfig from './advancedConfig/index';
import { ApproveDrawerTab, defaultBtnConfigArr } from './constant';
import { useLocation } from 'react-router-dom';
import type {
  ApproverConfigDataType,
  ApproverConfigType,
  AdvancedConfigType,
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
  const [tableName, setTableName] = useState('');
  const [useApprover, setApprover] = useState<string>('approver');
  const [approverConfigData, setApproverConfigData] = useState<ApproverConfigDataType>(processInitData(configData));
  function processInitData(initData: any) {
    if (!initData) {
      return {
        approverConfig: {
          approvalMode: 'any_sign',
          users: [],
          roles: []
        },
        buttonConfigs: [],
        fieldPermConfig: {
          useNodeConfig: false
        },
        advancedConfig: {
          autoApproveCfg: {},
          emptyApproverCfg: {}
        }
      };
    }

    const keys = Object.keys(initData);
    if (keys.length === 2 && keys.includes('name') && keys.includes('errorMsg')) {
      return {
        ...initData,
        buttonConfigs: defaultBtnConfigArr
      };
    } else {
      return initData;
    }
  }
  const [editValue, setEditValue] = useState('');
  const { approverConfig, buttonConfigs, fieldPermConfig, advancedConfig } = approverConfigData;

  function setApprovalConfigData<T extends keyof ApproverConfigDataType>(
    key: T,
    data: T extends 'buttonConfigs' ? ButtonConfigType[] : ApproverConfigType | FieldPermConfigType | AdvancedConfigType
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
      } else if (key === 'advancedConfig') {
        newData.advancedConfig = data as AdvancedConfigType;
      }
      return newData;
    });
  }
  const getMainMetaData = async () => {
    const mainMetaData = await getPageSetMetaData({ pageSetId: pageSetId });
    const { parentFields, tableName, childEntities } = await getEntityFieldsWithChildren(mainMetaData);
    const data: any = [];

    parentFields.forEach((item: any) => {
      const displayName = item.displayName || item.fieldDisplayName;
      data.push({
        displayName: displayName,
        fieldDisplayName: displayName,
        fieldName: item.fieldName,
        tableName: tableName,
        isSystemField: item.isSystemField
      });
    });
    childEntities.forEach((item: any) => {
      const { childTableName, childEntityName, childFields } = item;
      data.push({
        displayName: childEntityName,
        fieldDisplayName: childEntityName,
        fieldName: childTableName,
        tableName: childTableName,
        isSystemField: 0
      });
      childFields.forEach((childItem: any) => {
        const displayName = childItem.displayName || childItem.fieldDisplayName;
        data.push({
          displayName: displayName,
          fieldDisplayName: displayName,
          fieldName: childItem.fieldName,
          tableName: childTableName,
          parentDisplayName: childEntityName,
          isSystemField: childItem.isSystemField
        });
      });
    });
    setCkOptions(data);
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
        return <AdvancedConfig setApprovalConfigData={setApprovalConfigData} advancedConfig={advancedConfig || {}} />;
      default:
        return <div>审批人</div>;
    }
  };

  function handleSubmit() {
    let errorMsg = '';
    const { users = [], roles = [] } = approverConfigData.approverConfig || {};
    if (!users.length && !roles.length) {
      errorMsg = '节点缺少审批人';
    }
    if (!approverConfigData?.buttonConfigs?.length) {
      errorMsg = '节点缺少按钮配置';
    }
    approverConfigData.errorMsg = errorMsg;
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
