import React from 'react';
import loginBg from '../../../assets/images/login_bg.svg';
import styles from '../index.module.less';

const Left: React.FC = () => {
  return <div className={styles.loginPageLeft} style={{ backgroundImage: `url(${loginBg})` }}></div>;
};

export default Left;
