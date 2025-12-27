//登录设置
import loginBg from '@/assets/images/login_bg.svg';
import loginBgMask from '@/assets/images/login_bg_mask.svg';
import { Button, Card, Radio, Space, Spin, Switch, Typography } from '@arco-design/web-react';
import { IconRefresh, IconUpload } from '@arco-design/web-react/icon';
import { getRuntimeMobileURL, getRuntimeURL, TokenManager, UploadCommonComponent } from '@onebase/common';
import {
  loginConfigListByKeyApi,
  updateLoginConfigApi,
  uploadFile,
  type loginPermissionRes,
  type updateLoginConfigParams
} from '@onebase/platform-center';
import { useEffect, useRef, useState } from 'react';
import { thirdUserConfigKey } from './constant';
import ExternalLoginLinks from './ExternalLoginLinks';
import styles from './index.module.less';
import LoginForm from './loginForm';

interface ILoginPermissionProps {
  appId: string;
}

const LoginPermission: React.FC<ILoginPermissionProps> = ({ appId }) => {
  const uploadRef = useRef(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [imageUrl, setImageUrl] = useState<string>('');
  const tenantId = TokenManager.getTenantInfo()?.tenantId || '';
  const [loginConfigData, setLoginConfigData] = useState<loginPermissionRes[] | null>(null);
  const redirectURL = `${getRuntimeURL()}/#/onebase/runtime/?appId=${appId}&tenantId=${tenantId}`;
  const hrefPC = `${getRuntimeURL()}/#/third/login?redirectURL=${redirectURL}`;
  const hrefMobile = `${getRuntimeMobileURL()}/#/third/login?redirectURL=${redirectURL}`;

  const fetchLoginConfig = async () => {
    try {
      setLoading(true);
      const configKeys = [thirdUserConfigKey.ENABLE, thirdUserConfigKey.REGISTER_SHOW, thirdUserConfigKey.FORGOT_PWD];
      const params = {
        appId,
        configKeys
      };
      const res = await loginConfigListByKeyApi(params);
      setLoginConfigData(res);
    } catch (error) {
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLoginConfig();
  }, []);

  const navigateToRunTime = (text: string) => {
    window.open(text);
  };

  const updateLoginSettings = async (configKey: string, configValue: string) => {
    try {
      const params: updateLoginConfigParams = {
        appId: appId,
        configKey: configKey,
        configValue: configValue
      };
      await updateLoginConfigApi(params);
    } catch (error) {
      console.log('error');
    }
  };

  const handleSwitchChange = async (key: string, value: string) => {
    await updateLoginSettings(key, value);
    await fetchLoginConfig();
  };

  const getEachConfigValue = (type: string) => {
    const targetConfig = loginConfigData?.find((item) => item.configKey === type);
    return targetConfig ? targetConfig.configValue : 'false';
  };

  const convertStrToBoolean = (value: string) => {
    const newValue = value === 'true' ? true : false;
    return newValue;
  };

  const canShowURL = convertStrToBoolean(getEachConfigValue(thirdUserConfigKey.ENABLE) || '');

  return (
    <Spin loading={loading} style={{ width: '100%' }}>
      <div className={styles.loginWrapper}>
        {/* 顶部：登录设置开关 + 外部地址链接 */}
        <div className={styles.headerContent}>
          <Space>
            <Typography.Text bold>登录设置</Typography.Text>
            <Typography.Text>允许外部用户登录</Typography.Text>
            <Switch
              size="small"
              onChange={(value: boolean) => {
                handleSwitchChange(thirdUserConfigKey.ENABLE, JSON.stringify(value));
              }}
              checked={canShowURL}
            />
          </Space>
          {canShowURL && <ExternalLoginLinks hrefPC={hrefPC} hrefMobile={hrefMobile} onNavigate={navigateToRunTime} />}
        </div>

        {/* 中间：宣传区 + 登录预览区 + 右侧配置区 */}
        <div className={styles.bodyContent}>
          <Card className={styles.previewImage} bodyStyle={{ padding: 0 }}>
            {/* TODO(shenyue): 改成真实渲染 */}
            <div className={styles.wrapper}>
              <div
                className={styles.loginPageLeft}
                style={{ backgroundImage: `url(${imageUrl ? imageUrl : loginBgMask})` }}
              >
                <img src={loginBg} alt="loginBg" className={styles.loginBg} />
              </div>
              <div className={styles.loginPageRight}>
                <LoginForm
                  appId={appId}
                  showRegister={convertStrToBoolean(getEachConfigValue(thirdUserConfigKey.REGISTER_SHOW) || '')}
                  showForgotPWD={convertStrToBoolean(getEachConfigValue(thirdUserConfigKey.FORGOT_PWD) || '')}
                />
              </div>
            </div>
          </Card>

          {/* 右侧：配置区 */}
          <div className={styles.rightContent}>
            <Space>
              <Typography.Text type="primary" bold style={{ color: '#000000' }}>
                展示图
              </Typography.Text>
              <Typography.Text type="secondary">(建议尺寸: 720*900)</Typography.Text>
            </Space>

            {/* 上传组件 */}
            <>
              <UploadCommonComponent
                imagePreview={true}
                onUpdateUrl={setImageUrl}
                uploadRef={uploadRef}
                getUploadFile={uploadFile}
              />
              <>
                <Space direction="horizontal">
                  <Button
                    type="primary"
                    icon={<IconUpload />}
                    onClick={() => {
                      (uploadRef as any).current?.getRootDOMNode()?.querySelector('input[type="file"]').click();
                    }}
                  >
                    点击上传
                  </Button>
                  <Button
                    type="default"
                    icon={<IconRefresh />}
                    style={{ marginLeft: 8 }}
                    onClick={() => {
                      setImageUrl(loginBgMask);
                    }}
                  >
                    重置
                  </Button>
                </Space>
                <Space>
                  <Typography.Text type="secondary" className={styles.rightText}>
                    支持jpg、gif、png格式, 不超过5M
                  </Typography.Text>
                </Space>
              </>
            </>
            {/* 注册入口配置 */}
            <Typography.Text type="secondary" className={styles.rightText}>
              注册入口
            </Typography.Text>
            <Radio.Group
              value={getEachConfigValue(thirdUserConfigKey.REGISTER_SHOW)}
              onChange={handleSwitchChange.bind(null, thirdUserConfigKey.REGISTER_SHOW)}
            >
              <Radio value="true">显示</Radio>
              <Radio value="false">隐藏</Radio>
            </Radio.Group>

            {/* 忘记密码入口配置 */}
            <Typography.Text className={styles.rightText}>忘记密码入口</Typography.Text>
            <Radio.Group
              value={getEachConfigValue(thirdUserConfigKey.FORGOT_PWD)}
              onChange={handleSwitchChange.bind(null, thirdUserConfigKey.FORGOT_PWD)}
            >
              <Radio value="true">显示</Radio>
              <Radio value="false">隐藏</Radio>
            </Radio.Group>
          </div>
        </div>
      </div>
    </Spin>
  );
};

export default LoginPermission;
