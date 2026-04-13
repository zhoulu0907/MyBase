import React from 'react';
import styles from '../index.module.less';

interface LeftProps {
  loginImageUrl?: string;
  defaultImage: string;
  configLoaded: boolean;
}

const Left: React.FC<LeftProps> = ({ loginImageUrl, defaultImage, configLoaded }) => {
  if (!configLoaded) {
    return <div className={styles.loginPageLeft}></div>;
  }

  return (
    <div className={styles.loginPageLeft}>
      <img src={loginImageUrl || defaultImage} alt="loginBg" className={styles.loginBg} />
    </div>
  );
};

export default Left;
