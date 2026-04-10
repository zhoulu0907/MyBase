/**
 * 天工 OAuth 回调组件
 * @deprecated 此文件已废弃，请使用 @onebase/product-tiangong 包中的 TiangongOAuthCallback 组件
 * import { TiangongOAuthCallback } from '@onebase/product-tiangong';
 */

console.warn(
  '[DEPRECATED] pages/OAuthCallback 已废弃，请使用 @onebase/product-tiangong 包。\n' +
  '新用法: import { TiangongOAuthCallback } from "@onebase/product-tiangong";'
);

import { Message } from '@arco-design/web-react';
import { useEffect, useRef } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { useI18n } from '../../hooks/useI18n';
import { tiangongLogin, getPermissionInfo, CodeType } from '@onebase/platform-center';
import { TokenManager, UserPermissionManager, getOrCreateDeviceInfo } from '@onebase/common';
import styles from './index.module.less';

const OAuthCallback: React.FC = () => {
  const { t } = useI18n();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
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
                import('@/store/singals/user_permission').then(({ userPermissionSignal }) => {
                  userPermissionSignal.setPermissionInfo(permissionInfo);
                });
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
              Message.error((error as any)?.message || t('oauth.callback.loginFailed'));
              navigate('/login', { replace: true });
            }
          }
        } else {
          Message.error(t('oauth.callback.codeMissing'));
          navigate('/login', { replace: true });
        }
      };

      handleOAuthCallback();
    }
  }, []);

  return (
    <div className={styles.callbackContainer}>
      <div className={styles.loadingContent}>
        <div className={styles.spinner} />
        <p className={styles.text}>{t('oauth.callback.processing')}</p>
      </div>
    </div>
  );
};

export default OAuthCallback;
