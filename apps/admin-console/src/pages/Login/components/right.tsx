import LogoSVG from '@/assets/images/ob_logo.svg';
import { Button, Checkbox, Form, Input, Message, Space, Typography } from '@arco-design/web-react';
import { IconLock, IconUser } from '@arco-design/web-react/icon';
import { getOrCreateDeviceInfo, SliderCaptcha, TokenManager, type SliderCaptchaRef } from '@onebase/common';
import { adminLogin, checkCaptchaApi, getCaptchaApi, type LoginRequest } from '@onebase/platform-center';
import { useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useI18n } from '../../../hooks/useI18n';
import { useRememberMe } from '../../../hooks/useRememberMe';
import styles from '../index.module.less';

const { Paragraph } = Typography;

const Right: React.FC = () => {
  const navigate = useNavigate();
  const [accountForm] = Form.useForm();
  const { t } = useI18n();
  const sliderCaptchaRef = useRef<SliderCaptchaRef>(null);

  const hash = window.location.hash;
  const match = hash.match(/\/tenant\/([^\/]+)/);
  const tenantId = match ? match[1] : '1';

  // 使用记住我hook
  const { rememberMe, savedAccount, saveRememberMe } = useRememberMe();

  // 状态管理
  const [loading, setLoading] = useState(false);

  // 组件初始化时设置保存的账号
  useState(() => {
    if (savedAccount) {
      accountForm.setFieldValue('username', savedAccount);
    }

    // 如果已经登录了就自动跳转到首页
    if (TokenManager.isTokenValid()) {
      navigate('/onebase/platform-info');
    }
  });

  // 处理记住我状态变化
  const handleRememberMeChange = (checked: boolean) => {
    const username = accountForm.getFieldValue('username') || '';
    saveRememberMe(username, checked);
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
      const headers = {
        'X-Tenant-Id': tenantId
      };
      const loginResp = await adminLogin(values, headers);
      // 显示成功消息并跳转
      console.log('loginResp: ', loginResp);
      if (loginResp.accessToken) {
        Message.success(t('auth.loginSuccess'));
        // 存储 token 信息（需要导入相应的 token 管理工具）
        TokenManager.setToken(
          {
            userId: loginResp.userId,
            accessToken: loginResp.accessToken,
            refreshToken: loginResp.refreshToken,
            expiresTime: loginResp.expiresTime,
            tenantId: loginResp.tenantId
          },
          rememberMe
        );
        navigate('/onebase/platform-info');
      }
    } catch (error: any) {
      console.error('登录error:', error);
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
    // 验证码通过后重新提交表单
    const values = await accountForm.getFieldsValue();

    const deviceId = await getOrCreateDeviceInfo();
    handleSubmit({
      username: values.username,
      password: values.password,
      captchaVerification: token,
      deviceId: deviceId
    });
  };

  // 登录按钮点击事件 - 先验证滑块验证码
  const handleLoginClick = async () => {
    try {
      // 先验证表单
      await accountForm.validate();

      if (accountForm.getFieldValue('captchaVerification')) {
        // TODO(mickey): refactor
        const deviceId = await getOrCreateDeviceInfo();

        handleAccountLogin({
          username: accountForm.getFieldValue('username'),
          password: accountForm.getFieldValue('password'),
          captchaVerification: accountForm.getFieldValue('captchaVerification'),
          deviceId: deviceId
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
      <div className={styles.loginFormContainer}>
        <img src={LogoSVG} alt="logo" />
        <h1 className={styles.title}>欢迎登录平台管理系统</h1>
        <Form
          form={accountForm}
          layout="vertical"
          onSubmit={handleLoginClick}
          autoComplete="off"
          requiredSymbol={false}
          className={styles.loginForm}
        >
          <Form.Item
            field="username"
            label="用户名"
            initialValue=""
            rules={[
              { required: true, message: '请输入账号' },
              { minLength: 3, message: '账号至少3个字符' }
            ]}
          >
            <Input placeholder={t('auth.userAccount')} allowClear size="large" prefix={<IconUser />} />
          </Form.Item>

          <Form.Item
            field="password"
            label="密码"
            initialValue=""
            rules={[
              { required: true, message: '请输入密码' },
              { minLength: 6, message: '密码至少6个字符' }
            ]}
          >
            <Input.Password placeholder={t('auth.password')} allowClear size="large" prefix={<IconLock />} />
          </Form.Item>

          <Form.Item field="captchaVerification" hidden={true}>
            <Input allowClear size="large" />
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
          <span>《用户协议》</span>和<span>《隐私政策》</span>
        </Paragraph>
      </div>
    </div>
  );
};

export default Right;
