import '@arco-design/web-react/dist/css/arco.css';
import '@arco-themes/react-cyansu-ob03/index.less';

import { ConfigProvider } from '@arco-design/web-react';
import { ErrorPage, getAiGenURL } from '@onebase/common';
import { loadTheme } from '@onebase/ui-kit/src/utils/theme';
import { registerMicroApps, start } from 'qiankun';
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { ErrorBoundary } from 'react-error-boundary';
import App from './App.tsx';
import './i18n';
import './index.css';

registerMicroApps([
  {
    name: 'chat',
    entry: getAiGenURL(),
    container: '#ai-genapp-container',
    activeRule: (location) => location.hash.startsWith('#/aigen')
  }
]);

start();

async function init() {
  await loadTheme({
    default: () => import('./themes/theme.less'),
    tiangong: () => import('./themes/theme_tiangong.less'),
    lingji: () => import('./themes/theme_lingji.less')
  });

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
}

init();
