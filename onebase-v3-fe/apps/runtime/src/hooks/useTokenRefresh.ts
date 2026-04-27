import { TokenManager } from '@onebase/common';
import { useEffect, useRef } from 'react';

/**
 * Token 自动刷新 Hook
 * 在 token 即将过期时自动刷新
 */
export const useTokenRefresh = () => {
  const refreshTimerRef = useRef<number | null>(null);

  useEffect(() => {
    const checkAndRefreshToken = async () => {
      const tokenInfo = TokenManager.getTokenInfo();

      if (!tokenInfo || !tokenInfo.expiresTime) {
        return;
      }

      // 计算距离过期的时间（毫秒）
      const timeUntilExpiry = tokenInfo.expiresTime - Date.now();

      // 如果距离过期时间少于5分钟，尝试刷新token
      if (timeUntilExpiry < 5 * 60 * 1000 && timeUntilExpiry > 0) {
        console.log('Token即将过期，尝试刷新...');

        try {
          const refreshed = await TokenManager.refreshToken();
          if (refreshed) {
            console.log('Token刷新成功');
          } else {
            console.log('Token刷新失败，需要重新登录');
            // 清除无效token
            TokenManager.clearToken();
            // 可以在这里触发重新登录逻辑
            window.location.href = '/login';
          }
        } catch (error) {
          console.error('Token刷新出错:', error);
          TokenManager.clearToken();
          window.location.href = '/login';
        }
      }
    };

    // 立即检查一次
    checkAndRefreshToken();

    // 设置定时器，每分钟检查一次
    refreshTimerRef.current = window.setInterval(checkAndRefreshToken, 60 * 1000);

    return () => {
      if (refreshTimerRef.current) {
        clearInterval(refreshTimerRef.current);
      }
    };
  }, []);

  return null;
};
