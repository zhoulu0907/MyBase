// 过滤 Arco Design 的 findDOMNode 警告
const originalError = console.error;
console.error = (...args: any[]) => {
  if (args[0]?.includes?.('findDOMNode is deprecated')) return;
  originalError.apply(console, args);
};

import { ConfigProvider } from '@arco-design/web-react';
import { envConfig, ErrorPage, getAiGenURL, TokenManager, generateSignature, loadThemeAtPosition, loadTheme } from '@onebase/common';
import { registerMicroApps, start } from 'qiankun';
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { ErrorBoundary } from 'react-error-boundary';
import App from './App.tsx';
import './i18n';
import './index.css';
import { initPlugins } from './plugin';
import { getPlatformExports } from './products';

const ARCO_THEME_MAP = {
  lingji: () => import('@arco-themes/react-cyansu-ob03/index.less'),
  tiangong: () => import('@arco-themes/react-tiangong/index.less')
};

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
    const match = hash.match(/^#\/onebase\/([^/?]+)/);
    if (match && match[1]) {
      const tenantId = match[1];
      console.log('[App Builder] Early init: Found tenantId', tenantId);
      TokenManager.setCurIdentifyId(tenantId);

      const existingTenantId = TokenManager.getTenantInfo()?.tenantId;
      if (!existingTenantId) {
        TokenManager.setTenantId(tenantId);
        console.log('[App Builder] Set tenant_id from URL:', tenantId);
      }

      initPlugins();
    }
  } catch (e) {
    console.error('[App Builder] Early init failed:', e);
  }

  // 获取平台配置
  const platformExports = await getPlatformExports();
  const platformTheme = platformExports?.themeName || envConfig?.THEME || 'tiangong';
  const theme = platformTheme === 'lingji' ? 'lingji' : 'tiangong';

  console.log('[App Builder] Using theme:', theme, 'from platform:', platformExports?.config?.platform);

  // 先加载 Arco 基础样式
  await loadThemeAtPosition({
    theme: 'default',
    themeMap: {
      default: () => import('@arco-design/web-react/dist/css/arco.css')
    }
  });

  // 再加载主题样式
  await loadThemeAtPosition({
    theme,
    themeMap: ARCO_THEME_MAP,
    defaultTheme: 'tiangong'
  });

  // 加载本地覆盖样式
  await loadTheme({
    default: () => import('./themes/theme.less'),
    tiangong: () => import('./themes/theme_tiangong.less'),
    lingji: () => import('./themes/theme_lingji.less')
  }, theme);

  // 调用平台包的主题加载器（如果有额外逻辑）
  if (platformExports?.themeLoader) {
    await platformExports.themeLoader();
  }

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