import { Button, Layout, Menu } from '@arco-design/web-react';
import {
  IconApps,
  IconFile,
  IconIdcard,
  IconList,
  IconMenuFold,
  IconMenuUnfold,
  IconUserGroup
} from '@arco-design/web-react/icon';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import type { MenuItemType } from './menuData';
import styles from './sider.module.less';
import { hasMenu } from '@/utils/permission';
import { CORP_MENUS } from '@/constants/permission'

const { Sider } = Layout;
const MenuItem = Menu.Item;
const SubMenu = Menu.SubMenu;

interface SiderProps {
  className?: string;
  collapsed?: boolean;
  onCollapse?: (collapsed: boolean) => void;
  menuItems?: MenuItemType[];
}

const AppSider: React.FC<SiderProps> = ({ className, collapsed = false, onCollapse, menuItems = [] }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const [selectedKeys, setSelectedKeys] = useState<string[]>([]);
  const iconStyle = { fontSize: '18px' };
  // 默认菜单项
  const defaultMenuItems = useMemo(
    () => [
      {
        key: 'organization',
        title: '组织管理',
        icon: <IconList style={iconStyle} />,
        path: '/onebase/setting/organization',
        permissionKey: CORP_MENUS.DEPT
      },
      {
        key: 'user',
        title: '用户管理',
        icon: <IconUserGroup style={iconStyle} />,
        path: '/onebase/setting/user',
        permissionKey: CORP_MENUS.USER
      },
      {
        key: 'enterpriseInfo',
        title: '企业信息',
        icon: <IconIdcard style={iconStyle} />,
        path: '/onebase/setting/enterpriseInfo',
        permissionKey: CORP_MENUS.CORP_INFO
      },
      {
        key: 'authorized-application',
        title: '授权应用',
        icon: <IconIdcard style={iconStyle} />,
        path: '/onebase/setting/authorized-application',
        permissionKey: CORP_MENUS.AUTHORIZED
      },
      {
        key: 'tenant',
        title: '个人中心',
        icon: <IconIdcard style={iconStyle} />,
        path: '/onebase/setting/tenant',
        permissionKey: CORP_MENUS.PROFILE
      }
    ],
    []
  );

  // 使用传入的菜单项或默认菜单项
  const finalMenuItems = useMemo(() => {
    return menuItems.length > 0 ? menuItems : defaultMenuItems;
  }, [menuItems, defaultMenuItems]);

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
  const handleMenuClick = useCallback(
    (key: string) => {
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
    },
    [finalMenuItems, navigate]
  );

  // 处理折叠按钮点击
  const handleCollapseClick = useCallback(() => {
    if (onCollapse) {
      onCollapse(!collapsed);
    }
  }, [onCollapse, collapsed]);

  // 递归渲染菜单项
  const renderMenuItems = React.useCallback((items: MenuItemType[]): React.ReactNode => {
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
  }, [collapsed]);

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
          <Menu mode="vertical" selectedKeys={selectedKeys} onClickMenuItem={handleMenuClick} levelIndent={29}>
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
