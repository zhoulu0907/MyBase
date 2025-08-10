import React, { useEffect, useState } from 'react';
// import { useNavigate } from 'react-router-dom';
import { Layout, Menu, Tooltip } from '@arco-design/web-react';
import { IconCommon, IconShareAlt } from '@arco-design/web-react/icon';
import DataSourcePage from './Pages/DataSource';
import EntityPage from './Pages/Entity';
import styles from './index.module.less';

const DataFactoryPage: React.FC = () => {
  // const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('check-entity');
  // const urlParams = new URLSearchParams(window.location.search);

  // const updateUrl = (tab: string) => {
  //   if (tab) {
  //     urlParams.set('fac-tab', tab);
  //     setActiveTab(tab);
  //   }
  //   navigate(`${window.location.pathname}?${urlParams.toString()}`);
  // };

  const handleMenuClick = (key: string) => {
    setActiveTab(key);
    // updateUrl(key);
  };

  useEffect(() => {
    // if (urlParams.get('fac-tab')) {
    //   setActiveTab(urlParams.get('fac-tab') as string);
    // } else {
    //   updateUrl('data-source');
    // }
  }, []);

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
        </Menu>
      </Layout.Sider>

      {/* 右侧内容 */}
      <Layout.Content className={styles['content']}>
        {activeTab === 'data-source' && <DataSourcePage />}
        {activeTab === 'check-entity' && <EntityPage />}
      </Layout.Content>
    </Layout>
  );
};

export default DataFactoryPage;
