import appPermissionSVG from '@/assets/images/app_auth.svg';
import appPermissionActiveSVG from '@/assets/images/app_auth_active.svg';
import baseSettingSVG from '@/assets/images/base_setting.svg';
import baseSettingActiveSVG from '@/assets/images/base_setting_active.svg';
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
import AppPermission from './components/AppPermission';
import BasicSetting from './components/BasicSetting';
import { useAppStore } from '@/store/store_app';
import styles from './index.module.less';

const MenuItem = Menu.Item;
const Sider = Layout.Sider;
const Content = Layout.Content;
const Footer = Layout.Footer;

const AppSettingPage: FC = () => {
  const [form] = Form.useForm();
  const { curAppId, curAppInfo, setCurAppInfo } = useAppStore();

  const [appData, setAppData] = useState<Application>();
  const [activeTab, setActiveTab] = useState('baseSetting');
  const [collapsed, setCollapsed] = useState<boolean>(false);
  const [saveLoading, setSaveLoading] = useState<boolean>(false); // 保存按钮状态

  useEffect(() => {
    curAppId && getApplicationData();
  }, [curAppId]);

  const getApplicationData = async () => {
    const params: GetApplicationReq = {
      id: curAppId
    };
    const res = await getApplication(params);
    setAppData(res);
  };

  const handleSave = () => {
    switch (activeTab) {
      case 'baseSetting':
        handleSaveApp();
        break;

      case 'appPermission':
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
          })
        }
      } catch (_error) {
      } finally {
        setSaveLoading(false);
      }
    });
  };

  return (
    <div className={styles.appSettingPage}>
      <Layout style={{ height: '100%' }}>
        <Layout>
          <Sider
            collapsible
            collapsed={collapsed}
            trigger={<IconMenuFold fontSize={20} style={{ width: '100%', textAlign: 'right' }} />}
            onCollapse={() => setCollapsed((prev) => !prev)}
          >
            <Menu defaultSelectedKeys={[activeTab]} onClickMenuItem={setActiveTab}>
              <MenuItem key="baseSetting" style={{ display: 'flex' }}>
                <img
                  src={activeTab === 'baseSetting' ? baseSettingActiveSVG : baseSettingSVG}
                  alt="基础设置"
                  style={{ marginRight: 16 }}
                />
                基础设置
              </MenuItem>
              <MenuItem key="appPermission" style={{ display: 'flex' }}>
                <img
                  src={activeTab === 'appPermission' ? appPermissionActiveSVG : appPermissionSVG}
                  alt="应用权限"
                  style={{ marginRight: 16 }}
                />
                应用权限
              </MenuItem>
            </Menu>
          </Sider>
          <Content className={styles.content}>
            {activeTab === 'baseSetting' && <BasicSetting form={form} data={appData!} />}
            {activeTab === 'appPermission' && <AppPermission />}
          </Content>
        </Layout>
        {activeTab === 'baseSetting' && (
          <Footer className={styles.footer}>
            <Button type="primary" loading={saveLoading} onClick={handleSave}>
              保存
            </Button>
          </Footer>
        )}
      </Layout>
    </div>
  );
};

export default AppSettingPage;
