import jsNodeIcon from '@/assets/flow/connect/js_node.svg';
import { Button, Dropdown, Menu, Tag, Typography } from '@arco-design/web-react';
import { IconMoreVertical } from '@arco-design/web-react/icon';
import { TypeCode, type ConnectInstance } from '@onebase/app';
import dayjs from 'dayjs';
import React from 'react';
import styles from './index.module.less';

/**
 * FlowCard 组件
 * 用于流程管理页面的卡片展示
 */
export interface CardProps {
  data: ConnectInstance;
  onEdit: Function;
  onDelete: Function;
}

const ConnectInstanceCard: React.FC<CardProps> = ({ data, onEdit, onDelete }) => {
  const handleEdit = (id: string) => {
    onEdit(id);
  };

  const handleDelete = (id: string) => {
    onDelete(id);
  };

  return (
    <div
      className={styles.card}
      onClick={() => {
        handleEdit(data.id);
      }}
    >
      <div className={styles.cardHeader}>
        <div className={styles.cardHeaderIcon}>
          {data.typeCode === TypeCode.SCRIPT && <img src={jsNodeIcon} alt="" />}
        </div>
        <div className={styles.cardHeaderContent}>
          <Typography.Text ellipsis={{ showTooltip: true }} className={styles.cardHeaderContentTitle}>
            {data.connectorName}
          </Typography.Text>
        </div>
      </div>
      <div className={styles.cardBody}>
        <div className={styles.cardBodyRow}>
          <div className={styles.cardBodyRowLabel}>关联类型</div>
          <div className={styles.cardBodyRowContent}>JavaScript脚本</div>
        </div>

        <div className={styles.cardBodyRow}>
          <div className={styles.cardBodyRowLabel}>创建时间</div>
          <div className={styles.cardBodyRowContent}>{dayjs(data.createTime).format('YYYY-MM-DD HH:mm:ss')}</div>
        </div>
      </div>
      <div
        className={styles.cardFooter}
        onClick={(e) => {
          e.stopPropagation();
        }}
      >
        <div className={styles.cardFooterLeft}>
          <Tag color="green">已启用</Tag>
        </div>
        <div className={styles.cardFooterRight}>
          <Button
            type="text"
            size="small"
            onClick={(e) => {
              e.stopPropagation();
              handleEdit(data.id);
            }}
          >
            编辑
          </Button>
          <div
            onClick={(e) => {
              e.stopPropagation();
            }}
          >
            <Dropdown
              position="bl"
              trigger="click"
              droplist={
                <Menu>
                  <Menu.Item
                    key="delete"
                    onClick={(e) => {
                      e.stopPropagation();
                      handleDelete(data.id);
                    }}
                  >
                    删除
                  </Menu.Item>
                </Menu>
              }
            >
              <IconMoreVertical className={styles.cardFooterRightBtn} />
            </Dropdown>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ConnectInstanceCard;
