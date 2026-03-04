import { Layout } from '@arco-design/web-react';
import React, { useEffect, useRef } from 'react';
import { Outlet } from 'react-router-dom';
import { AppHeader } from './components/header';
import { loadMicroApp } from 'qiankun';
import { getAiCopilotURL } from '@onebase/common';
import styles from './index.module.less';

const Content = Layout.Content;

const Home: React.FC = () => {
  const containerRef = useRef(null);

  useEffect(() => {
    if (!containerRef.current) return;
    const microApp = loadMicroApp({
      name: 'ai-copilot',
      entry: getAiCopilotURL(),
      container: containerRef.current || '',
      props: {
        from: 'main app'
      }
    });

    return () => {
      microApp.unmount();
    };
  }, []);
  return (
    <Layout className={styles.myAppPage}>
      <AppHeader className={styles.myAppPageHeader} />

      <Layout className={styles.myAppPageContent}>
        <div id="ai-copilot" className={styles.aiCopilot} ref={containerRef}></div>
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
