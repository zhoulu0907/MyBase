/**
 * 默认平台实现
 * 提供所有平台功能的空实现，供各平台包覆盖
 */

import React from 'react';
export { config } from './config';
export { usePlatformInit } from './hooks/usePlatformInit';
export { useSupervisionPlugin } from './hooks/useSupervisionPlugin';
export { LingjiCallback, TiangongOAuthCallback } from './components';
export { LingjiAppCard, TiangongAppCard } from './components';
export { LingjiLayout, TiangongLayout } from './components';
export { LingjiSider, TiangongSider } from './components';
export { VerticalMenuItem } from './components';
export { PlatformRoutes } from './routes';
export { themeLoader } from './styles';