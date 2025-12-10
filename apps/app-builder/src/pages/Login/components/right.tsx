import LogoSVG from '@/assets/images/ob_logo.svg';

import { Button, Checkbox, Form, Input, Message, Space, Typography } from '@arco-design/web-react';
import { IconLock, IconUser } from '@arco-design/web-react/icon';
import {
  getHashQueryParam,
  getOrCreateDeviceInfo,
  SECURITY_CATEGORY_MFA,
  SliderCaptcha,
  TokenManager,
  type SliderCaptchaRef
} from '@onebase/common';
import {
  checkCaptchaApi,
  getCaptchaApi,
  getTenantSecurityConfig,
  tenantLogin,
  type LoginRequest,
  type TenantLoginResponse,
  type TenantSecurityConfig
} from '@onebase/platform-center';
import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useI18n } from '../../../hooks/useI18n';
import { useRememberMe } from '../../../hooks/useRememberMe';
import styles from '../index.module.less';
import { VerifyModal } from './verify';

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

  // 新增用于校验手机号和校验邮箱的状态
  const [mfaVerifyStatus, setMfaVerifyStatus] = useState<string>('');
  const [showVerifyModal, setShowVerifyModal] = useState(false);

  const [verifyCode, setVerifyCode] = useState<string>('');
  // 保存当前的 captchaVerification，用于 MFA 验证后登录
  const savedCaptchaVerificationRef = useRef<string>('');

  // 组件初始化时设置保存的账号
  useEffect(() => {
    if (savedAccount) {
      accountForm.setFieldValue('account', savedAccount);
    }

    // handleGetTenantSecurityConfig();

    // 如果已经登录了就自动跳转到首页
    // if (TokenManager.isTokenValid()) {
    //   const redirectURL = getHashQueryParam('redirectURL');
    //   if (redirectURL) {
    //     window.location.href = redirectURL;
    //   } else {
    //     navigate('/onebase/enterprise-app');
    //   }
    // }
  }, []);

  const handleGetTenantSecurityConfig = async () => {
    const req = {
      tenantId: tenantId,
      categoryCode: [SECURITY_CATEGORY_MFA]
    };
    const securityConfigs = await getTenantSecurityConfig(req);
    console.log(securityConfigs);

    if (securityConfigs) {
      (securityConfigs as TenantSecurityConfig[]).forEach((config) => {
        if (config.categoryCode === SECURITY_CATEGORY_MFA && config.securityConfigItemRespVO.length > 0) {
          const securityConfigItem = config.securityConfigItemRespVO[0];

          if (securityConfigItem.configValue.includes('email')) {
            setMfaVerifyStatus('email');
          }
          if (securityConfigItem.configValue.includes('phone')) {
            setMfaVerifyStatus('phone');
          }
        }
      });
    }
  };

  // 处理记住我状态变化
  const handleRememberMeChange = (checked: boolean) => {
    const account = accountForm.getFieldValue('account') || '';
    saveRememberMe(account, checked);
  };

  // 清除登录验证码相关状态
  const clearLoginVerification = () => {
    savedCaptchaVerificationRef.current = '';
    accountForm.setFieldValue('captchaVerification', '');
    setVerifyCode('');
  };

  // 账号密码登录
  const handleAccountLogin = async (values: LoginRequest, mfaCode?: string) => {
    setLoading(true);

    try {
      // 优先使用传入的 captchaVerification，如果没有则使用保存的
      const captchaVerification = values.captchaVerification || savedCaptchaVerificationRef.current;

      // 如果没有验证码token，则先进行验证码验证
      if (!captchaVerification) {
        // 显示滑块验证码
        sliderCaptchaRef.current?.showCaptcha();
        setLoading(false);
        return;
      }

      // 保存 captchaVerification 到 ref，以便后续使用
      savedCaptchaVerificationRef.current = captchaVerification;

      if (mfaVerifyStatus === 'phone' || mfaVerifyStatus === 'email') {
        const codeToUse = mfaCode || verifyCode;
        if (!codeToUse || codeToUse === '') {
          setShowVerifyModal(true);
          setLoading(false);
          return;
        }
      }

      const headers = {
        'X-Tenant-Id': tenantId
      };

      const deviceId = await getOrCreateDeviceInfo();

      const loginData: LoginRequest = {
        username: values.username!,
        password: values.password!,
        captchaVerification: captchaVerification,
        deviceId: deviceId
      };

      // 如果有 MFA 验证码，添加到登录数据中
      // 注意：如果后端需要单独的字段，可能需要扩展 LoginRequest 类型
      const codeToUse = mfaCode || verifyCode;
      if (codeToUse) {
        (loginData as any).verifyCode = codeToUse;
        (loginData as any).verifyType = mfaVerifyStatus;
      }

      const response: TenantLoginResponse = await tenantLogin(loginData, headers);

      if (response.accessToken) {
        TokenManager.setCurIdentifyId(tenantId);

        TokenManager.setToken(
          {
            userId: response.userId,
            accessToken: response.accessToken,
            refreshToken: response.refreshToken,
            expiresTime: response.expiresTime,
            tenantId: response.tenantId,
            loginURL: window.location.href // 当前地址
          },
          rememberMe
        );

        // 保存记住我状态和账号信息
        saveRememberMe(values.username!, rememberMe);

        Message.success(t('auth.loginSuccess'));

        // 登录成功，清除验证码相关状态
        savedCaptchaVerificationRef.current = '';
        accountForm.setFieldValue('captchaVerification', '');
        setVerifyCode('');

        const redirectURL = getHashQueryParam('redirectURL');
        if (redirectURL) {
          window.location.href = redirectURL;
        } else {
          navigate(`/onebase/${tenantId}/home/enterprise-app`);
        }

        return;
      } else {
        Message.error(t('auth.loginFailed'));
        // 登录失败，清除验证码，下次需要重新验证
        clearLoginVerification();
      }
    } catch (error: any) {
      console.error('登录失败:', error);
      // 登录失败，清除验证码，下次需要重新验证
      clearLoginVerification();
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
    const deviceId = await getOrCreateDeviceInfo();

    // 保存 captchaVerification 到表单和 ref
    accountForm.setFieldValue('captchaVerification', token);
    savedCaptchaVerificationRef.current = token;

    const values = await accountForm.getFieldsValue();
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

      const deviceId = await getOrCreateDeviceInfo();

      if (accountForm.getFieldValue('captchaVerification')) {
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
        <h1 className={styles.title}>欢迎登录空间管理系统</h1>

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
              { required: true, message: '请输入用户名' },
              { minLength: 3, message: '账号至少3个字符' }
            ]}
          >
            <Input placeholder="请输入用户名" allowClear size="large" prefix={<IconUser />} />
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
            <Input.Password placeholder="请输入密码" allowClear size="large" prefix={<IconLock />} />
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
            <Button type="primary" htmlType="submit" long loading={loading} size="large" className={styles.loginButton}>
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

      <VerifyModal
        verifyType={mfaVerifyStatus}
        visible={showVerifyModal}
        onCancel={() => {
          setShowVerifyModal(false);
          setLoading(false);
        }}
        onOk={async (values: { verifyCode: string; verifyType: string }) => {
          // 关闭弹窗
          setShowVerifyModal(false);

          // 获取表单数据并调用登录接口，直接传递验证码
          const formValues = await accountForm.getFieldsValue();
          const deviceId = await getOrCreateDeviceInfo();

          // 使用保存的 captchaVerification 或表单中的值
          const captchaVerification = savedCaptchaVerificationRef.current || formValues.captchaVerification;

          // 调用登录接口，直接传递验证码
          await handleAccountLogin(
            {
              username: formValues.username,
              password: formValues.password,
              captchaVerification: captchaVerification,
              deviceId: deviceId
            },
            values.verifyCode
          );
        }}
      />
    </div>
  );
};

export default Right;
