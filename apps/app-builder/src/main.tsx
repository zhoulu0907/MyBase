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

registerMicroApps([
  {
    name: 'chat',
    entry: getAiGenURL(),
    container: '#ai-genapp-container',
    activeRule: (location) => location.hash.startsWith('#/chat'),
  }
]);

start();

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ConfigProvider prefixCls="pc">
      <ErrorBoundary FallbackComponent={ErrorPage}>
        <App />
        <div id="ai-genapp-container"></div>
        <div id="copilot-container"></div>
      </ErrorBoundary>
    </ConfigProvider>
  </StrictMode>
);
