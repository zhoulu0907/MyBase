import { Layout } from '@arco-design/web-react';
import React, { useEffect, useState } from 'react';
import { Navigate, Route, Routes, useParams } from 'react-router-dom';
import AppBreadcrumb from '../../components/Breadcrumb';
import AppHeader from './components/header';
import AppSider from './components/sider';

import ApplicationPage from './pages/Application';
import OrganizationPage from './pages/Organization';
import RolePage from './pages/Role';
import SpaceInfo from './pages/SpaceInfo';
import AiCopilotDoc from './pages/AiCopilotDoc/index';
import SystemDictPage from './pages/SystemDict';
import UserPage from './pages/User';

import styles from './index.module.less';
import BusinessPage from './pages/Business';
import EnterpriseInfoPage from './pages/Business/components/enterprise-information';
import CreateBusinessPage from './pages/Business/createBusiness';
import RedirectEnterprise from './pages/Business/redirectEnterprise';
import ProfilePage from './pages/Profile';
import ProfileEditPage from './pages/Profile/edit';
import SecurityPage from './pages/Security';
import PluginPage from './pages/Plugin';
import ExternalUserPage from './pages/ExternalUser';
import { getTenantInfoFromSession, setTenantInfoFromSession } from '@/utils';
import { TokenManager } from '@onebase/common';
import { getTenantInfo, type TenantInfo } from '@onebase/platform-center';

const Content = Layout.Content;

const SettingPage: React.FC = () => {
  const [collapsed, setCollapsed] = useState(false);
  const [avatarUrl, setAvatarUrl] = useState<string>('');
  const { tenantId } = useParams();

  const [tenantInfo, setTenantInfo] = useState<TenantInfo | null>(() => getTenantInfoFromSession());

  const handleTenantInfoChange = (info: TenantInfo) => {
    setTenantInfo(info);
    setTenantInfoFromSession(info); // 继续保持和 session 同步
  };

  const handleCollapse = (collapsed: boolean) => {
    setCollapsed(collapsed);
  };

  // 获取用户信息
  const tokenInfo = TokenManager.getTokenInfo();

  useEffect(() => {
    if (tokenInfo?.accessToken) {
      getInfo();
    }
  }, [tokenInfo?.accessToken]);

  const getInfo = async () => {
    const tenantInfoRes = await getTenantInfo(tenantId || '');
    if (tenantInfoRes) {
      setTenantInfoFromSession(tenantInfoRes);
      setTenantInfo(tenantInfoRes);
    }
  };

  return (
    <Layout className={styles.settingPage}>
      <AppHeader className={styles.settingPageHeader} avatarUrl={avatarUrl} tenantInfo={tenantInfo} />

      <Layout className={styles.settingPageContent}>
        <AppSider collapsed={collapsed} onCollapse={handleCollapse} />
        <Layout className={styles.settingPageContentMain}>
          <AppBreadcrumb />

          <Content className={styles.content}>
            <div className={styles.contentInner}>
              <Routes>
                <Route path="application" element={<ApplicationPage />} />
                <Route path="user" element={<UserPage />} />
                <Route path="role" element={<RolePage />} />
                <Route path="organization" element={<OrganizationPage />} />
                <Route path="system-dict" element={<SystemDictPage />} />
                <Route path="security" element={<SecurityPage />} />
                <Route path="spaceInfo" element={<SpaceInfo onTenantInfoChange={handleTenantInfoChange} />} />
                <Route path="copilotdoc" element={<AiCopilotDoc />} />
                <Route path="profile" element={<ProfilePage />} />
                <Route path="plugin" element={<PluginPage />} />
                <Route path="externalUser" element={<ExternalUserPage />} />
                <Route path="enterprise" element={<BusinessPage />}>
                  <Route path="create-enterprise" element={<CreateBusinessPage />} />
                  <Route path=":enterpriseName" element={<RedirectEnterprise />} />
                  <Route path=":enterpriseName/:activeTab" element={<EnterpriseInfoPage />} />
                </Route>
                <Route
                  path="profile/edit"
                  element={<ProfileEditPage setAvatarUrl={setAvatarUrl} avatarUrl={avatarUrl} />}
                />
                <Route path="" element={<Navigate to="application" replace />} />
              </Routes>
            </div>
          </Content>
        </Layout>
      </Layout>
    </Layout>
  );
};

export default SettingPage;
