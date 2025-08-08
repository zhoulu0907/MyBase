import LogoSVG from '@/assets/images/ob_logo.svg';
import settingSVG from '@/assets/images/setting_icon.svg';
import { UserPermissionManager } from '@/utils/permission';
import { Avatar, Button, Dropdown, Layout, Menu, Tabs } from '@arco-design/web-react';
import { IconPoweroff, IconUser } from '@arco-design/web-react/icon';
import { TokenManager } from '@onebase/common';
import { getPermissionInfo } from '@onebase/platform-center';
import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useLocation, useNavigate } from 'react-router-dom';
import styles from './header.module.less';

const { Header } = Layout;

interface HeaderProps {
  className?: string;
}

const AppHeader: React.FC<HeaderProps> = ({ className }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { t } = useTranslation();

  const [nickname, setNickname] = useState('U');

  // Tab 切换
  // 根据当前路径设置 activeTab
  const getTabKeyFromPath = (pathname: string) => {
    if (pathname.includes('/onebase/app-center')) return 'app-center';
    if (pathname.includes('/onebase/mall-center')) return 'mall-center';
    if (pathname.includes('/onebase/help-center')) return 'help-center';
    return 'my-app';
  };
  const [activeTab, setActiveTab] = useState(() => getTabKeyFromPath(location.pathname));

  useEffect(() => {
    setActiveTab(getTabKeyFromPath(location.pathname));
  }, [location.pathname]);

  // 获取用户信息
  const tokenInfo = TokenManager.getTokenInfo();

  useEffect(() => {
    if (tokenInfo?.accessToken) {
      getInfo();
    }
  }, [tokenInfo]);

  const getInfo = async () => {
    const res = await getPermissionInfo();
    UserPermissionManager.setUserPermissionInfo(res);
    setNickname(res.user.nickname);
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
    <Menu>
      <Menu.Item key="profile">
        <IconUser />
        {t('header.profile')}
      </Menu.Item>
      <Menu.Item key="logout" onClick={handleLogout}>
        <IconPoweroff />
        {t('header.logout')}
      </Menu.Item>
    </Menu>
  );

  return (
    <Header className={`${styles.header} ${className || ''}`}>
      <div className={styles.headerContent}>
        <div className={styles.logo}>
          <img src={LogoSVG} />
        </div>

        <Tabs
          type="line"
          activeTab={activeTab}
          onChange={(key) => {
            setActiveTab(key);
            switch (key) {
              case 'my-app':
                navigate('/onebase/my-app');
                break;
              case 'app-center':
                navigate('/onebase/app-center');
                break;
              case 'mall-center':
                navigate('/onebase/mall-center');
                break;
              case 'help-center':
                navigate('/onebase/help-center');
                break;
              default:
                break;
            }
          }}
          size="large"
        >
          <Tabs.TabPane key="my-app" title="我的应用" />
          <Tabs.TabPane key="app-center" title="应用中心" />
          <Tabs.TabPane key="mall-center" title="商超中心" />
          <Tabs.TabPane key="help-center" title="帮助中心" />
        </Tabs>

        <div className={styles.userInfo}>
          <Button
            shape="circle"
            icon={<img src={settingSVG} alt="Setting" />}
            onClick={() => navigate('/onebase/setting')}
          />

          <Dropdown droplist={userMenu} position="bottom">
            <div className={styles.userDropdown}>
              <Avatar size={32} style={{ backgroundColor: '#4FAE7B' }}>
                {nickname?.charAt(0) || 'U'}
              </Avatar>
            </div>
          </Dropdown>
        </div>
      </div>
    </Header>
  );
};

export { AppHeader };
