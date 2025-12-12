import corpSVG from '@/assets/images/building-line.svg';
import dictSVG from '@/assets/images/file.svg';
import organizationSVG from '@/assets/images/organization-chart.svg';
import securitySVG from '@/assets/images/security.svg';
import tenantInfoSVG from '@/assets/images/space-ship-line.svg';
import appLicationManageSVG from '@/assets/images/terminal-window-line.svg';
import userSVG from '@/assets/images/user-group.svg';
import roleSVG from '@/assets/images/user.svg';
import userInfoSVG from '@/assets/images/userInfo.svg';
import plugSVG from '@/assets/images/plug.svg';
import externalUserSVG from '@/assets/images/external_user.svg';
import { Button, Layout, Menu } from '@arco-design/web-react';
import { IconMenuFold, IconMenuUnfold } from '@arco-design/web-react/icon';
import { hasMenu, TENANT_MENUS } from '@onebase/common';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
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

interface MenuGroupConfig {
  title: string;
  key: string;
  children: MenuItemType[];
}

const AppSider: React.FC<SiderProps> = ({ className, collapsed = false, onCollapse, menuItems = [] }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const [selectedKeys, setSelectedKeys] = useState<string[]>([]);

  const { tenantId } = useParams();

  // 默认菜单项
  const menuConfig: MenuGroupConfig[] = [
    {
      key: 'application_management',
      title: '应用管理',
      children: [
        {
          key: 'application',
          title: '应用管理',
          icon: <img src={appLicationManageSVG} />,
          path: `/onebase/${tenantId}/setting/application`,
          permissionKey: TENANT_MENUS.APP
        }
      ]
    },
    {
      key: 'userAndOrigantion',
      title: '用户与组织',
      children: [
        {
          key: 'user',
          title: '用户管理',
          icon: <img src={userSVG} />,
          path: `/onebase/${tenantId}/setting/user`,
          permissionKey: TENANT_MENUS.USER
        },
        {
          key: 'role',
          title: '角色管理',
          icon: <img src={roleSVG} />,
          path: `/onebase/${tenantId}/setting/role`,
          permissionKey: TENANT_MENUS.ROLE
        },
        {
          key: 'organization',
          title: '组织管理',
          icon: <img src={organizationSVG} />,
          path: `/onebase/${tenantId}/setting/organization`,
          permissionKey: TENANT_MENUS.DEPT
        }
      ]
    },
    {
      key: 'systemConfig',
      title: '系统配置',
      children: [
        {
          key: 'spaceInfo',
          title: '空间信息',
          icon: <img src={tenantInfoSVG} />,
          path: `/onebase/${tenantId}/setting/spaceInfo`,
          permissionKey: TENANT_MENUS.INFO
        },
        {
          key: 'system-dict',
          title: '数据字典管理',
          icon: <img src={dictSVG} />,
          path: `/onebase/${tenantId}/setting/system-dict`,
          permissionKey: TENANT_MENUS.DICT
        },
        {
          key: 'security',
          title: '安全设置',
          icon: <img src={securitySVG} />,
          path: `/onebase/${tenantId}/setting/security`,
          permissionKey: TENANT_MENUS.SECURITY
        },
        {
          key: 'profile',
          title: '个人中心',
          icon: <img src={userInfoSVG} />,
          path: `/onebase/${tenantId}/setting/profile`,
          permissionKey: TENANT_MENUS.PROFILE
        }
      ]
    },
    {
      key: 'extraFunction',
      title: '扩展功能',
      children: [
        {
          key: 'enterprise',
          title: '插件管理',
          icon: <img src={plugSVG} />,
          path: `/onebase/${tenantId}/setting/plugin`,
          permissionKey: TENANT_MENUS.CORP
        },
        {
          key: 'externalUser',
          title: '外部用户',
          icon: <img src={externalUserSVG} />,
          path: `/onebase/${tenantId}/setting/externalUser`,
          permissionKey: TENANT_MENUS.CORP
        },
        {
          key: 'enterprise',
          title: '企业管理',
          icon: <img src={corpSVG} />,
          path: `/onebase/${tenantId}/setting/enterprise`,
          permissionKey: TENANT_MENUS.CORP
        }
      ]
    }
  ];

  const getDefaultKeys = () => {
    let defaultOpenKeys: string[] = [];
    let defaultSelectedKeys: string[] = [];
    menuConfig.map((item) => {
      defaultOpenKeys.push(item.key);
      item.children?.map((child) => {
        defaultSelectedKeys.push(child.key);
      });
    });
    return { defaultOpenKeys, defaultSelectedKeys };
  };

  const platMenuData = () => {
    let result: MenuItemType[] = [];
    menuConfig?.forEach((menu) => {
      if (menu.children?.length > 0) {
        menu.children.forEach((item) => {
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

          // 如果 children length === 0，则不渲染这个菜单
          if (item.children && item.children.length === 0) {
            return null;
          }

          if (item.children && item.children.length > 0) {
            const childrenNodes = renderMenuItems(item.children) as React.ReactNode[];
            const hasChildren = Array.isArray(childrenNodes) && childrenNodes.filter(Boolean).length > 0;
            if (!hasChildren && !item.path) return null;
            return (
              <SubMenu
                key={item.key}
                className={styles.subMenuWrapper}
                title={
                  <span>
                    {item.icon}
                    <span>{item.title}</span>
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
              className={styles.menuItemWrapper}
              style={collapsed ? { padding: '0 10px' } : { display: 'flex', alignItems: 'center' }}
            >
              <div className={styles.menuItemWrapper}>
                {item.icon}
                <span className={styles.menuTitle}>{item.title}</span>
              </div>
            </MenuItem>
          );
        })
        .filter(Boolean);
    },
    [collapsed]
  );

  const defaultKeys = getDefaultKeys();

  const renderContent = () => {
    return (
      <Menu
        mode="vertical"
        selectedKeys={selectedKeys}
        defaultOpenKeys={defaultKeys.defaultOpenKeys}
        defaultSelectedKeys={defaultKeys.defaultSelectedKeys}
        onClickMenuItem={handleMenuClick}
      >
        {renderMenuItems(menuConfig)}
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
        <div className={styles.menuContainer}>{renderContent()}</div>

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
