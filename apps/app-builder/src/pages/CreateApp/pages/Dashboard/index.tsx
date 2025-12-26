import baseSettingSVG from '@/assets/images/appRelease/base_setting.svg';
import appPermissionSVG from '@/assets/images/appRelease/app_auth.svg';
import appReleaseSVG from '@/assets/images/appRelease/app_release.svg';
import { ReactSVG } from 'react-svg';
import { Layout, Menu } from '@arco-design/web-react';
import { useEffect, useState, type FC } from 'react';
import DataSet from './components/DataSet';
import Dashboard from './components/Dashboard';
import DashboardTemplate from './components/DashboardTemplate';
import AppBreadcrumb from '@/components/Breadcrumb';
import styles from './index.module.less';

const Sider = Layout.Sider;
const Content = Layout.Content;
interface BreadcrumbItemType {
  key: string;
  title: string;
  path?: string;
}

const DashboardPage: FC = () => {
  const menuData = [
    { title: '数据集', icon: baseSettingSVG, key: 'dataSet' },
    { title: '大屏', icon: appPermissionSVG, key: 'largeScreen' },
    { title: '大屏模板', icon: appReleaseSVG, key: 'screenTemplate' }
  ];

  const [activeTab, setActiveTab] = useState('dataSet');
  const [breadcrumbItems, setBreadcrumbItems] = useState<BreadcrumbItemType[]>([
    { key: 'screenTemplate', title: '大屏报表' },
    { key: 'dataSet', title: '数据集' }
  ]); // 菜单路径
  const [collapsed, setCollapsed] = useState<boolean>(false);
  const handleMenuClick = (key: string) => {
    setActiveTab(key);
  };
  useEffect(() => {
    const currentBreadcrumb = menuData.find((ele) => ele.key === activeTab);
    if (currentBreadcrumb) {
      setBreadcrumbItems([
        { key: 'screenTemplate', title: '大屏报表' },
        { key: currentBreadcrumb.key, title: currentBreadcrumb.title }
      ]);
    }
  }, [activeTab]);

  return (
    <Layout className={styles.appSettingPage}>
      <Layout className={styles.settingContent}>
        <Sider collapsed={collapsed}>
          <Menu
            onCollapseChange={() => setCollapsed(!collapsed)}
            onClickMenuItem={handleMenuClick}
            selectedKeys={[activeTab]}
          >
            {menuData.map((item) => (
              <Menu.Item key={item.key} style={{ display: 'flex' }}>
                <ReactSVG
                  className={styles.menuIcon}
                  src={item.icon}
                  beforeInjection={(svg) => {
                    const fillColor = activeTab === item.key ? 'rgb(var(--primary-6))' : '#4e5969';
                    svg.querySelectorAll('*').forEach((el) => el.removeAttribute('fill'));
                    svg.setAttribute('fill', fillColor);
                    svg.setAttribute('width', '18px');
                    svg.setAttribute('height', '18px');
                  }}
                />
                {item.title}
              </Menu.Item>
            ))}
          </Menu>
        </Sider>
        {/* 右侧内容 */}
        <div className={styles.rightContent}>
          <AppBreadcrumb items={breadcrumbItems} />
          <Content className={styles.content}>
            <div className={styles.contentInner}>
              {activeTab === 'dataSet' && <DataSet />}
              {activeTab === 'largeScreen' && <Dashboard />}
              {activeTab === 'screenTemplate' && <DashboardTemplate />}
            </div>
          </Content>
        </div>
      </Layout>
    </Layout>
  );
};

export default DashboardPage;
