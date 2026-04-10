import React from 'react';
import loginBg from '../../../assets/images/login_bg.svg';
import styles from '../index.module.less';

const Left: React.FC = () => {
  return (
    <div className={styles.loginPageLeft}>
      <img src={loginBg} alt="loginBg" className={styles.loginBg} />
    </div>
  );
};

export default Left;
