import { useEffect, useState } from 'react';
import { Switch, Radio, type TableColumnProps } from '@arco-design/web-react';
import { IconQuestionCircle } from '@arco-design/web-react/icon';
import FieldTable from '../../common/filedComponent/index';
import './style.less';

export default function FieldConfig({ setCcRecipientsConfigData, fieldPermConfig }: any) {
  const [nodeSwitch, setNodeSwitch] = useState<boolean>(fieldPermConfig?.useNodeConfig || false);
  const [tbData, setTbData] = useState(fieldPermConfig?.fieldConfigs);

  function changeNodeSwitch(flag: boolean) {
    setNodeSwitch(flag);
  }
  useEffect(() => {
    saveData();
  }, [nodeSwitch]);

  function saveData() {
    const fieldPermConfig = {
      useNodeConfig: nodeSwitch,
      fieldConfigs: tbData
    };
    setCcRecipientsConfigData('fieldPermConfig', fieldPermConfig);
  }

  const onTableChange = () => {
    saveData();
  };

  function handleSwitchChange(row: any, type: string, flag: boolean) {
    let permType = '';
    if (type === 'batchApproval') {
      permType = 'read';
    } else if (type === 'hideFlag') {
      permType = 'hidden';
    }

    if (flag) {
      let _row = { ...row };
      _row.fieldPermType = permType;
      handleSave(_row);
    }
  }

  function handleSave(row: any) {
    const newData = [...tbData];
    const index = newData.findIndex((item) => {
      const itemId = item.parentDisplayName ? item.parentDisplayName + item.fieldName : item.fieldName;
      const fid = row.parentDisplayName ? row.parentDisplayName + row.fieldName : row.fieldName;
      return itemId === fid;
    });
    newData.splice(index, 1, { ...newData[index], ...row });
    setTbData(newData);
  }

  const columnsTable: TableColumnProps[] = [
    {
      title: '字段名称',
      dataIndex: 'fieldName',
      render: (val: any, row: any) => {
        return row.parentDisplayName ? row.parentDisplayName + '.' + row.displayName : row.displayName;
      }
    },
    {
      title: '只读',
      dataIndex: 'batchApproval',
      render: (val: any, row: any) => {
        return (
          <Radio
            checked={row.fieldPermType === 'read'}
            onChange={(checked: boolean) => handleSwitchChange(row, 'batchApproval', checked)}
          />
        );
      }
    },
    {
      title: '隐藏',
      dataIndex: 'index',
      render: (_: any, row: any) => (
        <Radio
          checked={row.fieldPermType === 'hidden'}
          onChange={(checked: boolean) => handleSwitchChange(row, 'hideFlag', checked)}
        />
      )
    }
  ];

  const setTableData = (v: any) => {
    const tableMap = new Map<string, any>(tbData?.map((item: any) => [item.fieldName, item]));
    const addType = v?.map((item: any) => ({
      ...item,
      fieldPermType: tableMap.has(item.fieldName) ? tableMap.get(item.fieldName).fieldPermType : 'read'
    }));
    setTbData(addType);
  };
  return (
    <div className="field-config">
      <div className="title-box">
        <p className="p-title">字段权限</p>
        <p style={{ fontSize: 'small', color: 'rgba(134, 144, 156, 1)' }}>
          字段默认为只读状态，如需设为“隐藏”，请在下方添加配置
        </p>
        <div className="right-switch">
          <p>节点独立配置</p>
          <p className="switch-outer">
            <IconQuestionCircle />
            <Switch onChange={changeNodeSwitch} checked={nodeSwitch} />
          </p>
        </div>
      </div>

      {nodeSwitch && (
        <FieldTable
          onTableChange={onTableChange}
          columnsTable={columnsTable}
          tbData={tbData}
          setTableData={setTableData}
          title={'添加字段'}
        />
      )}
    </div>
  );
}
