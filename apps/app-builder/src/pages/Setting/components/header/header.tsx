import LogoSVG from '@/assets/images/ob_logo.svg';
import UserProfileAvtar from '@/components/UserProfileAvatar';

import { useI18n } from '@/hooks/useI18n';
import { UserPermissionManager } from '@/utils/permission';
import { Button, Dropdown, Layout, Menu, Typography } from '@arco-design/web-react';
import { IconApps, IconExport } from '@arco-design/web-react/icon';
import { TokenManager } from '@onebase/common';
import React from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import styles from './header.module.less';

const { Header } = Layout;

interface HeaderProps {
  className?: string;
  avatarUrl: string;
}

const AppHeader: React.FC<HeaderProps> = ({ className, avatarUrl }) => {
  const navigate = useNavigate();
  const { t } = useI18n();

  const { tenantId } = useParams();

  // 获取用户信息
  const userPermissionInfo = UserPermissionManager.getUserPermissionInfo();

  // 登出处理
  const handleLogout = async () => {
    // TODO(mickey): 联调后打开
    // await systemLogout();
    TokenManager.clearToken();
    // 跳转到登录页
    navigate('/login', { replace: true });
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
          <UserProfileAvtar adminInfo={userPermissionInfo?.user} avatarUrl={avatarUrl} />
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
        <div className={styles.logo} onClick={() => navigate(`/onebase/${tenantId}/home/enterprise-app`)}>
          <img src={LogoSVG} alt="logo" />
        </div>

        <div className={styles.userInfo}>
          <Button
            type="secondary"
            icon={<IconApps />}
            onClick={() => navigate(`/onebase/${tenantId}/home/enterprise-app`)}
            className={styles.backBtn}
          >
            应用中心
          </Button>

          <div className={styles.username}>{userPermissionInfo?.user.nickname}</div>

          <Dropdown droplist={userMenu} position="bottom">
            <div className={styles.userDropdown}>
              <UserProfileAvtar adminInfo={userPermissionInfo?.user} avatarUrl={avatarUrl} />
            </div>
          </Dropdown>
        </div>
      </div>
    </Header>
  );
};

export default AppHeader;
