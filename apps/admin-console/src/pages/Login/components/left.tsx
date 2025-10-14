import React from 'react';
import loginBg from '../../../assets/images/login_bg.svg';
import loginBgMask from '../../../assets/images/login_bg_mask.svg';
import styles from '../index.module.less';

const Left: React.FC = () => {
  return (
    <div className={styles.loginPageLeft} style={{ backgroundImage: `url(${loginBgMask})` }}>
      <img src={loginBg} alt="loginBg" className={styles.loginBg} />
    </div>
  );
};

export default Left;
