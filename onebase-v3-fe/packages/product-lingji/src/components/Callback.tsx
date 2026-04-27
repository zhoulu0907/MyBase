import React from 'react';
import { Message } from '@arco-design/web-react';
import { useEffect, useRef } from 'react';
import type { NavigateFunction } from 'react-router-dom';
import { getPermissionInfo, CodeType } from '@onebase/platform-center';
import { TokenManager, UserPermissionManager } from '@onebase/common';

interface LingjiLoginData {
  userId: string;
  accessToken: string;
  refreshToken: string;
  expiresTime: number;
  tenantId: string;
}

/**
 * Callback 组件的 Props
 * navigate 函数由主应用传入，避免 useNavigate hook context 问题
 */
export interface CallbackProps {
  navigate?: NavigateFunction;
}

/**
 * 灵畿 SSO 回调组件
 * 处理灵畿平台的 SSO 登录回调
 */
export const LingjiCallback: React.FC<CallbackProps> = ({ navigate }) => {
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

  useEffect(() => {
    // 确保登录逻辑只执行一次
    if (!processedRef.current) {
      processedRef.current = true;

      const handleCallback = async () => {
        // 从 sessionStorage 读取灵畿登录数据
        const loginDataStr = sessionStorage.getItem('lingji_login_data');

        if (!loginDataStr) {
          Message.error('登录数据不存在，请重新登录');
          doNavigate('/login');
          return;
        }

        try {
          const loginData: LingjiLoginData = JSON.parse(loginDataStr);

          if (!loginData.accessToken || !loginData.tenantId) {
            Message.error('登录数据不完整，请重新登录');
            doNavigate('/login');
            return;
          }

          // 清除临时数据
          sessionStorage.removeItem('lingji_login_data');

          // 1. 设置当前身份ID
          TokenManager.setCurIdentifyId(loginData.tenantId);

          // 2. 设置 token 信息
          TokenManager.setToken(
            {
              userId: loginData.userId,
              accessToken: loginData.accessToken,
              refreshToken: loginData.refreshToken,
              expiresTime: loginData.expiresTime,
              tenantId: loginData.tenantId,
              loginSource: 'lingji',
              loginURL: window.location.href
            },
            true
          );

          // 3. 获取并设置用户权限信息
          try {
            const permissionInfo = await getPermissionInfo(CodeType.TENANT);
            UserPermissionManager.setUserPermissionInfo(permissionInfo);
          } catch (error) {
            console.error('获取权限信息失败:', error);
          }

          // 4. 跳转到应用管理页面
          doNavigate(`/onebase/${loginData.tenantId}/setting/application`);
        } catch (error) {
          console.error('灵畿登录处理失败:', error);
          Message.error((error as any)?.message || '登录处理失败');
          doNavigate('/login');
        }
      };

      handleCallback();
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