import type { UserPermissionInfo } from '@/utils/permission';
import { signal } from '@preact/signals-react';

export const createUserPermissionSignal = () => {
  const permissionInfo = signal<UserPermissionInfo | null>(null);
  const setPermissionInfo = (newPermissionInfo: UserPermissionInfo) => {
    permissionInfo.value = newPermissionInfo;
  };

  return {
    permissionInfo,
    setPermissionInfo
  };
};

// 创建默认的 store 实例（向后兼容）
export const userPermissionSignal = createUserPermissionSignal();
