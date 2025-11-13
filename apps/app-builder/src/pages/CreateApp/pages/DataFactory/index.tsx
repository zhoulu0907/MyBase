import { useAppStore } from '@/store/store_app';
import { Layout, Menu, Tooltip } from '@arco-design/web-react';
import { IconBook, IconCommon, IconShareAlt } from '@arco-design/web-react/icon';
import { getHashQueryParam } from '@onebase/common';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';
import DataDictPage from './pages/DataDict';
import DataSourcePage from './pages/DataSource';
import DataSourceManagementPage from './pages/DataSourceManagement';
import EntityPage from './pages/Entity';
import EtlDataFactoryPage from './pages/ETLDataFactory';

const DataFactoryPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState('check-entity');
  const [appId, setAppId] = useState('');
  const { setCurAppId, curAppId, clearCurAppId } = useAppStore();
  const [collapsed, setCollapsed] = useState(false);

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

  return (
    <Layout className={styles.dataFactoryPage}>
      <Layout.Sider breakpoint="xl" className={styles.sider} collapsed={collapsed}>
        {/* 左侧菜单 */}
        <div className={styles.title}> {collapsed ? null : '数据资产'}</div>
        <Menu
          className={styles.menu}
          mode="pop"
          onClickMenuItem={handleMenuClick}
          selectedKeys={[activeTab]}
          collapse={collapsed}
        >
          <Menu.Item key="check-entity">
            <Tooltip content="业务实体管理" position="right">
              <IconCommon />
              业务实体管理
            </Tooltip>
          </Menu.Item>
          <Menu.Item key="data-source">
            <Tooltip content="数据源管理" position="right">
              <IconShareAlt />
              数据源管理
            </Tooltip>
          </Menu.Item>
          <Menu.Item key="data-dict">
            <Tooltip content="数据字典" position="right">
              <IconBook />
              数据字典
            </Tooltip>
          </Menu.Item>
        </Menu>

        <div className={styles.dataProcessTitle}> {collapsed ? null : '数据处理'}</div>
        <Menu
          className={styles.dataProcessMenu}
          mode="pop"
          onClickMenuItem={handleMenuClick}
          selectedKeys={[activeTab]}
          hasCollapseButton
          onCollapseChange={() => setCollapsed(!collapsed)}
        >
          <Menu.Item key="data-factory">
            <Tooltip content="数据工厂" position="right">
              <IconCommon />
              数据工厂
            </Tooltip>
          </Menu.Item>
          <Menu.Item key="datasource-management">
            <Tooltip content="输入源" position="right">
              <IconShareAlt />
              输入源
            </Tooltip>
          </Menu.Item>
        </Menu>
      </Layout.Sider>

      {/* 右侧内容 */}
      <Layout.Content className={styles.content}>
        {activeTab === 'data-source' && <DataSourcePage />}
        {activeTab === 'check-entity' && <EntityPage appId={appId} />}
        {activeTab === 'data-dict' && <DataDictPage />}
        {activeTab === 'data-factory' && <EtlDataFactoryPage />}
        {activeTab === 'datasource-management' && <DataSourceManagementPage />}
      </Layout.Content>
    </Layout>
  );
};

export default DataFactoryPage;
