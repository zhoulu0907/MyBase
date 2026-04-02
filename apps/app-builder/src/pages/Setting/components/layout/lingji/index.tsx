import React from 'react';
import { Layout } from '@arco-design/web-react';
import AppBreadcrumb from '@/components/Breadcrumb';
import LingjiSider from '../../sider/lingji';
import styles from './index.module.less';

const Content = Layout.Content;

interface LingjiLayoutProps {
  children: React.ReactNode;
  collapsed: boolean;
  onCollapse: (collapsed: boolean) => void;
}

const LingjiLayout: React.FC<LingjiLayoutProps> = ({ children, collapsed, onCollapse }) => {
  return (
    <Layout className={styles.settingPageContent}>
      <LingjiSider collapsed={collapsed} onCollapse={onCollapse} />
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

export default LingjiLayout;
