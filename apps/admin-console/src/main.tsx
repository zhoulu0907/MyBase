import '@arco-design/web-react/dist/css/arco.css';

import { envConfig, loadThemeAtPosition, loadTheme } from '@onebase/common';
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import App from './App.tsx';
import './i18n';
import './index.css';

const ARCO_THEME_MAP = {
  lingji: () => import('@arco-themes/react-cyansu-ob03/index.less'),
  tiangong: () => import('@arco-themes/react-tiangong/index.less')
};

async function init() {
  // 加载 Arco 主题（天工或灵畿）
  const rawTheme = envConfig?.THEME;
  const theme = rawTheme === 'lingji' ? 'lingji' : 'tiangong';
  await loadThemeAtPosition({
    theme,
    themeMap: ARCO_THEME_MAP,
    defaultTheme: 'tiangong'
  });

  await loadTheme({
    default: () => import('./themes/theme.less'),
    tiangong: () => import('./themes/theme_tiangong.less'),
    lingji: () => import('./themes/theme_lingji.less')
  }, theme);

  createRoot(document.getElementById('root')!).render(
    <StrictMode>
      <App />
    </StrictMode>
  );
}

init();
