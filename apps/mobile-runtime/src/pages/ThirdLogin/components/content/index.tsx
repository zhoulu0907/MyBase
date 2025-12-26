import logoIcon from '@/assets/images/logo-icon.svg';
import phoneIcon from '@/assets/images/login/phone.svg';
import passwordIcon from '@/assets/images/login/password.svg';
import { Tabs, Form, Input, Button, Toast } from '@arco-design/mobile-react';
import { type IFormInstance } from '@arco-design/mobile-react/esm/form';
import { ValidatorType } from '@arco-design/mobile-utils';
import { useRememberMe } from '@/hooks/useRememberMe';
import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { getApplicationLeast, type Application } from '@onebase/app';
import {
  forgotPWD,
  sendVerifyCodeApi,
  getCaptchaApi,
  checkCaptchaApi,
  sassLogin,
  innerLogin,
  login,
  runtimeCorpLogin,
  runtimeThirdLogin,
  type forgotPWDParams,
  type LoginRequest,
  type LoginResponse,
  type RuntimeAccountLoginRequest,
  type RuntimeCorpLoginRequest,
  type RuntimeMobileLoginRequest,
  type RuntimeThirdLoginRequest,
  type ThirdUserLoginResponse
} from '@onebase/platform-center';
import { appIconMap } from '@onebase/ui-kit';
import {
  DynamicIcon,
  getOrCreateDeviceInfo,
  PUBLISH_MODULE,
  sm2Encrypt,
  TokenManager,
  getPublicKey,
  getHashQueryParam,
  type SliderCaptchaRef
} from '@onebase/common';
import VerifyCode from '../VerifyCode';
import ConfirmInfoForm from '../ConfirmInfoForm';
import RegisterForm from '../RegisterForm';
import { SliderCaptcha } from '../../../Login/components/Captcha';
import styles from '../../index.module.less';

interface FormRef {
  dom: HTMLFormElement;
  form: IFormInstance;
}

const LoginContent: React.FC = () => {
  const navigate = useNavigate();
  // 使用记住我hook
  const { rememberMe, savedAccount, saveRememberMe } = useRememberMe();

  const [appInfo, setAppInfo] = useState<Application>({
    id: '',
    appName: '',
    appCode: '',
    appStatus: 0
  });
  const [activeTab, setActiveTab] = useState(0);
  const [userMobile, setUserMobile] = useState('');
  // 确认页面
  const [visibleConfirmInfo, setVisibleConfirmInfo] = useState<boolean>(false);
  // 注册页面
  const [visibleRegister, setVisibleRegister] = useState<boolean>(false);
  // 状态管理
  const [loading, setLoading] = useState(false);
  const [appId, setAppId] = useState('');
  const [tenantId, setTenantId] = useState('');

  // 校验规则
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
  const verifyCodeFormRef = useRef<FormRef>(null);
  const passwordFormRef = useRef<FormRef>(null);
  const sliderCaptchaRef = useRef<SliderCaptchaRef>(null);

  // 从 window.location.hash 中解析 redirectURL，再从 redirectURL 解析 appId 和 tenantId
  useEffect(() => {
    const rawHash = window.location.hash;
    const prefix = '#/third/login?redirectURL=';
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

  // 登录按钮点击事件 - 先验证滑块验证码
  const handleLoginClick = async () => {
    try {
      setLoading(true);
      // 先验证表单
      if (activeTab === 0) {
        await verifyCodeFormRef.current?.form.validateFields();
      }
      if (activeTab === 1) {
        await passwordFormRef.current?.form.validateFields();
      }
      // 显示滑块验证码
      sliderCaptchaRef.current?.showCaptcha();
    } catch (error) {
      console.error('表单验证失败:', error);
      Toast.error('请检查表单填写是否正确');
      setLoading(false);
    }
  };

  // 验证码验证成功回调
  const handleCaptchaSuccess = async (token: string) => {
    if (activeTab === 0) {
      const values = await verifyCodeFormRef.current?.form.getFieldsValue();
      if (!values) {
        return;
      }
      const deviceId = await getOrCreateDeviceInfo();
      handleSubmit({
        appId: appId,
        loginType: 'verifycode',
        mobile: values.mobile,
        password: values.password,
        verifyCode: values.verifyCode,
        captchaVerification: token,
        deviceId: deviceId
      });
    }
    if (activeTab === 1) {
      const values = await passwordFormRef.current?.form.getFieldsValue();
      if (!values) {
        return;
      }
      const deviceId = await getOrCreateDeviceInfo();
      handleSubmit({
        appId: appId,
        loginType: 'password',
        mobile: values.mobile,
        password: values.password,
        captchaVerification: token,
        deviceId: deviceId
      });
    }
  };
  // 表单提交处理
  const handleSubmit = async (values: RuntimeThirdLoginRequest) => {
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

      let loginData: RuntimeThirdLoginRequest = {
        appId: appId,
        loginType: values.loginType,
        mobile: values.mobile,
        captchaVerification: captchaVerification,
        deviceId: values.deviceId
      };
      if (values.loginType === 'password' && values.password) {
        // 密码加密
        const password = await sm2Encrypt(getPublicKey(), values.password);
        loginData.password = password;
      } else if (values.loginType === 'verifycode') {
        loginData.verifyCode = values.verifyCode;
      }

      const response: ThirdUserLoginResponse = await runtimeThirdLogin(loginData, headers);

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
            userUnRegistFlag: response.userUnRegistFlag,
            loginURL: window.location.href, // 当前地址
            userAppRelationFlag: response.userAppRelationFlag,
            email: response.email,
            nickName: response.nickName
          },
          rememberMe
        );

        // 保存记住我状态和账号信息
        saveRememberMe(values.mobile!, rememberMe);

        Toast.success('登录成功');
        const redirectURL = getHashQueryParam('redirectURL');
        if (response.userAppRelationFlag) {
          setVisibleConfirmInfo(true);
          return;
        } else if (redirectURL) {
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
        if (response?.userUnRegistFlag) {
          setVisibleRegister(true);
        }
      }
    } catch (error: any) {
      console.error('登录失败:', error);
      Toast.error(error.message || '登录失败');
    } finally {
      setLoading(false);
    }
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

  return (
    <div className={styles.loginPageContent}>
      <div className={styles.titleContainer}>
        {getAppIcon()}
        <h1 className={styles.title}>欢迎登录{appInfo?.appName ? ` ${appInfo.appName}` : 'Onebase'}</h1>
      </div>
      <div className={styles.tabs}>
        <Tabs
          activeTab={activeTab}
          tabBarHasDivider={false}
          onChange={(_tab, index) => setActiveTab(index)}
          tabBarArrange={'start'}
          disabled={loading}
          showUnderline
          underlineAdaptive
          underlineThick={'0.12rem'}
          underlineInnerStyle={{
            backgroundColor: '#E8FFFE',
            position: 'relative',
            bottom: '0.18rem',
            zIndex: -1
          }}
          tabs={[
            { key: 'verifyCode', title: '验证码登录' },
            { key: 'password', title: '密码登录' }
          ]}
        >
          <Form ref={verifyCodeFormRef} layout="vertical" className={styles.loginForm}>
            <Form.Item label="手机号" field="mobile" rules={rules.mobile}>
              <Input
                label={<img src={phoneIcon} alt="logo" className={styles.loginFormIcon} />}
                placeholder="请输入手机号"
                maxLength={11}
                onChange={(_, value) => setUserMobile(value)}
              />
            </Form.Item>
            <Form.Item label="短信验证码" field="verifyCode">
              <VerifyCode userMobile={userMobile} verifyType={'mobile'} sendVerifyCode={sendVerifyCodeApi} />
            </Form.Item>
            <Button
              type="primary"
              size="large"
              loading={loading}
              className={styles.loginButton}
              onClick={handleLoginClick}
            >
              登录
            </Button>
            <p className={styles.tips}>没有账号？登录即注册</p>
          </Form>
          <Form ref={passwordFormRef} layout="vertical" className={styles.loginForm}>
            <Form.Item label="手机号" field="mobile" rules={rules.mobile}>
              <Input
                label={<img src={phoneIcon} alt="logo" className={styles.loginFormIcon} />}
                placeholder="请输入手机号"
                maxLength={11}
                onChange={(_, value) => setUserMobile(value)}
              />
            </Form.Item>
            <Form.Item field="password" label="密码" rules={rules.password}>
              <Input
                type="password"
                label={<img src={passwordIcon} alt="logo" className={styles.loginFormIcon} />}
                placeholder="请输入密码"
              />
            </Form.Item>
            <Button
              type="primary"
              size="large"
              loading={loading}
              className={styles.loginButton}
              onClick={handleLoginClick}
            >
              登录
            </Button>
            <p className={styles.tips}>
              没有账号？ <span onClick={() => setActiveTab(0)}>短信验证码登录</span>，登录即注册
            </p>
          </Form>
        </Tabs>
      </div>
      <div className={styles.loginFooter}>
        <div className={styles.footerText}>
          登录即表示同意
          <span onClick={() => navigate('/onebase/runtime-home/protocol')}>《用户协议》</span>和
          <span onClick={() => navigate('/onebase/runtime-home/privacy')}>《隐私政策》</span>
        </div>
      </div>

      {/* 滑块验证码组件 */}
      <SliderCaptcha
        ref={sliderCaptchaRef}
        getCaptchaApi={getCaptchaApi}
        checkCaptchaApi={checkCaptchaApi}
        onSuccess={handleCaptchaSuccess}
        onError={() => setLoading(false)}
      />

      {/* 确认页面 */}
      {visibleConfirmInfo && (
        <ConfirmInfoForm onGoBack={() => setVisibleConfirmInfo(false)} tenantId={tenantId} appId={appId} />
      )}

      {/* 注册页面 */}
      {visibleRegister && (
        <RegisterForm
          appId={appId}
          tenantId={tenantId}
          mobile={userMobile}
          onGoBack={() => {
            setVisibleRegister(false);
          }}
        />
      )}
    </div>
  );
};

export default LoginContent;
