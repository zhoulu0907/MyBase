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
    title: '上游分支',
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
  function handleSubmit() {
    // handleConfigSubmit(data);
  }
  return (
    <>
      <Header />
      <div className={styles.sink}>
        <div className={styles.configTitle}>
          上游分支<span className={styles.titleTips}>等待所有应到达的上游分支完成，合并为一条路径后继续流程</span>
        </div>
        <div className={styles.configContent}>
          <Table columns={columns} data={data} pagination={false} />
        </div>
      </div>
      <BottomBtn handleSubmit={handleSubmit} />
    </>
  );
}
