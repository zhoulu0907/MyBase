/**
 * 灵畿产品包
 * 导出灵畿平台特有的组件、hooks、配置等
 */

// 导出配置
export { config } from './config';

// 导出 hooks 和初始化函数
export { usePlatformInit, initLingjiPlatform, useSupervisionPlugin } from './hooks';

// 导出组件（同时导出别名和原名）
export { LingjiCallback } from './components/Callback';
export { LingjiCallback as Callback } from './components/Callback';
export { LingjiAppCard as AppCard } from './components/AppCard';
export { LingjiLayout as Layout } from './components/Layout';
export { LingjiSider as Sider } from './components/Sider';
export { LingjiLogo } from './components/Logo';
export { LingjiLogo as Logo } from './components/Logo';

// 导出路由
export { PlatformRoutes } from './routes';

// 导出工具函数
export { initSupervisionPlugin, updatePageInfo, showPlugin, hidePlugin, destroyPlugin, isPluginInitialized, isPluginLoaded, extractRouteInfo } from './utils/supervision';

// 导出主题
export { themeLoader, themeName } from './styles';