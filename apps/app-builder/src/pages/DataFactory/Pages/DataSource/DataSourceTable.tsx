import React from 'react';
import { Button, Table, type TableColumnProps, Space } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import styles from './index.module.less';

const data = [
  {
    key: '1',
    name: 'Jane Doe',
    salary: 23000,
    address: '32 Park Road, London',
    email: 'jane.doe@example.com',
  },
  {
    key: '2',
    name: 'Alisa Ross',
    salary: 25000,
    address: '35 Park Road, London',
    email: 'alisa.ross@example.com',
  },
  {
    key: '3',
    name: 'Kevin Sandra',
    salary: 22000,
    address: '31 Park Road, London',
    email: 'kevin.sandra@example.com',
  },
  {
    key: '4',
    name: 'Ed Hellen',
    salary: 17000,
    address: '42 Park Road, London',
    email: 'ed.hellen@example.com',
  },
  {
    key: '5',
    name: 'William Smith',
    salary: 27000,
    address: '62 Park Road, London',
    email: 'william.smith@example.com',
  },
];

const DataSourceTable = ({ 
  handlePageType, 
  onEdit 
}: { 
  handlePageType: (tab: string) => void;
  onEdit: (id: number) => void;
}) => {
  const gotoEdit = (id: number) => {
    onEdit(id);
  };

  const gotoDelete = () => {
    // TODO: 删除数据源
  };

  const columns: TableColumnProps[] = [
    {
      title: 'Name',
      dataIndex: 'name',
    },
    {
      title: 'Salary',
      dataIndex: 'salary',
    },
    {
      title: 'Address',
      dataIndex: 'address',
    },
    {
      title: 'Email',
      dataIndex: 'email',
    },
    {
      title: '操作',
      dataIndex: 'operation',
      render: (_, record: { key: string }) => (
        <Space>
          <Button type='text' size='mini' style={{ marginRight: 8 }} onClick={() => gotoEdit(parseInt(record.key))}>
            编辑
          </Button>
          <Button type='text' size='mini' onClick={() => gotoDelete()}>
            删除
          </Button>
        </Space>
      ),
      fixed: 'right',
      width: 100,
    }
  ];

  return (
    <div>
      <div className={styles.operationHeader}>
        <div className={styles.operationHeaderLeft}>
          数据源管理
        </div>
        <Button type='primary' onClick={() => {
          handlePageType('create-ds');
        }}>
          <IconPlus />
          创建数据源
        </Button>
      </div>
      <Table columns={columns} data={data} />
    </div>
  );
};

export default DataSourceTable;