import { Button, Layout, Menu } from '@arco-design/web-react';
import { IconDesktop, IconFile, IconMenuFold, IconMenuUnfold, IconSettings, IconUser } from '@arco-design/web-react/icon';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import type { MenuItemType } from './menuData';
import styles from './sider.module.less';

const { Sider } = Layout;
const MenuItem = Menu.Item;
const SubMenu = Menu.SubMenu;

interface SiderProps {
  className?: string;
  collapsed?: boolean;
  onCollapse?: (collapsed: boolean) => void;
  menuItems?: MenuItemType[];
}

const AppSider: React.FC<SiderProps> = ({
  className,
  collapsed = false,
  onCollapse,
  menuItems = []
}) => {
  const navigate = useNavigate();
  const location = useLocation();
  const [selectedKeys, setSelectedKeys] = useState<string[]>([]);

  // 默认菜单项
  const defaultMenuItems = useMemo(() => [
    {
        key: 'platform-info',
        title: '平台信息',
        icon: <IconDesktop />,
        path: '/onebase/platform-info',
    },
    {
        key: 'tenant',
        title: '租户管理',
        icon: <IconDesktop />,
        path: '/onebase/tenant',
    },
    {
        key: 'administrator',
        title: '平台管理员',
        icon: <IconUser />,
        path: '/onebase/administrator',
    },
    // {
    //   key: 'user',
    //   title: '用户管理',
    //   icon: <IconUser />,
    //   children: [
    //     {
    //       key: 'user-list',
    //       title: '用户列表',
    //       path: '/onebase/user/list',
    //     },
    //     {
    //       key: 'user-profile',
    //       title: '用户档案',
    //       path: '/onebase/user/profile',
    //     },
    //     {
    //       key: 'user-permissions',
    //       title: '权限管理',
    //       children: [
    //         {
    //           key: 'role-management',
    //           title: '角色管理',
    //           path: '/onebase/user/permissions/roles',
    //         },
    //         {
    //           key: 'permission-settings',
    //           title: '权限设置',
    //           path: '/onebase/user/permissions/settings',
    //         },
    //       ],
    //     },
    //   ],
    // },
    // {
    //   key: 'content',
    //   title: '内容管理',
    //   icon: <IconFile />,
    //   children: [
    //     {
    //       key: 'article',
    //       title: '文章管理',
    //       path: '/onebase/content/article',
    //     },
    //     {
    //       key: 'media',
    //       title: '媒体管理',
    //       path: '/onebase/content/media',
    //     },
    //   ],
    // },
    // {
    //   key: 'system',
    //   title: '系统设置',
    //   icon: <IconSettings />,
    //   children: [
    //     {
    //       key: 'general',
    //       title: '常规设置',
    //       path: '/onebase/system/general',
    //     },
    //     {
    //       key: 'security',
    //       title: '安全设置',
    //       path: '/onebase/system/security',
    //     },
    //   ],
    // },
  ], []);

  // 使用传入的菜单项或默认菜单项
  const finalMenuItems = useMemo(() => {
    return menuItems.length > 0 ? menuItems : defaultMenuItems;
  }, [menuItems, defaultMenuItems]);

  // 查找选中菜单项的函数
  const findSelectedKeys = React.useCallback((items: MenuItemType[], path: string): string[] => {
    for (const item of items) {
      if (item.path === path) {
        return [item.key];
      }
      if (item.children) {
        const childKeys = findSelectedKeys(item.children, path);
        if (childKeys.length > 0) {
          return [item.key, ...childKeys];
        }
      }
    }
    return [];
  }, []);

  // 根据当前路径设置选中的菜单项
  useEffect(() => {
    const keys = findSelectedKeys(finalMenuItems, location.pathname);
    setSelectedKeys(keys);

  }, [location.pathname, findSelectedKeys]);

  // 处理菜单点击
  const handleMenuClick = useCallback((key: string) => {
    const findPathByKey = (items: MenuItemType[], targetKey: string): string | null => {
      for (const item of items) {
        if (item.key === targetKey) {
          return item.path || null;
        }
        if (item.children) {
          const path = findPathByKey(item.children, targetKey);
          if (path) return path;
        }
      }
      return null;
    };

    const path = findPathByKey(finalMenuItems, key);
    if (path) {
      navigate(path);
    }
  }, [finalMenuItems, navigate]);

  // 处理折叠按钮点击
  const handleCollapseClick = useCallback(() => {
    if (onCollapse) {
      onCollapse(!collapsed);
    }
  }, [onCollapse, collapsed]);

  // 递归渲染菜单项
  const renderMenuItems = React.useCallback((items: MenuItemType[]): React.ReactNode => {
    return items.map((item) => {
      if (item.children && item.children.length > 0) {
        return (
          <SubMenu
            key={item.key}
            title={
              <span>
                {item.icon}
                <span className={styles.menuTitle}>{item.title}</span>
              </span>
            }
          >
            {renderMenuItems(item.children)}
          </SubMenu>
        );
      }

      return (
        <MenuItem
          key={item.key}
          disabled={item.disabled}
        >
          {item.icon}
          <span className={styles.menuTitle}>{item.title}</span>
        </MenuItem>
      );
    });
  }, []);

  return (
    <Sider
      className={`${styles.sider} ${className || ''}`}
      collapsed={collapsed}
      onCollapse={onCollapse}
      trigger={null}
      width={240}
      collapsedWidth={64}
    >
      <div className={styles.siderContent}>
        <div className={styles.menuContainer}>
          <Menu
            mode="vertical"
            selectedKeys={selectedKeys}
            onClickMenuItem={handleMenuClick}
            levelIndent={29}
          >
            {renderMenuItems(finalMenuItems)}
          </Menu>
        </div>

        <div className={styles.collapseButtonContainer}>
          <Button
            type="text"
            icon={collapsed ? <IconMenuUnfold /> : <IconMenuFold />}
            onClick={handleCollapseClick}
            className={styles.collapseButton}
          />
        </div>
      </div>
    </Sider>
  );
};

export default AppSider;