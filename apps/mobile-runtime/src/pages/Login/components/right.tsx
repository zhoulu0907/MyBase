import { Button, Checkbox, Form, Input, Toast } from '@arco-design/mobile-react';
import { useForm } from '@arco-design/mobile-react/esm/form';
import IconSquareChecked from '@arco-design/mobile-react/esm/icon/IconSquareChecked';
import IconSquareUnchecked from '@arco-design/mobile-react/esm/icon/IconSquareUnchecked';
import IconSquareDisabled from '@arco-design/mobile-react/esm/icon/IconSquareDisabled';
import { getHashQueryParam, TokenManager, type SliderCaptchaRef } from '@onebase/common';
import { SliderCaptcha } from './Captcha';
import { checkCaptchaApi, getCaptchaApi, login, type LoginRequest, type LoginResponse } from '@onebase/platform-center';
import { getApplication } from '@onebase/app';
import { useEffect, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useI18n } from '../../../hooks/useI18n';
import { useRememberMe } from '../../../hooks/useRememberMe';
import styles from '../index.module.less';
import type { IIconBase } from '@icon-park/react/lib/runtime';
import { ValidatorType } from '@arco-design/mobile-utils';
import logoIcon from '../../../assets/images/logo-icon.svg';

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


const Right: React.FC = () => {
  const navigate = useNavigate();
  const [form] = useForm();
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
        navigate(`/onebase/runtime-home/${appId}`);
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
            tenantId: response.tenantId
          },
          rememberMe
        );

        // 保存记住我状态和账号信息
        saveRememberMe(values.username!, rememberMe);

        Toast.success(t('auth.loginSuccess'));
        const redirectURL = getHashQueryParam('redirectURL');
        if (redirectURL) {
          window.location.href = redirectURL;
        } else {
          // 跳转到首页
          navigate(`/onebase/runtime-home/${appId}`);
        }

        return;
      } else {
        Toast.error(t('auth.loginFailed'));
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
      await form.validateFields();

      // 显示滑块验证码
      sliderCaptchaRef.current?.showCaptcha();
    } catch (error) {
      console.error('表单验证失败:', error);
      Toast.error('请检查表单填写是否正确');
    }
  };

  const DynamicIcon = ({ IconComponent, ...rest }: DynamicIconProps) => {
    if (!IconComponent) return null;
    return <IconComponent {...rest} />;
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
        },
      },
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
        },
      },
    ],
  }
  const toSubmit = () => {
    form.submit();
  };
  const squareIcon = {
    normal: <IconSquareUnchecked />,
    active: <IconSquareChecked />,
    disabled: <IconSquareDisabled />,
    activeDisabled: <IconSquareChecked />,
  }
  return (
    <div className={styles.loginPageRight}>
      <div className={styles.titleContainer}>
        <img src={logoIcon} alt="logo" className={styles.loginLogo} />
        <h1 className={styles.title}>欢迎登录{appInfo.appName ? ` ${appInfo.appName} 应用` : 'Onebase'}</h1>
      </div>
      <div className={styles.loginFormContainer}>
        <Form
          form={form}
          layout="vertical"
          onSubmit={handleLoginClick}
          // autoComplete="off"
          // requiredSymbol={false}
          className={styles.loginForm}
        >
          <Form.Item
            field="username"
            label="账号"
            initialValue=""
            rules={rules.username}
          >
            <Input placeholder={t('auth.userAccount')} clearable={false} />
          </Form.Item>

          <Form.Item
            field="password"
            label="密码"
            initialValue=""
            rules={rules.password}
          >
            <Input type="password" placeholder={t('auth.password')} clearable={false} />
          </Form.Item>
          <div className={styles.rememberMeContainer}>
            <Checkbox
              value={rememberMe ? 2 : 1}
              checked={rememberMe}
              defaultCheck={true}
              style={{ display: 'flex' }}
              icons={squareIcon}
              onChange={handleRememberMeChange}
            >{t('auth.rememberMe')}</Checkbox>
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
          <span>《用户协议》</span>
          和
          <span>《隐私政策》</span>
        </div>
      </div>
    </div>
  );
};

export default Right;
