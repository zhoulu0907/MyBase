//登录设置
import loginBg from '@/assets/images/login_bg.svg';
import { Button, Card, Radio, Space, Spin, Switch, Typography, Message } from '@arco-design/web-react';
import { IconRefresh, IconUpload } from '@arco-design/web-react/icon';
import { getRuntimeMobileURL, getRuntimeURL, TokenManager, UploadCommonComponent } from '@onebase/common';
import {
  getAppNavigationConfig,
  updateAppNavigationConfig,
  type GetAppNavigationConfigRes
} from '@onebase/app';
import { downloadFile, uploadFile } from '@onebase/platform-center';
import { useEffect, useRef, useState } from 'react';
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
  const [configData, setConfigData] = useState<GetAppNavigationConfigRes | null>(null);
  const redirectURL = `${getRuntimeURL()}/#/onebase/${tenantId}/${appId}/runtime/`;
  const hrefPC = `${getRuntimeURL()}/#/third/login?redirectURL=${redirectURL}`;
  const hrefMobile = `${getRuntimeMobileURL()}/#/third/login?redirectURL=${redirectURL}`;

  const fetchConfig = async () => {
    try {
      setLoading(true);
      const res = await getAppNavigationConfig({ id: appId });
      setConfigData(res);
      if (res.appLoginMainPic) {
        const url = await downloadFile(res.appLoginMainPic, appId);
        if (url) {
          setImageUrl(url);
        }
      }
    } catch (error) {
      console.error('获取配置失败:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchConfig();
  }, []);

  const navigateToRunTime = (text: string) => {
    window.open(text);
  };

  const updateConfig = async (params: Partial<GetAppNavigationConfigRes>) => {
    try {
      setLoading(true);
      await updateAppNavigationConfig({
        id: appId,
        ...params
      });
      Message.success('保存成功');
      await fetchConfig();
    } catch (error) {
      console.error('更新配置失败:', error);
      Message.error('保存失败');
    } finally {
      setLoading(false);
    }
  };

  const handleSwitchChange = async (key: string, value: boolean) => {
    const valueStr = value ? '1' : '0';
    await updateConfig({ [key]: valueStr });
  };

  const handleImageUpload = async (formData: FormData, onProgress?: (progressEvent: ProgressEvent) => void) => {
    try {
      const res = await uploadFile(formData, onProgress);
      if (res) {
        await updateConfig({ appLoginMainPic: res });
      }
      return res;
    } catch (error) {
      console.error('上传图片失败:', error);
      Message.error('上传图片失败');
      return '';
    }
  };

  const handleImageUrlUpdate = async (fileId: string) => {
    if (fileId) {
      const url = await downloadFile(fileId, appId);
      if (url) {
        setImageUrl(url);
      }
    } else {
      setImageUrl('');
    }
  };

  const handleResetImage = async () => {
    await updateConfig({ appLoginMainPic: '' });
    setImageUrl('');
  };

  const getConfigValue = (key: keyof GetAppNavigationConfigRes) => {
    return configData?.[key] || '0';
  };

  const convertStrToBoolean = (value: string) => {
    return value === '1';
  };

  const canShowURL = convertStrToBoolean(getConfigValue('appThirdUserEnable'));

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
                handleSwitchChange('appThirdUserEnable', value);
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
              <div className={styles.loginPageLeft}>
                <img src={imageUrl || loginBg} alt="loginBg" className={styles.loginBg} />
              </div>
              <div className={styles.loginPageRight}>
                <LoginForm
                  appId={appId}
                  showRegister={convertStrToBoolean(getConfigValue('appUserRegisterShow'))}
                  showForgotPWD={convertStrToBoolean(getConfigValue('appUserForgetPwdShow'))}
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
                onUpdateUrl={handleImageUrlUpdate}
                uploadRef={uploadRef}
                getUploadFile={handleImageUpload}
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
                    onClick={handleResetImage}
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
              value={getConfigValue('appUserRegisterShow')}
              onChange={(value: string) => handleSwitchChange('appUserRegisterShow', value === '1')}
            >
              <Radio value="1">显示</Radio>
              <Radio value="0">隐藏</Radio>
            </Radio.Group>

            {/* 忘记密码入口配置 */}
            <Typography.Text className={styles.rightText}>忘记密码入口</Typography.Text>
            <Radio.Group
              value={getConfigValue('appUserForgetPwdShow')}
              onChange={(value: string) => handleSwitchChange('appUserForgetPwdShow', value === '1')}
            >
              <Radio value="1">显示</Radio>
              <Radio value="0">隐藏</Radio>
            </Radio.Group>
          </div>
        </div>
      </div>
    </Spin>
  );
};

export default LoginPermission;
