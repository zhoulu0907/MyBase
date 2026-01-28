import '@arco-design/web-react/dist/css/arco.css';
import '@arco-themes/react-cyansu-ob03/index.less';
import '@onebase/ui-kit/src/theme.less';

import { ConfigProvider } from '@arco-design/web-react';
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import DynamicFormDebug from './pages/Debug/DynamicFormDebug';
import './index.css';

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ConfigProvider prefixCls="pc">
      <DynamicFormDebug />
    </ConfigProvider>
  </StrictMode>
);
