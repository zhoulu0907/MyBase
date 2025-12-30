import UserProfileAvatar from '@/components/UserProfileAvatar';

import { useI18n } from '@/hooks/useI18n';
import { logout } from '@/utils/session';
import { Button, Divider, Dropdown, Layout, Menu, Typography } from '@arco-design/web-react';
import { IconApps, IconExport } from '@arco-design/web-react/icon';
import { TokenManager, UserPermissionManager } from '@onebase/common';
import { CodeType, getPermissionInfo, getTenantInfo, systemLogout } from '@onebase/platform-center';
import { userPermissionSignal } from '@/store/singals/user_permission';
import { getTenantInfoFromSession, setTenantInfoFromSession } from '@/utils';
import React, { useEffect } from 'react';
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
  const tokenInfo = TokenManager.getTokenInfo();
  const userPermissionInfo = UserPermissionManager.getUserPermissionInfo();

  useEffect(() => {
    if (tokenInfo?.accessToken) {
      getInfo();
    }
  }, [tokenInfo?.accessToken]);

  const getInfo = async () => {
    const res = await getPermissionInfo(CodeType.TENANT);
    UserPermissionManager.setUserPermissionInfo(res);
    userPermissionSignal.setPermissionInfo(res);

    const tenantInfoRes = await getTenantInfo(tenantId || '');
    if (tenantInfoRes) {
      setTenantInfoFromSession(tenantInfoRes);
    }
  };

  // 登出处理
  const handleLogout = async () => {
    await systemLogout();
    logout(navigate);
  };

  const maskMobile = (value: string) => {
    let reg = /(\d{3})\d{4}(\d{4})/;
    const formatMobile = value.replace(reg, '$1****$2');
    return formatMobile;
  };

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

  const tenantInfo = getTenantInfoFromSession();

  return (
    <Header className={`${styles.header} ${className || ''}`}>
      <div className={styles.headerContent}>
        <div className={styles.logo} onClick={() => navigate(`/onebase/${tenantId}/home/enterprise-app`)}>
          <TenantLogo tenantInfo={tenantInfo} />
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
              <UserProfileAvatar adminInfo={userPermissionInfo?.user} avatarUrl={avatarUrl} />
            </div>
          </Dropdown>
        </div>
      </div>
    </Header>
  );
};

export default AppHeader;
