import { TokenManager } from '@onebase/common';
import { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

/**
 * 登录状态检查 Hook
 * 在应用启动时检查token状态，决定是否跳转
 */
export const useAuthCheck = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [isChecking, setIsChecking] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    const checkAuthStatus = () => {
      try {
        // 检查token是否有效
        const isValid = TokenManager.isTokenValid();
        setIsAuthenticated(isValid);

        if (isValid) {
          // 已登录
          console.log('用户已登录');

          // 如果在登录页面，重定向到首页
          if (location.pathname === '/login') {
            navigate('/onebase', { replace: true });
          }
        } else {
          // 未登录
          console.log('用户未登录');

          // 清除可能存在的无效token
          TokenManager.clearToken();

          // 如果不在登录页面，重定向到登录页
          if (location.pathname !== '/login') {
            navigate('/login', { replace: true });
          }
        }
      } catch (error) {
        console.error('检查登录状态失败:', error);
        // 出错时清除token并跳转到登录页
        TokenManager.clearToken();
        if (location.pathname !== '/login') {
          navigate('/login', { replace: true });
        }
      } finally {
        setIsChecking(false);
      }
    };

    // 立即检查登录状态
    checkAuthStatus();
  }, [navigate, location.pathname]);

  return {
    isChecking,
    isAuthenticated
  };
};
