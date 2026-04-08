import React from 'react';
import { Layout } from '@arco-design/web-react';
import { useLocation } from 'react-router-dom';
import AppBreadcrumb from '@/components/Breadcrumb';
import TiangongSider from '../../sider/tiangong';
import styles from './tiangong.module.less';

const Content = Layout.Content;

interface TiangongLayoutProps {
  children: React.ReactNode;
}

const TiangongLayout: React.FC<TiangongLayoutProps> = ({ children }) => {
  const location = useLocation();
  const isApplicationPage = location.pathname.includes('/application');

  return (
    <Layout className={styles.settingPageContent}>
      <TiangongSider />
      <Layout className={styles.settingPageContentMain}>
        {!isApplicationPage && <AppBreadcrumb />}
        <Content className={styles.content}>
          <div className={styles.contentInner}>
            {children}
          </div>
        </Content>
      </Layout>
    </Layout>
  );
};

export default TiangongLayout;
