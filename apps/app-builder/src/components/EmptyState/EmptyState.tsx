import React from 'react';
import emptyTableSvg from '@/assets/images/empty-table.svg';
import emptyCardSvg from '@/assets/images/empty-card.svg';
import styles from './index.module.less';

export interface EmptyStateProps {
  /** 空状态类型：table 表格空状态，card 卡片空状态 */
  type?: 'table' | 'card';
  /** 描述文字 */
  description?: string;
  /** 自定义样式类名 */
  className?: string;
  /** 自定义样式 */
  style?: React.CSSProperties;
}

const EmptyState: React.FC<EmptyStateProps> = ({
  type = 'table',
  description = '暂无数据',
  className,
  style
}) => {
  const iconSrc = type === 'table' ? emptyTableSvg : emptyCardSvg;

  return (
    <div className={`${styles.emptyState} ${className || ''}`} style={style}>
      <img src={iconSrc} alt={description} className={styles.icon} />
      <span className={styles.description}>{description}</span>
    </div>
  );
};

export default EmptyState;