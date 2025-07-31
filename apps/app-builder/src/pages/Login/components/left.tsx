import React from 'react';
import loginBg from '../../../assets/images/login_bg.svg';
import { useI18n } from '../../../hooks/useI18n';
import styles from '../index.module.less';

const Left: React.FC = () => {
  const { t } = useI18n();

  return (
    <div
      className={styles.loginPageLeft}
      style={{ backgroundImage: `url(${loginBg})` }}
    >
      <div className={styles.loginPageLeftContent}>
        <div className={styles.loginPageLeftTitle}>
          <h1>{t('auth.leftTitle1')}</h1>
          <h1>{t('auth.leftTitle2')}</h1>
        </div>
      </div>
    </div>
  );
};

export default Left;
