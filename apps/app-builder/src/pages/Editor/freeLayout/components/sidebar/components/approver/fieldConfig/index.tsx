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

export default function FieldConfig({ setApprovalConfigData, fieldPermConfig, ckOptions, tableName }: FieldConfigType) {
  let [nodeSwitch, setNodeSwitch] = useState(fieldPermConfig.useNodeConfig);
  const [tbData, setTbData] = useState(fieldPermConfig?.fieldConfigs || []);
  const [curKeyArr, setCurKeyArr] = useState<any[]>([]);
  let editRef = useRef<ChildComponentRef>();
  let hiddenRef = useRef<ChildComponentRef>();
  let writeArr: any = [];
  let hiddenArr: any = [];
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
      fieldConfigs: tbData.map((item: any) => {
        item.fieldDisplayName = item.displayName || item.fieldDisplayName;
        return {
          ...item,
          tableName
        };
      })
    };
    setApprovalConfigData('fieldPermConfig', fieldPermConfig);
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
    const tableMap = new Map<string, any>(tbData?.map((item: any) => [item.fieldName, item]));
    const addType = v?.map((item: any) => ({
      ...item,
      fieldPermType: tableMap.has(item.fieldName) ? tableMap.get(item.fieldName).fieldPermType : 'read'
    }));
    setTbData(addType);
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
    const index = newData.findIndex((item) => row.fieldName === item.fieldName);
    newData.splice(index, 1, { ...newData[index], ...row });
    setTbData(newData);
  }

  useEffect(() => {
    if (Array.isArray(tbData)) {
      let cur_key_arr: any[] = [];
      tbData.forEach((item: any) => {
        cur_key_arr.push(item.fieldName);
      });
      setCurKeyArr(cur_key_arr);
    }
    onTableChange();
  }, [tbData]);

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
          curKeyArr={curKeyArr}
        />
      )}
    </div>
  );
}
