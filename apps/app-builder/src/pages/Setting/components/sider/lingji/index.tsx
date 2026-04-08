import { userPermissionSignal } from '@/store/singals/user_permission';
import { Button, Layout, Menu } from '@arco-design/web-react';
import { IconMenuFold, IconMenuUnfold } from '@arco-design/web-react/icon';
import { hasMenu } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useCallback, useMemo } from 'react';
import { useParams } from 'react-router-dom';
import { getMenuConfig, platMenuData, type MenuItemType } from '../../../utils/menuData';
import { useMenuSelection } from '../../../utils/useMenuSelection';
import styles from './index.module.less';

const { Sider } = Layout;
const MenuItem = Menu.Item;
const SubMenu = Menu.SubMenu;

interface LingjiSiderProps {
  className?: string;
  collapsed?: boolean;
  onCollapse?: (collapsed: boolean) => void;
}

const LingjiSider: React.FC<LingjiSiderProps> = ({ className, collapsed = false, onCollapse }) => {
  useSignals();

  const { permissionInfo } = userPermissionSignal;
  const permissionReady = !!permissionInfo.value;
  const { tenantId } = useParams();

  const menuConfig = useMemo(() => getMenuConfig(tenantId), [tenantId]);
  const finalMenuItems = useMemo(() => platMenuData(menuConfig), [menuConfig]);
  const { selectedKeys, onMenuClick } = useMenuSelection(finalMenuItems, permissionReady);

  const handleCollapseClick = useCallback(() => {
    if (onCollapse) {
      onCollapse(!collapsed);
    }
  }, [onCollapse, collapsed]);

  const defaultSelectedKeys = () => menuConfig.map((item) => item.key);

  const renderMenuItems = React.useCallback(
    (items: MenuItemType[]): React.ReactNode => {
      return items
        .map((item) => {
          const permissionKey = item.permissionKey;
          if (permissionReady && permissionKey && !hasMenu(permissionKey as any)) return null;

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
              style={collapsed ? { padding: '0 12px' } : { display: 'flex', alignItems: 'center' }}
            >
              <div className={styles.menuItemContent}>
                {selectedKeys.includes(item.key) ? item.iconActive : item.icon}
                <span className={styles.menuTitle}>{item.title}</span>
              </div>
            </MenuItem>
          );
        })
        .filter(Boolean);
    },
    [collapsed, permissionReady, selectedKeys]
  );

  return (
    <Sider
      className={`${styles.sider} ${className || ''}`}
      collapsed={collapsed}
      onCollapse={onCollapse}
      trigger={null}
      width={240}
    >
      <div className={styles.siderContent}>
        <div className={styles.sliderTitle}>AI+零代码开发平台</div>
        <div className={styles.menuContainer}>
          <Menu
            mode="vertical"
            autoOpen={true}
            selectedKeys={selectedKeys}
            defaultSelectedKeys={defaultSelectedKeys()}
            onClickMenuItem={onMenuClick}
          >
            {renderMenuItems(menuConfig)}
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

export default LingjiSider;
