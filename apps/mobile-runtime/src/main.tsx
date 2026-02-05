// 导入Arco Design Mobile React的样式
import '@arco-design/mobile-react/dist/style.css';
// import '@arco-themes/react-cyansu-ob03/index.less';
// import '@onebase/ui-kit/src/theme.less';

import setRootPixel from '@arco-design/mobile-react/tools/flexible';
import { ErrorPage } from '@onebase/common';
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { ErrorBoundary } from 'react-error-boundary';
import './acro_reset.less';
import App from './App.tsx';
import './i18n';
import './index.css';

setRootPixel();
createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ErrorBoundary FallbackComponent={ErrorPage}>
      <App />
    </ErrorBoundary>
  </StrictMode>
);
