import { Layout } from '@arco-design/web-react';
import React, { useState } from 'react';
import { Route, Routes, Navigate } from 'react-router-dom';
import AppBreadcrumb from '../../components/Breadcrumb';
import AppHeader from './components/header';
import AppSider from './components/sider';

import OrganizationPage from './pages/Organization';
import EnterpriseInfo from './pages/EnterpriseInfo';
import UserPage from './pages/User';

import styles from './index.module.less';
import AuthorizedApplication from './pages/authorizedApp';
import TenantPage from './pages/Tenant';
import TenantEditPage from './pages/Tenant/edit';


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
                <Route path="user" element={<UserPage />} />
                <Route path="organization" element={<OrganizationPage />} />
                <Route path="enterpriseInfo" element={<EnterpriseInfo />} />
                <Route path="authorized-application" element={<AuthorizedApplication />} />
                <Route path="tenant" element={<TenantPage />} />
                <Route path="tenant/edit" element={<TenantEditPage setAvatarUrl={setAvatarUrl} avatarUrl={avatarUrl} />} />
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
