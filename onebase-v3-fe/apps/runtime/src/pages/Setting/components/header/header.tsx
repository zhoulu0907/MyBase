import UserProfileAvatar from '@/components//UserProfileAvatar';
import { useI18n } from '@/hooks/useI18n';
import { getTenantInfoFromSession, logout } from '@/utils/session';
import { Button, Divider, Dropdown, Layout, Menu, Typography } from '@arco-design/web-react';
import { IconApps, IconExport } from '@arco-design/web-react/icon';
import { UserPermissionManager } from '@onebase/common';
import React from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import styles from './header.module.less';
import TenantLogo from '@/components/TenantLogo';

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
    // await runtimeLogout();
    logout(navigate);
  };

  const maskMobile = (value: string) => {
    let reg = /(\d{3})\d{4}(\d{4})/;
    const formatMobile = value.replace(reg, '$1****$2');
    return formatMobile;
  };

  const tenantInfo = getTenantInfoFromSession();

  // 用户菜单
  const userMenu = (
    <Menu style={{ marginRight: '10px' }}>
      <Menu.Item key="info" style={{ height: 'auto' }}>
        <div className={styles.adminInformation}>
          <UserProfileAvatar adminInfo={userPermissionInfo?.user} avatarUrl={avatarUrl} />
          <Typography.Text>{userPermissionInfo?.user?.nickname || ''}</Typography.Text>
          <span className={styles.mobileColor}>{maskMobile(userPermissionInfo?.user?.mobile || '')}</span>
        </div>
      </Menu.Item>
      <Divider style={{ margin: '4px 0' }} />
      <Menu.Item key="logout" onClick={handleLogout}>
        <IconExport style={{ color: '#F53F3F' }} />
        <Typography.Text type="error">{t('header.logout')}</Typography.Text>
      </Menu.Item>
    </Menu>
  );

  return (
    <Header className={`${styles.header} ${className || ''}`}>
      <div className={styles.headerContent}>
        <div className={styles.logo} onClick={() => navigate(`/onebase/${tenantId}/runtime/my-app`)}>
          <TenantLogo tenantInfo={tenantInfo} />
        </div>

        <div className={styles.userInfo}>
          <Button
            type="secondary"
            icon={<IconApps />}
            onClick={() => navigate(`/onebase/${tenantId}/runtime/my-app`)}
            className={styles.backBtn}
          >
            应用中心
          </Button>

          <div className={styles.username}>{userPermissionInfo?.user.nickname}</div>

          <Dropdown droplist={userMenu} position="bottom">
            <div className={styles.userDropdown}>
              <UserProfileAvatar adminInfo={userPermissionInfo?.user} avatarUrl={avatarUrl} />
            </div>
          </Dropdown>
        </div>
      </div>
    </Header>
  );
};

export default AppHeader;
