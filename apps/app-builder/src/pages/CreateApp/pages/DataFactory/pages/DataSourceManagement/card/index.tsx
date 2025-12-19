import { Dropdown, Menu, Typography } from '@arco-design/web-react';
import { IconMoreVertical } from '@arco-design/web-react/icon';
import { type ETLFlowMgmt } from '@onebase/app';
import dayjs from 'dayjs';
import React from 'react';

import styles from './index.module.less';
/**
 * ETLFlowCard 组件
 * 用于流程管理页面的卡片展示
 */
export interface DataSourceCardProps {
  data: ETLFlowMgmt;
  handleEdit: Function;
  handleDelete: Function;
  handlePage: Function;
}

const DataSourceCard: React.FC<DataSourceCardProps> = ({ data, handleEdit, handleDelete, handlePage }) => {
  return (
    <div className={styles.card}>
      <div className={styles.cardHeader}>
        <div className={styles.cardHeaderContentLeft}>
          <Typography.Text className={styles.cardHeaderContentTitle}>{data.flowName}</Typography.Text>

          <div className={styles.cardHeaderSubTitle}>
            <div className={styles.cardHeaderTag}></div>

            <div className={styles.cardHeaderSubTitleItem}>
              最后更新: {dayjs(data.lastSuccessTime).format('YYYY-MM-DD mm:ss')}
            </div>
          </div>
        </div>
      </div>
      <div className={styles.cardBody}>
        <div className={styles.cardBodyRow}></div>
      </div>
      <div className={styles.cardFooter}>
        <div className={styles.cardFooterLeft}></div>
        <div className={styles.cardFooterRight}>
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
