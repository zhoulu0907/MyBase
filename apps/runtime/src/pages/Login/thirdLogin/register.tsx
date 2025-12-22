import { emailValidator, filterSpace, phoneValidator } from '@/utils/validator';
import { Button, Form, Input, Link, Typography } from '@arco-design/web-react';
import { IconMobile } from '@arco-design/web-react/icon';
import { getHashQueryParam, getOrCreateDeviceInfo, getPublicKey, sm2Encrypt } from '@onebase/common';
import { thirdUserRegisterApi, type thirdUserRegisterParams } from '@onebase/platform-center';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import ConfirmInfoForm from './confirmInfo';
import styles from './register.module.less';
import type { IRegisterProps, registerInfo } from './type';

// 补充用户信息页面
const RegisterForm: React.FC<IRegisterProps> = ({ appId, isRelatedApp, tenantId, onGoBack }) => {
  const [registerForm] = Form.useForm();
  const navigate = useNavigate();
  const [visible, setVisible] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(false);
  const [registerInfo, setRegisterInfo] = useState<registerInfo | null>(null);

  const handleSubmitUserInfo = async () => {
    try {
      setLoading(true);
      // 先验证表单
      const values = await registerForm.validate();
      const headers = {
        'X-Tenant-Id': tenantId
      };
      values.password = await sm2Encrypt(getPublicKey(), values?.password || '');

      const deviceId = await getOrCreateDeviceInfo();

      const registerParams: thirdUserRegisterParams = {
        appId: appId,
        mobile: filterSpace(values?.mobile),
        email: filterSpace(values?.email),
        password: values?.password,
        nickName: filterSpace(values?.nickName),
        deviceId: deviceId
      };
      const response = await thirdUserRegisterApi(registerParams, headers);
      console.log('response', response);
      if (response) {
        setRegisterInfo(response);
        if (isRelatedApp) {
          setVisible(true);
        } else {
          const redirectURL = getHashQueryParam('redirectURL');
          if (redirectURL) {
            navigate(`/onebase/${appId}/${tenantId}/runtime`);
          } else {
            // 跳转到首页
            navigate(`/onebase/runtime/?appId=${appId}`);
          }
        }
      }
    } catch (error) {
      console.log('error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      {visible ? (
        <ConfirmInfoForm
          initialValues={registerInfo}
          onGoBack={() => {
            setVisible(false);
          }}
        />
      ) : (
        <div className={styles.registerPage}>
          {/* 返回按钮 */}
          <Link className={styles.back} onClick={onGoBack}>
            ← 返回
          </Link>

          {/* 标题 */}
          <Typography.Title heading={2}>请补充用户信息</Typography.Title>

          {/* 表单区域 */}
          <Form layout="vertical" form={registerForm} onSubmit={handleSubmitUserInfo}>
            <Form.Item
              label="手机号"
              field="mobile"
              rules={[{ required: true, message: '请输入手机号' }, { validator: phoneValidator }]}
            >
              <Input placeholder="输入手机号" maxLength={11} prefix={<IconMobile />} />
            </Form.Item>
            {/* 姓名输入框 */}
            <Form.Item label="姓名" field="nickName" required rules={[{ required: true, message: '请输入姓名' }]}>
              <Input placeholder="请输入姓名" />
            </Form.Item>

            {/* 邮箱输入框（选填） */}
            <Form.Item label="邮箱(选填)" field="email" rules={[{ validator: emailValidator }]}>
              <Input placeholder="请输入邮箱" />
            </Form.Item>

            {/* 密码输入框（带可见性切换） */}
            <Form.Item
              label="密码"
              field="password"
              required
              rules={[
                { required: true, message: '请输入密码' },
                { min: 6, message: '密码长度不能少于6位' },
                { max: 20, message: '密码长度不能超过20位' }
              ]}
            >
              <Input.Password placeholder="请输入密码" />
            </Form.Item>

            {/* 确认密码输入框（带可见性切换） */}
            <Form.Item
              label="确认密码"
              field="confirmNewPassword"
              required
              dependencies={['newPassword']}
              rules={[
                { required: true, message: '请再次输入密码' },
                { min: 6, message: '密码长度不能少于6位' },
                { max: 20, message: '密码长度不能超过20位' },
                {
                  validator: (value: any, callback: (error?: string) => void) => {
                    if (!value) {
                      callback('请再次输入密码');
                    } else if (registerForm.getFieldValue('password') !== value) {
                      callback('两次输入的密码不一致');
                    } else {
                      callback();
                    }
                  }
                }
              ]}
            >
              <Input.Password placeholder="请输入确认密码" />
            </Form.Item>

            <Button
              type="primary"
              long
              size="large"
              className={styles.loginButton}
              onClick={handleSubmitUserInfo}
              loading={loading}
            >
              确认
            </Button>
          </Form>
        </div>
      )}
    </>
  );
};

export default RegisterForm;
