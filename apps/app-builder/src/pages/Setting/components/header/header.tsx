import defaultAvatar from '@/assets/images/default_avatar.png';
import navBackSVG from '@/assets/images/nav_back.svg';
import LogoSVG from '@/assets/images/ob_logo.svg';
import { useI18n } from '@/hooks/useI18n';
import { UserPermissionManager } from '@/utils/permission';
import { Avatar, Button, Dropdown, Layout, Menu } from '@arco-design/web-react';
import { IconPoweroff, IconUser } from '@arco-design/web-react/icon';
import { TokenManager } from '@onebase/common';
import React from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './header.module.less';

const { Header } = Layout;

interface HeaderProps {
  className?: string;
}

const AppHeader: React.FC<HeaderProps> = ({ className }) => {
  const navigate = useNavigate();
  const { t } = useI18n();

  // 获取用户信息
  const tokenInfo = TokenManager.getTokenInfo();
  const userPermissionInfo = UserPermissionManager.getUserPermissionInfo();

  // 登出处理
  const handleLogout = () => {
    // 清除 token
    TokenManager.clearToken();
    // 跳转到登录页
    navigate('/login', { replace: true });
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
        <div className={styles.logo} onClick={() => navigate('/onebase/')}>
          <img src={LogoSVG} alt="logo" />
        </div>

        <div className={styles.userInfo}>
          <Button
            type="secondary"
            icon={<img src={navBackSVG} alt="MyApp" />}
            onClick={() => navigate('/onebase/my-app')}
            className={styles.backBtn}
          >
            我的应用
          </Button>

          <div className={styles.username}>{userPermissionInfo?.user.nickname}</div>

          <Dropdown droplist={userMenu} position="bottom">
            <div className={styles.userDropdown}>
              <Avatar size={32} style={{ backgroundColor: '#4FAE7B' }}>
                <img src={defaultAvatar} alt="avatar" />
              </Avatar>
            </div>
          </Dropdown>
        </div>
      </div>
    </Header>
  );
};

export default AppHeader;
