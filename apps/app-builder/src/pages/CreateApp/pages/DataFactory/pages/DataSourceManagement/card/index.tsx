import { Dropdown, Menu, Tag, Typography } from '@arco-design/web-react';
import { IconEdit, IconMoreVertical } from '@arco-design/web-react/icon';
import { type PageDatasourceItem } from '@onebase/app';
import React from 'react';

import styles from './index.module.less';
/**
 * Card 组件
 */
export interface DataSourceCardProps {
  data: PageDatasourceItem;
  handleEdit: Function;
  handleDelete: Function;
  handlePage: Function;
}

const DataSourceCard: React.FC<DataSourceCardProps> = ({ data, handleEdit, handleDelete, handlePage }) => {
  return (
    <div className={styles.card}>
      <div className={styles.cardHeader}>
        <div className={styles.cardHeaderContentLeft}>
          <Typography.Text className={styles.cardHeaderContentTitle}>{data.datasourceName || ''}</Typography.Text>
        </div>
        <div className={styles.cardHeaderContentRight}>
          <Tag color={data.readonly ? 'gray' : 'blue'}>{data.readonly ? '只读' : '读写'}</Tag>
        </div>
      </div>
      <div className={styles.cardBody}>
        <div className={styles.cardBodyRow}>
          <div className={styles.cardBodyRowLabel}> 数据源类型</div>
          <div className={styles.cardBodyRowContent}>{data.datasourceType || ''}</div>
        </div>
        <div className={styles.cardBodyRow}>
          <div className={styles.cardBodyRowLabel}> 采集状态</div>
          <div className={styles.cardBodyRowContent}>{data.collectStatus ? '成功' : '失败'}</div>
        </div>
      </div>
      <div className={styles.cardFooter}>
        <div className={styles.cardFooterLeft}></div>
        <div className={styles.cardFooterRight}>
          <div
            style={{ display: 'flex', alignItems: 'center', gap: 4, cursor: 'pointer' }}
            onClick={() => handleEdit(data.id)}
          >
            <IconEdit />
            <span>编辑</span>
          </div>
          <Dropdown
            position="bl"
            trigger="click"
            droplist={
              <Menu>
                <Menu.Item key="delete" onClick={() => handleDelete(data.id)}>
                  删除
                </Menu.Item>
              </Menu>
            }
          >
            <IconMoreVertical />
          </Dropdown>
        </div>
      </div>
    </div>
  );
};

export default DataSourceCard;
