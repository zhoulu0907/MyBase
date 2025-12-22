import styles from './index.module.less';
import Left from './left';
import Right from './right';

export default function ThirdLogin() {
  return (
    <div className={styles.loginPage}>
      <Left />
      <Right />
    </div>
  );
}
