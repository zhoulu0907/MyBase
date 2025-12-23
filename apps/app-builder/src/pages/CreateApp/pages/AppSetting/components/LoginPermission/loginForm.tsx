import { Button, Checkbox, Form, Input, Space, Tabs, Typography } from '@arco-design/web-react';
import { IconLock, IconMobile } from '@arco-design/web-react/icon';
import { getApplication } from '@onebase/app';
import { appIconMap } from '@onebase/ui-kit';
import { useEffect, useState } from 'react';
import styles from './index.module.less';
import { ThirdLoginMap, ThirdLoginType } from './constant';
import DynamicIcon from '@/components/DynamicIcon';
import { useAppStore } from '@/store';
import { useI18n } from '@/hooks/useI18n';

const { Paragraph } = Typography;
const TabPane = Tabs.TabPane;

interface ILoginFormProps {
  appId: string;
  showForgotPWD: boolean;
  showRegister: boolean;
}

const LoginForm: React.FC<ILoginFormProps> = ({ appId, showForgotPWD, showRegister }) => {
  const [form] = Form.useForm();
  const { t } = useI18n();
  const [loginType, setLoginType] = useState<string>(ThirdLoginType.VERIFYCODE);
  const { curAppInfo, setCurAppInfo } = useAppStore();

  const handleGetApplication = async () => {
    if (appId) {
      const appResp = await getApplication({ id: appId });
      if (appResp) {
        setCurAppInfo({
          iconName: appResp.iconName || '',
          iconColor: appResp.iconColor || '',
          appName: appResp.appName || '--',
          appStatus: appResp.appStatus || 0
        });
      }
    }
  };

  useEffect(() => {
    if (appId) {
      handleGetApplication();
    }
  }, [appId]);

  return (
    <div className={styles.loginPageRight}>
      <div className={styles.loginFormContainer}>
        {curAppInfo.iconName && (
          <div className={styles.appInfo}>
            <div
              className={styles.appIcon}
              style={{
                background: curAppInfo.iconColor || 'transparent'
              }}
            >
              <DynamicIcon
                IconComponent={appIconMap[curAppInfo.iconName as keyof typeof appIconMap]}
                theme="outline"
                size="40"
                fill="#F2F3F5"
              />
            </div>
            <div className={styles.appName}>{curAppInfo.appName}</div>
          </div>
        )}
        <h1 className={styles.title}>欢迎登录</h1>

        <Form form={form} layout="vertical" className={styles.loginForm}>
          <Tabs activeTab={loginType} onChange={setLoginType}>
            {ThirdLoginMap?.map((item) => {
              return (
                <TabPane key={item.value} title={item.label}>
                  <Form.Item label="手机号" field="mobile" disabled>
                    <Input placeholder="输入手机号" maxLength={11} prefix={<IconMobile />} />
                  </Form.Item>
                  {item.value === ThirdLoginType.VERIFYCODE && (
                    <Form.Item>
                      <Input
                        placeholder="请输入手机验证码"
                        disabled
                        suffix={
                          <Button type="text" size="small">
                            获取手机验证码
                          </Button>
                        }
                      />
                    </Form.Item>
                  )}
                  {item.value === ThirdLoginType.PASSWORD && (
                    <Form.Item field="password" label="密码" disabled>
                      <Input.Password placeholder={t('auth.password')} allowClear size="large" prefix={<IconLock />} />
                    </Form.Item>
                  )}

                  <Form.Item>
                    <Space className={styles.formActions}>
                      <Checkbox>{t('auth.rememberMe')}</Checkbox>
                      {showForgotPWD && (
                        <Button type="text" size="small">
                          {t('auth.forgotPassword')}
                        </Button>
                      )}
                    </Space>
                  </Form.Item>

                  <Form.Item>
                    <Button type="primary" htmlType="submit" long size="large" className={styles.loginButton}>
                      {t('auth.loginButton')}
                    </Button>

                    {showRegister && (
                      <div
                        style={{
                          margin: '8px auto',
                          textAlign: 'center'
                        }}
                      >
                        没有账号？登录即注册
                      </div>
                    )}
                  </Form.Item>
                </TabPane>
              );
            })}
          </Tabs>
        </Form>
      </div>
      <div className={styles.loginFooter}>
        <Paragraph className={styles.footerText}>
          登录即表示同意
          <span>《用户协议》</span>和<span>《隐私政策》</span>
        </Paragraph>
      </div>
    </div>
  );
};

export default LoginForm;
