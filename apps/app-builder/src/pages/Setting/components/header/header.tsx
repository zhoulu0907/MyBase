import UserProfileAvatar from '@/components/UserProfileAvatar';

import TenantLogo from '@/components/TenantLogo';
import { useI18n } from '@/hooks/useI18n';
import { userPermissionSignal } from '@/store/singals/user_permission';
import { setTenantInfoFromSession } from '@/utils';
import { logout } from '@/utils/session';
import { Button, Divider, Dropdown, Layout, Menu, Typography } from '@arco-design/web-react';
import { IconApps, IconExport } from '@arco-design/web-react/icon';
import { TokenManager, UserPermissionManager } from '@onebase/common';
import { CodeType, getPermissionInfo, getTenantInfo, systemLogout, type TenantInfo } from '@onebase/platform-center';
import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import styles from './header.module.less';
import mapPinIcon from '@/assets/images/map-pin-2-line.svg';
import { getPlatform } from '@/products';

const { Header } = Layout;

interface HeaderProps {
  className?: string;
  avatarUrl: string;
  tenantInfo: TenantInfo | null;
}

const AppHeader: React.FC<HeaderProps> = ({ className, avatarUrl, tenantInfo }) => {
  const navigate = useNavigate();
  const { t } = useI18n();

  const { tenantId } = useParams();

  const [tenantInfor,setTenantInfor] = useState<any>({})

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
    setTenantInfor(res)
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

  return (
    <Header className={`${styles.header} ${className || ''}`}>
      <div className={styles.headerContent}>
        <div className={styles.logo} onClick={() => navigate(`/onebase/${tenantId}/home/enterprise-app`)}>
          <TenantLogo tenantInfo={tenantInfo || tenantInfor} />
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

          {/* 当前租户名称 - 仅灵畿显示 */}
          {getPlatform() === 'lingji' && (
            <div className={styles.tenantName}>
              <img src={mapPinIcon} alt="tenant" className={styles.tenantIcon} />
              <span>{tenantInfo?.name || tenantInfor?.tenant?.name || ''}</span>
            </div>
          )}

          {/* 用户名 - 仅非灵畿显示 */}
          {getPlatform() !== 'lingji' && (
            <div className={styles.username}>{userPermissionInfo?.user.nickname}</div>
          )}

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
