import React from 'react';

export interface MenuItemType {
  key: string;
  title: string;
  icon?: React.ReactNode;
  iconActive?: React.ReactNode;
  iconClass?: string;
  iconActiveClass?: string;
  path?: string;
  children?: MenuItemType[];
  disabled?: boolean;
  permissionKey?: string;
}

// 示例：根据用户权限过滤菜单
export const filterMenuByPermissions = (menuItems: MenuItemType[], userPermissions: string[]): MenuItemType[] => {
  return menuItems.filter((item) => {
    // 检查当前菜单项是否有权限
    const hasPermission = !item.key.includes('admin') || userPermissions.includes('admin');

    if (!hasPermission) return false;

    // 递归过滤子菜单
    if (item.children) {
      const filteredChildren = filterMenuByPermissions(item.children, userPermissions);
      if (filteredChildren.length > 0) {
        return {
          ...item,
          children: filteredChildren
        };
      }
      return false;
    }

    return true;
  });
};
