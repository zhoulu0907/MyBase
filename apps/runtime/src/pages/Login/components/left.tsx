import React from 'react';
import styles from '../index.module.less';

interface LeftProps {
  loginImageUrl?: string;
  defaultImage: string;
}

const Left: React.FC<LeftProps> = ({ loginImageUrl, defaultImage }) => {
  return (
    <div className={styles.loginPageLeft}>
      <img src={loginImageUrl || defaultImage} alt="loginBg" className={styles.loginBg} />
    </div>
  );
};

export default Left;
