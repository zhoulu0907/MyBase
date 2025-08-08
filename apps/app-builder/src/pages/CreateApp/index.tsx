import { Layout } from '@arco-design/web-react';
import React from 'react';
import { Outlet } from 'react-router-dom';
import { AppHeader } from './components/header';
import styles from './index.module.less';

const Content = Layout.Content;

const Home: React.FC = () => {
  return (
    <Layout className={styles.myAppPage}>
      <AppHeader className={styles.myAppPageHeader} />

      <Layout className={styles.myAppPageContent}>
        <Layout>
          <Content className={styles.content}>
            <div className={styles.contentInner}>
              <Outlet />
            </div>
          </Content>
        </Layout>
      </Layout>
    </Layout>
  );
};

export default Home;
