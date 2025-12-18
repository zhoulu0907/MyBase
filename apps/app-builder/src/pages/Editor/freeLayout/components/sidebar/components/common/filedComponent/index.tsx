import { Button, Table, type TableColumnProps } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import { getEntityFieldsWithChildren, getPageSetMetaData } from '@onebase/app';
import { forwardRef, useEffect, useImperativeHandle, useState } from 'react';
import { useLocation } from 'react-router-dom';
import FieldModal from './FieldModal';
import './style.less';

/**
 * @param onTableChange 表格数据变化时回调
 * @param title 弹窗标题
 * @param tbData 表格数据
 * @param setTableData 维护表格数据
 * @param columnsTable 表格配置
 */

const FieldTable = forwardRef(({ onTableChange, title, tbData, setTableData, columnsTable }: any, ref) => {
  // keyArr是专门给FieldModal弹窗用的，帮助弹窗反选
  const [curKeyArr, setCurKeyArr] = useState<any[]>([]);
  const [selectRowkeyArr, setSelectRowKeyArr] = useState<any[]>([]);
  const [fmVisible, setFmVisible] = useState(false);
  const [ckOptions, setCkOptions] = useState([]);
  const [childTableMap, setChildTableMap] = useState<any>(new Map()); // 子表map
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const pageSetId = searchParams.get('pageSetId') || '';
  const baseColumns: TableColumnProps[] = [
    {
      title: '操作',
      width: 95,
      dataIndex: 'tableName',
      render: (val: any, row: any) => {
        return (
          <Button type="text" onClick={() => handleDelRow(row)}>
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
  function handleDelRow(row: any) {
    let _data = [...tbData];
    _data = _data.filter((item) => {
      const itemId = item.parentDisplayName ? item.parentDisplayName + item.fieldName : item.fieldName;
      if (Array.isArray(row)) {
        return !row.includes(itemId);
      } else {
        const fid = row.parentDisplayName ? row.parentDisplayName + row.fieldName : row.fieldName;
        return itemId !== fid;
      }
    });
    setTableData(_data);
    setSelectRowKeyArr([]);
  }
  function handleDelMass(fid: any) {
    handleDelRow(fid);
    setSelectRowKeyArr([]);
  }
  function mergeDataToTable(arr: Array<any>) {
    setTableData(arr);
  }

  const getMainMetaData = async () => {
    const mainMetaData = await getPageSetMetaData({ pageSetId: pageSetId });
    const { parentFields, tableName, childEntities } = await getEntityFieldsWithChildren(mainMetaData);
    const data: any = []; // 处理后的数据

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
      // 设置子表map
      const newChildTableMap = new Map(childTableMap);
      newChildTableMap.set(childTableName, item);
      setChildTableMap(newChildTableMap);
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
    if (Array.isArray(tbData)) {
      let cur_key_arr: any[] = [];
      tbData.forEach((item: any) => {
        item.displayName = item.fieldDisplayName;
        if (item.tableName !== item.fieldName && childTableMap.has(item.tableName)) {
          item.parentDisplayName = childTableMap.get(item.tableName).childEntityName;
        }
        const fid = item.parentDisplayName ? item.parentDisplayName + item.fieldName : item.fieldName;
        cur_key_arr.push(fid);
      });
      setCurKeyArr(cur_key_arr);
    }
    onTableChange();
  }, [tbData, ckOptions]);

  useEffect(() => {
    getMainMetaData();
  }, []);

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
          <Button type="primary" onClick={() => handleDelMass(selectRowkeyArr)}>
            批量删除
          </Button>
        )}
      </div>
      <Table
        className="field-table-wrapper"
        rowKey={(record: any) =>
          record.parentDisplayName ? record.parentDisplayName + record.fieldName : record.fieldName
        }
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
          title={title}
          curKeyArr={curKeyArr}
          mergeDataToTable={mergeDataToTable}
        />
      )}
    </>
  );
});
export default FieldTable;
