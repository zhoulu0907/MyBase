import { useEffect, useState } from 'react';
import { Switch, Checkbox, type TableColumnProps } from '@arco-design/web-react';
import { IconQuestionCircle } from '@arco-design/web-react/icon';
import FieldTable from '../../common/filedComponent/index';
import './style.less';

export default function FieldConfig({ setCcRecipientsConfigData, fieldPermConfig, ckOptions }) {
  const [nodeSwitch, setNodeSwitch] = useState<boolean>(fieldPermConfig?.useNodeConfig||false);
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
    const index = newData.findIndex((item) => row.fieldId === item.fieldId);
    newData.splice(index, 1, { ...newData[index], ...row });
    setTbData(newData);
  }

  const columnsTable: TableColumnProps[] = [
    {
      title: '字段名称',
      dataIndex: 'fieldName'
    },
    {
      title: '只读',
      dataIndex: 'batchApproval',
      render: (val: any, row: any) => {
        return (
          <Checkbox
            onChange={(flag: boolean) => handleSwitchChange(row, 'batchApproval', flag)}
            checked={row.fieldPermType === 'read'}
          />
        );
      }
    },
    {
      title: '隐藏',
      dataIndex: 'index',
      render: (_: any, row: any) => (
        <Checkbox
          checked={row.fieldPermType === 'hidden'}
          onChange={(flag: boolean) => handleSwitchChange(row, 'hideFlag', flag)}
        />
      )
    }
  ];

  const setTableData = (v) => {
    setTbData(v);
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

      <FieldTable
        onTableChange={onTableChange}
        ckOptions={ckOptions}
        columnsTable={columnsTable}
        tbData={tbData}
        setTableData={setTableData}
        fieldPermType={'read'}
      />
    </div>
  );
}
