import { DynamicIcon } from '@/components/DynamicIcon';

import { PUBLISH_MODULE } from '@/constants/permission';
import { appInfoSignal } from '@/store/app';
import { Button, Checkbox, Form, Input, Message, Space, Typography } from '@arco-design/web-react';
import { IconLock, IconMobile, IconUser } from '@arco-design/web-react/icon';
import { runtimeGetApplication } from '@onebase/app';
import {
  getHashQueryParam,
  getOrCreateDeviceInfo,
  SliderCaptcha,
  TokenManager,
  type SliderCaptchaRef
} from '@onebase/common';
import {
  checkCaptchaApi,
  corpLogin,
  getCaptchaApi,
  innerLogin,
  login,
  sassLogin,
  type LoginRequest,
  type LoginResponse,
  type RuntimeAccountLoginRequest,
  type RuntimeCorpLoginRequest,
  type RuntimeMobileLoginRequest
} from '@onebase/platform-center';
import { appIconMap } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useI18n } from '../../../hooks/useI18n';
import { useRememberMe } from '../../../hooks/useRememberMe';
import styles from '../index.module.less';

const { Paragraph } = Typography;

/**
 * 运行态登录一共有三种模式
1.企业登录（手机号 + 密码）仅需传递tenantId.
2.空间应用saas模式登录 （手机号 + 密码） 需传递appId+ tenantId
3.空间应用inner模式登录（账号+ 密码） 需传递appId+ tenantId
4.原本的登录模式（账号+密码） 需传递appId
 * @returns
 */

const Right: React.FC = () => {
  useSignals();

  const { curAppInfo, setCurAppInfo } = appInfoSignal;

  const navigate = useNavigate();
  const [form] = Form.useForm();
  const { t } = useI18n();
  const sliderCaptchaRef = useRef<SliderCaptchaRef>(null);

  const [appId, setAppId] = useState('');
  const [tenantId, setTenantId] = useState('');

  useEffect(() => {
    // 从 window.location.hash 中解析 redirectURL，再从 redirectURL 解析 appId 和 tenantId
    const rawHash = window.location.hash;
    const prefix = '#/login?redirectURL=';
    if (rawHash.startsWith(prefix)) {
      const redirectURL = rawHash.replace(prefix, '');
      let aid = getHashQueryParam('appId', redirectURL) || '';
      let tid = getHashQueryParam('tenantId', redirectURL) || '';
      setAppId(aid);
      setTenantId(tid);
    }
  }, []);

  // 使用记住我hook
  const { rememberMe, savedAccount, saveRememberMe } = useRememberMe();

  // 状态管理
  const [loading, setLoading] = useState(false);

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
    //     //企业登录
    //     if (!appId && tenantId) {
    //       navigate(`/onebase/runtime/my-app`);
    //     }
    //     if (appId && !tenantId) {
    //       navigate(`/onebase/runtime/?appId=${appId}`);
    //     }
    //     if (appId && tenantId) {
    //       navigate(`/onebase/runtime/?appId=${appId}&tenantId=${tenantId}`);
    //     }
    //   }
    //   return;
    // }
  }, []);

  useEffect(() => {
    if (appId) {
      handleGetApplication();
    }
  }, [appId]);

  const handleGetApplication = async () => {
    if (appId) {
      const res = await runtimeGetApplication({ id: appId });
      if (res) {
        setCurAppInfo(res);
      }
    }
  };

  // 处理记住我状态变化
  const handleRememberMeChange = (checked: boolean) => {
    const account = form.getFieldValue('account') || '';
    saveRememberMe(account, checked);
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

      if (curAppInfo.value.publishModel === PUBLISH_MODULE.SASS) {
        const sassloginData: RuntimeMobileLoginRequest = {
          password: values.password!,
          mobile: (values as RuntimeMobileLoginRequest).mobile!,
          appId: appId,
          captchaVerification: captchaVerification,
          deviceId: deviceId
        };

        response = await sassLogin(sassloginData, headers);
      } else if (curAppInfo.value.publishModel === PUBLISH_MODULE.INNER) {
        const innerloginData: RuntimeAccountLoginRequest = {
          password: values.password!,
          username: (values as RuntimeAccountLoginRequest).username!,
          appId: appId,
          captchaVerification: captchaVerification,
          deviceId: deviceId
        };
        response = await innerLogin(innerloginData, headers);
      } else if (!appId) {
        const innerloginData: RuntimeCorpLoginRequest = {
          password: values.password!,
          mobile: (values as RuntimeCorpLoginRequest).mobile!,
          captchaVerification: captchaVerification,
          deviceId: deviceId
        };
        response = await corpLogin(innerloginData, headers);
      } else {
        const loginData: LoginRequest = {
          username: (values as LoginRequest).username!,
          password: values.password!,
          captchaVerification: captchaVerification,
          deviceId: deviceId
        };

        response = await login(loginData, headers);
      }

      if (response && response.accessToken) {
        // 使用 TokenManager 存储 token 信息
        TokenManager.setToken(
          {
            userId: response.userId,
            accessToken: response.accessToken,
            refreshToken: response.refreshToken,
            expiresTime: response.expiresTime,
            tenantId: response.tenantId,
            adminFlag: response.adminFlag,
            corpId: response.corpId,
            loginURL: window.location.href // 当前地址
          },
          rememberMe
        );

        // 保存记住我状态和账号信息
        if (curAppInfo.value.publishModel === PUBLISH_MODULE.INNER) {
          saveRememberMe((values as RuntimeAccountLoginRequest).username!, rememberMe);
        } else if (curAppInfo.value.publishModel === PUBLISH_MODULE.SASS) {
          saveRememberMe((values as RuntimeMobileLoginRequest).mobile!, rememberMe);
        } else if (!appId) {
          saveRememberMe((values as RuntimeCorpLoginRequest).mobile!, rememberMe);
        } else {
          saveRememberMe((values as LoginRequest).username!, rememberMe);
        }

        Message.success(t('auth.loginSuccess'));
        const redirectURL = getHashQueryParam('redirectURL');
        if (redirectURL) {
          if (!appId) {
            //企业登录
            navigate('/onebase/runtime/my-app');
          } else {
            //saas模式 或者inner模式
            navigate(`/onebase/runtime/?appId=${appId}&tenantId=${tenantId}`);
          }
        } else {
          // 跳转到首页
          navigate(`/onebase/runtime/?appId=${appId}`);
        }

        return;
      } else {
        Message.error(t('auth.loginFailed'));
      }
    } catch (error: any) {
      console.error('登录失败:', error);
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

    const deviceId = await getOrCreateDeviceInfo();

    if (curAppInfo.value.publishModel === PUBLISH_MODULE.SASS) {
      handleSubmit({
        mobile: values.mobile,
        password: values.password,
        captchaVerification: token,
        deviceId: deviceId
      } as RuntimeMobileLoginRequest);
    } else if (curAppInfo.value.publishModel === PUBLISH_MODULE.INNER) {
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
      <div className={styles.loginFormContainer}>
        {curAppInfo.value.iconName && (
          <div className={styles.appInfo}>
            <div
              className={styles.appIcon}
              style={{
                background: curAppInfo.value.iconColor || 'transparent'
              }}
            >
              <DynamicIcon
                IconComponent={appIconMap[curAppInfo.value.iconName as keyof typeof appIconMap]}
                theme="outline"
                size="40"
                fill="#F2F3F5"
              />
            </div>
            <div className={styles.appName}>{curAppInfo.value.appName}</div>
          </div>
        )}
        <h1 className={styles.title}>欢迎登录</h1>

        <Form
          form={form}
          layout="vertical"
          onSubmit={handleLoginClick}
          autoComplete="off"
          requiredSymbol={false}
          className={styles.loginForm}
        >
          {((curAppInfo.value.publishModel === PUBLISH_MODULE.SASS || !appId) && (
            <Form.Item label="手机号" field="mobile" rules={[{ required: true, message: '请输入手机号' }]}>
              <Input placeholder="输入手机号" maxLength={11} prefix={<IconMobile />} />
            </Form.Item>
          )) || (
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
          )}

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
    </div>
  );
};

export default Right;
