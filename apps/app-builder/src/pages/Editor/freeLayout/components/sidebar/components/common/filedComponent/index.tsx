import { useEffect, useState, forwardRef, useImperativeHandle, useRef } from 'react';
import { Switch, Button, Table, type TableColumnProps } from '@arco-design/web-react';
import { IconQuestionCircle, IconPlus } from '@arco-design/web-react/icon';
import FieldModal from './FieldModal';
import './style.less';

/**
 * @param onTableChange 表格数据变化时回调
 * @param tbData 表格数据
 * @param setTableData 维护表格数据
 * @param ckOptions 字段配置
 * @param invert 排除数据 为了弹窗数据去重
 * @param columnsTable 表格配置
 */


const FieldTable = forwardRef(
  ({ onTableChange, tbData, setTableData, ckOptions, invert, columnsTable, fieldPermType }: any, ref) => {
    // keyArr是专门给FieldModal弹窗用的，帮助弹窗反选
    const [curKeyArr, setCurKeyArr] = useState<any[]>([]);
    const [selectRowkeyArr, setSelectRowKeyArr] = useState([]);
    const [fmVisible, setFmVisible] = useState(false);
    const baseColumns: TableColumnProps[] = [
      {
        title: '操作',
        width: 95,
        dataIndex: 'fieldId',
        render: (val: any, row: any) => {
          return (
            <Button type="text" onClick={() => handleDelRow(val)}>
              删除
            </Button>
          );
        }
      }
    ];

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
          return item.fieldId !== fid;
        });
      } else if (Array.isArray(fid)) {
        _data = _data.filter((item) => {
          return fid.indexOf(item.fieldId) < 0;
        });
      }
      setTableData(_data);
    }
    function mergeDataToTable(arr: Array<any>) {
      setTableData(arr);
    }

    useEffect(() => {
      if (Array.isArray(tbData)) {
        let cur_key_arr: any[] = [];
        tbData.forEach((item: any) => {
          cur_key_arr.push(item.fieldId);
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
          rowKey="fieldId"
          columns={[...(columnsTable || []), ...baseColumns]}
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
            title={'添加字段'}
            fieldPermType={fieldPermType}
            curKeyArr={curKeyArr}
            invert={invert}
            mergeDataToTable={mergeDataToTable}
          />
        )}
      </>
    );
  }
);
export default FieldTable;






