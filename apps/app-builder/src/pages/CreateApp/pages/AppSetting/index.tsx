import { Button, Form, Layout, Menu } from '@arco-design/web-react';
import { type FC, useState } from 'react';
// import { useNavigate } from 'react-router-dom';

import AppPermission from './components/AppPermission';
import BasicSetting from './components/BasicSetting';

import appPermissionSVG from '@/assets/images/app_auth.svg';
import appPermissionActiveSVG from '@/assets/images/app_auth_active.svg';
import baseSettingSVG from '@/assets/images/base_setting.svg';
import baseSettingActiveSVG from '@/assets/images/base_setting_active.svg';
import styles from './index.module.less';

const MenuItem = Menu.Item;
const Sider = Layout.Sider;
const Content = Layout.Content;
const Footer = Layout.Footer;

const AppSettingPage: FC = () => {
  // const navigate = useNavigate();
  const [form] = Form.useForm();

  const [activeTab, setActiveTab] = useState('baseSetting');

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
            {activeTab === 'baseSetting' && <BasicSetting form={form} />}
            {activeTab === 'appPermission' && <AppPermission />}
          </Content>
        </Layout>
        <Footer className={styles.footer}>
          <Button type="primary" onClick={() => {}}>
            保存
          </Button>
        </Footer>
      </Layout>
    </div>
  );
};

export default AppSettingPage;
