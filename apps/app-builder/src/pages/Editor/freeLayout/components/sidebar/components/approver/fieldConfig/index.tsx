import { useEffect, useState, forwardRef, useImperativeHandle, useRef } from 'react';
import { Switch, Button, Table, type TableColumnProps, Tooltip, Radio } from '@arco-design/web-react';
import { IconQuestionCircle, IconPlus } from '@arco-design/web-react/icon';
import FieldModal from './FieldModal';
import { type FieldConfigType } from '../constant';
import FieldTable from '../../common/filedComponent/index';
import './style.less';
/**
 * @param editable 是否可编辑
 * @param onTableChange 表格数据变化时回调
 * @param value 表格数据
 * @param ckOptions 字段配置
 * @param invert 排除数据 为了弹窗数据去重
 */

// 定义 ref 的类型接口
interface ChildComponentRef {
  getTbData: () => any[];
}

export default function FieldConfig({ setApprovalConfigData, fieldPermConfig, ckOptions }: FieldConfigType) {
  let [nodeSwitch, setNodeSwitch] = useState(fieldPermConfig.useNodeConfig);
  const [tbData, setTbData] = useState(fieldPermConfig?.fieldConfigs || []);
  const [curKeyArr, setCurKeyArr] = useState<any[]>([]);
  let editRef = useRef<ChildComponentRef>();
  let hiddenRef = useRef<ChildComponentRef>();
  let writeArr: any = [];
  let hiddenArr: any = [];
  useEffect(() => {
    const childTableMap = new Map();
    fieldPermConfig?.fieldConfigs?.forEach((item: any) => {
      if (item.fieldName === item.tableName) {
        childTableMap.set(item.tableName, item);
      }
    });
    fieldPermConfig?.fieldConfigs?.forEach((item: any) => {
      item.displayName = item.fieldDisplayName;
      if (item.tableName !== item.fieldName && childTableMap.has(item.tableName)) {
        item.parentDisplayName = childTableMap.get(item.tableName).displayName || item.fieldDisplayName;
      }
    });
    setTbData(fieldPermConfig?.fieldConfigs || []);
  }, [fieldPermConfig]);

  if (Array.isArray(fieldPermConfig.fieldConfigs)) {
    fieldPermConfig.fieldConfigs.forEach((item: any) => {
      if (item.fieldPermType === 'write') {
        writeArr.push(item);
      } else {
        hiddenArr.push(item);
      }
    });
  }
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
    setApprovalConfigData('fieldPermConfig', fieldPermConfig);
  }

  const columnsTable: TableColumnProps[] = [
    {
      title: '字段名称',
      dataIndex: 'fieldName',
      render: (val: any, row: any) => {
        return row.parentDisplayName ? row.parentDisplayName + ' _' + row.displayName : row.displayName;
      }
    },
    {
      title: '编辑',
      dataIndex: 'batchApproval',
      render: (val: any, row: any) => {
        return (
          <Radio
            checked={row.fieldPermType === 'write'}
            onChange={(checked: boolean) => handleSwitchChange(row, 'writeFlag', checked)}
          />
        );
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

  const onTableChange = () => {
    saveData();
  };

  const setTableData = (v: any) => {
    const tableMap = new Map<string, any>(
      tbData?.map((item: any) => [
        item.parentDisplayName ? item.parentDisplayName + item.fieldName : item.fieldName,
        item
      ])
    );
    const addType = v?.map((item: any) => {
      const fid = item.parentDisplayName ? item.parentDisplayName + item.fieldName : item.fieldName;
      return {
        ...item,
        fieldPermType: tableMap.has(fid) ? tableMap.get(fid).fieldPermType : 'read'
      };
    });
    setTbData(addType);
  };

  function handleSwitchChange(row: any, type: string, flag: boolean) {
    let permType = '';
    if (type === 'batchApproval') {
      permType = 'read';
    } else if (type === 'hideFlag') {
      permType = 'hidden';
    } else if (type === 'writeFlag') {
      permType = 'write';
    }

    if (flag) {
      let _row = { ...row };
      _row.fieldPermType = permType;
      handleSave(_row);
    }
  }

  function handleSave(row: any) {
    const newData = [...tbData];
    const index = newData.findIndex((item: any) => {
      const itemId = item.parentDisplayName ? item.parentDisplayName + item.fieldName : item.fieldName;
      const fid = row.parentDisplayName ? row.parentDisplayName + row.fieldName : row.fieldName;
      return itemId === fid;
    });
    newData.splice(index, 1, { ...newData[index], ...row });
    setTbData(newData);
  }
  return (
    <div className="field-config">
      <div className="title-box">
        <p className="p-title">字段权限</p>
        <p style={{ fontSize: 'small', color: 'rgba(134, 144, 156, 1)' }}>
          字段默认为只读状态，如需设为“可编辑”或“隐藏”，请在下方添加配置
        </p>
        <div className="right-switch">
          <p>节点独立配置</p>
          <p className="switch-outer">
            <Tooltip
              position="tr"
              trigger="hover"
              content="关闭时，字段权限跟随表单组件状态自动同步；开启后，可独立配置当前节点的字段权限。"
            >
              <IconQuestionCircle />
            </Tooltip>
            <Switch onChange={changeNodeSwitch} checked={nodeSwitch} />
          </p>
        </div>
      </div>

      {nodeSwitch && (
        <FieldTable
          onTableChange={onTableChange}
          ckOptions={ckOptions}
          columnsTable={columnsTable}
          tbData={tbData}
          setTableData={setTableData}
          title={'添加隐藏字段'}
        />
      )}
    </div>
  );
}
