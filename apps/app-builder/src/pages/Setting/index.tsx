import { Layout } from '@arco-design/web-react';
import React, { useState } from 'react';
import { Route, Routes } from 'react-router-dom';
import AppBreadcrumb from '../../components/Breadcrumb';
import OrganizationPage from '../Organization';
import RolePage from '../Role';
import SystemDictPage from '../SystemDict';
import TenantPage from '../Tenant';
import UserPage from '../User';
import AppHeader from './components/header';
import AppSider from './components/sider';
import Welcome from './components/Welcome';
import styles from './index.module.less';

const Content = Layout.Content;

const SettingPage: React.FC = () => {
  const [collapsed, setCollapsed] = useState(false);

  const handleCollapse = (collapsed: boolean) => {
    setCollapsed(collapsed);
  };

  return (
    <Layout className={styles.settingPage}>
      <AppHeader className={styles.settingPageHeader} />

      <Layout className={styles.settingPageContent}>
        <AppSider
          collapsed={collapsed}
          onCollapse={handleCollapse}
        />
        <Layout>
          <AppBreadcrumb />

          <Content className={styles.content}>
            <div className={styles.contentInner}>
              <Routes>
                <Route path="user" element={<UserPage />} />
                <Route path="role" element={<RolePage />} />
                <Route path="organization" element={<OrganizationPage />} />
                <Route path="system-dict" element={<SystemDictPage />} />
                <Route path="tenant" element={<TenantPage />} />
                <Route path="" element={<Welcome />} />
              </Routes>
            </div>
          </Content>
        </Layout>
      </Layout>
    </Layout>
  );
};

export default SettingPage;