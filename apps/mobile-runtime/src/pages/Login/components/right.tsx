import logoIcon from '@/assets/images/logo-icon.svg';
import { useI18n } from '@/hooks/useI18n';
import { useRememberMe } from '@/hooks/useRememberMe';
import { Button, Form, Input, Toast } from '@arco-design/mobile-react';
import { useForm } from '@arco-design/mobile-react/esm/form';
import { IconEyeInvisible, IconEyeVisible } from '@arco-design/mobile-react/esm/icon';
import { ValidatorType } from '@arco-design/mobile-utils';
import { getApplicationLeast, type Application } from '@onebase/app';
import {
  DynamicIcon,
  getHashQueryParam,
  getOrCreateDeviceInfo,
  getPublicKey,
  PUBLISH_MODULE,
  sm2Encrypt,
  TokenManager,
  type SliderCaptchaRef
} from '@onebase/common';
import {
  checkCaptchaApi,
  getCaptchaApi,
  innerLogin,
  login,
  LoginPlatform,
  runtimeCorpLogin,
  sassLogin,
  type LoginRequest,
  type LoginResponse,
  type RuntimeAccountLoginRequest,
  type RuntimeCorpLoginRequest,
  type RuntimeMobileLoginRequest
} from '@onebase/platform-center';
import { appIconMap } from '@onebase/ui-kit';
import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from '../index.module.less';
import { SliderCaptcha } from './Captcha';

const Right: React.FC = () => {
  const navigate = useNavigate();
  const [form] = useForm();
  const { t } = useI18n();
  const sliderCaptchaRef = useRef<SliderCaptchaRef>(null);

  const [appId, setAppId] = useState('');
  const [tenantId, setTenantId] = useState('');
  const [appInfo, setAppInfo] = useState<Application>();

  // 使用记住我hook
  const { rememberMe, savedAccount, saveRememberMe } = useRememberMe();
  const [showPassword, setShowPassword] = useState(false); // 显示密码

  // 状态管理
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    // 从 window.location.hash 中解析 redirectURL，再从 redirectURL 解析 appId 和 tenantId
    const rawHash = window.location.hash;
    const prefix = '#/login?redirectURL=';
    console.log('rawHash.startsWith(prefix)', rawHash.startsWith(prefix));
    if (rawHash.startsWith(prefix)) {
      const redirectURL = rawHash.replace(prefix, '');
      let aid = getHashQueryParam('appId', redirectURL) || '';
      let tid = getHashQueryParam('tenantId', redirectURL) || '';
      if (!aid) {
        const pathRedirect = (redirectURL.split('#/')[1] || '').split('/');
        aid = pathRedirect[1] || aid || '';
        tid = pathRedirect[2] || tid || '';
      }
      setAppId(aid);
      setTenantId(tid);
    } else {
      const aid = getHashQueryParam('appId') || '';
      const tid = getHashQueryParam('tenantId') || '';
      setAppId(aid);
      setTenantId(tid);
    }
  }, []);

  // 组件初始化时设置保存的账号
  useEffect(() => {
    if (savedAccount) {
      form.setFieldValue('account', savedAccount);
    }

    // 如果已经登录了就自动跳转到首
    // if (TokenManager.isTokenValid()) {
    //   const redirectURL = getHashQueryParam('redirectURL');
    //   if (redirectURL) {
    //     window.location.href = redirectURL;
    //   } else {
    //     // 跳转到首页
    //     navigate(`/onebase/runtime-home/${appId}/${tenantId}`);
    //   }
    //   return;
    // }

    // handleGetApplication();
  }, []);

  useEffect(() => {
    if (appId) {
      handleGetApplication();
    }
  }, [appId]);

  const handleGetApplication = async () => {
    if (appId) {
      const res = await getApplicationLeast({ id: appId });
      if (res) {
        setAppInfo(res);
      }
    }
  };

  // 账号密码登录
  const handleRuntimeLogin = async (
    values: RuntimeAccountLoginRequest | RuntimeMobileLoginRequest | LoginRequest | RuntimeCorpLoginRequest
  ) => {
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

      let response: LoginResponse | null = null;

      const deviceId = await getOrCreateDeviceInfo();

      if (values.password) {
        values.password = await sm2Encrypt(getPublicKey(), values.password);
      }

      if (appInfo?.publishModel === PUBLISH_MODULE.SASS) {
        const sassloginData: RuntimeMobileLoginRequest = {
          password: values.password!,
          mobile: (values as RuntimeMobileLoginRequest).mobile!,
          appId: appId,
          captchaVerification: captchaVerification,
          deviceId: deviceId,
          loginPlatform: LoginPlatform.MOBILE
        };

        response = await sassLogin(sassloginData, headers);
      } else if (appInfo?.publishModel === PUBLISH_MODULE.INNER) {
        const innerloginData: RuntimeAccountLoginRequest = {
          password: values.password!,
          username: (values as RuntimeAccountLoginRequest).username!,
          appId: appId,
          captchaVerification: captchaVerification,
          deviceId: deviceId,
          loginPlatform: LoginPlatform.MOBILE
        };
        response = await innerLogin(innerloginData, headers);
      } else if (!appId) {
        const innerloginData: RuntimeCorpLoginRequest = {
          password: values.password!,
          mobile: (values as RuntimeCorpLoginRequest).mobile!,
          captchaVerification: captchaVerification,
          deviceId: deviceId,
          loginPlatform: LoginPlatform.MOBILE
        };
        response = await runtimeCorpLogin(innerloginData, headers);
      } else {
        const loginData: LoginRequest = {
          username: (values as LoginRequest).username!,
          password: values.password!,
          captchaVerification: captchaVerification,
          deviceId: deviceId,
          loginPlatform: LoginPlatform.MOBILE
        };

        response = await login(loginData, headers);
      }

      if (response && response.accessToken) {
        // 使用 TokenManager 存储 token 信息

        if (appId && tenantId) {
          TokenManager.setCurIdentifyId(`${appId}_${tenantId}`);
        } else {
          if (appId) {
            TokenManager.setCurIdentifyId(appId);
          }
          if (tenantId) {
            TokenManager.setCurIdentifyId(tenantId);
          }
        }

        TokenManager.setToken(
          {
            userId: response.userId,
            accessToken: response.accessToken,
            refreshToken: response.refreshToken,
            expiresTime: response.expiresTime,
            tenantId: response.tenantId,
            corpId: response.corpId,
            loginSource: response.loginSource,
            loginURL: window.location.href // 当前地址
          },
          rememberMe
        );

        // 保存记住我状态和账号信息
        if (appInfo?.publishModel === PUBLISH_MODULE.INNER) {
          saveRememberMe((values as RuntimeAccountLoginRequest).username!, rememberMe);
        } else if (appInfo?.publishModel === PUBLISH_MODULE.SASS) {
          saveRememberMe((values as RuntimeMobileLoginRequest).mobile!, rememberMe);
        } else if (!appId) {
          saveRememberMe((values as RuntimeCorpLoginRequest).mobile!, rememberMe);
        } else {
          saveRememberMe((values as LoginRequest).username!, rememberMe);
        }

        Toast.success(t('auth.loginSuccess'));
        const redirectURL = getHashQueryParam('redirectURL');
        if (redirectURL) {
          if (!appId) {
            //企业登录
            navigate(`/onebase/${tenantId}/runtime-home`);
          } else {
            //saas模式 或者inner模式
            navigate(`/onebase/${appId}/${tenantId}/runtime-home`);
          }
        } else {
          // 跳转到首页
          navigate(`/onebase/${appId}/${tenantId}/runtime-home/`);
        }

        return;
      } else {
        Toast.error(t('auth.loginFailed'));
      }
    } catch (error: any) {
      console.error('登录失败:', error);
      Toast.error(error.message || '登录失败');
    } finally {
      setLoading(false);
    }
  };

  // 表单提交处理
  const handleSubmit = (
    _values: RuntimeAccountLoginRequest | RuntimeMobileLoginRequest | LoginRequest | RuntimeCorpLoginRequest
  ) => {
    handleRuntimeLogin(_values);
  };

  // 验证码验证成功回调
  const handleCaptchaSuccess = async (token: string) => {
    const values = await form.getFieldsValue();
    console.log('values:', values);

    const deviceId = await getOrCreateDeviceInfo();

    if (appInfo?.publishModel === PUBLISH_MODULE.SASS) {
      handleSubmit({
        mobile: values.mobile,
        password: values.password,
        captchaVerification: token,
        deviceId: deviceId
      } as RuntimeMobileLoginRequest);
    } else if (appInfo?.publishModel === PUBLISH_MODULE.INNER) {
      handleSubmit({
        username: values.username,
        password: values.password,
        captchaVerification: token,
        deviceId: deviceId
      } as RuntimeAccountLoginRequest);
    } else if (!appId) {
      handleSubmit({
        mobile: values.mobile,
        password: values.password,
        captchaVerification: token,
        deviceId: deviceId
      } as RuntimeCorpLoginRequest);
    } else {
      handleSubmit({
        username: values.username,
        password: values.password,
        captchaVerification: token,
        deviceId: deviceId
      } as LoginRequest);
    }
  };

  // 登录按钮点击事件 - 先验证滑块验证码
  const handleLoginClick = async () => {
    try {
      // 先验证表单
      await form.validateFields();

      // 显示滑块验证码
      sliderCaptchaRef.current?.showCaptcha();
    } catch (error) {
      console.error('表单验证失败:', error);
      Toast.error('请检查表单填写是否正确');
    }
  };

  const rules = {
    password: [
      {
        type: ValidatorType.Custom,
        validator: (val: string, callback: (error?: string) => void) => {
          if (!val) {
            callback('请输入密码');
          } else if (val.length < 6) {
            callback('密码至少6个字符');
          } else {
            callback();
          }
        }
      }
    ],
    username: [
      {
        type: ValidatorType.Custom,
        validator: (val: string, callback: (error?: string) => void) => {
          if (!val) {
            callback('请输入账号');
          } else if (val.length < 3) {
            callback('账号至少3个字符');
          } else {
            callback();
          }
        }
      }
    ],
    mobile: [
      {
        type: ValidatorType.Custom,
        validator: (val: string, callback: (error?: string) => void) => {
          if (!val) {
            callback('请输入手机号');
          } else {
            callback();
          }
        }
      }
    ]
  };

  const toSubmit = () => {
    form.submit();
  };

  const getAppIcon = () => {
    if (!appInfo?.iconName) {
      return <img src={logoIcon} alt="logo" className={styles.loginLogo} />;
    }
    return (
      <DynamicIcon
        IconComponent={appIconMap[appInfo.iconName as keyof typeof appIconMap]}
        theme="filled"
        size="0.88rem"
        fill="#fff"
        style={{
          padding: '0.2rem',
          borderRadius: '0.28rem',
          marginBottom: '0.48rem',
          verticalAlign: 'middle',
          backgroundColor: appInfo.iconColor || 'rgb(var(--primary-6))'
        }}
      />
    );
  };

  // 根据状态确定 Input 的 type
  const inputType = showPassword ? 'text' : 'password';

  // 根据状态确定显示的图标
  const EyeIcon = showPassword ? IconEyeVisible : IconEyeInvisible;

  return (
    <div className={styles.loginPageRight}>
      <div className={styles.titleContainer}>
        {getAppIcon()}
        <h1 className={styles.title}>欢迎登录{appInfo?.appName ? ` ${appInfo.appName}` : 'Onebase'}</h1>
      </div>
      <div className={styles.loginFormContainer}>
        <Form form={form} layout="vertical" onSubmit={handleLoginClick} className={styles.loginForm}>
          {((appInfo?.publishModel === PUBLISH_MODULE.SASS || !appId) && (
            <Form.Item label="手机号" field="mobile" rules={rules.mobile}>
              <Input placeholder={t('auth.mobile')} maxLength={11} />
            </Form.Item>
          )) || (
            <Form.Item field="username" label="用户名" initialValue="" rules={rules.username}>
              <Input placeholder={t('auth.userAccount')} clearable={false} />
            </Form.Item>
          )}

          <Form.Item
            field="password"
            label="密码"
            initialValue=""
            className={styles.passwordItem}
            rules={rules.password}
          >
            <Input
              type={inputType}
              placeholder={t('auth.password')}
              clearable
              suffix={
                <div className={styles.togglePassword} onClick={() => setShowPassword((prev) => !prev)}>
                  <EyeIcon />
                </div>
              }
            />
          </Form.Item>
          <div className={styles.rememberMeContainer}>
            <div className={styles.forgotPassword}> {t('auth.accountRegistration')}</div>
            <div className={styles.forgotPassword}> {t('auth.forgotPassword')}</div>
          </div>
          <Button type="primary" onClick={toSubmit} loading={loading} size="large" className={styles.loginButton}>
            {t('auth.loginButton')}
          </Button>
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
        <div className={styles.footerText}>
          登录即表示同意
          <span onClick={() => navigate('/onebase/runtime-home/protocol')}>《用户协议》</span>和
          <span onClick={() => navigate('/onebase/runtime-home/privacy')}>《隐私政策》</span>
        </div>
      </div>
    </div>
  );
};

export default Right;
