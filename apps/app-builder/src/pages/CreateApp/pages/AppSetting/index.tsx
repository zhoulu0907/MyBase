import appPermissionSVG from '@/assets/images/appRelease/app_auth.svg';
import appReleaseSVG from '@/assets/images/appRelease/app_release.svg';
import baseSettingSVG from '@/assets/images/appRelease/base_setting.svg';
import navigatorSettingSVG from '@/assets/images/appRelease/navigator_setting.svg';
import loginPermissionSVG from '@/assets/images/appRelease/app_release.svg';
import AppBreadcrumb from '@/components/Breadcrumb';
import { type Options } from '@/components/CreateApp/const';
import { useAppStore } from '@/store/store_app';
import { Button, Form, Layout, Menu, Message } from '@arco-design/web-react';
import { IconMenuFold } from '@arco-design/web-react/icon';
import {
  getApplication,
  updateApplication,
  updateAppNavigationConfig,
  type Application,
  type GetApplicationReq,
  type UpdateApplicationReq
} from '@onebase/app';
import { useEffect, useState, type FC } from 'react';
import { ReactSVG } from 'react-svg';
import AppReleasePage from '../AppRelease';
import AppPermission from './components/AppPermission';
import BasicSetting from './components/BasicSetting';
import NavigatorSetting from './components/NavigatorSetting';
import styles from './index.module.less';

const Sider = Layout.Sider;
const Content = Layout.Content;
const Footer = Layout.Footer;

interface BreadcrumbItemType {
  key: string;
  title: string;
  path?: string;
}

const AppSettingPage: FC = () => {
  const [form] = Form.useForm();
  const [navigatorForm] = Form.useForm();
  const { curAppId, curAppInfo, setCurAppInfo } = useAppStore();
  const menuData = [
    { title: '基础设置', icon: baseSettingSVG, key: 'baseSetting' },
    { title: '登录设置', icon: loginPermissionSVG, key: 'loginPermission' },
    { title: '应用权限', icon: appPermissionSVG, key: 'appPermission' },
    { title: '应用发布', icon: appReleaseSVG, key: 'appRelease' },
    { title: '导航设置', icon: navigatorSettingSVG, key: 'navigatorSetting' }
  ];

  const [appData, setAppData] = useState<Application>();
  const [navigatorData, setNavigatorData] = useState<any>();
  const [activeTab, setActiveTab] = useState('baseSetting');
  const [collapsed, setCollapsed] = useState<boolean>(false);
  const [saveLoading, setSaveLoading] = useState<boolean>(false); // 保存按钮状态
  const [breadcrumbItems, setBreadcrumbItems] = useState<BreadcrumbItemType[]>([
    { key: 'app-setting', title: '应用发布' },
    { key: 'baseSetting', title: '基础设置' }
  ]); // 菜单路径

  useEffect(() => {
    const currentBreadcrumb = menuData.find((ele) => ele.key === activeTab);
    if (currentBreadcrumb) {
      setBreadcrumbItems([
        { key: 'app-setting', title: '应用发布' },
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

  const handleSave = () => {
    switch (activeTab) {
      case 'baseSetting':
        handleSaveApp();
        break;
      case 'appPermission':
        break;
      case 'navigatorSetting':
        handleSaveNavigator();
        break;
      default:
        break;
    }
  };

  /* 基础设置编辑 */
  const handleSaveApp = async () => {
    form.validate(async (error, data) => {
      try {
        if (error !== null) return;
        setSaveLoading(true);
        const { appCode, appName, appMode, iconColor, iconName, description, tagIds, themeColor } = data;
        const params: UpdateApplicationReq = {
          id: curAppId,
          appCode,
          appMode,
          appName,
          description,
          iconColor,
          iconName,
          tagIds: tagIds?.map((t: Options) => t.value),
          themeColor
        };
        const res = await updateApplication(params);
        if (res) {
          Message.success('保存成功');
          setCurAppInfo({
            ...curAppInfo,
            iconName: iconName || '',
            iconColor: iconColor || '',
            appName: appName || '--'
          });
        }
      } catch (_error) {
        console.error('保存失败 _error:', _error);
      } finally {
        setSaveLoading(false);
      }
    });
  };

  // 导航设置
  const handleSaveNavigator = async () => {
    await navigatorForm.validate();
    const param = navigatorForm.getFieldsValue();
    console.log('param', param);

    const res = await updateAppNavigationConfig({
      id: curAppId,
      webDefaultMenu: param.webHomeType === 'custom' ? param.webDefaultMenu : 'default',
      webNavLayout: param.webNavLayout,
      mobileDefaultMenu: param.mobileHomeType === 'custom' ? param.mobileDefaultMenu : 'default',
      mobileNavLayout: param.mobileNavLayout
    });
    if (res) {
      Message.success('保存成功');
    }
  };

  return (
    <div className={styles.appSettingPage}>
      <Layout style={{ height: '100%' }}>
        <Layout className={styles.settingContent}>
          <Sider
            collapsible
            collapsed={collapsed}
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
              {activeTab === 'baseSetting' && <BasicSetting form={form} data={appData!} />}
              {activeTab === 'appPermission' && <AppPermission />}
              {activeTab === 'appRelease' && <AppReleasePage />}
              {activeTab === 'navigatorSetting' && <NavigatorSetting form={navigatorForm} data={navigatorData} />}

              {(activeTab === 'baseSetting' || activeTab === 'navigatorSetting') && (
                <Button className={styles.saveButton} type="primary" loading={saveLoading} onClick={handleSave}>
                  保存
                </Button>
              )}
            </Content>
          </div>
        </Layout>
      </Layout>
    </div>
  );
};

export default AppSettingPage;
