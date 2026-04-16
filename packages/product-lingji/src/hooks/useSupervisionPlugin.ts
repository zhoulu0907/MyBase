import { useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { showPlugin, hidePlugin, isPluginInitialized, extractRouteInfo, updatePageInfo } from '../utils/supervision';

/**
 * 监督插件 hook
 * 提供监督插件的状态和操作方法
 */
export function useSupervisionPlugin() {
  const location = useLocation();

  // 路由埋点监听
  useEffect(() => {
    const { pathname } = location;

    // 判断是否为需要隐藏插件的页面
    const hidePages = ['/login', '/lingji-callback', '/oauth', '/tenant'];
    const shouldHide = hidePages.some(page => pathname.startsWith(page));

    if (shouldHide) {
      hidePlugin();
    } else if (isPluginInitialized()) {
      // 插件已初始化且不在隐藏页面，更新埋点信息
      showPlugin();
      const { moduleCode, menuCode } = extractRouteInfo(pathname);
      updatePageInfo(moduleCode, menuCode);
    }
  }, [location.pathname]);

  return {
    isInitialized: isPluginInitialized(),
    updatePageInfo,
    show: showPlugin,
    hide: hidePlugin,
  };
}