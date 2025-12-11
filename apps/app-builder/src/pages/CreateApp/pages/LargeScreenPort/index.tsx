import baseSettingSVG from '@/assets/images/appRelease/base_setting.svg';
import appPermissionSVG from '@/assets/images/appRelease/app_auth.svg';
import appReleaseSVG from '@/assets/images/appRelease/app_release.svg';
import navigatorSettingSVG from '@/assets/images/appRelease/navigator_setting.svg';
import { ReactSVG } from 'react-svg';
import { type Options } from '@/components/CreateApp/const';
import { Button, Form, Layout, Menu, Message } from '@arco-design/web-react';
import { IconMenuFold } from '@arco-design/web-react/icon';
import {
  getApplication,
  updateApplication,
  type Application,
  type GetApplicationReq,
  type UpdateApplicationReq
} from '@onebase/app';
import { useEffect, useState, type FC } from 'react';
import DataSet from './components/DataSet';
import LargeScreen from './components/LargeScreen';
import ScreenTemplate from './components/ScreenTemplate';
import { useAppStore } from '@/store/store_app';
import AppBreadcrumb from '@/components/Breadcrumb';
import styles from './index.module.less';

const Sider = Layout.Sider;
const Content = Layout.Content;
interface BreadcrumbItemType {
  key: string;
  title: string;
  path?: string;
}

const AppSettingPage: FC = () => {
  const [form] = Form.useForm();
  const { curAppId, curAppInfo, setCurAppInfo } = useAppStore();
  const menuData = [
    { title: '数据集', icon: baseSettingSVG, key: 'dataSet' },
    { title: '大屏', icon: appPermissionSVG, key: 'largeScreen' },
    { title: '大屏模板', icon: appReleaseSVG, key: 'screenTemplate' }
  ];

  const [appData, setAppData] = useState<Application>();
  const [navigatorData, setNavigatorData] = useState<any>();
  const [activeTab, setActiveTab] = useState('dataSet');
  const [collapsed, setCollapsed] = useState<boolean>(false);
  const [saveLoading, setSaveLoading] = useState<boolean>(false); // 保存按钮状态
  const [breadcrumbItems, setBreadcrumbItems] = useState<BreadcrumbItemType[]>([
    { key: 'screenTemplate', title: '大屏报表' },
    { key: 'dataSet', title: '数据集' }
  ]); // 菜单路径

  useEffect(() => {
    const currentBreadcrumb = menuData.find((ele) => ele.key === activeTab);
    if (currentBreadcrumb) {
      setBreadcrumbItems([
        { key: 'screenTemplate', title: '大屏报表' },
        { key: currentBreadcrumb.key, title: currentBreadcrumb.title }
      ]);
    }
  }, [activeTab]);

  useEffect(() => {
    if (curAppId) {
      getApplicationData();
      getNavigatorData();
    }
  }, [curAppId]);

  const getApplicationData = async () => {
    const params: GetApplicationReq = {
      id: curAppId
    };
    const res = await getApplication(params);
    setAppData(res);
  };

  // todo 接口获取数据
  const getNavigatorData = async () => {
    setNavigatorData({});
  };

  return (
    <Layout className={styles.appSettingPage}>
      <Layout className={styles.settingContent}>
        <Sider
          trigger={<IconMenuFold fontSize={20} style={{ width: '100%', textAlign: 'right' }} />}
          onCollapse={() => setCollapsed((prev) => !prev)}
        >
          <Menu defaultSelectedKeys={[activeTab]} onClickMenuItem={setActiveTab}>
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
        <div className={styles.rightContent}>
          <AppBreadcrumb items={breadcrumbItems} />
          <Content className={styles.content}>
            <div className={styles.contentInner}>
              {activeTab === 'dataSet' && <DataSet />}
              {activeTab === 'largeScreen' && <LargeScreen />}
              {activeTab === 'screenTemplate' && <ScreenTemplate form={form} data={appData!} />}
            </div>
          </Content>
        </div>
      </Layout>
    </Layout>
  );
};

export default AppSettingPage;
