import Left from './components/left';
import Right from './components/right';
import styles from './index.module.less';

export default function Login() {
  return (
    <div className={styles.loginPage}>
      <Left />
      <Right />
    </div>
  );
}
