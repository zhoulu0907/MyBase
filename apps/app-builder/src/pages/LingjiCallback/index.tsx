/**
 * 灵畿 SSO 回调组件
 * @deprecated 此文件已废弃，请使用 @onebase/product-lingji 包中的 LingjiCallback 组件
 * import { LingjiCallback } from '@onebase/product-lingji';
 */

console.warn(
  '[DEPRECATED] pages/LingjiCallback 已废弃，请使用 @onebase/product-lingji 包。\n' +
  '新用法: import { LingjiCallback } from "@onebase/product-lingji";'
);

import { Message } from '@arco-design/web-react';
import { useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { useI18n } from '../../hooks/useI18n';
import { getPermissionInfo, CodeType } from '@onebase/platform-center';
import { TokenManager, UserPermissionManager } from '@onebase/common';
import styles from './index.module.less';

interface LingjiLoginData {
  userId: string;
  accessToken: string;
  refreshToken: string;
  expiresTime: number;
  tenantId: string;
}

const LingjiCallback: React.FC = () => {
  const { t } = useI18n();
  const navigate = useNavigate();
  const processedRef = useRef(false);

  useEffect(() => {
    // 确保登录逻辑只执行一次
    if (!processedRef.current) {
      processedRef.current = true;

      const handleCallback = async () => {
        // 从 sessionStorage 读取灵畿登录数据
        const loginDataStr = sessionStorage.getItem('lingji_login_data');

        if (!loginDataStr) {
          Message.error('登录数据不存在，请重新登录');
          navigate('/login');
          return;
        }

        try {
          const loginData: LingjiLoginData = JSON.parse(loginDataStr);

          if (!loginData.accessToken || !loginData.tenantId) {
            Message.error('登录数据不完整，请重新登录');
            navigate('/login');
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
            // 同时设置到信号中
            import('@/store/singals/user_permission').then(({ userPermissionSignal }) => {
              userPermissionSignal.setPermissionInfo(permissionInfo);
            });
          } catch (error) {
            console.error('获取权限信息失败:', error);
          }

          // 4. 跳转到应用管理页面
          navigate(`/onebase/${loginData.tenantId}/setting/application`);
        } catch (error) {
          console.error('灵畿登录处理失败:', error);
          Message.error((error as any)?.message || '登录处理失败');
          navigate('/login');
        }
      };

      handleCallback();
    }
  }, [navigate]);

  return (
    <div className={styles.callbackContainer}>
      <div className={styles.loadingContent}>
        <div className={styles.spinner} />
        <p className={styles.text}>{t('oauth.callback.processing')}</p>
      </div>
    </div>
  );
};

export default LingjiCallback;