import { Layout } from '@arco-design/web-react';
import React, { useState } from 'react';
import { Route, Routes } from 'react-router-dom';
import AppBreadcrumb from '../../components/Breadcrumb';
import PlatformInfo from '../PlatformInfo';
import Tenant from '../Tenant/index';
import Administrator from '../Administrator';
import AppHeader from './components/header';
import AppSider from './components/sider';
// import Welcome from './components/Welcome';
import styles from './index.module.less';

const Content = Layout.Content;

const Home: React.FC = () => {
  const [collapsed, setCollapsed] = useState(true);

  const handleCollapse = (collapsed: boolean) => {
    setCollapsed(collapsed);
  };

  return (
    <Layout className={styles.homePage}>
      <AppHeader className={styles.homePageHeader} />
      <Layout className={styles.homePageContent}>
        <AppSider
          collapsed={collapsed}
          onCollapse={handleCollapse}
        />
        <Layout>
          <AppBreadcrumb />

          <Content className={styles.content}>
            <div className={styles.contentInner}>
              <Routes>
                <Route path="platform-info" element={<PlatformInfo />} />
                <Route path="tenant" element={<Tenant />} />
                <Route path="administrator" element={<Administrator />} />
                {/* <Route path="" element={<Welcome />} /> */}
              </Routes>
            </div>
          </Content>
        </Layout>
      </Layout>
    </Layout>
  );
};

export default Home;