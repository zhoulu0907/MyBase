import { TokenManager, UserPermissionManager, ProjectStorage } from '@onebase/common';
import type { NavigateFunction } from 'react-router-dom';
import { getPlatform } from '@/products';

export const logout = async (navigate: NavigateFunction) => {
  const loginURL = TokenManager.getTokenInfo()?.loginURL;
  const platform = getPlatform();

  // 灵畿平台销毁监督插件
  if (platform === 'lingji') {
    try {
      const { destroyPlugin } = await import('@onebase/product-lingji');
      destroyPlugin();
    } catch (e) {
      console.error('[Session] 销毁监督插件失败:', e);
    }
  }

  TokenManager.clearToken();
  UserPermissionManager.clearUserPermissionInfo();
  // 清除 projectCode
  ProjectStorage.remove();
  // 跳转到登录页

  if (loginURL) {
    window.location.href = loginURL;
  } else {
    navigate('/login', { replace: true });
  }
};
