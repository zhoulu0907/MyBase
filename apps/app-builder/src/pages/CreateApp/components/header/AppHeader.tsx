import AppIconSVG from '@/assets/images/app_icon.svg';
import AvatarSVG from '@/assets/images/avatar.svg';
import { useI18n } from '@/hooks/useI18n';
import { useAppStore } from '@/store/store_app';
import { UserPermissionManager } from '@/utils/permission';
import { Button, Dropdown, Layout, Menu, Tabs } from '@arco-design/web-react';
import { AppStatus, getApplication, type GetApplicationReq } from '@onebase/app';
import { getRuntimeURL, TokenManager } from '@onebase/common';
import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import styles from './header.module.less';

const { Header } = Layout;

interface HeaderProps {
  className?: string;
}

const AppHeader: React.FC<HeaderProps> = ({ className }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { t } = useI18n();
  const { curAppId, setCurAppId } = useAppStore();

  // Tab 切换
  // 根据当前路径设置 activeTab
  const getTabKeyFromPath = (pathname: string) => {
    if (pathname.includes('onebase/create-app/page-manager')) return 'page-manager';
    if (pathname.includes('onebase/create-app/integrated-management')) return 'integrated-management';
    if (pathname.includes('onebase/create-app/data-factory')) return 'data-factory';
    if (pathname.includes('onebase/create-app/app-setting')) return 'app-setting';
    if (pathname.includes('onebase/create-app/app-release')) return 'app-release';
    return 'page-manager';
  };
  const [activeTab, setActiveTab] = useState(() => getTabKeyFromPath(location.pathname));
  const [appName, setAppName] = useState('未命名应用');
  const [appIcon, setAppIcon] = useState('');
  const [iconColor, setIconColor] = useState('');
  const [appStatus, setAppStatus] = useState(0);

  useEffect(() => {
    setActiveTab(getTabKeyFromPath(location.pathname));
  }, [location.pathname]);

  useEffect(() => {
    const searchParams = new URLSearchParams(location.search);
    const appId = searchParams.get('appId');
    if (appId) {
      setCurAppId(appId);
      handleGetApplication(appId);
    }
  }, []);

  const handleGetApplication = async (appId: string) => {
    const appReq: GetApplicationReq = {
      id: appId
    };
    const appResp = await getApplication(appReq);
    if (appResp) {
      if (appResp.iconName) {
        setAppIcon(appResp.iconName);
      }
      if (appResp.iconColor) {
        setIconColor(appResp.iconColor);
      }
      if (appResp.appName) {
        setAppName(appResp.appName);
      }
      if (appResp.appStatus) {
        setAppStatus(appResp.appStatus);
      }
    }
  };

  // 登出处理
  const handleLogout = () => {
    // 清除 token
    TokenManager.clearToken();
    UserPermissionManager.clearUserPermissionInfo();
    // 跳转到登录页
    navigate('/login', { replace: true });
  };

  // 用户菜单
  const userMenu = (
    <Menu>
      <Menu.Item key="profile">
        <div className={styles.userMenuInfo}>
          <div>{UserPermissionManager.getUserPermissionInfo()?.user.email}</div>
        </div>
      </Menu.Item>
      <Menu.Item key="logout" onClick={handleLogout} style={{ color: '#FF0000' }}>
        {t('header.logout')}
      </Menu.Item>
    </Menu>
  );

  const toRuntime = () => {
    const newWindow = window.open('', '_blank');
    if (newWindow) {
      const redirectURL = `${getRuntimeURL()}/#/onebase/runtime/${curAppId}/`;
      newWindow.location.href = `${getRuntimeURL()}/#/login?redirectURL=${redirectURL}`;
    }
  };

  return (
    <Header className={`${styles.header} ${className || ''}`}>
      <div className={styles.headerContent}>
        <div className={styles.appInfo}>
          <div
            className={styles.menuIcon}
            onClick={() => {
              navigate('/onebase/my-app');
            }}
          >
            <img src={AppIconSVG} alt="application icon" />
          </div>

          <div className={styles.myAppIcon} style={{ backgroundColor: iconColor }}>
            <i className={`iconfont ${appIcon || 'icon-box'}`} />
          </div>
          <div className={styles.appName}>{appName}</div>

          {appStatus == AppStatus.DEVELOPING && <div className={styles.appStatusDeveloping}>开发中</div>}

          {appStatus == AppStatus.PUBLISHED && <div className={styles.appStatusPublished}>已发布</div>}
          {appStatus == AppStatus.EDITING_AFTER_PUBLISH && (
            <div className={styles.appStatusEditAfterPublished}>已发布</div>
          )}
        </div>

        <Tabs
          type="line"
          activeTab={activeTab}
          onChange={(key) => {
            setActiveTab(key);
            switch (key) {
              case 'page-manager':
                navigate(`/onebase/create-app/page-manager?appId=${curAppId}`);
                break;
              case 'integrated-management':
                navigate(`/onebase/create-app/integrated-management?appId=${curAppId}`);
                break;
              case 'data-factory':
                navigate(`/onebase/create-app/data-factory?appId=${curAppId}`);
                break;
              case 'app-setting':
                navigate(`/onebase/create-app/app-setting?appId=${curAppId}`);
                break;
              case 'app-release':
                navigate(`/onebase/create-app/app-release?appId=${curAppId}`);
                break;
              default:
                break;
            }
          }}
          size="large"
        >
          <Tabs.TabPane key="data-factory" title={t('createApp.dataFactory')} />
          <Tabs.TabPane key="page-manager" title={t('createApp.pageManager')} />
          <Tabs.TabPane key="integrated-management" title={t('createApp.integratedManagement')} />

          <Tabs.TabPane key="app-setting" title={t('createApp.appSetting')} />
          <Tabs.TabPane key="app-release" title={t('createApp.appRelease')} />
        </Tabs>

        <div className={styles.userInfo}>
          <Button type="outline" size="small" onClick={toRuntime}>
            访问
          </Button>

          {UserPermissionManager.getUserPermissionInfo()?.user?.nickname || '未登录'}

          <Dropdown droplist={userMenu} position="bottom">
            <div className={styles.userDropdown}>
              <img src={AvatarSVG} />
            </div>
          </Dropdown>
        </div>
      </div>
    </Header>
  );
};

export { AppHeader };
