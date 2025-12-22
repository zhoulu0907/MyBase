import { Form, Input, Button, Typography, Link } from '@arco-design/web-react';
import styles from './register.module.less';
import { IconMobile, IconSafe } from '@arco-design/web-react/icon';

interface IConfirmInfoProps {
  onGoBack: () => void;
}
const UpdatePasswordForm: React.FC<IConfirmInfoProps> = ({ onGoBack }) => {
  const [form] = Form.useForm();

  return (
    <div className={styles.registerPage}>
      <Link className={styles.back} onClick={onGoBack}>
        ← 返回
      </Link>

      <Typography.Title heading={2}>找回密码</Typography.Title>

      {/* 表单区域 */}
      <Form layout="vertical" style={{ marginTop: '40px' }}>
        <Form.Item label="手机号" field="mobile" rules={[{ required: true, message: '请输入手机号' }]}>
          <Input placeholder="输入手机号" maxLength={11} prefix={<IconMobile />} />
        </Form.Item>
        <Form.Item label="验证码" field="captchaVerification">
          <Input allowClear size="large" placeholder="请输入验证码" prefix={<IconSafe />} />
        </Form.Item>

        {/* 密码输入框（带可见性切换） */}
        <Form.Item label="新密码" field="oldPassword" required rules={[{ required: true, message: '请输入密码' }]}>
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
                const newPassword = form.getFieldValue('oldPassword');
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
        <Button type="primary" long size="large" className={styles.loginButton}>
          确认
        </Button>
      </Form>
    </div>
  );
};

export default UpdatePasswordForm;
