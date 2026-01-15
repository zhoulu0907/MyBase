import React from 'react';
import { CustomNav } from '@onebase/ui-kit-mobile';
import logoIcon from '@/assets/images/logo-icon.svg';
import styles from './index.module.less';

const Protocol: React.FC = () => {
  return (
    <div className={styles.about}>
      <CustomNav title="关于我们" style={{ backgroundColor: 'transparent' }} />
      <div className={styles.container}>
        <div className={styles.basicInfo}>
          <img className={styles.icon} src={logoIcon} />
          <div className={styles.name}>OneBase</div>
          <div className={styles.version}>当前版本 3.0.6</div>
        </div>
        <div className={styles.desc}>
          数智化底座<br />
          AI + 零代码<br />
          让企业信息服务更简单
        </div>
        <div className={styles.copyright}>
          copyright &copy; {new Date().getFullYear()} onebase.com版权所有
        </div>
      </div>
      <div className={styles.leftBg} />
      <div className={styles.rightBg} />
    </div>
  );
};

export default Protocol;
