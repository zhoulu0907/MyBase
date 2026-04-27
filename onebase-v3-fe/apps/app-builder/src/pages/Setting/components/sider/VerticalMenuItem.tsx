import React from 'react';
import styles from './VerticalMenuItem.module.less';

export interface VerticalMenuItemProps {
  iconClass?: string;
  title: string;
  active?: boolean;
  onClick?: () => void;
  showDivider?: boolean;
}

const VerticalMenuItem: React.FC<VerticalMenuItemProps> = ({
  iconClass,
  title,
  active = false,
  onClick,
  showDivider = true
}) => {
  return (
    <div className={styles.menuItemWrapper}>
      <div className={`${styles.item} ${active ? styles.active : ''}`} onClick={onClick}>
        {iconClass && <span className={`${styles.icon} iconfont ${iconClass}`} />}
        <div className={styles.cell}>{title}</div>
      </div>
      {showDivider && <div className={styles.divider} />}
    </div>
  );
};

export default VerticalMenuItem;
