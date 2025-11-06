import { Divider } from '@arco-design/web-react';
import styles from './index.module.less';
import dayjs from 'dayjs';
import type { TableColumnProps } from '@arco-design/web-react';
import type { VersionData } from './indexType';

export const getVersionColumns = (
  handleView: (record: VersionData) => void,
  handleEditRemark: (record: VersionData) => void,
  handleDelete: (record: VersionData) => void
): TableColumnProps<VersionData>[] => [
  {
    key: 'version',
    title: '流程版本号',
    dataIndex: 'version'
  },
  {
    key: 'versionAlias',
    title: '流程版本备注',
    dataIndex: 'versionAlias',
    render: (text, record) => text + record.version
  },
  {
    key: 'versionStatus',
    title: '状态',
    dataIndex: 'versionStatus',
    render: (text: string) => {
      const classNameSelf = text === '已发布' ? 'published' : text === '设计中' ? 'designing' : 'history';
      return <span className={`${styles.versionStatus} ${styles[classNameSelf]}`}>{text}</span>;
    }
  },
  {
    key: 'creator',
    title: '创建人',
    dataIndex: 'creator',
    render: (record) => (
      <div className={styles.userCell}>
        <img src={record.avatar} className={styles.avatar} />
        <span>{record.name}</span>
      </div>
    )
  },
  {
    key: 'createTime',
    title: '创建时间',
    dataIndex: 'createTime',
    render: (text: string) => {
      return text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : '-';
    }
  },
  {
    key: 'updater',
    title: '更新人',
    dataIndex: 'updater',
    render: (record) => (
      <div className={styles.userCell}>
        {record ? (
          <>
            <img src={record.avatar} className={styles.avatar} />
            <span>{record.name}</span>
          </>
        ) : (
          <span>-</span>
        )}
      </div>
    )
  },
  {
    key: 'updateTime',
    title: '更新时间',
    dataIndex: 'updateTime',
    render: (text: string) => {
      return text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : '-';
    }
  },
  {
    key: 'operation',
    title: '操作',
    render: (_, record) => (
      <div className={styles.operation}>
        <span className={`${styles.operationBtn} ${styles.green}`} onClick={() => handleView(record)}>
          查看
        </span>
        <Divider type="vertical" />
        <span className={`${styles.operationBtn} ${styles.green}`} onClick={() => handleEditRemark(record)}>
          修改备注
        </span>
        <Divider type="vertical" />
        {record.versionStatus !== 'published' && (
          <span className={`${styles.operationBtn} ${styles.yellow}`} onClick={() => handleDelete(record)}>
            删除
          </span>
        )}
      </div>
    )
  }
];
