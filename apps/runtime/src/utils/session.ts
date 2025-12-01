import { TokenManager } from '@onebase/common';
import type { NavigateFunction } from 'react-router-dom';
import { UserPermissionManager } from './permission';

export const logout = (navigate: NavigateFunction) => {
  const loginURL = TokenManager.getTokenInfo()?.loginURL;
  TokenManager.clearToken();
  UserPermissionManager.clearUserPermissionInfo();
  // 跳转到登录页

  if (loginURL) {
    window.location.href = loginURL;
  } else {
    navigate('/login', { replace: true });
  }
};
