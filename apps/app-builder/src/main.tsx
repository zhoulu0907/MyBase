import '@arco-design/web-react/dist/css/arco.css';
import '@arco-themes/react-cyansu-ob03/index.less';
import '@onebase/ui-kit/src/theme.less';

import { ConfigProvider } from '@arco-design/web-react';
import { ErrorPage } from '@onebase/common';
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { ErrorBoundary } from 'react-error-boundary';
import App from './App.tsx';
import './i18n';
import './index.css';

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ConfigProvider prefixCls="pc">
      <ErrorBoundary FallbackComponent={ErrorPage}>
        <App />
      </ErrorBoundary>
    </ConfigProvider>
  </StrictMode>
);
