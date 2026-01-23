import { Typography } from '@arco-design/web-react';
import type { ConnectorItem } from '@onebase/app';
import React from 'react';
import styles from './index.module.less';

export interface ConnectorCardProps {
  data: ConnectorItem;
  onEdit?: (id: string) => void;
  onClick?: (data: ConnectorItem) => void;
}

const ConnectorCard: React.FC<ConnectorCardProps> = ({ data, onEdit, onClick }) => {
  const handleCardClick = () => {
    onClick?.(data);
  };

  const handleEditClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    onEdit?.(data.id);
  };

  return (
    <div className={styles.card} onClick={handleCardClick}>
      <div className={styles.cardHeader}>
        <div className={styles.cardHeaderIcon}>{data.icon}</div>
        <div className={styles.cardHeaderContent}>
          <Typography.Text ellipsis={{ showTooltip: true }} className={styles.cardHeaderContentTitle}>
            {data.name}
          </Typography.Text>
          <Typography.Text ellipsis={{ showTooltip: true }} className={styles.cardHeaderContentDesc}>
            v{data.fields.version}
          </Typography.Text>
        </div>
        <div className={styles.cardHeaderTag}>
          <div className={`${styles.cardHeaderTagTitle} ${styles[data.type]}`}>
            {data.type === 'system_preset' ? '系统预设' : '自定义'}
          </div>
        </div>
      </div>

      <div className={styles.cardBody}>
        <div className={styles.cardBodyRow}>
          <div className={styles.cardBodyRowLabel}>服务类型</div>
          <div className={styles.cardBodyRowContent}>{data.fields.serviceType}</div>
        </div>
        <div className={styles.cardBodyRow}>
          <div className={styles.cardBodyRowLabel}>版本号</div>
          <div className={styles.cardBodyRowContent}>{data.fields.version}</div>
        </div>
        <div className={styles.cardBodyRow}>
          <div className={styles.cardBodyRowLabel}>认证方式</div>
          <div className={styles.cardBodyRowContent}>{data.fields.authType}</div>
        </div>
        <div className={styles.cardBodyRow}>
          <div className={styles.cardBodyRowLabel}>实例数量</div>
          <div className={styles.cardBodyRowContent}>{data.fields.instanceCount}</div>
        </div>
      </div>

      <div className={styles.cardFooter}>
        {data.canEdit && (
          <div className={styles.editButton} onClick={handleEditClick}>
            编辑
          </div>
        )}
      </div>
    </div>
  );
};

export default ConnectorCard;
