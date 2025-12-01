import { Layout } from '@arco-design/web-react';
import React, { useState } from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import AppBreadcrumb from '../../components/Breadcrumb';
import AppHeader from './components/header';
import AppSider from './components/sider';

import ApplicationPage from './pages/Application';
import EnterpriseInfo from './pages/EnterpriseInfo';
import OrganizationPage from './pages/Organization';
import RolePage from './pages/Role';
import SpaceInfo from './pages/SpaceInfo';
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

const Content = Layout.Content;

const SettingPage: React.FC = () => {
  const [collapsed, setCollapsed] = useState(false);
  const [avatarUrl, setAvatarUrl] = useState<string>('');

  const handleCollapse = (collapsed: boolean) => {
    setCollapsed(collapsed);
  };

  return (
    <Layout className={styles.settingPage}>
      <AppHeader className={styles.settingPageHeader} avatarUrl={avatarUrl} />

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
                <Route path="spaceInfo" element={<SpaceInfo />} />
                <Route path="enterpriseInfo" element={<EnterpriseInfo />} />
                <Route path="profile" element={<ProfilePage />} />
                <Route path="enterprise" element={<BusinessPage />}>
                  <Route path="create-enterprise" element={<CreateBusinessPage />} />
                  <Route path=":enterpriseName" element={<RedirectEnterprise />} />
                  <Route path=":enterpriseName/:activeTab" element={<EnterpriseInfoPage />} />
                </Route>
                <Route
                  path="profile/edit"
                  element={<ProfileEditPage setAvatarUrl={setAvatarUrl} avatarUrl={avatarUrl} />}
                />
                <Route path="" element={<Navigate to="user" replace />} />
              </Routes>
            </div>
          </Content>
        </Layout>
      </Layout>
    </Layout>
  );
};

export default SettingPage;
