import { Radio } from '@arco-design/web-react';
import { useState, useEffect } from 'react';
import Header from '../../../header';
import CcRecipientsConfig from './ccRecipientsConfig/index';
import FieldConfig from './fieldConfig/index';
import BottomBtn from '../../../bottomBtn/index';
import { useLocation } from 'react-router-dom';
import { getEntityFieldsWithChildren, getPageSetMetaData } from '@onebase/app';
import styles from './index.module.less';

const RadioGroup = Radio.Group;

export default function CcRecipientsDreawer({ handleConfigSubmit, configData }: any) {
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const pageSetId = searchParams.get('pageSetId') || '';
  const [ckOptions, setCkOptions] = useState([]);
  // const [tbaleName, setTbaleName] = useState('');
  const [useCcRecipients, setCcRecipients] = useState<string>('ccRecipients');
  const [editValue, setEditValue] = useState('');

  const [ccRecipientsConfigData, setCcReConfigData] = useState(
    configData || {
      copyReceiverConfig: {
        handlerType: 'user'
      },
      fieldPermConfig: {
        useNodeConfig: false
      }
    }
  );
  useEffect(() => {
    const childTableMap = new Map();
    configData?.fieldPermConfig?.fieldConfigs.forEach((item: any) => {
      if (item.fieldName === item.tableName) {
        childTableMap.set(item.tableName, item);
      }
    });
    configData?.fieldPermConfig?.fieldConfigs.forEach((item: any) => {
      item.displayName = item.fieldDisplayName;
      if (item.tableName !== item.fieldName && childTableMap.has(item.tableName)) {
        item.parentDisplayName = childTableMap.get(item.tableName).displayName || item.fieldDisplayName;
      }
    });
    setCkOptions(configData);
  }, configData);

  const { copyReceiverConfig, fieldPermConfig } = ccRecipientsConfigData;

  function setCcRecipientsConfigData(key: any, data: any) {
    setCcReConfigData((prev: any) => {
      const newData = { ...prev };
      if (key === 'copyReceiverConfig') {
        newData.copyReceiverConfig = Object.assign({}, newData.copyReceiverConfig, data);
      } else if (key === 'fieldPermConfig') {
        const newFieldPermConfig = data?.fieldConfigs?.map((item: any) => {
          const { tableName, fieldName, fieldDisplayName, displayName, fieldPermType } = item;
          return {
            tableName,
            fieldName,
            fieldDisplayName: fieldDisplayName || displayName,
            fieldPermType
          };
        });
        newData.fieldPermConfig = {
          ...newData.fieldPermConfig,
          fieldConfigs: newFieldPermConfig
        };
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
    // setTbaleName(tableName);
    setCkOptions(data);
  };

  useEffect(() => {
    getMainMetaData();
  }, []);

  const renderContent = () => {
    switch (useCcRecipients) {
      case 'ccRecipients':
        return (
          <CcRecipientsConfig
            setCcRecipientsConfigData={setCcRecipientsConfigData}
            copyReceiverConfig={copyReceiverConfig}
          />
        );
      case 'fieldPermissions':
        return (
          <FieldConfig
            ckOptions={ckOptions}
            setCcRecipientsConfigData={setCcRecipientsConfigData}
            fieldPermConfig={fieldPermConfig}
          />
        );

      default:
        return <div>抄送人</div>;
    }
  };

  function handleSubmit() {
    let errorMsg = '';
    const { users = [], roles = [] } = ccRecipientsConfigData.copyReceiverConfig || {};
    if (!users.length && !roles.length) {
      errorMsg = '节点缺少抄送人';
    }
    ccRecipientsConfigData.errorMsg = errorMsg;
    handleConfigSubmit && handleConfigSubmit(ccRecipientsConfigData, editValue);
  }

  return (
    <>
      <Header changeName={(name) => setEditValue(name)} />
      <div className={styles.ccRecipients}>
        <RadioGroup
          className={styles.radioGroup}
          type="button"
          name="lang"
          value={useCcRecipients}
          onChange={(value) => setCcRecipients(value)}
        >
          <Radio value="ccRecipients">抄送人</Radio>
          <Radio value="fieldPermissions">字段权限</Radio>
        </RadioGroup>
        <div className={styles.content}>{renderContent()}</div>
      </div>
      <BottomBtn handleSubmit={handleSubmit} />
    </>
  );
}
