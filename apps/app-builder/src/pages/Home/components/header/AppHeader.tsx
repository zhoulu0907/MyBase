import LogoSVG from '@/assets/images/app_header_logo.svg';
import AvatarSVG from '@/assets/images/avatar.svg';
import spaceShipLine from '@/assets/images/space-ship-line.svg';
import { useI18n } from '@/hooks/useI18n';
import { userPermissionSignal } from '@/store/singals/user_permission';
import { UserPermissionManager } from '@/utils/permission';
import { Avatar, Divider, Dropdown, Layout, Menu, Tabs, Typography } from '@arco-design/web-react';
import { IconExport } from '@arco-design/web-react/icon';
import { TokenManager } from '@onebase/common';
import { getPermissionInfo, CodeType } from '@onebase/platform-center';
import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import styles from './header.module.less';

const { Header } = Layout;

interface HeaderProps {
  className?: string;
}

interface IAdminInfo {
  avatar: string;
  deptId: string;
  email: string;
  id: string;
  nickname: string;
  username: string;
  mobile: string;
}

const AppHeader: React.FC<HeaderProps> = ({ className }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { t } = useI18n();

  const [nickname, setNickname] = useState('U');
  const [adminInfo, setAdminInfo] = useState<IAdminInfo | null>(null);

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
  }, [tokenInfo?.accessToken]);

  const getInfo = async () => {
    const res = await getPermissionInfo(CodeType.TENANT);
    UserPermissionManager.setUserPermissionInfo(res);
    userPermissionSignal.setPermissionInfo(res);
    if (res.user) {
      setAdminInfo(res.user);
    }
    setNickname(res.user.nickname);
  };

  const maskMobile = (value?: string) => {
    let reg = /(\d{3})\d{4}(\d{4})/;
    const formatMobile = value?.replace(reg, '$1****$2');
    return formatMobile;
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

  const tenantAdminMenu = (
    <Menu>
      <Menu.Item key="info" style={{ height: '70px' }}>
        <div className={styles.adminInformation}>
          <Avatar size={32}>
            <img src={adminInfo?.avatar} />
          </Avatar>
          <Typography.Text>{adminInfo?.username}</Typography.Text>
          <Typography.Text type="secondary">{maskMobile(adminInfo?.mobile)}</Typography.Text>
        </div>
      </Menu.Item>
      <Divider style={{ margin: '4px 0' }} />
      <Menu.Item
        key="setting"
        onClick={() => {
          navigate('/onebase/setting');
        }}
      >
        <div className={styles.menu}>
          <img src={spaceShipLine} />
          {t('header.tenantManagementBackend')}
        </div>
      </Menu.Item>
      <Menu.Item key="logout" onClick={handleLogout}>
        <IconExport style={{ color: '#F53F3F' }} />
        <Typography.Text type="error">{t('header.logout')}</Typography.Text>
      </Menu.Item>
    </Menu>
  );

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
      <Menu.Item key="logout" onClick={handleLogout}>
        <IconExport style={{ color: '#F53F3F' }} />
        <Typography.Text type="error">{t('header.logout')}</Typography.Text>
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

          <Dropdown
            droplist={location.pathname?.startsWith('/onebase/enterprise-app') ? tenantAdminMenu : userMenu}
            position="bl"
          >
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
