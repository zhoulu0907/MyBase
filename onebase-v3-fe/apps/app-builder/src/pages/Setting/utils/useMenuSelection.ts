import { useEffect, useState, useCallback } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { findSelectedKeys, handleMenuClick, type MenuItemType } from './menuData';
import { getPlatform } from '@/products';

export const useMenuSelection = (
  finalMenuItems: MenuItemType[],
  permissionReady: boolean
) => {
  const navigate = useNavigate();
  const location = useLocation();
  const [selectedKeys, setSelectedKeys] = useState<string[]>([]);

  useEffect(() => {
    const keys = findSelectedKeys(finalMenuItems, location.pathname);
    setSelectedKeys(keys);
  }, [location.pathname, finalMenuItems]);

  // 灵畿平台一级菜单切换时更新监督插件埋点
  useEffect(() => {
    if (getPlatform() !== 'lingji' || selectedKeys.length === 0) return;

    import('@onebase/product-lingji').then(({ isPluginInitialized, updatePageInfo }) => {
      if (isPluginInitialized()) {
        // selectedKeys[0] 是当前选中的一级菜单 key
        const menuKey = selectedKeys[0];
        // 从配置中获取菜单编码
        const config = (window as any).supervision_config;
        const menuCode = config?.MENU_CODES?.[menuKey] || config?.DEFAULT_MENU_CODE;
        if (menuCode) {
          updatePageInfo(undefined, menuCode);
        }
      }
    });
  }, [selectedKeys]);

  const onMenuClick = useCallback(
    (key: string) => {
      handleMenuClick(key, finalMenuItems, navigate);
    },
    [finalMenuItems, navigate]
  );

  return {
    selectedKeys,
    onMenuClick
  };
};
