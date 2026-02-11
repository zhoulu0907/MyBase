import { Message } from '@arco-design/web-react';
import { useEffect } from 'react';
import { useParams, useSearchParams, useNavigate } from 'react-router-dom';
import { useI18n } from '../../hooks/useI18n';
import { tiangongLogin } from '@onebase/platform-center';
import styles from './index.module.less';

const OAuthCallback: React.FC = () => {
  const { t } = useI18n();
  const { appName } = useParams<{ appName: string }>();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    const handleOAuthCallback = async () => {
      const code = searchParams.get('code');

      if (!code) {
        Message.error(t('oauth.callback.codeMissing'));
        navigate('/login');
        return;
      }

      if (!appName) {
        Message.error(t('oauth.callback.appNameMissing'));
        navigate('/login');
        return;
      }

      try {
        await tiangongLogin({ code, appName });
      } catch (error: any) {
        console.error('天工登录失败:', error);
        if (error?.response?.status === 302) {
          return;
        }
        Message.error(error?.message || t('oauth.callback.loginFailed'));
        navigate('/login');
      }
    };

    handleOAuthCallback();
  }, [appName, searchParams, navigate, t]);

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
