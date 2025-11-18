import LogoSVG from '@/assets/images/ob_logo.svg';
import { useI18n } from '@/hooks/useI18n';
import { UserPermissionManager } from '@/utils/permission';
import { Avatar, Dropdown, Layout, Menu, Message } from '@arco-design/web-react';
import { IconPoweroff, IconUser } from '@arco-design/web-react/icon';
import { TokenManager } from '@onebase/common';
import { getPermissionInfo } from '@onebase/platform-center';
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './index.module.less';

const { Header } = Layout;

interface HeaderProps {
  className?: string;
}

const AppHeader: React.FC<HeaderProps> = ({ className }) => {
  const navigate = useNavigate();
  const { t } = useI18n();

  const [nickname, setNickname] = useState('U');
  // 获取用户信息
  const tokenInfo = TokenManager.getTokenInfo();

  useEffect(() => {
    if (tokenInfo?.accessToken) {
      getInfo();
    }
  }, [tokenInfo]);

  const getInfo = async () => {
    const res = await getPermissionInfo();
    console.log(res);
    UserPermissionManager.setUserPermissionInfo(res);
    setNickname(res.user.nickname);
  };

  // 登出处理
  const handleLogout = async () => {
    try {
      //   await platformLogout();
      // 清除 token
      TokenManager.clearToken();
      // 跳转到登录页
      navigate('/login', { replace: true });
    } catch (error) {
      console.error('登出失败: ', error);
      Message.error('登出失败');
    }
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
          <img src={LogoSVG} alt="logo" />
        </div>

        <div className={styles.userInfo}>
          <Dropdown droplist={userMenu} position="bottom">
            <div className={styles.userDropdown}>
              <Avatar size={32} style={{ backgroundColor: '#00b42a' }}>
                {nickname?.charAt(0) || 'U'}
              </Avatar>
            </div>
          </Dropdown>
        </div>
      </div>
    </Header>
  );
};

export default AppHeader;
