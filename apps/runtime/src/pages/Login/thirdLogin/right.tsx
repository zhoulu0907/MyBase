import { DynamicIcon } from '@/components/DynamicIcon';

import { ThirdLoginMap, ThirdLoginType } from '@/constants';
import { appInfoSignal } from '@/store/app';
import { filterSpace, phoneValidator } from '@/utils/validator';
import { Button, Checkbox, Form, Input, Link, Message, Space, Tabs, Typography } from '@arco-design/web-react';
import { IconLock, IconMobile } from '@arco-design/web-react/icon';
import { getApplicationLeast } from '@onebase/app';
import {
  getHashQueryParam,
  getOrCreateDeviceInfo,
  getPublicKey,
  SliderCaptcha,
  sm2Encrypt,
  TokenManager,
  VerifyInput,
  type SliderCaptchaRef
} from '@onebase/common';
import {
  checkCaptchaApi,
  getCaptchaApi,
  runtimeThirdLogin,
  sendVerifyCodeApi,
  type RuntimeThirdLoginRequest,
  type ThirdUserLoginResponse
} from '@onebase/platform-center';
import { appIconMap } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useI18n } from '../../../hooks/useI18n';
import { useRememberMe } from '../../../hooks/useRememberMe';
import styles from '../index.module.less';
import RegisterForm from './register';
import UpdatePasswordForm from './updatePassword';

const { Paragraph } = Typography;
const TabPane = Tabs.TabPane;

/**
 * 专门负责外部用户登录注册
 * @returns
 */

const Right: React.FC = () => {
  useSignals();

  const { curAppInfo, setCurAppInfo } = appInfoSignal;

  const navigate = useNavigate();
  const [form] = Form.useForm();
  const { t } = useI18n();
  const sliderCaptchaRef = useRef<SliderCaptchaRef>(null);
  const [loginType, setLoginType] = useState<string>(ThirdLoginType.VERIFYCODE);
  const [appId, setAppId] = useState('');
  const [tenantId, setTenantId] = useState('');
  const [visibleRegister, setVisibleRegister] = useState<boolean>(false);
  const [visbileUpdatePwd, setVisibleUpdatePwd] = useState<boolean>(false);
  const [isRelatedApp, setIsRelatedApp] = useState<boolean>(false);

  useEffect(() => {
    // 从 window.location.hash 中解析 redirectURL，再从 redirectURL 解析 appId 和 tenantId
    const rawHash = window.location.hash;
    const prefix = '#/third/login?redirectURL=';
    if (rawHash.startsWith(prefix)) {
      const redirectURL = rawHash.replace(prefix, '');
      let aid = getHashQueryParam('appId', redirectURL) || '';
      let tid = getHashQueryParam('tenantId', redirectURL) || '';
      setAppId(aid);
      setTenantId(tid);
    } else {
      let aid = getHashQueryParam('appId') || '';
      let tid = getHashQueryParam('tenantId') || '';
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
  }, []);

  useEffect(() => {
    if (appId) {
      handleGetApplication();
    }
  }, [appId]);

  const handleGetApplication = async () => {
    if (appId) {
      const res = await getApplicationLeast({ id: appId });
      console.log('res: ', res);
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
  const handleRuntimeLogin = async (values: RuntimeThirdLoginRequest) => {
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

      let response: ThirdUserLoginResponse | null = null;

      const deviceId = await getOrCreateDeviceInfo();

      values.password = await sm2Encrypt(getPublicKey(), values.password || '');
      const loginData: RuntimeThirdLoginRequest = {
        appId: appId,
        loginType: loginType,
        mobile: values.mobile,
        password: values.password!,
        captchaVerification: captchaVerification,
        deviceId: deviceId
      };
      response = await runtimeThirdLogin(loginData, headers);
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
            loginURL: window.location.href // 当前地址
          },
          rememberMe
        );

        // 保存记住我状态和账号信息
        saveRememberMe(values.mobile!, rememberMe);

        Message.success(t('auth.loginSuccess'));
        const redirectURL = getHashQueryParam('redirectURL');
        if (redirectURL) {
          navigate(`/onebase/${appId}/${tenantId}/runtime`);
        } else {
          // 跳转到首页
          navigate(`/onebase/runtime/?appId=${appId}`);
        }
        return;
      } else {
        if (response?.userUnRegistFlag) {
          setVisibleRegister(true);
        }
        setIsRelatedApp(response?.userAppRelationFlag || false);
      }
    } catch (error: any) {
      console.error('登录失败:', error);
    } finally {
      setLoading(false);
    }
  };

  // 表单提交处理
  const handleSubmit = (_values: RuntimeThirdLoginRequest) => {
    handleRuntimeLogin(_values);
  };

  // 验证码验证成功回调
  const handleCaptchaSuccess = async (token: string) => {
    const values = await form.getFieldsValue();

    const deviceId = await getOrCreateDeviceInfo();
    handleSubmit({
      appId: values.appId,
      loginType: values.loginType,
      mobile: filterSpace(values.mobile),
      password: values.password,
      captchaVerification: token,
      deviceId: deviceId
    });
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
      {!visibleRegister && !visbileUpdatePwd && (
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
            <Tabs activeTab={loginType} onChange={setLoginType}>
              {ThirdLoginMap?.map((item) => {
                return (
                  <TabPane key={item.value} title={item.label}>
                    <Form.Item
                      label="手机号"
                      field="mobile"
                      rules={[{ required: true, message: '请输入手机号' }, { validator: phoneValidator }]}
                    >
                      <Input placeholder="输入手机号" maxLength={11} prefix={<IconMobile />} />
                    </Form.Item>
                    {item.value === ThirdLoginType.VERIFYCODE && (
                      <Form.Item>
                        <VerifyInput
                          userMobile={form.getFieldValue('mobile')}
                          verifyType={'mobile'}
                          sendVerifyCode={sendVerifyCodeApi}
                        />
                      </Form.Item>
                    )}
                    {item.value === ThirdLoginType.PASSWORD && (
                      <Form.Item
                        field="password"
                        label="密码"
                        initialValue=""
                        rules={[
                          { required: true, message: '请输入密码' },
                          { minLength: 6, message: '密码至少6个字符' }
                        ]}
                      >
                        <Input.Password
                          placeholder={t('auth.password')}
                          allowClear
                          size="large"
                          prefix={<IconLock />}
                        />
                      </Form.Item>
                    )}

                    <Form.Item>
                      <Space className={styles.formActions}>
                        <Checkbox checked={rememberMe} onChange={handleRememberMeChange}>
                          {t('auth.rememberMe')}
                        </Checkbox>
                        <Button
                          type="text"
                          size="small"
                          onClick={() => {
                            setVisibleUpdatePwd(true);
                          }}
                        >
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

                      <div
                        style={{
                          margin: '8px auto',
                          textAlign: 'center'
                        }}
                      >
                        没有账号？
                        <Link
                          style={{ color: '#009E9E' }}
                          onClick={() => {
                            setLoginType(ThirdLoginType.VERIFYCODE);
                          }}
                        >
                          短信验证码登录
                        </Link>
                        ，登录即注册
                      </div>
                    </Form.Item>
                  </TabPane>
                );
              })}
            </Tabs>
          </Form>
        </div>
      )}

      {/* 注册页面 */}
      {visibleRegister && (
        <RegisterForm
          appId={appId}
          isRelatedApp={isRelatedApp}
          tenantId={tenantId}
          onGoBack={() => {
            setVisibleRegister(false);
          }}
        />
      )}

      {/* 忘记密码 */}
      {visbileUpdatePwd && (
        <UpdatePasswordForm
          onGoBack={() => {
            setVisibleUpdatePwd(false);
          }}
        />
      )}

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
