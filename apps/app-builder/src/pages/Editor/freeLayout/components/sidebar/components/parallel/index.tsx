import { useState } from 'react';
import { Table, type TableColumnProps } from '@arco-design/web-react';
import Header from '../../../header';
import BottomBtn from '../../../bottomBtn';
import styles from './index.module.less';

const columns: TableColumnProps[] = [
  {
    title: '分支名称',
    dataIndex: 'name'
  },
  {
    title: '跳转至',
    dataIndex: 'salary'
  },
  {
    title: '默认分支',
    dataIndex: 'address'
  }
];
const data = [
  {
    key: '1',
    name: '年休假',
    salary: '执行人1',
    address: '否',
    email: '1'
  },
  {
    key: '3',
    name: '年休假',
    salary: '执行人2',
    address: '否',
    email: '2'
  },
  {
    key: '2',
    name: '/',
    salary: '执行人3',
    address: '是',
    email: '3'
  }
];
export default function ApproveDreawer() {
  const [editValue, setEditValue] = useState('');

  function handleSubmit() {
    // handleConfigSubmit(data);
  }
  return (
    <>
      <Header changeName={(name) => setEditValue(name)} />
      <div className={styles.parallel}>
        <div className={styles.configTitle}>
          并行分支<span className={styles.titleTips}>执行满足所有条件的分支</span>
        </div>
        <div className={styles.configContent}>
          <Table columns={columns} data={data} pagination={false} />
        </div>
      </div>
      <BottomBtn handleSubmit={handleSubmit} />
    </>
  );
}
