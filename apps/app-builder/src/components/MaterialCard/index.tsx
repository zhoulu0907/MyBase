import React from 'react';
import { ICON_Map } from './icons';
import styles from './index.module.less';

interface MaterialCardProps {
  displayName: string;
  type: string;
  icon: string;
  id: string;
}

const MaterialCard: React.FC<MaterialCardProps> = ({ displayName, icon, type, id }) => {
  return (
    <div className={styles.materialCard} data-cp-type={type} data-cp-displayname={displayName} data-cp-id={id}>
      <div className={styles.icon}>{ICON_Map[icon]}</div>
      <div className={styles.text}>{displayName}</div>
    </div>
  );
};

export default MaterialCard;
