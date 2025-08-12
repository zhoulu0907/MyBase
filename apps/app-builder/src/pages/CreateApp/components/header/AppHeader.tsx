import AvatarSVG from '@/assets/images/avatar.svg';
import helpSVG from '@/assets/images/help_icon.svg';
import { useI18n } from '@/hooks/useI18n';
import { useAppStore } from '@/store';
import { UserPermissionManager } from '@/utils/permission';
import { Button, Dropdown, Layout, Menu, Tabs } from '@arco-design/web-react';
import { IconMenu } from '@arco-design/web-react/icon';
import { getApplication, type GetApplicationReq } from '@onebase/app';
import { TokenManager } from '@onebase/common';
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
  const [appStatus, setAppStatus] = useState('');

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
    console.log(appResp);
    if (appResp) {
      if (appResp.icon) {
        setAppIcon(appResp.icon);
      }
      if (appResp.iconColor) {
        setIconColor(appResp.iconColor);
      }
      if (appResp.appName) {
        setAppName(appResp.appName);
      }
      if (appResp.appStatusText) {
        setAppStatus(appResp.appStatusText);
      }
    }
  };

  // 登出处理
  const handleLogout = () => {
    // 清除 token
    TokenManager.clearToken();
    // 跳转到登录页
    navigate('/login');
  };

  // 用户菜单
  const userMenu = (
    // <Menu>
    //   <Menu.Item key="profile">
    //     <IconUser />
    //     {t('header.profile')}
    //   </Menu.Item>
    //   <Menu.Item key="logout" onClick={handleLogout}>
    //     <IconPoweroff />
    //     {t('header.logout')}
    //   </Menu.Item>
    // </Menu>
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

  return (
    <Header className={`${styles.header} ${className || ''}`}>
      <div className={styles.headerContent}>
        <div className={styles.appInfo}>
          <Button
            shape="square"
            icon={<IconMenu />}
            onClick={() => {
              navigate('/onebase/my-app');
            }}
            className={styles.menuIcon}
          />

          {/* <Button iconOnly shape="square" icon={<IconUser />} style={{ backgroundColor: '#E0A951' }} /> */}
          <div className={styles.myAppIcon} style={{ backgroundColor: iconColor }}>
            <i className={`iconfont ${appIcon || 'icon-box'}`} />
          </div>
          <div className={styles.appName}>{appName}</div>
          <Button type="text" style={{ background: '#eaf0fd' }}>
            {appStatus}
          </Button>
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
          <Button
            type="text"
            shape="circle"
            icon={<img src={helpSVG} alt="Help" style={{ width: 30 }} />}
            // onClick={() => navigate('/onebase/setting')}
          />

          <Button type="outline" /* onClick={() => navigate('/onebase/setting')} */>{t('createApp.preview')}</Button>

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
