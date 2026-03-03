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

  // 直接执行登录逻辑
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
          navigate(`/onebase/${response.tenantId}/home/enterprise-app`);
        }
      })
      .catch(error => {
        console.error('天工登录失败:', error);
        if ((error as any)?.response?.status !== 302) {
          Message.error((error as any)?.message || t('oauth.callback.loginFailed'));
          navigate('/login');
        }
      });
  } else {
    Message.error(t('oauth.callback.codeMissing'));
    navigate('/login');
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
