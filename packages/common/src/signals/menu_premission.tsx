import { computed, signal } from '@preact/signals-react';
import { isRuntimeEnv, MenuPermission } from 'src/utils';

export const createMenuPermissionSignal = () => {
  const menuPermission = signal<MenuPermission | null>(null);
  const setMenuPermission = (newMenuPermission: MenuPermission) => {
    menuPermission.value = newMenuPermission;
  };

  const canCreate = computed(() => {
    return isRuntimeEnv() ? menuPermission.value?.operationPermission.canCreate : true;
  });

  const canEdit = computed(() => {
    return isRuntimeEnv() ? menuPermission.value?.operationPermission.canEdit : true;
  });

  const canDelete = computed(() => {
    return isRuntimeEnv() ? menuPermission.value?.operationPermission.canDelete : true;
  });

  const canImport = computed(() => {
    return isRuntimeEnv() ? menuPermission.value?.operationPermission.canImport : true;
  });

  const canExport = computed(() => {
    return isRuntimeEnv() ? menuPermission.value?.operationPermission.canExport : true;
  });

  const canShare = computed(() => {
    return isRuntimeEnv() ? menuPermission.value?.operationPermission.canShare : true;
  });

  return {
    menuPermission,
    setMenuPermission,
    canCreate,
    canEdit,
    canDelete,
    canImport,
    canExport,
    canShare
  };
};

export const menuPermissionSignal = createMenuPermissionSignal();
