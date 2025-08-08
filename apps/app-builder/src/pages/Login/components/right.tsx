import LogoSVG from '@/assets/images/ob_logo.svg';
import { Button, Checkbox, Form, Input, Message, Space, Tabs, Typography } from '@arco-design/web-react';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

/**
 * // @ts-expect-error: no types for sm-crypto
import { sm2 } from 'sm-crypto';
 */

import { TokenManager } from '@onebase/common';
import { login, type LoginRequest, type LoginResponse } from '@onebase/platform-center';
import { useI18n } from '../../../hooks/useI18n';
import { useRememberMe } from '../../../hooks/useRememberMe';
import styles from '../index.module.less';

const { Paragraph } = Typography;
const TabPane = Tabs.TabPane;

interface LoginFormData {
  account?: string;
  password?: string;
  mobile?: string;
  smsCode?: string;
}

const Right: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const { t } = useI18n();

  // 使用记住我hook
  const { rememberMe, savedAccount, saveRememberMe } = useRememberMe();

  // 状态管理
  const [loginType, setLoginType] = useState<'account' | 'mobile'>('account');
  const [loading, setLoading] = useState(false);
  const [smsCountdown, setSmsCountdown] = useState(0);

  // 组件初始化时设置保存的账号
  useState(() => {
    if (savedAccount) {
      form.setFieldValue('account', savedAccount);
    }
  });

  // 处理记住我状态变化
  const handleRememberMeChange = (checked: boolean) => {
    const account = form.getFieldValue('account') || '';
    saveRememberMe(account, checked);
  };

  // 发送短信验证码
  const sendSmsCode = async () => {
    try {
      const mobile = form.getFieldValue('mobile');
      if (!mobile) {
        Message.error('请先输入手机号');
        return;
      }

      // 验证手机号格式
      const mobileRegex = /^1[3-9]\d{9}$/;
      if (!mobileRegex.test(mobile)) {
        Message.error('请输入正确的手机号');
        return;
      }

      setLoading(true);

      // TODO: 调用发送短信验证码接口
      // await smsService.sendSmsCode({ mobile });

      // 模拟发送短信验证码
      await new Promise((resolve) => setTimeout(resolve, 1000));

      Message.success('验证码已发送');
      setSmsCountdown(60);

      // 倒计时
      const timer = setInterval(() => {
        setSmsCountdown((prev) => {
          if (prev <= 1) {
            clearInterval(timer);
            return 0;
          }
          return prev - 1;
        });
      }, 1000);
    } catch (error) {
      Message.error('发送失败，请重试');
    } finally {
      setLoading(false);
    }
  };

  //   TODO(mickey): 调通后解除注释
  // 账号密码登录
  const handleAccountLogin = async (values: LoginFormData) => {
    try {
      setLoading(true);

      const loginData: LoginRequest = {
        username: values.account!,
        password: values.password!
      };

      const response: LoginResponse = await login(loginData);

      if (response.accessToken) {
        // 使用 TokenManager 存储 token 信息
        TokenManager.setToken(
          {
            userId: response.userId,
            accessToken: response.accessToken,
            refreshToken: response.refreshToken,
            expiresTime: response.expiresTime
          },
          rememberMe
        );

        // 保存记住我状态和账号信息
        saveRememberMe(values.account!, rememberMe);

        Message.success(t('auth.loginSuccess'));
        // 跳转到首页
        navigate('/onebase/my-app');

        return;
      } else {
        Message.error(t('auth.loginFailed'));
      }
    } catch (error: any) {
      console.error('登录失败:', error);
      Message.error(error.message || t('auth.invalidCredentials'));
    } finally {
      setLoading(false);
    }
  };

  // 表单提交处理
  const handleSubmit = (_values: LoginFormData) => {
    handleAccountLogin(_values);
  };

  return (
    <div className={styles.loginPageRight}>
      <div className={styles.loginPageHeader}>
        <img src={LogoSVG} alt="logo" />
        {/* <div>ONE BASE</div> */}
      </div>
      <div className={styles.loginFormContainer}>
        <Tabs activeTab={loginType} onChange={(key) => setLoginType(key as 'account' | 'mobile')} type="text">
          <TabPane key="account" title={t('auth.accountLogin')}>
            <Form form={form} layout="vertical" onSubmit={handleSubmit} autoComplete="off" className={styles.loginForm}>
              <Form.Item
                field="account"
                initialValue=""
                rules={[
                  { required: true, message: '请输入账号' },
                  { minLength: 3, message: '账号至少3个字符' }
                ]}
              >
                <Input placeholder={t('auth.userAccount')} allowClear size="large" />
              </Form.Item>

              <Form.Item
                field="password"
                initialValue=""
                rules={[
                  { required: true, message: '请输入密码' },
                  { minLength: 6, message: '密码至少6个字符' }
                ]}
              >
                <Input.Password placeholder={t('auth.password')} allowClear size="large" />
              </Form.Item>

              <Form.Item>
                <Space className={styles.formActions}>
                  <Checkbox checked={rememberMe} onChange={handleRememberMeChange}>
                    {t('auth.rememberMe')}
                  </Checkbox>
                  <Button type="text" size="small">
                    {t('auth.forgotPassword')}
                  </Button>
                </Space>
              </Form.Item>

              <Form.Item>
                <Button
                  type="primary"
                  htmlType="submit"
                  long
                  loading={loading}
                  size="large"
                  className={styles.loginButton}
                >
                  {t('auth.loginButton')}
                </Button>
              </Form.Item>
            </Form>
          </TabPane>

          <TabPane key="mobile" title={t('auth.smsLogin')}>
            <Form form={form} layout="vertical" onSubmit={handleSubmit} autoComplete="off" className={styles.loginForm}>
              <Form.Item
                field="mobile"
                rules={[
                  { required: true, message: '请输入手机号' },
                  {
                    validator: (value: string | undefined) => {
                      if (!value) return Promise.resolve();
                      const mobileRegex = /^1[3-9]\d{9}$/;
                      if (!mobileRegex.test(value)) {
                        return Promise.reject('请输入正确的手机号');
                      }
                      return Promise.resolve();
                    }
                  }
                ]}
              >
                <Input placeholder={t('auth.mobile')} allowClear size="large" maxLength={11} />
              </Form.Item>

              <Form.Item
                field="smsCode"
                rules={[
                  { required: true, message: '请输入短信验证码' },
                  { length: 6, message: '验证码为6位数字' }
                ]}
              >
                <Space align="center" className={styles.smsContainer}>
                  <Input placeholder={t('auth.smsCode')} allowClear size="large" maxLength={6} />
                  <Button
                    type="secondary"
                    size="large"
                    disabled={smsCountdown > 0}
                    loading={loading}
                    onClick={sendSmsCode}
                    className={styles.smsButton}
                  >
                    {smsCountdown > 0 ? `${smsCountdown}s` : t('auth.getSmsCode')}
                  </Button>
                </Space>
              </Form.Item>

              <Form.Item>
                <Button
                  type="primary"
                  htmlType="submit"
                  long
                  loading={loading}
                  size="large"
                  className={styles.loginButton}
                >
                  {t('auth.loginButton')}
                </Button>
              </Form.Item>
            </Form>
          </TabPane>
        </Tabs>
      </div>
      <div className={styles.loginFooter}>
        <Paragraph className={styles.footerText}>
          登录即表示同意
          <Button type="text" size="small">
            《用户协议》
          </Button>
          和
          <Button type="text" size="small">
            《隐私政策》
          </Button>
        </Paragraph>
      </div>
    </div>
  );
};

export default Right;
