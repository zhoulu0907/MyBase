import React from 'react';
import { Message } from '@arco-design/web-react';
import { useEffect, useRef } from 'react';
import { tiangongLogin, getPermissionInfo, CodeType } from '@onebase/platform-center';
import { TokenManager, UserPermissionManager, getOrCreateDeviceInfo } from '@onebase/common';

/**
 * OAuthCallback 组件的 Props
 * navigate 函数由主应用传入，避免 useNavigate hook context 问题
 */
export interface OAuthCallbackProps {
  navigate?: (path: string, options?: { replace?: boolean }) => void;
}

/**
 * 天工 OAuth 回调组件
 * 处理天工平台的 OAuth 登录回调
 */
export const TiangongOAuthCallback: React.FC<OAuthCallbackProps> = ({ navigate }) => {
  const processedRef = useRef(false);

  // 导航函数：优先使用传入的 navigate，否则使用 window.location
  const doNavigate = (path: string, options?: { replace?: boolean }) => {
    if (navigate) {
      navigate(path, options);
    } else {
      // fallback: 使用 hash 路由跳转
      window.location.hash = '#' + path;
      if (options?.replace) {
        window.history.replaceState(null, '', window.location.href);
      }
    }
  };

  // 从 URL 获取参数（不依赖 useSearchParams）
  const getSearchParams = () => {
    const params = new URLSearchParams(window.location.search);
    return {
      code: params.get('code'),
      state: params.get('state')
    };
  };

  useEffect(() => {
    if (!processedRef.current) {
      processedRef.current = true;

      const handleOAuthCallback = async () => {
        const { code, state } = getSearchParams();

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
                  doNavigate(redirectPath, { replace: true });
                } catch (error) {
                  console.error('解析state失败:', error);
                  doNavigate(`/onebase/${response.tenantId}/setting/application`, { replace: true });
                }
              } else {
                doNavigate(`/onebase/${response.tenantId}/setting/application`, { replace: true });
              }
            }
          } catch (error) {
            console.error('天工登录失败:', error);
            if ((error as any)?.response?.status !== 302) {
              Message.error((error as any)?.message || '登录失败');
              doNavigate('/login', { replace: true });
            }
          }
        } else {
          Message.error('缺少授权码');
          doNavigate('/login', { replace: true });
        }
      };

      handleOAuthCallback();
    }
  }, [navigate]);

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