import LogoSVG from '@/assets/images/app_header_logo.svg';
import AvatarSVG from '@/assets/images/avatar.svg';
import { useI18n } from '@/hooks/useI18n';
import { userPermissionSignal } from '@/store/singals/user_permission';
import { UserPermissionManager } from '@/utils/permission';
import { Dropdown, Layout, Menu, Tabs } from '@arco-design/web-react';
import { TokenManager } from '@onebase/common';
import { getPermissionInfo } from '@onebase/platform-center';
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
    userPermissionSignal.setPermissionInfo(res);
    setNickname(res.user.nickname);
  };

  // 登出处理
  const handleLogout = async () => {
    // TODO(mickey): 联调后打开
    // await systemLogout();
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
          <div>{UserPermissionManager.getUserPermissionInfo()?.user.username}</div>
        </div>
      </Menu.Item>
      <Menu.Item
        key="setting"
        onClick={() => {
          navigate('/onebase/setting');
        }}
      >
        {t('header.tenantManagement')}
      </Menu.Item>
      <Menu.Item key="logout" onClick={handleLogout} style={{ color: '#FF0000' }}>
        {t('header.logout')}
      </Menu.Item>
    </Menu>
  );

  return (
    <Header className={`${styles.header} ${className || ''}`}>
      <div className={styles.headerContent}>
        <div className={styles.logo}>
          <img src={LogoSVG} alt="logo" />
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
          {/* <Tabs.TabPane key="my-app" title="我的应用" /> */}
          {/* <Tabs.TabPane key="app-center" title="应用中心" />
          <Tabs.TabPane key="mall-center" title="商超中心" />
          <Tabs.TabPane key="help-center" title="帮助中心" /> */}
        </Tabs>

        <div className={styles.userInfo}>
          {UserPermissionManager.getUserPermissionInfo()?.user?.nickname || '未登录'}

          <Dropdown droplist={userMenu} position="bl">
            <div className={styles.userDropdown}>
              <img src={AvatarSVG} alt="avatar" />
            </div>
          </Dropdown>
        </div>
      </div>
    </Header>
  );
};

export { AppHeader };
