//登录设置
import { useEffect, useState } from 'react';
import { Switch, Typography, Upload, Button, Radio, Card, Space, Spin } from '@arco-design/web-react';
import { IconUpload, IconRefresh, IconCopy } from '@arco-design/web-react/icon';
import { copyToClipboard, getRuntimeURL, TokenManager } from '@onebase/common';
import styles from './index.module.less';
import loginBg from '../../../../../../assets/images/login_bg.svg';
import loginBgMask from '../../../../../../assets/images/login_bg_mask.svg';
import {
  loginConfigListByKeyApi,
  updateLoginConfigApi,
  type loginPermissionRes,
  type updateLoginConfigParams
} from '@onebase/platform-center';
import { thirdUserConfigKey } from './constant';

interface ILoginPermissionProps {
  appId: string;
}

const LoginPermission: React.FC<ILoginPermissionProps> = ({ appId }) => {
  const [loading, setLoading] = useState<boolean>(false);
  const tenantId = TokenManager.getTenantInfo()?.tenantId || '';
  const [loginConfigData, setLoginConfigData] = useState<loginPermissionRes[] | null>(null);
  const redirectURL = `${getRuntimeURL()}/#/onebase/runtime/?appId=${appId}&tenantId=${tenantId}`;
  const href = `${getRuntimeURL()}/#/third/login?redirectURL=${redirectURL}`;

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
    const targetConfig = loginConfigData?.find(item => item.configKey === type);
    return targetConfig ? targetConfig.configValue : "false";
  };

  const canShowURL = JSON.parse(getEachConfigValue(thirdUserConfigKey.ENABLE) || '')

  return (
    <Spin loading={loading} style={{ width: '100%' }}>
      <div className={styles.loginWrapper}>
        {/* 顶部：登录设置开关 + 外部地址链接 */}
        <div className={styles.headerContent}>
          <Space>
            <Typography.Text>允许外部用户登录</Typography.Text>
            <Switch
              onChange={(value: boolean) => {
                handleSwitchChange(thirdUserConfigKey.ENABLE, JSON.stringify(value))
              }}
              checked={canShowURL}
            />
          </Space>
          {canShowURL && <div className={styles.linkContent}>
            <span>本应用外部用户登录/注册地址:</span>
            <div className={styles.linkText} onClick={() => navigateToRunTime(href)}>
              www.onebase.com/app/externalusers
            </div>
            <IconCopy onClick={() => copyToClipboard(href)} style={{ fontSize: 16 }} />
          </div>}
        </div>

        {/* 中间：宣传区 + 登录预览区 + 右侧配置区 */}
        <div className={styles.bodyContent}>
          <Card
            className={styles.previewImage}
            title="登录页预览"
            style={{ width: 420, height: 500, flexShrink: 0 }}
            bodyStyle={{ padding: 0 }}
          >
            {/* <img src={loginBg} width={210} height={500} alt="loginBg" className={styles.loginBg} /> */}
          </Card>

          {/* 右侧：配置区 */}
          <div className={styles.rightContent}>
            <Space>
              展示图
              <Typography.Text type="secondary">(建议尺寸: 720*900)</Typography.Text>
            </Space>

            {/* 上传组件 */}
            <Upload
              action="/api/upload" // 替换为实际上传接口
              listType="text"
              showUploadList={false}
              style={{ marginBottom: 16 }}
            >
              <Button type="primary" icon={<IconUpload />}>
                点击上传
              </Button>
            </Upload>
            <Button
              type="default"
              icon={<IconRefresh />}
              style={{ marginLeft: 8 }}
              onClick={() => console.log('重置展示图')}
            >
              重置
            </Button>
            <Typography.Text type="secondary" className={styles.rightText}>
              支持jpg、gif、png格式, 不超过5M
            </Typography.Text>

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
