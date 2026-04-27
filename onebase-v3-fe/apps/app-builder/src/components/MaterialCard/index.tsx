import React from 'react';
import { ICON_Map } from './icons';
import styles from './index.module.less';

interface MaterialCardProps {
  displayName: string;
  type: string;
  icon: string;
  id: string;
  layout?: 'column' | 'row';
  disabled?: boolean;
}

const MaterialCard: React.FC<MaterialCardProps> = ({
  displayName,
  icon,
  type,
  id,
  layout = 'row',
  disabled = false
}) => {
  return (
    <div
      className={`${styles.materialCard} ${styles[layout]} ${disabled && 'disabled-drag'} `}
      data-cp-type={type}
      data-cp-displayname={displayName}
      data-cp-id={id}
      style={{ cursor: disabled ? 'not-allowed' : 'pointer' }}
    >
      <div className={styles.icon}>{ICON_Map[icon]}</div>
      <div className={styles.text}>{displayName}</div>
    </div>
  );
};

export default MaterialCard;
