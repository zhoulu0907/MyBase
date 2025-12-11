import { Layout } from '@arco-design/web-react';
import React from 'react';
import { Navigate, Route, Routes, useLocation } from 'react-router-dom';
import CreateAppPage from '../CreateApp';
import AppSettingPage from '../CreateApp/pages/AppSetting';
import DataFactoryPage from '../CreateApp/pages/DataFactory';
import IntegratedManagementPage from '../CreateApp/pages/IntegratedManagement';
import LargeScreenPort from '../CreateApp/pages/LargeScreenPort';
import PageManagerPage from '../CreateApp/pages/PageManager';
import { AppHeader } from './components/header';
import styles from './index.module.less';
import AppCenterPage from './pages/AppCenter';
import HelpCenterPage from './pages/HelpCenter';
import MallCenterPage from './pages/MallCenter';
import EnterpriseAppPage from './pages/EnterpriseApp';

const Content = Layout.Content;

const Home: React.FC = () => {
  const location = useLocation();

  return (
    <Layout className={styles.homePage}>
      {location.pathname.includes('create-app') || location.pathname.includes('preview-app') ? null : (
        <AppHeader className={styles.myAppPageHeader} />
      )}

      <Layout className={styles.myAppPageContent}>
        <Layout>
          <Content className={styles.content}>
            <div className={styles.contentInner}>
              <Routes>
                <Route path="/" element={<Navigate to="enterprise-app" replace />} />
                <Route path="enterprise-app" element={<EnterpriseAppPage />} />
                <Route path="app-center" element={<AppCenterPage />} />
                <Route path="mall-center" element={<MallCenterPage />} />
                <Route path="help-center" element={<HelpCenterPage />} />

                <Route path="create-app" element={<CreateAppPage />}>
                  <Route index element={<PageManagerPage />} />
                  <Route path="page-manager" element={<PageManagerPage />} />
                  <Route path="integrated-management/*" element={<IntegratedManagementPage />} />
                  <Route path="data-factory" element={<DataFactoryPage />} />
                  <Route path="screen-port" element={<LargeScreenPort />} />
                  <Route path="app-setting" element={<AppSettingPage />} />
                </Route>
              </Routes>
            </div>
          </Content>
        </Layout>
      </Layout>
    </Layout>
  );
};

export default Home;
