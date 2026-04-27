import { getCorpResourceById, UserPermissionManager } from '@onebase/common';
import { getFileUrlById } from '@onebase/platform-center';

export interface WorkbenchRuntimeUserInfo {
  avatar: string;
  name: string;
  runtime?: boolean;
}

export function getWorkbenchRuntimeUserInfo(
  fallback?: Partial<WorkbenchRuntimeUserInfo>
): WorkbenchRuntimeUserInfo {
  const userPermissionInfo = UserPermissionManager.getUserPermissionInfo();
  const avatarId = userPermissionInfo?.user?.avatar;
  const nickname = userPermissionInfo?.user?.nickname || '用户';

  let avatarUrl = '';
  if (avatarId) {
    const url = fallback?.runtime ? getCorpResourceById(avatarId) : getFileUrlById(avatarId);
    avatarUrl = url && !url.includes('undefined') ? url : '';
  }
  
  return {
    avatar: avatarUrl,
    name: nickname
  };
}
