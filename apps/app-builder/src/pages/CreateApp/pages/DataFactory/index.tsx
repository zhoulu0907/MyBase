import { useAppStore } from '@/store/store_app';
import { Layout, Menu, Tooltip } from '@arco-design/web-react';
import { IconBook, IconCommon, IconShareAlt } from '@arco-design/web-react/icon';
import { getHashQueryParam } from '@onebase/common';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';
import DataDictPage from './pages/DataDict';
import DataSourcePage from './pages/DataSource';
import EntityPage from './pages/Entity';

const DataFactoryPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState('check-entity');
  const [appId, setAppId] = useState(''); 
  const { setCurAppId, curAppId, clearCurAppId } = useAppStore();

  const handleMenuClick = (key: string) => {
    setActiveTab(key);
  };

  useEffect(() => {
    clearCurAppId();
    const appId = getHashQueryParam('appId');

    if (appId) {
      setCurAppId(appId);
      setAppId(appId);
      console.log('从URL参数获取到appId:', appId);
    } else {
      console.warn('URL参数中未找到appId');
    }
  }, []);

  // 如果appId未设置，可以显示加载状态或错误提示
  if (!curAppId) {
    return (
      <div
        style={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          height: '100vh',
          fontSize: '16px',
          color: '#666'
        }}
      >
        正在加载应用信息...
      </div>
    );
  }

  return (
    <Layout className={styles['data-factory-page']}>
      <Layout.Sider breakpoint="xl" width={48} className={styles['sider']}>
        {/* 左侧菜单 */}
        <Menu className={styles['menu']} mode="pop" onClickMenuItem={handleMenuClick} selectedKeys={[activeTab]}>
          <Menu.Item key="check-entity">
            <Tooltip content="业务实体管理" position="right">
              <IconCommon />
            </Tooltip>
          </Menu.Item>
          <Menu.Item key="data-source">
            <Tooltip content="数据源管理" position="right">
              <IconShareAlt />
            </Tooltip>
          </Menu.Item>
          <Menu.Item key="data-dict">
            <Tooltip content="数据字典" position="right">
              <IconBook />
            </Tooltip>
          </Menu.Item>
        </Menu>
      </Layout.Sider>

      {/* 右侧内容 */}
      <Layout.Content className={styles['content']}>
        {activeTab === 'data-source' && <DataSourcePage />}
        {activeTab === 'check-entity' && <EntityPage appId={appId} />}
        {activeTab === 'data-dict' && <DataDictPage />}
      </Layout.Content>
    </Layout>
  );
};

export default DataFactoryPage;
