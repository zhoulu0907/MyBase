import { TokenManager, UserPermissionManager } from '@onebase/common';
import type { CorpDetailResponse } from '@onebase/platform-center';
import type { NavigateFunction } from 'react-router-dom';

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

export const TENANT_INFO_KEY = 'runtime_tenant_info';

export const getTenantInfoFromSession = (): CorpDetailResponse | null => {
  const tenantInfo = sessionStorage.getItem(TENANT_INFO_KEY);
  return !!tenantInfo ? JSON.parse(tenantInfo) : null;
};
export const setTenantInfoFromSession = (tenantInfo: CorpDetailResponse | null) => {
  sessionStorage.setItem(TENANT_INFO_KEY, JSON.stringify(tenantInfo));
};
