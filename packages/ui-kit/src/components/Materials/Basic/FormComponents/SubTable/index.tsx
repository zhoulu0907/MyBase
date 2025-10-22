import { useSignals } from '@preact/signals-react/runtime';

import { ENTITY_FIELD_TYPE } from '@/components/DataFactory';
import { Button, Form, Input, Layout, Table } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import { useEffect, useState } from 'react';
import './index.css';
import { type XSubTableConfig } from './schema';

const XSubTable = (props: XSubTableConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { columns, id, runtime = true, label, layout, tooltip, labelColSpan = 100, status, verify } = props;

  useSignals();

  const [subTableData, setSubTableData] = useState<any[]>([]);
  const [subTableColumns, setSubTableColumns] = useState<any[]>([]);

  useEffect(() => {
    console.log('subTableData: ', subTableData);
  }, [subTableData]);

  const handleAdd = () => {
    console.log('add');
    const newData = columns.reduce((acc, column) => {
      acc[column.dataIndex] = '';
      return acc;
    }, {});
    newData.key = `${subTableData.length + 1}`;

    setSubTableData((prevData) => {
      const newDataArray = [...prevData, newData];
      return newDataArray;
    });
  };

  const handleDelete = (key: string) => {
    setSubTableData((prevData) => {
      const filteredData = prevData.filter((item) => item.key !== key);
      return filteredData;
    });
  };

  useEffect(() => {
    console.log(columns);
    const tableColumns = [];

    for (const column of columns) {
      if (column.dataType === ENTITY_FIELD_TYPE.TEXT.VALUE || column.dataType === ENTITY_FIELD_TYPE.ID.VALUE) {
        tableColumns.push({
          ...column,
          width: 200,
          render: (col: any, record: any, index: number) => {
            return (
              <Form.Item initialValue={col} field={`${id}.${index}.${column.dataIndex}`}>
                <Input size="small" />
              </Form.Item>
            );
          }
        });
      }
    }
    tableColumns.push({
      title: '操作',
      dataIndex: 'action',
      width: 100,
      render: (_col: any, record: any, index: number) => {
        return (
          <Button type="text" onClick={() => handleDelete(record.key)}>
            删除
          </Button>
        );
      }
    });
    setSubTableColumns(tableColumns);
  }, [columns]);

  return (
    <Layout className="XSubTable">
      <div className="subTableHeader">{label.text}</div>
      <div className="subTableContent">
        <Table columns={subTableColumns} data={subTableData} size="small" />
      </div>
      <div className="subTableFooter">
        <Button
          className="addButton"
          type="outline"
          icon={<IconPlus />}
          style={{ pointerEvents: runtime ? 'unset' : 'none', marginTop: 10 }}
          onClick={handleAdd}
        >
          新增一项
        </Button>
      </div>
    </Layout>
  );
};

export default XSubTable;
