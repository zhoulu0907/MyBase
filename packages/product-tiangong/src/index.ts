/**
 * 天工产品包
 * 导出天工平台特有的组件、hooks、配置等
 */

// 导出配置
export { config } from './config';

// 导出组件（同时导出别名和原名）
export { TiangongOAuthCallback } from './components/OAuthCallback';
export { TiangongOAuthCallback as OAuthCallback } from './components/OAuthCallback';
export { TiangongAppCard as AppCard } from './components/AppCard';
export { TiangongLayout as Layout } from './components/Layout';
export { TiangongSider as Sider } from './components/Sider';
export { VerticalMenuItem } from './components/VerticalMenuItem';

// 导出路由
export { PlatformRoutes } from './routes';

// 导出主题
export { themeLoader, themeName } from './styles';