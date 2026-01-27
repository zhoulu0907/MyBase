import { Typography } from '@arco-design/web-react';
import type { ConnectorItem } from '@onebase/app';
import React from 'react';
import styles from './index.module.less';

export interface ConnectorCardProps {
  data: ConnectorItem;
  isSelected?: boolean;
  onEdit?: (id: string) => void;
  onClick?: (data: ConnectorItem) => void;
}

const ConnectorCard: React.FC<ConnectorCardProps> = ({ data, isSelected, onClick }) => {
  const handleCardClick = () => {
    onClick?.(data);
  };

  return (
    <div
      className={`${styles.card} ${isSelected ? styles.selected : ''}`}
      onClick={handleCardClick}
    >
      <div className={styles.cardHeader}>
        <div className={styles.cardHeaderIcon}>{data.icon}</div>
        <div className={styles.cardHeaderContent}>
          <div className={styles.titleRow}>
            <Typography.Text ellipsis={{ showTooltip: true }} className={styles.cardHeaderContentTitle}>
              {data.name}
            </Typography.Text>
            <div className={`${styles.cardHeaderTagTitle} ${styles[data.type]}`}>
              {data.type === 'system_preset' ? '系统预设' : '自定义'}
            </div>
          </div>
          <Typography.Text ellipsis={{ showTooltip: true }} className={styles.cardHeaderContentDesc}>
            v{data.fields.version}
          </Typography.Text>
        </div>
      </div>

      <div className={styles.cardBody}>
        <div className={styles.cardBodyRow}>
          <div className={styles.cardBodyRowLabel}>服务类型:</div>
          <div className={styles.cardBodyRowContent}>{data.fields.serviceType || '-'}</div>
        </div>
        <div className={styles.cardBodyRow}>
          <div className={styles.cardBodyRowLabel}>默认参数:</div>
          <div className={styles.cardBodyRowContent} title={data.fields.defaultParams}>{data.fields.defaultParams || '-'}</div>
        </div>
        <div className={styles.cardBodyRow}>
          <div className={styles.cardBodyRowLabel}>认证方式:</div>
          <div className={styles.cardBodyRowContent}>{data.fields.authType}</div>
        </div>
        <div className={styles.cardBodyRow}>
          <div className={styles.cardBodyRowLabel}>实例数量:</div>
          <div className={styles.cardBodyRowContent}>{data.fields.instanceCount}</div>
        </div>
      </div>
    </div>
  );
};

export default ConnectorCard;
