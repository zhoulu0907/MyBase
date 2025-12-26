import phoneIcon from '@/assets/images/login/phone.svg';
import passwordIcon from '@/assets/images/login/password.svg';
import CustomNav from '@/pages/components/Nav';
import { Form, Input, Button } from '@arco-design/mobile-react';
import { type IFormInstance } from '@arco-design/mobile-react/esm/form';
import { IconSuccessCircle } from '@arco-design/mobile-react/esm/icon';
import { ValidatorType } from '@arco-design/mobile-utils';
import { useRef, useState } from 'react';
import VerifyCode from '../ThirdLogin/components/VerifyCode';
import {
  sendVerifyCodeApi,
  forgotPWD,
  checkCaptchaApi,
  runtimeThirdLogin,
  type forgotPWDParams,
  type ThirdUserLoginResponse
} from '@onebase/platform-center';
import { getOrCreateDeviceInfo, getPublicKey, sm2Encrypt, TokenManager, getHashQueryParam } from '@onebase/common';
import { useNavigate } from 'react-router-dom';
import styles from './index.module.less';

interface FormRef {
  dom: HTMLFormElement;
  form: IFormInstance;
}

const ForgetPassword = () => {
  const navigate = useNavigate();
  const tenantId = getHashQueryParam('tenantId') || '';

  const formRef = useRef<FormRef>(null);
  const [loading, setLoading] = useState(false);
  const [userMobile, setUserMobile] = useState('');

  // 校验规则
  const rules = {
    mobile: [
      {
        type: ValidatorType.Custom,
        validator: (val: string, callback: (error?: string) => void) => {
          const mobileRegex = /^1[3-9]\d{9}$/;
          if (!val) {
            callback('请输入手机号');
          } else if (!mobileRegex.test(val)) {
            callback('请输入正确的手机号');
          }
          callback();
        }
      }
    ],
    verifyCode: [
      {
        type: ValidatorType.Custom,
        validator: (val: string, callback: (error?: string) => void) => {
          if (!val) {
            callback('请输入验证码');
          } else {
            callback();
          }
        }
      }
    ],
    password: [
      {
        type: ValidatorType.Custom,
        validator: (val: string, callback: (error?: string) => void) => {
          if (!val) {
            callback('请输入账号');
          } else if (val.length < 6) {
            callback('密码长度不能少于6位');
          } else if (val.length > 20) {
            callback('密码长度不能超过20位');
          }
          callback();
        }
      }
    ],
    confirmNewPassword: [
      {
        type: ValidatorType.Custom,
        validator: (val: string, callback: (error?: string) => void) => {
          const password = formRef.current?.form.getFieldValue('password');
          if (!val) {
            callback('请再次输入密码');
          } else if (password !== val) {
            callback('两次输入的密码不一致');
          }
          callback();
        }
      }
    ]
  };

  const handleSubmit = async () => {
    try {
      await formRef.current?.form.validateFields();
      const values = formRef.current?.form.getFieldsValue();
      if (!values) {
        return;
      }
      setLoading(true);

      const headers = {
        'X-Tenant-Id': tenantId
      };
      const password = await sm2Encrypt(getPublicKey(), values?.password || '');

      const params: forgotPWDParams = {
        mobile: values.mobile,
        password: password,
        verifyCode: values.verifyCode
      };
      const res = await forgotPWD(params, headers);

      if (res) {
        navigate(-1);
      }
      setLoading(true);
    } catch (err) {
      console.log(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.forgetPassword}>
      <CustomNav title="" />
      <div className={styles.content}>
        <div className={styles.title}>找回密码</div>
        <Form ref={formRef} layout="vertical" className={styles.forgetPasswordForm}>
          <Form.Item label="手机号" field="mobile" rules={rules.mobile}>
            <Input
              label={<img src={phoneIcon} alt="phone" className={styles.loginFormIcon} />}
              placeholder="请输入手机号"
              maxLength={11}
              onChange={(_, value) => setUserMobile(value)}
            />
          </Form.Item>
          <Form.Item label="短信验证码" field="verifyCode" rules={rules.verifyCode}>
            <VerifyCode userMobile={userMobile} verifyType={'mobile'} sendVerifyCode={sendVerifyCodeApi} />
          </Form.Item>
          <Form.Item label="密码" field="password" rules={rules.password}>
            <Input
              label={<img src={passwordIcon} alt="password" className={styles.popupFormIcon} />}
              placeholder="请输入新密码"
              type="password"
            />
          </Form.Item>
          <Form.Item label="确认密码" field="confirmNewPassword" rules={rules.confirmNewPassword}>
            <Input type="password" label={<IconSuccessCircle />} placeholder="请再次输入新密码" />
          </Form.Item>
        </Form>
        <Button type="primary" size="large" className={styles.submitBtn} loading={loading} onClick={handleSubmit}>
          确认
        </Button>
      </div>
    </div>
  );
};
export default ForgetPassword;
