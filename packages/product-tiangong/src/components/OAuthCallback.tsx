import React, { useEffect, useRef } from 'react';
import { Message } from '@arco-design/web-react';
import { NavigateFunction } from 'react-router-dom';
import { tiangongLogin, getPermissionInfo, CodeType } from '@onebase/platform-center';
import { TokenManager, UserPermissionManager, getOrCreateDeviceInfo } from '@onebase/common';

interface TiangongOAuthCallbackProps {
  searchParams?: URLSearchParams;
  navigate?: NavigateFunction;
}

/**
 * 天工 OAuth 回调组件
 * 处理天工平台的 OAuth 登录回调
 */
export const TiangongOAuthCallback: React.FC<TiangongOAuthCallbackProps> = ({ searchParams: searchParamsProp, navigate: navigateProp }) => {
  const searchParams = searchParamsProp || new URLSearchParams(window.location.search);
  const navigate = navigateProp || (() => { throw new Error('navigate function is required'); });
  const processedRef = useRef(false);

  useEffect(() => {
    if (!processedRef.current) {
      processedRef.current = true;

      const handleOAuthCallback = async () => {
        const code = searchParams.get('code');
        const state = searchParams.get('state');

        if (code) {
          try {
            const deviceId = await getOrCreateDeviceInfo();

            const response = await tiangongLogin({ code, deviceId });
            if (response && response.accessToken) {
              TokenManager.setCurIdentifyId(response.tenantId);

              TokenManager.setToken({
                userId: response.userId,
                accessToken: response.accessToken,
                refreshToken: response.refreshToken,
                expiresTime: response.expiresTime,
                tenantId: response.tenantId,
                loginSource: 'tiangong',
                loginURL: window.location.href
              }, true);

              try {
                const permissionInfo = await getPermissionInfo(CodeType.TENANT);
                UserPermissionManager.setUserPermissionInfo(permissionInfo);
              } catch (error) {
                console.error('获取权限信息失败:', error);
              }

              if (state) {
                try {
                  const redirectPath = atob(decodeURIComponent(state));
                  navigate(redirectPath, { replace: true });
                } catch (error) {
                  console.error('解析state失败:', error);
                  navigate(`/onebase/${response.tenantId}/setting/application`, { replace: true });
                }
              } else {
                navigate(`/onebase/${response.tenantId}/setting/application`, { replace: true });
              }
            }
          } catch (error) {
            console.error('天工登录失败:', error);
            if ((error as any)?.response?.status !== 302) {
              Message.error((error as any)?.message || '登录失败');
              navigate('/login', { replace: true });
            }
          }
        } else {
          Message.error('缺少授权码');
          navigate('/login', { replace: true });
        }
      };

      handleOAuthCallback();
    }
  }, []);

  return (
    <div style={{
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      minHeight: '100vh',
      backgroundColor: '#f5f5f5'
    }}>
      <div style={{ textAlign: 'center', padding: '40px' }}>
        <div style={{
          width: '40px',
          height: '40px',
          border: '3px solid #e5e5e5',
          borderTopColor: '#165dff',
          borderRadius: '50%',
          animation: 'spin 1s linear infinite',
          margin: '0 auto 20px'
        }} />
        <p style={{ color: '#666', fontSize: '14px' }}>正在处理登录...</p>
      </div>
    </div>
  );
};