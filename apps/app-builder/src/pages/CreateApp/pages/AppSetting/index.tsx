import { type FC, useEffect, useState } from 'react';
import { /* useNavigate, */ useSearchParams } from 'react-router-dom';
// import { useTranslation } from 'react-i18next';
import { Layout, Button, Menu, Form, Message } from '@arco-design/web-react';

import {
  getApplication,
  updateApplication,
  type Application,
  type GetApplicationReq,
  type UpdateApplicationReq
} from '@onebase/app';
import BasicSetting from './components/BasicSetting';
import AppPermission from './components/AppPermission';
import baseSettingSVG from '@/assets/images/base_setting.svg';
import baseSettingActiveSVG from '@/assets/images/base_setting_active.svg';
import appPermissionSVG from '@/assets/images/app_auth.svg';
import appPermissionActiveSVG from '@/assets/images/app_auth_active.svg';
import { type Options } from '@/components/CreateApp/const';
import styles from './index.module.less';

const MenuItem = Menu.Item;
const Sider = Layout.Sider;
const Content = Layout.Content;
const Footer = Layout.Footer;

const AppSettingPage: FC = () => {
  // const { t } = useTranslation();
  // const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const appId = searchParams.get('appId') || '';

  const [form] = Form.useForm();

  const [appData, setAppData] = useState<Application>();
  const [activeTab, setActiveTab] = useState('baseSetting');
  const [saveLoading, setSaveLoading] = useState<boolean>(false); // 保存按钮状态

  useEffect(() => {
    getApplicationData();
  }, []);

  const getApplicationData = async () => {
    const params: GetApplicationReq = {
      id: appId
    };
    const res = await getApplication(params);
    setAppData(res);
    console.log(res, 'app info');
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
      if (error !== null) return;
      setSaveLoading(true);
      const { appCode, appName, iconColor, iconName, description, tagIds, themeColor } = data;
      const params: UpdateApplicationReq = {
        id: appId,
        appCode,
        appMode: 'classic',
        appName,
        datasourceId: 1,
        description,
        iconColor,
        iconName,
        tagIds: tagIds?.map((t: Options) => t.value),
        themeColor
      };
      const res = await updateApplication(params);
      if (res) {
        Message.success('保存成功');
      }
      setSaveLoading(false);
    });
  };

  return (
    <div className={styles.appSettingPage}>
      <Layout style={{ height: '100%' }}>
        <Layout>
          <Sider style={{ width: 200 }}>
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
            {activeTab === 'baseSetting' && <BasicSetting form={form} data={appData} />}
            {activeTab === 'appPermission' && <AppPermission />}
          </Content>
        </Layout>
        <Footer className={styles.footer}>
          <Button type="primary" loading={saveLoading} onClick={handleSave}>
            保存
          </Button>
        </Footer>
      </Layout>
    </div>
  );
};

export default AppSettingPage;
