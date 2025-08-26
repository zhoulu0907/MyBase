import { Button } from '@arco-design/web-react';
import type { FallbackProps } from 'react-error-boundary';
import './index.css';

export function ErrorPage({ error }: FallbackProps) {
  return (
    <div className="errorPage">
      <div className="content">
        <div className="contentTitle">500</div>
        <p>出错啦</p>
        <pre style={{ color: 'transparent' }}>{error.message}</pre>
        <Button type="secondary" onClick={() => (window.location.href = '/onebase/my-app')}>
          返回首页
        </Button>
      </div>
    </div>
  );
}
