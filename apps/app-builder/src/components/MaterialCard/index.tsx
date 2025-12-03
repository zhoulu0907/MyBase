import React from 'react';
import { ICON_Map } from './icons';
import styles from './index.module.less';

interface MaterialCardProps {
  displayName: string;
  type: string;
  icon: string;
  id: string;
  layout?: 'column' | 'row';
}

const MaterialCard: React.FC<MaterialCardProps> = ({ displayName, icon, type, id, layout = 'row' }) => {
  return (
    <div
      className={`${styles.materialCard} ${styles[layout]}`}
      data-cp-type={type}
      data-cp-displayname={displayName}
      data-cp-id={id}
    >
      <div className={styles.icon}>{ICON_Map[icon]}</div>
      <div className={styles.text}>{displayName}</div>
    </div>
  );
};

export default MaterialCard;
