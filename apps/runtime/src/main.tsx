import '@arco-design/web-react/dist/css/arco.css';
import '@arco-themes/react-cyansu-ob03/index.less';

import { ConfigProvider } from '@arco-design/web-react';
import { envConfig, TokenManager } from '@onebase/common';
import { loadTheme } from '@onebase/ui-kit/src/utils/theme';
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import App from './App.tsx';
import './i18n';
import './index.css';
import { initPlugins } from './plugin';

async function loadArcoTheme() {
  const rawTheme = envConfig?.THEME;
  const theme = rawTheme === 'lingji' ? 'lingji' : 'tiangong';
  console.log('[ThemeLoader] Loading arco theme:', theme);
  
  if (theme === 'lingji') {
    await import('@arco-themes/react-cyansu-ob03/index.less');
  } else {
    await import('@arco-themes/react-tiangong/index.less');
  }
}

async function init() {
  // 提前解析路由获取 tenantId/appId 并初始化插件
  try {
    const hash = window.location.hash;
    // 匹配 #/onebase/:tenantId[/possibleAppId]
    const match = hash.match(/^#\/onebase\/([^/?]+)(?:\/([^/?]+))?/);
    if (match && match[1]) {
      const tenantId = match[1];
      const possibleAppId = match[2];
      
      let appId = '';
      if (possibleAppId && !['runtime', 'setting', 'runtime-dev'].includes(possibleAppId)) {
        appId = possibleAppId;
      }
      
      console.log('[Runtime] Early init: Found tenantId', tenantId, 'appId', appId);
      
      if (appId && tenantId) {
        TokenManager.setCurIdentifyId(`${appId}_${tenantId}`);
      } else if (tenantId) {
        TokenManager.setCurIdentifyId(tenantId);
      } else if (appId) {
        TokenManager.setCurIdentifyId(appId);
      }
      
      initPlugins();
    }
  } catch (e) {
    console.error('[Runtime] Early init failed:', e);
  }

  await loadArcoTheme();

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
