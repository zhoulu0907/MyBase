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
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useI18n } from '../../../hooks/useI18n';
import { useRememberMe } from '../../../hooks/useRememberMe';
import styles from '../index.module.less';
// import type { LoginRequest } from '@/types/login';
import { login, type LoginRequest } from '@onebase/platform-center';
import { TokenManager } from '@onebase/common';

const { Paragraph } = Typography;
const TabPane = Tabs.TabPane;

const Right: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const { t } = useI18n();


  // 使用记住我hook
  const { rememberMe, savedAccount, saveRememberMe } = useRememberMe();

  // 状态管理
  const [loginType, setLoginType] = useState<'account' | 'mobile'>('account');
  const [loading, setLoading] = useState(false);

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
    setLoading(true);

    console.log('values:', values);

    try {
      const loginResp = await login(values);
      console.log('loginRes:', loginResp);
      // 显示成功消息并跳转
      if (loginResp.accessToken) {
        Message.success(t('auth.loginSuccess'));
        // 存储 token 信息（需要导入相应的 token 管理工具）
        TokenManager.setToken({
          userId: loginResp.userId,
          accessToken: loginResp.accessToken,
          refreshToken: loginResp.refreshToken,
          expiresTime: loginResp.expiresTime,
        }, rememberMe);
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
                field="username"
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