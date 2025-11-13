import React from 'react';
import CustomNav from '@/pages/components/Nav';
import styles from './index.module.less';

const demoAvatarSrc = 'https://sf1-cdn-tos.toutiaostatic.com/obj/arco-mobile/_static_/small_image_5.jpg';

const Protocol: React.FC = () => {

  return (
    <div className={styles.about}>
      <CustomNav title="关于我们" />
      <div className={styles.container}>
        <div className={styles.basicInfo}>
          <img className={styles.icon} src={demoAvatarSrc} />
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
