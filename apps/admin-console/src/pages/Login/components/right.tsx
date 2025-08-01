import LogoSVG from '@/assets/images/logo.svg';
import {
    Button,
    Checkbox,
    Form,
    Input,
    Message,
    Space,
    Tabs,
    Typography
} from '@arco-design/web-react';
import { Captcha, TokenManager, type CaptchaRef } from '@onebase/common';
import { getBackendURL, getSm2PublicKey, login as sessionLogin } from '@onebase/platform-center';
import { useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
// @ts-expect-error: no types for sm-crypto
import { sm2 } from 'sm-crypto';
import { useI18n } from '../../../hooks/useI18n';
import { useRememberMe } from '../../../hooks/useRememberMe';
import styles from '../index.module.less';
import type { LoginRequest } from '@/types/login';

const { Paragraph } = Typography;
const TabPane = Tabs.TabPane;

interface LoginFormData {
  account?: string;
  password?: string;
  mobile?: string;
  smsCode?: string;
}

const Right: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const { t } = useI18n();

  // const captchaRef = useRef<CaptchaRef>(null);

  // 使用记住我hook
  const { rememberMe, savedAccount, saveRememberMe } = useRememberMe();

  // 状态管理
  const [loginType, setLoginType] = useState<'account' | 'mobile'>('account');
  const [loading, setLoading] = useState(false);
  // const [smsCountdown, setSmsCountdown] = useState(0);

  // 组件初始化时设置保存的账号
  useState(() => {
    if (savedAccount) {
      form.setFieldValue('account', savedAccount);
    }
  });

  // 处理记住我状态变化
  const handleRememberMeChange = (checked: boolean) => {
    const account = form.getFieldValue('account') || '';
    saveRememberMe(account, checked);
  };


  // 表单提交处理
  const handleSubmit = async (values: LoginRequest) => {
    values.username = values.account;
    delete values.account;

    console.log('values:', values);

    // 显示成功消息并跳转
    Message.success(t('auth.loginSuccess'));
    navigate('/onebase');

  };

  return (
    <div className={styles.loginPageRight}>
      <div className={styles.loginPageHeader}>
        <img src={LogoSVG} alt="logo" />
        <div>ONE BASE</div>
      </div>
      <div className={styles.loginFormContainer}>

        <Tabs
          activeTab={loginType}
          onChange={(key) => setLoginType(key as 'account' | 'mobile')}
          type="text"
        >
          <TabPane key="account" title={t('auth.accountLogin')}>
            <Form
              form={form}
              layout="vertical"
              onSubmit={handleSubmit}
              autoComplete="off"
              className={styles.loginForm}
            >
              <Form.Item
                field="account"
                initialValue=""
                rules={[
                  { required: true, message: '请输入账号' },
                  { minLength: 3, message: '账号至少3个字符' }
                ]}
              >
                <Input
                  placeholder={t('auth.userAccount')}
                  allowClear
                  size="large"
                />
              </Form.Item>

              <Form.Item
                field="password"
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
                />
              </Form.Item>

              <Form.Item>
                <Space className={styles.formActions}>
                  <Checkbox
                    checked={rememberMe}
                    onChange={handleRememberMeChange}
                  >
                    {t('auth.rememberMe')}
                  </Checkbox>
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
      <div className={styles.loginFooter}>
        <Paragraph className={styles.footerText}>
            登录即表示同意
            <Button type="text" size="small">《用户协议》</Button>
            和
            <Button type="text" size="small">《隐私政策》</Button>
        </Paragraph>
      </div>
    </div>
  );
};

export default Right;