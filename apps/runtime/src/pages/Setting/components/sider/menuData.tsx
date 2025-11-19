import PlatformInfoSVG from '@/assets/images/platform-info.svg';
import { IconDashboard, IconFile, IconNotification, IconSettings, IconUser } from '@arco-design/web-react/icon';
import React from 'react';

// 菜单项类型定义
export interface MenuItemType {
  key: string;
  title: string;
  icon?: React.ReactNode;
  path?: string;
  children?: MenuItemType[];
  disabled?: boolean;
  permissionKey?: string;
}

// 示例：自定义菜单数据
export const customMenuItems: MenuItemType[] = [
  {
    key: 'platform-info',
    title: '平台信息',
    icon: <PlatformInfoSVG />,
    path: '/platform-info'
  },
  {
    key: 'analytics',
    title: '数据分析',
    icon: <IconDashboard />,
    children: [
      {
        key: 'user-analytics',
        title: '用户分析',
        path: '/analytics/user'
      },
      {
        key: 'business-analytics',
        title: '业务分析',
        path: '/analytics/business'
      },
      {
        key: 'realtime-analytics',
        title: '实时监控',
        path: '/analytics/realtime'
      }
    ]
  },
  {
    key: 'user-management',
    title: '用户管理',
    icon: <IconUser />,
    children: [
      {
        key: 'user-list',
        title: '用户列表',
        path: '/user/list'
      },
      {
        key: 'user-groups',
        title: '用户分组',
        path: '/user/groups'
      },
      {
        key: 'permissions',
        title: '权限管理',
        icon: <IconSettings />,
        children: [
          {
            key: 'role-management',
            title: '角色管理',
            path: '/user/permissions/roles'
          },
          {
            key: 'permission-settings',
            title: '权限设置',
            path: '/user/permissions/settings'
          },
          {
            key: 'access-control',
            title: '访问控制',
            path: '/user/permissions/access'
          }
        ]
      }
    ]
  },
  {
    key: 'content',
    title: '内容管理',
    icon: <IconFile />,
    children: [
      {
        key: 'articles',
        title: '文章管理',
        path: '/content/articles'
      },
      {
        key: 'media',
        title: '媒体管理',
        path: '/content/media'
      },
      {
        key: 'categories',
        title: '分类管理',
        path: '/content/categories'
      }
    ]
  },
  {
    key: 'notifications',
    title: '通知管理',
    icon: <IconNotification />,
    children: [
      {
        key: 'notification-list',
        title: '通知列表',
        path: '/notifications/list'
      },
      {
        key: 'notification-templates',
        title: '通知模板',
        path: '/notifications/templates'
      }
    ]
  },
  {
    key: 'system',
    title: '系统设置',
    icon: <IconSettings />,
    children: [
      {
        key: 'general-settings',
        title: '常规设置',
        path: '/system/general'
      },
      {
        key: 'security-settings',
        title: '安全设置',
        path: '/system/security'
      },
      {
        key: 'backup-restore',
        title: '备份恢复',
        path: '/system/backup'
      }
    ]
  }
];

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

// 示例：根据用户角色获取菜单
export const getMenuByRole = (role: string): MenuItemType[] => {
  switch (role) {
    case 'admin':
      return customMenuItems;
    case 'manager':
      return customMenuItems.filter((item) => !['system'].includes(item.key));
    case 'user':
      return customMenuItems.filter((item) => ['dashboard', 'analytics'].includes(item.key));
    default:
      return [];
  }
};
