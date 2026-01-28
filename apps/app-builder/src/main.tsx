import '@arco-design/web-react/dist/css/arco.css';
import '@arco-themes/react-cyansu-ob03/index.less';
import '@onebase/ui-kit/src/theme.less';

import { ConfigProvider } from '@arco-design/web-react';
import { ErrorPage } from '@onebase/common';
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { ErrorBoundary } from 'react-error-boundary';
import { registerMicroApps, start, initGlobalState } from 'qiankun';
import App from './App.tsx';
import './i18n';
import './index.css';

const actions = initGlobalState({
  user: { name: 'admin' }
});
registerMicroApps([
  {
    name: 'chat',
    entry: 'http://localhost:7100', // 子应用运行端口
    container: '#subapp-container',
    activeRule: (location) => location.hash.startsWith('#/chat'),
    props: actions
  }
]);

start();

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ConfigProvider prefixCls="pc">
      <ErrorBoundary FallbackComponent={ErrorPage}>
        <App />
      </ErrorBoundary>
    </ConfigProvider>
  </StrictMode>
);
