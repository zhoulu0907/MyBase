import { Button } from '@arco-design/web-react';
import type { FallbackProps } from 'react-error-boundary';
import styles from './index.module.less';

export default function ErrorPage({ error }: FallbackProps) {
  return (
    <div className={styles.errorPage}>
      <div className={styles.content}>
        <div className={styles.contentTitle}>500</div>
        <p>出错啦</p>
        <pre style={{ color: 'transparent' }}>{error.message}</pre>
        <Button type="secondary" onClick={() => (window.location.href = '/onebase/my-app')}>
          返回首页
        </Button>
      </div>
    </div>
  );
}
