import { TokenManager, UserPermissionManager, ProjectStorage } from '@onebase/common';
import type { NavigateFunction } from 'react-router-dom';
import { destroyPlugin } from './supervisionPlugin';

export const logout = (navigate: NavigateFunction) => {
  const loginURL = TokenManager.getTokenInfo()?.loginURL;

  // 销毁监督插件
  destroyPlugin();

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
