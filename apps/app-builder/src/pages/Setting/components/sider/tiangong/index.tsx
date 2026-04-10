import { userPermissionSignal } from '@/store/singals/user_permission';
import { hasMenu } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useMemo } from 'react';
import { useParams } from 'react-router-dom';
import { getMenuConfig, platMenuData, type MenuItemType } from '../../../utils/menuData';
import { useMenuSelection } from '../../../utils/useMenuSelection';
import VerticalMenuItem from '../VerticalMenuItem';
import styles from './index.module.less';

interface TiangongSiderProps {
  className?: string;
}

const TiangongSider: React.FC<TiangongSiderProps> = ({ className }) => {
  useSignals();

  const { permissionInfo } = userPermissionSignal;
  const permissionReady = !!permissionInfo.value;
  const { tenantId } = useParams();

  const menuConfig = useMemo(() => getMenuConfig(tenantId), [tenantId]);
  const finalMenuItems = useMemo(() => platMenuData(menuConfig), [menuConfig]);
  const { selectedKeys, onMenuClick } = useMenuSelection(finalMenuItems, permissionReady);

  const renderVerticalMenuItems = React.useCallback(
    (items: MenuItemType[]): React.ReactNode => {
      return items
        .map((item) => {
          const permissionKey = item.permissionKey;
          if (permissionReady && permissionKey && !hasMenu(permissionKey as any)) return null;

          if (item.children && item.children.length === 0) {
            return null;
          }

          const isActive = selectedKeys.includes(item.key);
          const iconClass = isActive && item.iconActiveClass ? item.iconActiveClass : item.iconClass;

          return (
            <VerticalMenuItem
              key={item.key}
              iconClass={iconClass}
              title={item.title}
              active={isActive}
              onClick={() => onMenuClick(item.key)}
              showDivider={false}
            />
          );
        })
        .filter(Boolean);
    },
    [permissionReady, selectedKeys, onMenuClick]
  );

  return (
    <div className={`${styles.verticalSider} ${className || ''}`}>
      <div className={styles.verticalSiderContent}>
        <div className={styles.verticalMenuWrapper}>
          <div className={styles.verticalMenuContainer}>
            {renderVerticalMenuItems(menuConfig)}
          </div>
        </div>
      </div>
    </div>
  );
};

export default TiangongSider;
