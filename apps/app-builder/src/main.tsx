import '@arco-design/web-react/dist/css/arco.css';
import '@arco-themes/react-cyansu-ob03/index.less';
import '@onebase/ui-kit/src/theme.less';

import { ConfigProvider } from '@arco-design/web-react';
import { ErrorPage } from '@onebase/common';
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { ErrorBoundary } from 'react-error-boundary';
import { registerMicroApps, start, initGlobalState } from 'qiankun';
import { loadMicroApp, type MicroApp } from 'qiankun';
import { getAiGenURL } from '@onebase/common';
import App from './App.tsx';
import './i18n';
import './index.css';

const actions = initGlobalState({
  user: { name: 'admin' }
});
const copilotActions = initGlobalState({
  user: { id: '123123' }
});

registerMicroApps([
  {
    name: 'chat',
    entry: getAiGenURL(), // 子应用运行端口
    container: '#chat-container',
    activeRule: (location) => location.hash.startsWith('#/chat'),
    props: actions
  }
]);

// loadMicroApp({
//   name: 'copilot',
//   entry: 'http://localhost:8888',
//   container: '#copilot-container',
//   props: copilotActions
// });

start();

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ConfigProvider prefixCls="pc">
      <ErrorBoundary FallbackComponent={ErrorPage}>
        <App />
        <div id="chat-container"></div>
        <div id="copilot-container"></div>
      </ErrorBoundary>
    </ConfigProvider>
  </StrictMode>
);
