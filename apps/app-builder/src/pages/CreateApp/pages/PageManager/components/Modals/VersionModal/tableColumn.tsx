import { Divider, Button, Popconfirm } from '@arco-design/web-react';
import styles from './index.module.less';
import dayjs from 'dayjs';
import type { TableColumnProps } from '@arco-design/web-react';
import type { VersionData } from './indexType';

enum VersionStatus {
  DESIGNING = '设计中',
  PUBLISHED = '已发布',
  HISTORY = '历史版本'
}

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
    dataIndex: 'versionAlias'
  },
  {
    key: 'versionStatus',
    title: '状态',
    dataIndex: 'versionStatus',
    render: (text: string) => {
      const classNameSelf =
        text === VersionStatus.PUBLISHED ? 'published' : text === VersionStatus.DESIGNING ? 'designing' : 'history';
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
      <div>
        <Button type="text" status="success" onClick={() => handleView(record)}>
          查看
        </Button>
        <Divider type="vertical" />
        <Button type="text" status="success" onClick={() => handleEditRemark(record)}>
          修改备注
        </Button>

        {record.versionStatus !== VersionStatus.PUBLISHED && (
          <>
            <Popconfirm title="确定要删除此流程版本吗？" onOk={() => handleDelete(record)} focusLock>
              <Divider type="vertical" />
              <Button type="text" status="warning">
                删除
              </Button>
            </Popconfirm>
          </>
        )}
      </div>
    )
  }
];
