import { Form, Input, Button, Typography, Link } from '@arco-design/web-react';
import styles from './register.module.less';
import { IconMobile } from '@arco-design/web-react/icon';
import { useState } from 'react';
import { forgotPWD, sendVerifyCodeApi, type forgotPWDParams } from '@onebase/platform-center';
import { VerifyInput } from '@onebase/common';
import { phoneValidator } from '@/utils/validator';

interface IConfirmInfoProps {
  onGoBack: () => void;
}
const UpdatePasswordForm: React.FC<IConfirmInfoProps> = ({ onGoBack }) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState<boolean>(false);
  const [verifyCode, setVerifyCode] = useState<string>('');

  const handleSubmit = async () => {
    try {
      setLoading(true);
      // 先验证表单
      const values = await form.validate();
      const params: forgotPWDParams = {
        mobile: values.mobile,
        password: values.password,
        verifyCode: verifyCode
      };
      await forgotPWD(params);
    } catch (error) {
      console.log('error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.registerPage}>
      <Link className={styles.back} onClick={onGoBack}>
        ← 返回
      </Link>

      <Typography.Title heading={2}>找回密码</Typography.Title>

      {/* 表单区域 */}
      <Form layout="vertical" form={form} style={{ marginTop: '40px' }}>
        <Form.Item
          label="手机号"
          field="mobile"
          rules={[{ required: true, message: '请输入手机号' }, { validator: phoneValidator }]}
        >
          <Input placeholder="输入手机号" maxLength={11} prefix={<IconMobile />} />
        </Form.Item>
        <Form.Item field="verifyCode">
          <VerifyInput
            userMobile={form.getFieldValue('mobile')}
            verifyType={'mobile'}
            sendVerifyCode={sendVerifyCodeApi}
            onChange={setVerifyCode}
            verifyCode={verifyCode}
          />
        </Form.Item>

        {/* 密码输入框（带可见性切换） */}
        <Form.Item
          label="新密码"
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
            { required: true, message: '请输入密码' },
            {
              validator: (value, cb) => {
                if (!value) return cb();
                const newPassword = form.getFieldValue('password');
                if (value !== newPassword) {
                  return cb('两次输入的密码不一致');
                }
                return cb();
              }
            }
          ]}
        >
          <Input.Password placeholder="请输入密码" />
        </Form.Item>
        <Button
          type="primary"
          long
          size="large"
          className={styles.loginButton}
          loading={loading}
          onClick={handleSubmit}
        >
          确认
        </Button>
      </Form>
    </div>
  );
};

export default UpdatePasswordForm;
