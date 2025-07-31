import { Layout } from '@arco-design/web-react';
import React from 'react';
import { Route, Routes, useLocation } from 'react-router-dom';
import AppCenterPage from '../AppCenter';
import HelpCenterPage from '../HelpCenter';
import MallCenterPage from '../MallCenter';
import MyAppPage from '../MyApp';
import CreateAppPage from '../CreateApp';
import PageManagerPage from '../PageManager';
import IntegratedManagementPage from '../IntegratedManagement';
import DataFactoryPage from '../DataFactory';
import AppSettingPage from '../AppSetting';
import AppReleasePage from '../AppRelease';
import { AppHeader } from './components/header';
import styles from './index.module.less';


const Content = Layout.Content;

const Home: React.FC = () => {
  const location = useLocation();


  return (
    <Layout className={styles.homePage}>
      { location.pathname.includes('create-app') ?  null : <AppHeader className={styles.myAppPageHeader} />}


      <Layout className={styles.myAppPageContent}>

        <Layout>

          <Content className={styles.content}>
            <div className={styles.contentInner}>
              <Routes>
                <Route path="my-app" element={<MyAppPage />} />
                <Route path="app-center" element={<AppCenterPage />} />
                <Route path="mall-center" element={<MallCenterPage />} />
                <Route path="help-center" element={<HelpCenterPage />} />

                <Route path="create-app" element={<CreateAppPage />}>
                  <Route index element={<PageManagerPage />} /> 
                  <Route path="page-manager" element={<PageManagerPage />} />
                  <Route path="integrated-management" element={<IntegratedManagementPage />} />
                  <Route path="data-factory" element={<DataFactoryPage />} />
                  <Route path="app-setting" element={<AppSettingPage />} />
                  <Route path="app-release" element={<AppReleasePage />} />
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