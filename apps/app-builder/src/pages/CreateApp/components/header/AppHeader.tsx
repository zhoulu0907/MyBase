import AppIconSVG from '@/assets/images/app_icon.svg';
import TabMiddleBgSVG from '@/assets/images/tab_bg.svg';
import TabFirstBgSVG from '@/assets/images/tab_first_bg.svg';
import TabFirstSelectBgSVG from '@/assets/images/tab_first_select_bg.svg';
import TabLastSelectBgSVG from '@/assets/images/tab_last_select_bg.svg';
import TabMiddleSelectBgSVG from '@/assets/images/tab_select_bg.svg';
import VisitIconSVG from '@/assets/images/visit.svg';
import DynamicIcon from '@/components/DynamicIcon';

import { useI18n } from '@/hooks/useI18n';
import { useAppStore } from '@/store/store_app';
import { UserPermissionManager } from '@/utils/permission';
import { Button, Layout, Menu, Tabs } from '@arco-design/web-react';
import { AppStatus, getApplication, type GetApplicationReq } from '@onebase/app';
import { getRuntimeURL, TokenManager } from '@onebase/common';
import { appIconMap } from '@onebase/ui-kit';
import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import styles from './header.module.less';

const { Header } = Layout;

interface HeaderProps {
  className?: string;
}

// tabs标题顺序，获取当前选型卡下标；
const tabsList = ['data-factory', 'page-manager', 'integrated-management', 'app-setting'];

const AppHeader: React.FC<HeaderProps> = ({ className }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { t } = useI18n();
  const { curAppId, setCurAppId, curAppInfo, setCurAppInfo } = useAppStore();

  // Tab 切换
  // 根据当前路径设置 activeTab
  const getTabKeyFromPath = (pathname: string) => {
    if (pathname.includes('onebase/create-app/page-manager')) return 'page-manager';
    if (pathname.includes('onebase/create-app/integrated-management')) return 'integrated-management';
    if (pathname.includes('onebase/create-app/data-factory')) return 'data-factory';
    if (pathname.includes('onebase/create-app/app-setting')) return 'app-setting';
    return 'page-manager';
  };
  const [activeTab, setActiveTab] = useState(() => getTabKeyFromPath(location.pathname));
  const [activeIndex, setActiveIndex] = useState(tabsList.findIndex((tab) => location.pathname.includes(tab)));

  useEffect(() => {
    setActiveTab(getTabKeyFromPath(location.pathname));
    setActiveIndex(tabsList.findIndex((tab) => location.pathname.includes(tab)));
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
      setCurAppInfo({
        iconName: appResp.iconName || '',
        iconColor: appResp.iconColor || '',
        appName: appResp.appName || '--',
        appStatus: appResp.appStatus || 0
      });
    }
  };

  // 登出处理
  const handleLogout = async () => {
    // TODO(mickey): 联调后打开
    // await systemLogout();
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
      <Menu.Item key="logout" onClick={handleLogout} style={{ color: '#bfa6a6ff' }}>
        {t('header.logout')}
      </Menu.Item>
    </Menu>
  );

  const toRuntime = () => {
    const tenantId = TokenManager.getTenantInfo()?.tenantId || '';

    const newWindow = window.open('', '_blank');
    if (newWindow) {
      const redirectURL = `${getRuntimeURL()}/#/onebase/runtime/?appId=${curAppId}&tenantId=${tenantId}`;
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
              navigate('/onebase/enterprise-app');
            }}
          >
            <img src={AppIconSVG} alt="application icon" />
          </div>

          <div className={styles.myAppIcon} style={{ backgroundColor: curAppInfo?.iconColor }}>
            <DynamicIcon
              IconComponent={appIconMap[curAppInfo?.iconName as keyof typeof appIconMap]}
              theme="outline"
              size="14"
              fill="#F2F3F5"
            />
          </div>
          <div className={styles.appName}>{curAppInfo?.appName}</div>

          {curAppInfo?.appStatus === AppStatus.DEVELOPING && <div className={styles.appStatusDeveloping}>开发中</div>}
          {curAppInfo?.appStatus == AppStatus.PUBLISHED && <div className={styles.appStatusPublished}>已发布</div>}
          {curAppInfo?.appStatus == AppStatus.EDITING_AFTER_PUBLISH && (
            <div className={styles.appStatusEditAfterPublished}>已发布</div>
          )}
        </div>

        <Tabs
          className="createAppTabs"
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
              default:
                break;
            }
          }}
          size="large"
          renderTabTitle={(tabTitle, info) => {
            const currentIndex = tabsList.findIndex((tab) => tab === info.key);
            const tabBg = () => {
              if (info.isActive) {
                if (info.key === tabsList[0]) {
                  return TabFirstSelectBgSVG;
                } else if (info.key === tabsList[tabsList.length - 1]) {
                  return TabLastSelectBgSVG;
                } else {
                  return TabMiddleSelectBgSVG;
                }
              } else {
                if (currentIndex >= activeIndex) return;
                return info.key === tabsList[0] ? TabFirstBgSVG : TabMiddleBgSVG;
              }
            };
            return (
              <span
                style={{
                  position: 'relative'
                }}
              >
                {tabTitle}
                <img
                  src={tabBg()}
                  style={{
                    position: 'absolute',
                    top: '50%',
                    left: '50%',
                    transform: 'translate(-50%, -50%)',
                    zIndex: -1
                  }}
                />
              </span>
            );
          }}
        >
          <Tabs.TabPane key="data-factory" title={t('createApp.dataFactory')} />
          <Tabs.TabPane key="page-manager" title={t('createApp.pageManager')} />
          <Tabs.TabPane key="integrated-management" title={t('createApp.integratedManagement')} />
          <Tabs.TabPane key="app-setting" title={t('createApp.appRelease')} />
        </Tabs>

        <div className={styles.userInfo}>
          <Button type="secondary" size="small" onClick={toRuntime} className={styles.visitButton}>
            <div className={styles.visitButtonContent}>
              <img src={VisitIconSVG} alt="visit" />
              <div>访问</div>
            </div>
          </Button>

          {/* {UserPermissionManager.getUserPermissionInfo()?.user?.nickname || '未登录'}

          <Dropdown droplist={userMenu} position="bottom">
            <div className={styles.userDropdown}>
              <img src={AvatarSVG} alt="avatar" />
            </div>
          </Dropdown> */}
        </div>
      </div>
    </Header>
  );
};

export { AppHeader };
