import defaultAvatar from '@/assets/images/default_avatar.png';
import LogoSVG from '@/assets/images/ob_logo.svg';
import { useI18n } from '@/hooks/useI18n';
import { UserPermissionManager } from '@/utils/permission';
import { logout } from '@/utils/session';
import { Avatar, Button, Dropdown, Layout, Menu, Typography } from '@arco-design/web-react';
import { IconApps, IconExport } from '@arco-design/web-react/icon';
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
  const userPermissionInfo = UserPermissionManager.getUserPermissionInfo();

  // 登出处理
  const handleLogout = async () => {
    // TODO(mickey): 联调后打开
    // await systemLogout();

    logout(navigate);
  };

  const maskMobile = (value: string) => {
    let reg = /(\d{3})\d{4}(\d{4})/;
    const formatMobile = value.replace(reg, '$1****$2');
    return formatMobile;
  };

  // 用户菜单
  const userMenu = (
    <Menu>
      <Menu.Item key="info" style={{ height: '60px' }}>
        <div className={styles.adminInformation}>
          <Avatar size={32}>
            <img src={LogoSVG} />
          </Avatar>
          <Typography.Text>{userPermissionInfo?.user?.nickname || ''}</Typography.Text>
        </div>
      </Menu.Item>
      <Menu.Item key="profile">{maskMobile(userPermissionInfo?.user?.mobile || '')}</Menu.Item>
      <Menu.Item key="logout" onClick={handleLogout}>
        <IconExport style={{ color: '#F53F3F' }} />
        <Typography.Text type="error">{t('header.logout')}</Typography.Text>
      </Menu.Item>
    </Menu>
  );

  return (
    <Header className={`${styles.header} ${className || ''}`}>
      <div className={styles.headerContent}>
        <div className={styles.logo} onClick={() => navigate('/onebase/runtime/my-app')}>
          <img src={LogoSVG} alt="logo" />
        </div>

        <div className={styles.userInfo}>
          <Button
            type="secondary"
            icon={<IconApps />}
            onClick={() => navigate('/onebase/runtime/my-app')}
            className={styles.backBtn}
          >
            应用中心
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
