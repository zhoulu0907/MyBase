import LogoSVG from '@/assets/images/logo.svg';
import { Button, Checkbox, Form, Input, Message, Space, Tabs, Typography } from '@arco-design/web-react';
import { SliderCaptcha, TokenManager, type SliderCaptchaRef } from '@onebase/common';
import { adminLogin, checkCaptchaApi, getCaptchaApi, type LoginRequest } from '@onebase/platform-center';
import { useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useI18n } from '../../../hooks/useI18n';
import { useRememberMe } from '../../../hooks/useRememberMe';
import styles from '../index.module.less';


const { Paragraph } = Typography;
const TabPane = Tabs.TabPane;

interface LoginFormData {
  username?: string;
  password?: string;
  mobile?: string;
  smsCode?: string;
}

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
  useState(() => {
    if (savedAccount) {
      form.setFieldValue('username', savedAccount);
    }
  });

  // 处理记住我状态变化
  const handleRememberMeChange = (checked: boolean) => {
    const username = form.getFieldValue('username') || '';
    saveRememberMe(username, checked);
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

      const loginResp = await adminLogin(values);
      // 显示成功消息并跳转
      if (loginResp.accessToken) {
        Message.success(t('auth.loginSuccess'));
        // 存储 token 信息（需要导入相应的 token 管理工具）
        TokenManager.setToken(
          {
            userId: loginResp.userId,
            accessToken: loginResp.accessToken,
            refreshToken: loginResp.refreshToken,
            expiresTime: loginResp.expiresTime,
          },
          rememberMe
        );
        navigate('/onebase/platform-info');
      } else {
        Message.error(t('auth.loginFailed'));
      }
    } catch (error: any) {
      console.error('登录error:', error);
      Message.error(error.message || t('auth.loginFailed'));
    } finally {
      setLoading(false);
    }
  };

  // 表单提交处理
  const handleSubmit = async (_values: LoginRequest) => {
    handleAccountLogin(_values);


  };

  // 验证码验证成功回调
  const handleCaptchaSuccess = async (token: string) => {
    const values = await form.getFieldsValue();
    console.log('values:', values);
    handleSubmit({username: values.username, password: values.password, captchaVerification: token});
  };

   // 登录按钮点击事件 - 先验证滑块验证码
  const handleLoginClick = async () => {
    try {
      // 先验证表单
      await form.validate();

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
        <div>ONE BASE</div>
      </div>
      <div className={styles.loginFormContainer}>
        <Tabs activeTab={loginType} onChange={(key) => setLoginType(key as 'account' | 'mobile')} type="text">
          <TabPane key="account" title={t('auth.accountLogin')}>
            <Form form={form} layout="vertical" onSubmit={handleLoginClick} autoComplete="off" className={styles.loginForm}>
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

              <Form.Item>
                <Space className={styles.formActions}>
                  <Checkbox checked={rememberMe} onChange={handleRememberMeChange}>
                    {t('auth.rememberMe')}
                  </Checkbox>
                </Space>
              </Form.Item>

              <Form.Item>
                <Button
                  type="primary"
                  onClick={handleLoginClick}
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
            <Form form={form} layout="vertical" onSubmit={handleLoginClick} autoComplete="off" className={styles.loginForm}>
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