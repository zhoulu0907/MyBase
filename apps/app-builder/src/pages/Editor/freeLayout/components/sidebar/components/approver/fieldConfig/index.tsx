import { useEffect, useState, forwardRef, useImperativeHandle, useRef } from 'react';
import { Switch, Button, Table, type TableColumnProps, Tooltip } from '@arco-design/web-react';
import { IconQuestionCircle, IconPlus } from '@arco-design/web-react/icon';
import FieldModal from './FieldModal';
import { type FieldConfigType } from '../constant';
import './style.less';
/**
 * @param editable 是否可编辑
 * @param onTableChange 表格数据变化时回调
 * @param value 表格数据
 * @param ckOptions 字段配置
 * @param invert 排除数据 为了弹窗数据去重
 */
const FieldTable = forwardRef(({ editable, onTableChange, value, ckOptions, invert }: any, ref) => {
  // keyArr是专门给FieldModal弹窗用的，帮助弹窗反选
  const [curKeyArr, setCurKeyArr] = useState<any[]>([]);
  const [selectRowkeyArr, setSelectRowKeyArr] = useState([]);
  const [fmVisible, setFmVisible] = useState(false);

  const columns: TableColumnProps[] = [
    {
      title: '字段名称',
      dataIndex: 'fieldName'
    },
    {
      title: '操作',
      width: 95,
      dataIndex: 'fieldUuid',
      render: (val: any, row: any) => {
        return (
          <Button type="text" onClick={() => handleDelRow(val)}>
            删除
          </Button>
        );
      }
    }
  ];
  const [tbData, setTbData] = useState(value);

  function handleTbSelect(keyArr: any, rowArr: any) {
    setSelectRowKeyArr(keyArr);
  }
  function handleAddFiled() {
    setFmVisible(true);
  }
  function handleDelRow(fid: any) {
    let _data = [...tbData];
    if (typeof fid === 'string') {
      _data = _data.filter((item) => {
        return item.fieldUuid !== fid;
      });
    } else if (Array.isArray(fid)) {
      _data = _data.filter((item) => {
        return fid.indexOf(item.fieldUuid) < 0;
      });
    }
    setTbData(_data);
  }
  function mergeDataToTable(arr: Array<any>) {
    setTbData(arr);
  }

  useEffect(() => {
    if (Array.isArray(tbData)) {
      let cur_key_arr: any[] = [];
      tbData.forEach((item: any) => {
        cur_key_arr.push(item.fieldUuid);
      });
      setCurKeyArr(cur_key_arr);
    }
    onTableChange();
  }, [tbData]);

  useImperativeHandle(ref, () => ({
    getTbData: () => tbData
  }));

  return (
    <>
      <p style={{ paddingBottom: '6px' }}>{editable ? '可编辑字段' : '隐藏字段'}</p>
      <div className="flex-btw">
        <Button onClick={handleAddFiled} type="primary" icon={<IconPlus />}>
          添加字段
        </Button>
        {selectRowkeyArr?.length > 0 && (
          <Button type="primary" className="gray-btn" onClick={() => handleDelRow(selectRowkeyArr)}>
            批量删除
          </Button>
        )}
      </div>
      <Table
        className="field-table-wrapper"
        rowKey="fieldUuid"
        columns={columns}
        data={tbData}
        pagination={false}
        rowSelection={{
          type: 'checkbox',
          onChange: (keyArr: any, rowArr: any) => handleTbSelect(keyArr, rowArr)
        }}
      />
      {fmVisible && (
        <FieldModal
          fmVisible={fmVisible}
          ckOptions={ckOptions}
          setFmVisible={setFmVisible}
          isEdit={editable}
          curKeyArr={curKeyArr}
          invert={invert}
          mergeDataToTable={mergeDataToTable}
        />
      )}
    </>
  );
});

// 定义 ref 的类型接口
interface ChildComponentRef {
  getTbData: () => any[];
}

export default function FieldConfig({ setApprovalConfigData, fieldPermConfig, ckOptions }: FieldConfigType) {
  let [nodeSwitch, setNodeSwitch] = useState(fieldPermConfig.useNodeConfig);
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
    const editTable: any = editRef?.current?.getTbData() || [];
    const hiddenTable: any = hiddenRef?.current?.getTbData() || [];
    const fieldPermConfig = {
      useNodeConfig: nodeSwitch,
      fieldConfigs: [...editTable, ...hiddenTable]
    };
    setApprovalConfigData('fieldPermConfig', fieldPermConfig);
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
            <Tooltip position='tr' trigger='hover' content='关闭时，字段权限跟随表单组件状态自动同步；开启后，可独立配置当前节点的字段权限。'>
              <IconQuestionCircle />
            </Tooltip>
            <Switch onChange={changeNodeSwitch} checked={nodeSwitch} />
          </p>
        </div>
      </div>
      {nodeSwitch && (
        <>
          <FieldTable
            editable={true}
            ref={editRef}
            onTableChange={saveData}
            value={writeArr}
            invert={hiddenArr}
            ckOptions={ckOptions}
          />
          <div style={{ height: 24 }}></div>
          <FieldTable
            editable={false}
            ref={hiddenRef}
            onTableChange={saveData}
            value={hiddenArr}
            invert={writeArr}
            ckOptions={ckOptions}
          />
        </>
      )}
    </div>
  );
}
