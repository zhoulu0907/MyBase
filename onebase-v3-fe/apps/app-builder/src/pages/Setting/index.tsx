import { Layout } from '@arco-design/web-react';
import React, { useState } from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import AppHeader from './components/header';
import { LingjiLayout, TiangongLayout } from './components/layout';

import ApplicationPage from './pages/Application';
import OrganizationPage from './pages/Organization';
import RolePage from './pages/Role';
import SpaceInfo from './pages/SpaceInfo';
import AiCopilotDoc from './pages/AiCopilotDoc/index';
import AiWXMini from './pages/AiWXMini/index';
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
import { isTiangongPlatform, useIframeDetection, useIframeNavigation, useTenantInfo, useCollapsed } from './utils';

const SettingPage: React.FC = () => {
  const [avatarUrl, setAvatarUrl] = useState<string>('');
  const isIframe = useIframeDetection();
  const { tenantInfo, handleTenantInfoChange } = useTenantInfo();
  const { collapsed, handleCollapse } = useCollapsed();

  useIframeNavigation(isIframe);

  const shouldUseTiangongLayout = isTiangongPlatform();

  const renderRoutes = () => (
    <Routes>
      <Route path="application" element={<ApplicationPage />} />
      <Route path="user" element={<UserPage />} />
      <Route path="role" element={<RolePage />} />
      <Route path="organization" element={<OrganizationPage />} />
      <Route path="system-dict" element={<SystemDictPage />} />
      <Route path="security" element={<SecurityPage />} />
      <Route path="spaceInfo" element={<SpaceInfo onTenantInfoChange={handleTenantInfoChange} />} />
      <Route path="copilotdoc" element={<AiCopilotDoc />} />
      <Route path="wxmini" element={<AiWXMini />} />
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
  );

  return (
    <Layout className={styles.settingPage}>
      {!isIframe && <AppHeader className={styles.settingPageHeader} avatarUrl={avatarUrl} tenantInfo={tenantInfo} />}
      
      {shouldUseTiangongLayout ? (
        <TiangongLayout>
          {renderRoutes()}
        </TiangongLayout>
      ) : (
        <LingjiLayout collapsed={collapsed} onCollapse={handleCollapse}>
          {renderRoutes()}
        </LingjiLayout>
      )}
    </Layout>
  );
};

export default SettingPage;
