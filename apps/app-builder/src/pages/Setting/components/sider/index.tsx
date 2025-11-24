import corpSVG from '@/assets/images/building-line.svg';
import dictSVG from '@/assets/images/file.svg';
import organizationSVG from '@/assets/images/organization-chart.svg';
import tenantInfoSVG from '@/assets/images/space-ship-line.svg';
import appLicationManageSVG from '@/assets/images/terminal-window-line.svg';
import roleSVG from '@/assets/images/user.svg';
import userInfoSVG from '@/assets/images/userInfo.svg';
import vectorSVG from '@/assets/images/vector.svg';
import { TENANT_MENUS } from '@/constants/permission';
import { hasMenu } from '@/utils/permission';
import { Button, Layout, Menu } from '@arco-design/web-react';
import { IconMenuFold, IconMenuUnfold } from '@arco-design/web-react/icon';
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

interface MenuItemConfig {
  key: string;
  title: string;
  icon: React.ReactNode;
  path: string;
  permissionKey: string;
}

interface MenuGroupConfig {
  title: string;
  items: MenuItemConfig[];
}

const AppSider: React.FC<SiderProps> = ({ className, collapsed = false, onCollapse, menuItems = [] }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const [selectedKeys, setSelectedKeys] = useState<string[]>([]);
  const iconStyle = { fontSize: '18px' };
  // 默认菜单项
  const menuConfig: MenuGroupConfig[] = [
    {
      title: '应用管理',
      items: [
        {
          key: 'application',
          title: '应用管理',
          icon: <img src={appLicationManageSVG} />,
          path: '/onebase/setting/application',
          permissionKey: TENANT_MENUS.INFO
        }
      ]
    },
    {
      title: '用户与组织',
      items: [
        {
          key: 'user',
          title: '用户管理',
          icon: <img src={vectorSVG} />,
          path: '/onebase/setting/user',
          permissionKey: TENANT_MENUS.USER
        },
        {
          key: 'role',
          title: '角色管理',
          icon: <img src={roleSVG} />,
          path: '/onebase/setting/role',
          permissionKey: TENANT_MENUS.ROLE
        },
        {
          key: 'organization',
          title: '组织管理',
          icon: <img src={organizationSVG} />,
          path: '/onebase/setting/organization',
          permissionKey: TENANT_MENUS.DEPT
        }
      ]
    },
    {
      title: '系统配置',
      items: [
        {
          key: 'spaceInfo',
          title: '空间信息',
          icon: <img src={tenantInfoSVG} />,
          path: '/onebase/setting/spaceInfo',
          permissionKey: TENANT_MENUS.INFO
        },
        {
          key: 'system-dict',
          title: '数据字典管理',
          icon: <img src={dictSVG} />,
          path: '/onebase/setting/system-dict',
          permissionKey: TENANT_MENUS.DICT
        },
        {
          key: 'tenant',
          title: '个人中心',
          icon: <img src={userInfoSVG} />,
          path: '/onebase/setting/tenant',
          permissionKey: TENANT_MENUS.INFO
        }
      ]
    },
    {
      title: '扩展功能',
      items: [
        {
          key: 'enterprise',
          title: '企业管理',
          icon: <img src={corpSVG} />,
          path: '/onebase/setting/enterprise',
          permissionKey: TENANT_MENUS.CORP
        }
      ]
    }
  ];

  const platMenuData = () => {
    let result: any[] = [];
    menuConfig?.forEach((menu) => {
      if (menu.items?.length > 0) {
        menu.items.forEach((item) => {
          result.push(item);
        });
      }
    });
    return result;
  };

  // 使用传入的菜单项或默认菜单项
  const finalMenuItems = useMemo(() => {
    return menuItems.length > 0 ? menuItems : platMenuData();
  }, [menuItems, menuConfig]);

  // 查找选中菜单项的函数
  const findSelectedKeys = React.useCallback((items: MenuItemType[], path: string): string[] => {
    for (const item of items) {
      if (path === item.path || path.startsWith(item.path + '/')) {
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
  const handleMenuClick = (key: string) => {
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

    const path = findPathByKey(finalMenuItems, key.replace('.$', ''));
    if (path) {
      navigate(path);
    }
  };

  // 处理折叠按钮点击
  const handleCollapseClick = useCallback(() => {
    if (onCollapse) {
      onCollapse(!collapsed);
    }
  }, [onCollapse, collapsed]);

  // 递归渲染菜单项
  const renderMenuItems = React.useCallback(
    (items: MenuItemType[]): React.ReactNode => {
      return items
        .map((item) => {
          // TODO 后端返回数据暂未更新，暂不开启权限控制
          const permissionKey = item.permissionKey;
          if (permissionKey && !hasMenu(permissionKey as any)) return null;

          if (item.children && item.children.length > 0) {
            const childrenNodes = renderMenuItems(item.children) as React.ReactNode[];
            const hasChildren = Array.isArray(childrenNodes) && childrenNodes.filter(Boolean).length > 0;
            if (!hasChildren && !item.path) return null;
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
                {childrenNodes}
              </SubMenu>
            );
          }

          return (
            <MenuItem
              key={item.key}
              disabled={item.disabled}
              style={collapsed ? { padding: '0 10px' } : { display: 'flex', alignItems: 'center' }}
            >
              {item.icon}
              <span className={styles.menuTitle}>{item.title}</span>
            </MenuItem>
          );
        })
        .filter(Boolean);
    },
    [collapsed]
  );

  const renderContent = (data: MenuItemConfig[]) => {
    return (
      <Menu mode="vertical" selectedKeys={selectedKeys} onClickMenuItem={handleMenuClick} levelIndent={29}>
        {renderMenuItems(data)}
      </Menu>
    );
  };

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
          {menuConfig.map((group) => (
            <Menu.ItemGroup
              key={group.title}
              title={group.title}
              style={{ fontSize: '12px', color: '#999', marginBottom: '8px' }}
            >
              {renderContent(group.items)}
            </Menu.ItemGroup>
          ))}
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
