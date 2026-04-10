import { getApplicationLeast } from '@onebase/app';
import { getHashTenantIdAndAppId } from '@onebase/common';
import { downloadFile } from '@onebase/platform-center';
import { useEffect, useState } from 'react';
import loginBg from '../../assets/images/login_bg.svg';
import Left from './components/left';
import Right from './components/right';
import styles from './index.module.less';

export default function Login() {
  const [appId, setAppId] = useState('');
  const [tenantId, setTenantId] = useState('');
  const [appInfo, setAppInfo] = useState<any>(null);
  const [loginImageUrl, setLoginImageUrl] = useState<string>('');

  useEffect(() => {
    getHashTenantIdAndAppId(setTenantId, setAppId);
  }, []);

  useEffect(() => {
    if (appId) {
      fetchApplicationInfo();
    }
  }, [appId]);

  const fetchApplicationInfo = async () => {
    if (!appId) return;

    try {
      const res = await getApplicationLeast({ id: appId });
      if (res) {
        setAppInfo(res);

        if (res.appLoginMainPic) {
          const url = await downloadFile(res.appLoginMainPic, appId);
          if (url) {
            setLoginImageUrl(url);
          }
        }
      }
    } catch (error) {
      console.error('获取应用信息失败:', error);
    }
  };

  return (
    <div className={styles.loginPage}>
      <Left loginImageUrl={loginImageUrl} defaultImage={loginBg} />
      <Right appInfo={appInfo} appId={appId} tenantId={tenantId} />
    </div>
  );
}
