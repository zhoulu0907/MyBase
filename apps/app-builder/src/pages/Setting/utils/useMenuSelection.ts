import { useEffect, useState, useCallback } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { findSelectedKeys, handleMenuClick, type MenuItemType } from './menuData';

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
