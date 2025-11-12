import { Button, Checkbox, Form, Input, Message, Space, Typography } from '@arco-design/web-react';
import { IconLock, IconUser } from '@arco-design/web-react/icon';
import { getHashQueryParam, SliderCaptcha, TokenManager, type SliderCaptchaRef } from '@onebase/common';
import { checkCaptchaApi, getCaptchaApi, login, type LoginRequest, type LoginResponse } from '@onebase/platform-center';
import { getApplication } from '@onebase/app';
import { appIconMap } from '@onebase/ui-kit';
import { useEffect, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useI18n } from '../../../hooks/useI18n';
import { useRememberMe } from '../../../hooks/useRememberMe';
import styles from '../index.module.less';
import type { IIconBase } from '@icon-park/react/lib/runtime';

interface DynamicIconProps extends IIconBase {
  IconComponent: React.ComponentType<any>;
  theme?: 'outline' | 'filled' | 'two-tone' | 'multi-color';
  size?: number | string;
  fill?: string;
  style?: React.CSSProperties;
}

interface APP_INFO {
  appName: string;
  iconName: string;
  iconColor: string;
}

const { Paragraph } = Typography;

const Right: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const { t } = useI18n();
  const sliderCaptchaRef = useRef<SliderCaptchaRef>(null);

  const [appInfo, setAppInfo] = useState<APP_INFO>({
    appName: '',
    iconName: '',
    iconColor: ''
  });

  // 从路由中获取 appid 参数
  const { appId } = useParams<{ appId?: string }>();
  const { tenantId } = useParams<{ tenantId?: string }>();

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
    if (TokenManager.isTokenValid()) {
      const redirectURL = getHashQueryParam('redirectURL');
      if (redirectURL) {
        window.location.href = redirectURL;
      } else {
        // 跳转到首页
        navigate(`/onebase/runtime/${appId}`);
      }
      return;
    }

    handleGetApplication();
  }, []);

  const handleGetApplication = async () => {
    const redirectURL = getHashQueryParam('redirectURL');
    if (redirectURL) {
      const startIndex = redirectURL.indexOf('/runtime/');
      const runtimeLength = '/runtime/'.length;
      const endRedirectURL = redirectURL.slice(startIndex + runtimeLength);
      const endIndex = endRedirectURL?.indexOf('/');
      const applicationId = redirectURL.slice(startIndex + runtimeLength, startIndex + runtimeLength + endIndex);

      if (applicationId) {
        const res = await getApplication({ id: applicationId });
        if (res) {
          setAppInfo({ appName: res.appName || '', iconName: res.iconName || '', iconColor: res.iconColor || '' });
        }
      }
    }
  };

  // 处理记住我状态变化
  const handleRememberMeChange = (checked: boolean) => {
    const account = form.getFieldValue('account') || '';
    saveRememberMe(account, checked);
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
        'Tenant-Id': tenantId || '1'
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
            tenantId: response.tenantWebsite,
            corpId: response.corpId,
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
          // 跳转到首页
          navigate(`/onebase/runtime/${appId}`);
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

      // 显示滑块验证码
      sliderCaptchaRef.current?.showCaptcha();
    } catch (error) {
      console.error('表单验证失败:', error);
      Message.error('请检查表单填写是否正确');
    }
  };

  const DynamicIcon = ({ IconComponent, ...rest }: DynamicIconProps) => {
    if (!IconComponent) return null;
    return <IconComponent {...rest} />;
  };

  return (
    <div className={styles.loginPageRight}>
      <div className={styles.loginFormContainer}>
        {appInfo.iconName && (
          <div className={styles.appInfo}>
            <div
              className={styles.appIcon}
              style={{
                background: appInfo.iconColor || 'transparent'
              }}
            >
              <DynamicIcon
                IconComponent={appIconMap[appInfo.iconName as keyof typeof appIconMap]}
                theme="outline"
                size="40"
                fill="#F2F3F5"
              />
            </div>
            <div className={styles.appName}>{appInfo.appName}</div>
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
