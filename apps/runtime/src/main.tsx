import '@arco-design/web-react/dist/css/arco.css';
import '@arco-themes/react-cyansu-ob03/index.less';

import { ConfigProvider } from '@arco-design/web-react';
import { loadTheme } from '@onebase/ui-kit/src/utils/theme';
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import App from './App.tsx';
import './i18n';
import './index.css';
import { initPlugins } from './plugin';

initPlugins();

async function init() {
  await loadTheme({
    default: () => import('./themes/theme.less'),
    tiangong: () => import('./themes/theme_tiangong.less'),
    lingji: () => import('./themes/theme_lingji.less')
  });

  createRoot(document.getElementById('root')!).render(
    <StrictMode>
      <ConfigProvider prefixCls="pc">
        {/* <ErrorBoundary FallbackComponent={ErrorPage}> */}
        <App />
        {/* </ErrorBoundary> */}
      </ConfigProvider>
    </StrictMode>
  );
}

init();
