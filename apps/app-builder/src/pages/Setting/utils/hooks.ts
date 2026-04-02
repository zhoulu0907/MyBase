import { useEffect, useState } from 'react';
import { useParams, useLocation } from 'react-router-dom';
import { TokenManager } from '@onebase/common';
import { getTenantInfo, type TenantInfo } from '@onebase/platform-center';
import { getTenantInfoFromSession, setTenantInfoFromSession } from '@/utils';

export const useIframeDetection = () => {
  const [isIframe, setIsIframe] = useState(false);

  useEffect(() => {
    setIsIframe(window.self !== window.top);
  }, []);

  useEffect(() => {
    if (isIframe) {
      const message = { timestamp: new Date().getTime(), type: 'loaded' };
      console.log('[Iframe] postMessage:', message);
      window.parent.postMessage(message, '*');
    }
  }, [isIframe]);

  return isIframe;
};

export const useIframeNavigation = (isIframe: boolean) => {
  const location = useLocation();

  useEffect(() => {
    if (isIframe) {
      const currentPath = location.pathname;
      const message = { timestamp: new Date().getTime(), type: 'redirect', url: currentPath };
      console.log('[Iframe] postMessage:', message);
      window.parent.postMessage(message, '*');
    }
  }, [location.pathname, isIframe]);
};

export const useTenantInfo = () => {
  const { tenantId } = useParams();
  const [tenantInfo, setTenantInfo] = useState<TenantInfo | null>(() => getTenantInfoFromSession());
  const tokenInfo = TokenManager.getTokenInfo();

  const handleTenantInfoChange = (info: TenantInfo) => {
    setTenantInfo(info);
    setTenantInfoFromSession(info);
  };

  useEffect(() => {
    if (tokenInfo?.accessToken) {
      getInfo();
    }
  }, [tokenInfo?.accessToken]);

  const getInfo = async () => {
    const tenantInfoRes = await getTenantInfo(tenantId || '');
    if (tenantInfoRes) {
      setTenantInfoFromSession(tenantInfoRes);
      setTenantInfo(tenantInfoRes);
    }
  };

  return {
    tenantInfo,
    handleTenantInfoChange
  };
};

export const useCollapsed = (initialState = false) => {
  const [collapsed, setCollapsed] = useState(initialState);

  const handleCollapse = (collapsed: boolean) => {
    setCollapsed(collapsed);
  };

  return {
    collapsed,
    handleCollapse
  };
};
