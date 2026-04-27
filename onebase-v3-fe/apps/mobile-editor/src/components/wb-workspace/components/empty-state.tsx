import EmptyIcon from '@/assets/images/empty.svg';
import styles from '../index.module.less';

/**
 * 空状态组件
 */
export function EmptyState() {
  return (
    <div className={styles.formEmpty}>
      <div className={styles.formEmptyContent}>
        <img src={EmptyIcon} alt="页面无组件" />
        拖拽左侧面板里的组件到这里
        <br />
        开始使用吧！
      </div>
    </div>
  );
}
