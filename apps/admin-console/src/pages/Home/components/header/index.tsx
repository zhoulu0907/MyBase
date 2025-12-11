import LogoSVG from '@/assets/images/ob_logo.svg';

import { useI18n } from '@/hooks/useI18n';
import { Avatar, Dropdown, Layout, Menu, Message, Typography } from '@arco-design/web-react';
import { IconExport } from '@arco-design/web-react/icon';
import { TokenManager } from '@onebase/common';
import { getPlatformAdminInfoApi, platformLogout } from '@onebase/platform-center';
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
  const [mobile, setMobile] = useState<string>('');
  // 获取用户信息
  const tokenInfo = TokenManager.getTokenInfo();

  useEffect(() => {
    if (tokenInfo?.accessToken) {
      getPlatformAdminInfo();
    }
  }, [tokenInfo]);

  const maskMobile = (value: string) => {
    let reg = /(\d{3})\d{4}(\d{4})/;
    const formatMobile = value.replace(reg, '$1****$2');
    return formatMobile;
  };

  const getPlatformAdminInfo = async () => {
    if (tokenInfo?.userId) {
      const res = await getPlatformAdminInfoApi(tokenInfo?.userId);
      setNickname(res.nickname);
      const mobile = res.mobile;
      const formatMobile = maskMobile(mobile);
      setMobile(formatMobile);
    }
  };

  // 登出处理
  const handleLogout = async () => {
    try {
      await platformLogout();
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
      <Menu.Item key="info" style={{ height: '60px' }}>
        <div className={styles.adminInformation}>
          <Avatar size={32} style={{ backgroundColor: '#009E9E' }}>
            {nickname?.charAt(0) || 'U'}
          </Avatar>
          <Typography.Text>{nickname}</Typography.Text>
        </div>
      </Menu.Item>
      <Menu.Item key="profile">{mobile}</Menu.Item>
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

        <div className={styles.userInfo}>
          <Dropdown droplist={userMenu} position="bottom">
            <div className={styles.userDropdown}>
              <Avatar size={32} style={{ backgroundColor: '#009E9E' }}>
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
