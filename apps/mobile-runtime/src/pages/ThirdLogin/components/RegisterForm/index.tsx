import emailIcon from '@/assets/images/login/email.svg';
import passwordIcon from '@/assets/images/login/password.svg';
import { Form, Input, Button, Popup } from '@arco-design/mobile-react';
import { type IFormInstance } from '@arco-design/mobile-react/esm/form';
import { IconUser, IconSuccessCircle } from '@arco-design/mobile-react/esm/icon';
import { ValidatorType } from '@arco-design/mobile-utils';
import { useRef, useState } from 'react';
import { getOrCreateDeviceInfo, getPublicKey, sm2Encrypt, TokenManager } from '@onebase/common';
import { thirdUserRegisterApi, type thirdUserRegisterParams } from '@onebase/platform-center';
import CustomNav from '@/pages/components/Nav';
import styles from '../../index.module.less';

interface IRegisterProps {
  visible: boolean;
  tenantId: string;
  mobile: string;
  appId: string;
  onOk: () => void;
  onCancel: () => void;
}

interface FormRef {
  dom: HTMLFormElement;
  form: IFormInstance;
}

const RegisterForm: React.FC<IRegisterProps> = ({ visible, appId, mobile, tenantId, onOk, onCancel }) => {
  const formRef = useRef<FormRef>(null);
  const [loading, setLoading] = useState(false);
  // 校验规则
  const rules = {
    username: [
      {
        type: ValidatorType.Custom,
        validator: (val: string, callback: (error?: string) => void) => {
          if (!val) {
            callback('请输入账号');
          } else if (val.length < 3) {
            callback('账号至少3个字符');
          }
          callback();
        }
      }
    ],
    email: [
      {
        type: ValidatorType.Custom,
        validator: (val: string, callback: (error?: string) => void) => {
          if (val) {
            callback('请输入账号');
          }
          callback();
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

  const filterSpace = (value: string) => {
    return value ? value.replace(/\s+/g, '') : '';
  };

  const handleSubmit = async () => {
    formRef.current?.form.validateFields().then(async (valid) => {
      // 表单验证通过
      if (valid) {
        const values = formRef.current?.form.getFieldsValue();
        if (!values) {
          return;
        }
        setLoading(true);

        const headers = {
          'X-Tenant-Id': tenantId
        };
        const password = await sm2Encrypt(getPublicKey(), values?.password || '');

        const deviceId = await getOrCreateDeviceInfo();

        const registerParams: thirdUserRegisterParams = {
          appId: appId,
          mobile: filterSpace(mobile),
          email: filterSpace(values?.email),
          password: password,
          nickName: filterSpace(values?.nickName),
          deviceId: deviceId
        };
        const response = await thirdUserRegisterApi(registerParams, headers);

        if (response) {
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
            false
          );
          onOk();
        }
      }
    });
  };

  return (
    <Popup visible={visible} close={onCancel} direction="bottom" maskClosable={false} className={styles.popup}>
      <div className={styles.popupContent}>
        <CustomNav title="" />

        <div className={styles.popupHeader}>
          <div className={styles.popupTitle}>请补充用户信息</div>
        </div>
        <Form ref={formRef} layout="vertical" className={styles.popupForm}>
          <Form.Item label="姓名" field="nickName" rules={rules.username}>
            <Input label={<IconUser />} placeholder="请输入姓名" />
          </Form.Item>
          <Form.Item label="邮箱(选填)" field="email" rules={rules.email}>
            <Input
              label={<img src={emailIcon} alt="email" className={styles.popupFormIcon} />}
              placeholder="请输入邮箱(选填)"
            />
          </Form.Item>
          <Form.Item label="密码" field="password" rules={rules.password}>
            <Input
              label={<img src={passwordIcon} alt="password" className={styles.popupFormIcon} />}
              placeholder="请输入密码"
              type="password"
            />
          </Form.Item>
          <Form.Item label="确认密码" field="confirmNewPassword" rules={rules.confirmNewPassword}>
            <Input type="password" label={<IconSuccessCircle />} placeholder="请输入确认密码" />
          </Form.Item>
        </Form>
        <div className={styles.popupBtn}>
          <Button type="primary" size="large" loading={loading} onClick={handleSubmit}>
            确认
          </Button>
        </div>
      </div>
    </Popup>
  );
};

export default RegisterForm;
