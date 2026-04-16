import { useEffect } from 'react';
import { initSupervisionPlugin, isPluginInitialized, updatePageInfo, extractRouteInfo } from '../utils/supervision';
import { TokenManager } from '@onebase/common';

/**
 * 初始化灵畿平台（可在组件外部调用）
 */
export function initLingjiPlatform() {
  const tokenInfo = TokenManager.getToken();
  if (tokenInfo && !isPluginInitialized()) {
    // 尝试初始化，如果用户信息还未加载则重试
    const tryInit = (retries: number) => {
      if (retries <= 0) {
        console.log('[SupervisionPlugin] 初始化重试次数用尽');
        return;
      }

      // 检查配置是否加载
      const config = (window as any).supervision_config;
      if (!config) {
        console.log('[SupervisionPlugin] 配置未加载，等待重试...');
        setTimeout(() => tryInit(retries - 1), 500);
        return;
      }

      console.log('[SupervisionPlugin] 开始初始化...');
      initSupervisionPlugin().then((success) => {
        if (success) {
          // 初始化成功后，立即设置当前页面的编码
          const pathname = window.location.pathname;
          const { moduleCode, menuCode } = extractRouteInfo(pathname);
          updatePageInfo(moduleCode, menuCode);
          console.log('[SupervisionPlugin] 初始化后设置编码:', { moduleCode, menuCode });
        }
      });
    };

    tryInit(5);
  }
}

/**
 * 灵畿平台初始化 hook
 * 在用户登录后初始化监督插件
 */
export function usePlatformInit() {
  useEffect(() => {
    initLingjiPlatform();
  }, []);
}