import { Button, Table, type TableColumnProps } from '@arco-design/web-react';
import styles from './index.module.less';

const columns: TableColumnProps[] = [
  {
    title: '序号',
    dataIndex: 'key',
    width: 100
  },
  {
    title: '姓名',
    dataIndex: 'name'
  },
  {
    title: '手机号',
    dataIndex: 'phone'
  },
  {
    title: '邮箱',
    dataIndex: 'email',
    ellipsis: true
  },
  {
    title: '部门',
    dataIndex: 'department'
  },
  {
    title: '操作',
    dataIndex: 'op',
    render: (_, _record) => (
      <Button onClick={() => {}} type="text">
        移除
      </Button>
    )
  }
];

const data = [
  {
    key: '1',
    name: 'Jane Doe',
    phone: 23000,
    email: 'jane.doe@example.com',
    department: '32 Park Road, London'
  },
  {
    key: '2',
    name: 'Alisa Ross',
    phone: 25000,
    email: 'alisa.ross@example.com',
    department: '35 Park Road, London'
  },
  {
    key: '3',
    name: 'Kevin Sandra',
    phone: 22000,
    email: 'kevin.sandra@example.com',
    department: '31 Park Road, London'
  },
  {
    key: '4',
    name: 'Ed Hellen',
    phone: 17000,
    email: 'ed.hellen@example.com',
    department: '42 Park Road, London'
  },
  {
    key: '5',
    name: 'William Smith',
    phone: 27000,
    email: 'william.smith@example.com',
    department: '62 Park Road, London'
  },
  {
    key: '6',
    name: 'William Smith',
    phone: 27000,
    email: 'william.smith@example.com',
    department: '62 Park Road, London'
  }
];

// 管理员面板
const Admin = () => {
  return <Table className={styles.table} columns={columns} data={data} />;
};

export default Admin;
