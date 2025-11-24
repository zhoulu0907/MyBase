import AvatarSVG from '@/assets/images/avatar.svg';
import BuildingLine from '@/assets/images/building-line.svg';
import LogoAvatarSVG from '@/assets/images/ob_logo.svg';
import { DynamicIcon } from '@/components/DynamicIcon';
import { useI18n } from '@/hooks/useI18n';
import { appInfoSignal } from '@/store/app';
import { UserPermissionManager } from '@/utils/permission';
import { getHashQueryParam } from '@/utils/router';
import { Avatar, Divider, Dropdown, Layout, Menu, Typography } from '@arco-design/web-react';
import { IconExport } from '@arco-design/web-react/icon';
import { getApplication, type GetApplicationReq } from '@onebase/app';
import { TokenManager } from '@onebase/common';
import { CodeType, getPermissionInfo } from '@onebase/platform-center';
import { appIconMap } from '@onebase/ui-kit';
import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import styles from './header.module.less';

const { Header } = Layout;

interface HeaderProps {
  className?: string;
}

const AppHeader: React.FC<HeaderProps> = ({ className }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { t } = useI18n();
  const { curAppInfo, setCurAppInfo } = appInfoSignal;

  const [mobile, setMobile] = useState<string>('');
  const [userInfo, setUserInfo] = useState<any>(null);
  // 获取用户信息
  const tokenInfo = TokenManager.getTokenInfo();

  useEffect(() => {
    if (tokenInfo?.accessToken) {
      getInfo();
    }
  }, [tokenInfo?.accessToken]);

  useEffect(() => {
    // 正则匹配 /onebase/runtime/ 后面的两个数字（appId 和 tenantId）
    // 例子: /onebase/runtime/123944716126027776/141778708868268032
    // match[1] 是 appId, match[2] 是 tenantId
    const match = location.pathname.match(/\/onebase\/runtime\/(\d+)\/(\d+)/);
    console.log('match: ', match);
    if (match && match[1]) {
      console.log('match[1]: ', match[1]);
      handleGetApplication(match[1]);
    }
  }, []);

  const handleGetApplication = async (appId: string) => {
    const appReq: GetApplicationReq = {
      id: appId
    };
    const appResp = await getApplication(appReq);
    if (appResp) {
      setCurAppInfo(appResp);
    }
  };

  const maskMobile = (value: string) => {
    let reg = /(\d{3})\d{4}(\d{4})/;
    const formatMobile = value.replace(reg, '$1****$2');
    return formatMobile;
  };

  const getInfo = async () => {
    const res = await getPermissionInfo(CodeType.CORP);
    UserPermissionManager.setUserPermissionInfo(res);
    const mobile = res.user.mobile;
    const formatMobile = maskMobile(mobile);
    setMobile(formatMobile);
    setUserInfo(res.user);
  };

  // 登出处理
  const handleLogout = async () => {
    // TODO(mickey): 联调后打开
    // await systemLogout();
    TokenManager.clearToken();
    UserPermissionManager.clearUserPermissionInfo();
    // 跳转到登录页

    const appId = getHashQueryParam('appId');
    const tenantId = getHashQueryParam('tenantId');

    if (!appId && tenantId) {
      navigate(`/onebase/runtime/my-app`, { replace: true });
    }
    if (appId && !tenantId) {
      navigate(`/onebase/runtime/?appId=${appId}`, { replace: true });
    }
    if (appId && tenantId) {
      navigate(`/onebase/runtime/?appId=${appId}&tenantId=${tenantId}`, { replace: true });
    }

    // 退出登录后刷新页面，确保状态清空
    window.location.reload();
  };

  // 用户菜单
  const userMenu = (
    <Menu>
      <Menu.Item key="info" style={{ height: '70px' }}>
        <div className={styles.adminInformation}>
          <Avatar size={32}>
            <img src={userInfo?.avatar} />
          </Avatar>
          <Typography.Text>{userInfo?.nickname}</Typography.Text>
          <Typography.Text type="secondary">{maskMobile(mobile)}</Typography.Text>
        </div>
      </Menu.Item>
      <Divider style={{ margin: '4px 0' }} />
      {tokenInfo?.adminFlag && (
        <Menu.Item
          key="setting"
          onClick={() => {
            navigate('/onebase/setting');
          }}
        >
          <div className={styles.headerContent}>
            <img src={BuildingLine} />
            <span>企业管理后台</span>
          </div>
        </Menu.Item>
      )}
      <Menu.Item key="logout" onClick={handleLogout}>
        <IconExport style={{ color: '#F53F3F' }} />
        <Typography.Text type="error">{t('header.logout')}</Typography.Text>
      </Menu.Item>
    </Menu>
  );

  return (
    <Header className={`${styles.header} ${className || ''}`}>
      <div className={styles.headerContent}>
        {(curAppInfo.value.iconName && (
          <div className={styles.appInfo}>
            <div className={styles.myAppIcon} style={{ backgroundColor: curAppInfo.value.iconColor }}>
              <DynamicIcon
                IconComponent={appIconMap[curAppInfo.value.iconName as keyof typeof appIconMap]}
                theme="outline"
                size="14"
                fill="#F2F3F5"
              />
            </div>
            <div className={styles.appName}>{curAppInfo.value.appName}</div>
          </div>
        )) || <img src={LogoAvatarSVG} />}

        <div className={styles.userInfo}>
          {UserPermissionManager.getUserPermissionInfo()?.user?.nickname || '未登录'}

          <Dropdown droplist={userMenu} position="bottom">
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
