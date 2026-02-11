import React from 'react';
import { IconArrowLeft } from '@arco-design/web-react/icon';
import styles from './header.module.less';

interface HeaderProps {
  title: string;
}

const BackPrevPage: React.FC<HeaderProps> = ({ title }) => {
  return (
    <div className={styles.backWrapper}>
      <div className={styles.backIconWrapper}>
        <IconArrowLeft onClick={() => history.go(-1)} />
      </div>
      <div className={styles.backTitle}>{title}</div>
    </div>
  );
};

export default BackPrevPage;
