import logoSVG from '@/assets/images/logo.svg';
import settingSVG from '@/assets/images/setting_icon.svg';
import { Avatar, Button, Dropdown, Layout, Menu } from '@arco-design/web-react';
import { IconPoweroff, IconUser } from '@arco-design/web-react/icon';
import { TokenManager } from '@onebase/common';
import React, { useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import styles from './header.module.less';

const { Header } = Layout;

interface HeaderProps {
  className?: string;
}

const AppHeader: React.FC<HeaderProps> = ({ className }) => {
  const navigate = useNavigate();
  const { t } = useTranslation();

  // 获取用户信息
  const tokenInfo = TokenManager.getTokenInfo();

  useEffect(() => {
    console.log(tokenInfo);
  }, [tokenInfo]);

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
        <div className={styles.logo} onClick={() => navigate('/onebase/')}>
          <img src={logoSVG} alt="Logo" className={styles.logoSvg} />
          <h1>{t('header.title')}</h1>
        </div>

        <div className={styles.userInfo}>
          <Button
            shape="circle"
            icon={<img src={settingSVG} alt="Setting" />}
            onClick={() => navigate('/onebase/setting')}
          />

          <Dropdown droplist={userMenu} position="bottom">
            <div className={styles.userDropdown}>
              <Avatar size={32} style={{ backgroundColor: '#4FAE7B' }}>
                {tokenInfo?.username?.toString().charAt(0) || 'U'}
              </Avatar>
            </div>
          </Dropdown>
        </div>
      </div>
    </Header>
  );
};

export default AppHeader;
