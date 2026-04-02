import React from 'react';
import { Layout } from '@arco-design/web-react';
import AppBreadcrumb from '@/components/Breadcrumb';
import TiangongSider from '../../sider/tiangong';
import styles from './tiangong.module.less';

const Content = Layout.Content;

interface TiangongLayoutProps {
  children: React.ReactNode;
}

const TiangongLayout: React.FC<TiangongLayoutProps> = ({ children }) => {
  return (
    <Layout className={styles.settingPageContent}>
      <TiangongSider />
      <Layout className={styles.settingPageContentMain}>
        <AppBreadcrumb />
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
