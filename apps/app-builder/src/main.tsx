import '@arco-design/web-react/dist/css/arco.css';

import { ConfigProvider } from '@arco-design/web-react';
import { ErrorPage, getAiGenURL, TokenManager, generateSignature } from '@onebase/common';
import { loadTheme } from '@onebase/ui-kit/src/utils/theme';
import { registerMicroApps, start } from 'qiankun';
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { ErrorBoundary } from 'react-error-boundary';
import App from './App.tsx';
import './i18n';
import './index.css';
import { initPlugins } from './plugin';

async function loadArcoTheme() {
  const rawTheme = (window as unknown as { global_config?: { THEME?: string } }).global_config?.THEME;
  const theme = rawTheme === 'lingji' ? 'lingji' : 'tiangong';
  console.log('[ThemeLoader] Loading arco theme:', theme);
  
  if (theme === 'lingji') {
    await import('@arco-themes/react-cyansu-ob03/index.less');
  } else {
    await import('@arco-themes/react-tiangong/index.less');
  }
}
const tokenInfo = TokenManager.getTokenInfo();
const tenantInfo = TokenManager.getTenantInfo();
registerMicroApps([
  {
    name: 'chat',
    entry: getAiGenURL(),
    container: '#ai-genapp-container',
    activeRule: (location) => location.hash.startsWith('#/aigen'),
    props: {
      generateSignature: generateSignature,
      tokenInfo: tokenInfo,
      tenantInfo: tenantInfo
    }
  }
]);

start();

async function init() {
  // 提前解析路由获取 tenantId 并初始化插件
  try {
    const hash = window.location.hash;
    // 匹配 #/onebase/:tenantId
    const match = hash.match(/^#\/onebase\/([^/?]+)/);
    if (match && match[1]) {
      const tenantId = match[1];
      console.log('[App Builder] Early init: Found tenantId', tenantId);
      TokenManager.setCurIdentifyId(tenantId);
      initPlugins();
    }
  } catch (e) {
    console.error('[App Builder] Early init failed:', e);
  }

  // 加载 Arco 主题（天工或灵畿）
  await loadArcoTheme();

  // 加载本地覆盖样式
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
