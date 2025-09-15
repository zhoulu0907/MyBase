import LogoSVG from '@/assets/images/ob_logo.svg';
import { Button, Checkbox, Form, Input, Message, Space, Tabs, Typography } from '@arco-design/web-react';
import { getHashQueryParam, SliderCaptcha, TokenManager, type SliderCaptchaRef } from '@onebase/common';
import { checkCaptchaApi, getCaptchaApi, login, type LoginRequest, type LoginResponse } from '@onebase/platform-center';
import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useI18n } from '../../../hooks/useI18n';
import { useRememberMe } from '../../../hooks/useRememberMe';
import styles from '../index.module.less';
import { UserPermissionManager } from '@/utils/permission';
import { getPermissionInfo } from '@onebase/platform-center';

const { Paragraph } = Typography;
const TabPane = Tabs.TabPane;

const Right: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const { t } = useI18n();
  const sliderCaptchaRef = useRef<SliderCaptchaRef>(null);

  const hash = window.location.hash;
  const match = hash.match(/\/tenant\/([^\/]+)/);
  const tenantId = match ? match[1] : '1';

  // 使用记住我hook
  const { rememberMe, savedAccount, saveRememberMe } = useRememberMe();

  // 状态管理
  const [loginType, setLoginType] = useState<'account' | 'mobile'>('account');
  const [loading, setLoading] = useState(false);
  const [smsCountdown, setSmsCountdown] = useState(0);

  // 组件初始化时设置保存的账号
  useEffect(() => {
    if (savedAccount) {
      form.setFieldValue('account', savedAccount);
    }

    // 如果已经登录了就自动跳转到首页
    if (TokenManager.isTokenValid()) {
      const redirectURL = getHashQueryParam('redirectURL');
      if (redirectURL) {
        window.location.href = redirectURL;
      } else {
        // 跳转到首页
        navigate('/onebase/my-app');
      }
    }
  }, []);

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

  const getInfo = async () => {
    const res = await getPermissionInfo();
    UserPermissionManager.setUserPermissionInfo(res);
  };

  // 账号密码登录
  const handleAccountLogin = async (values: LoginRequest) => {
    setLoading(true);

    try {
      const captchaVerification = values.captchaVerification;
      // 如果没有验证码token，则先进行验证码验证
      if (!captchaVerification) {
        // 显示滑块验证码
        sliderCaptchaRef.current?.showCaptcha();
        return;
      }

      const headers = {
        'Tenant-Id': tenantId
      };

      const loginData: LoginRequest = {
        username: values.username!,
        password: values.password!,
        captchaVerification: captchaVerification
      };

      const response: LoginResponse = await login(loginData, headers);

      if (response.accessToken) {
        // 使用 TokenManager 存储 token 信息
        TokenManager.setToken(
          {
            userId: response.userId,
            accessToken: response.accessToken,
            refreshToken: response.refreshToken,
            expiresTime: response.expiresTime,
            tenantId: response.tenantWebsite
          },
          rememberMe
        );

        // 保存记住我状态和账号信息
        saveRememberMe(values.username!, rememberMe);

        Message.success(t('auth.loginSuccess'));

        const redirectURL = getHashQueryParam('redirectURL');
        if (redirectURL) {
          window.location.href = redirectURL;
        } else {
          await getInfo()
          // 跳转到首页
          navigate('/onebase/my-app');
        }

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
  const handleSubmit = (_values: LoginRequest) => {
    handleAccountLogin(_values);
  };

  // 验证码验证成功回调
  const handleCaptchaSuccess = async (token: string) => {
    const values = await form.getFieldsValue();
    console.log('values:', values);
    handleSubmit({ username: values.username, password: values.password, captchaVerification: token });
  };

  // 登录按钮点击事件 - 先验证滑块验证码
  const handleLoginClick = async () => {
    try {
      // 先验证表单
      await form.validate();

      if (form.getFieldValue('captchaVerification')) {
        handleAccountLogin({
          username: form.getFieldValue('username'),
          password: form.getFieldValue('password'),
          captchaVerification: form.getFieldValue('captchaVerification')
        });
        return;
      }

      // 显示滑块验证码
      sliderCaptchaRef.current?.showCaptcha();
    } catch (error) {
      console.error('表单验证失败:', error);
      Message.error('请检查表单填写是否正确');
    }
  };

  return (
    <div className={styles.loginPageRight}>
      <div className={styles.loginPageHeader}>
        <img src={LogoSVG} alt="logo" />
      </div>
      <div className={styles.loginFormContainer}>
        <Tabs activeTab={loginType} onChange={(key) => setLoginType(key as 'account' | 'mobile')} type="text">
          <TabPane key="account" title={t('auth.accountLogin')}>
            <Form
              form={form}
              layout="vertical"
              onSubmit={handleLoginClick}
              autoComplete="off"
              className={styles.loginForm}
            >
              <Form.Item
                field="username"
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

              <Form.Item field="captchaVerification" hidden={true}>
                <Input allowClear size="large" placeholder="请输入隐藏验证码" />
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
            <Form
              form={form}
              layout="vertical"
              onSubmit={handleLoginClick}
              autoComplete="off"
              className={styles.loginForm}
            >
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

      {/* 滑块验证码组件 */}
      <SliderCaptcha
        ref={sliderCaptchaRef}
        getCaptchaApi={getCaptchaApi}
        checkCaptchaApi={checkCaptchaApi}
        onSuccess={handleCaptchaSuccess}
        onError={() => setLoading(false)}
      />

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
