import { UserPermissionManager } from '@onebase/common';
import { getFileUrlById } from '@onebase/platform-center';

export interface WorkbenchRuntimeUserInfo {
  avatar: string;
  name: string;
}

export function getWorkbenchRuntimeUserInfo(
  fallback?: Partial<WorkbenchRuntimeUserInfo>
): WorkbenchRuntimeUserInfo {
  const userPermissionInfo = UserPermissionManager.getUserPermissionInfo();
  const avatarId = userPermissionInfo?.user?.avatar;
  const avatarUrl = avatarId ? getFileUrlById(avatarId) || '' : '';
  const nickname = userPermissionInfo?.user?.nickname || '';

  return {
    avatar: avatarUrl || fallback?.avatar || '',
    name: nickname || fallback?.name || ''
  };
}
