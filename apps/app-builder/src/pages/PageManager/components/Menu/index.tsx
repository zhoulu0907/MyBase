import { Button, Menu } from '@arco-design/web-react';
import { IconMenuFold, IconMenuUnfold } from '@arco-design/web-react/icon';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import styles from './index.module.less';

const MenuItem = Menu.Item;
const SubMenu = Menu.SubMenu;

// 菜单项类型定义
export interface MenuItemType {
  key: string;
  title: string;
  icon?: React.ReactNode;
  path?: string;
  children?: MenuItemType[];
  disabled?: boolean;
}

// 菜单组件属性接口
export interface MenuComponentProps {
  /** 菜单数据 */
  menuItems?: MenuItemType[];
  /** 是否折叠 */
  collapsed?: boolean;
  /** 折叠状态改变回调 */
  onCollapse?: (collapsed: boolean) => void;
  /** 菜单点击回调 */
  onMenuClick?: (key: string, item: MenuItemType) => void;
  /** 选中的菜单项 */
  selectedKeys?: string[];
  /** 展开的子菜单 */
  openKeys?: string[];
  /** 展开子菜单改变回调 */
  onOpenChange?: (openKeys: string[]) => void;
  /** 自定义类名 */
  className?: string;
  /** 菜单模式 */
  mode?: 'vertical' | 'horizontal' | 'pop';
  /** 是否显示折叠按钮 */
  showCollapseButton?: boolean;
  /** 菜单宽度 */
  width?: number;
  /** 折叠时宽度 */
  collapsedWidth?: number;
}

const MenuComponent: React.FC<MenuComponentProps> = ({
  menuItems = [],
  collapsed = false,
  onCollapse,
  onMenuClick,
  selectedKeys = [],
  openKeys = [],
  onOpenChange,
  className = '',
  mode = 'vertical',
  showCollapseButton = true,
  width = 240,
  collapsedWidth = 64
}) => {
  // 内部状态管理
  const [internalSelectedKeys, setInternalSelectedKeys] = useState<string[]>(selectedKeys);
  const [internalOpenKeys, setInternalOpenKeys] = useState<string[]>(openKeys);
  const [internalCollapsed, setInternalCollapsed] = useState(collapsed);

  // 同步外部状态
  useEffect(() => {
    setInternalSelectedKeys(selectedKeys);
  }, [selectedKeys]);

  useEffect(() => {
    setInternalOpenKeys(openKeys);
  }, [openKeys]);

  useEffect(() => {
    setInternalCollapsed(collapsed);
  }, [collapsed]);

  // 处理菜单点击
  const handleMenuClick = useCallback(
    (key: string) => {
      // 查找对应的菜单项
      const findMenuItem = (items: MenuItemType[], targetKey: string): MenuItemType | null => {
        for (const item of items) {
          if (item.key === targetKey) {
            return item;
          }
          if (item.children) {
            const found = findMenuItem(item.children, targetKey);
            if (found) return found;
          }
        }
        return null;
      };

      const menuItem = findMenuItem(menuItems, key);
      if (menuItem) {
        setInternalSelectedKeys([key]);
        onMenuClick?.(key, menuItem);
      }
    },
    [menuItems, onMenuClick]
  );

  // 处理子菜单展开/收缩
  const handleOpenChange = useCallback(
    (keys: string[]) => {
      setInternalOpenKeys(keys);
      onOpenChange?.(keys);
    },
    [onOpenChange]
  );

  // 处理折叠按钮点击
  const handleCollapseClick = useCallback(() => {
    const newCollapsed = !internalCollapsed;
    setInternalCollapsed(newCollapsed);
    onCollapse?.(newCollapsed);
  }, [internalCollapsed, onCollapse]);

  // 递归渲染菜单项
  const renderMenuItems = useCallback(
    (items: MenuItemType[]): React.ReactNode => {
      return items.map((item) => {
        if (item.children && item.children.length > 0) {
          return (
            <SubMenu
              key={item.key}
              title={
                <span className={styles.menuItemTitle}>
                  {item.icon && <span className={styles.menuIcon}>{item.icon}</span>}
                  {!internalCollapsed && <span className={styles.menuText}>{item.title}</span>}
                </span>
              }
            >
              {renderMenuItems(item.children)}
            </SubMenu>
          );
        }

        return (
          <MenuItem key={item.key} disabled={item.disabled}>
            <span className={styles.menuItemTitle}>
              {item.icon && <span className={styles.menuIcon}>{item.icon}</span>}
              {!internalCollapsed && <span className={styles.menuText}>{item.title}</span>}
            </span>
          </MenuItem>
        );
      });
    },
    [internalCollapsed]
  );

  // 计算菜单容器样式
  const menuContainerStyle = useMemo(() => {
    return {
      width: internalCollapsed ? collapsedWidth : width,
      transition: 'width 0.2s ease-in-out'
    };
  }, [internalCollapsed, width, collapsedWidth]);

  return (
    <div className={`${styles.menuContainer} ${className}`} style={menuContainerStyle}>
      <div className={styles.menuWrapper}>
        <Menu
          mode={mode}
          selectedKeys={internalSelectedKeys}
          openKeys={internalOpenKeys}
          onClickMenuItem={handleMenuClick}
          levelIndent={internalCollapsed ? 0 : 29}
          className={styles.menu}
        >
          {renderMenuItems(menuItems)}
        </Menu>
      </div>

      {showCollapseButton && (
        <div className={styles.collapseButtonContainer}>
          <Button
            type="text"
            icon={internalCollapsed ? <IconMenuUnfold /> : <IconMenuFold />}
            onClick={handleCollapseClick}
            className={styles.collapseButton}
            title={internalCollapsed ? '展开菜单' : '收起菜单'}
          />
        </div>
      )}
    </div>
  );
};

export default MenuComponent;
