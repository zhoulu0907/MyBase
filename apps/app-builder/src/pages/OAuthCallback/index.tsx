import { Message } from '@arco-design/web-react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { useI18n } from '../../hooks/useI18n';
import { tiangongLogin } from '@onebase/platform-center';
import { TokenManager } from '@onebase/common';
import styles from './index.module.less';

const OAuthCallback: React.FC = () => {
  const { t } = useI18n();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  // 使用sessionStorage确保登录逻辑只执行一次
  const loginProcessed = sessionStorage.getItem('tiangong_login_processed');
  if (!loginProcessed) {
    sessionStorage.setItem('tiangong_login_processed', 'true');
    
    const code = searchParams.get('code');
    if (code) {
      tiangongLogin({ code })
        .then(response => {
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
            // 清除标志，以便下次登录时可以重新执行
            sessionStorage.removeItem('tiangong_login_processed');
            navigate(`/onebase/${response.tenantId}/home/enterprise-app`);
          }
        })
        .catch(error => {
          console.error('天工登录失败:', error);
          // 清除标志，以便下次登录时可以重新执行
          sessionStorage.removeItem('tiangong_login_processed');
          if ((error as any)?.response?.status !== 302) {
            Message.error((error as any)?.message || t('oauth.callback.loginFailed'));
            navigate('/login');
          }
        });
    } else {
      Message.error(t('oauth.callback.codeMissing'));
      navigate('/login');
    }
  }

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
