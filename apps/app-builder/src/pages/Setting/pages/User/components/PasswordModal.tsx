import { Form, Input, Modal } from '@arco-design/web-react';
import React from 'react';

type ResetPasswordModalProps = {
  visible: boolean;
  onCancel: () => void;
  onOk: (password: string) => void;
};
const PasswordModal: React.FC<ResetPasswordModalProps> = ({ visible, onCancel, onOk }) => {
  const [form] = Form.useForm();

  const handleOk = async () => {
    const values = await form.validate();
    onOk(values.password);
  };

  return (
    <Modal
      title="重置密码"
      visible={visible}
      onCancel={onCancel}
      onOk={handleOk}
      autoFocus={false}
      focusLock={true}
      okButtonProps={{ status: 'danger' }}
    >
      <Form form={form} layout="vertical">
        <Form.Item
          label="新密码"
          field="password"
          rules={[
            { required: true, message: '请输入新密码' },
            { min: 6, message: '密码长度不能少于6位' },
            { max: 20, message: '密码长度不能超过20位' }
          ]}
        >
          <Input.Password placeholder="请输入新密码" defaultVisibility={false} />
        </Form.Item>
        <Form.Item
          label="确认密码"
          field="confirmPassword"
          dependencies={['password']}
          rules={[
            { required: true, message: '请再次输入密码' },
            {
              validator: (value: any, callback: (error?: string) => void) => {
                if (!value) {
                  callback('请再次输入密码');
                } else if (form.getFieldValue('password') !== value) {
                  callback('两次输入的密码不一致');
                } else {
                  callback();
                }
              }
            }
          ]}
        >
          <Input.Password placeholder="请再次输入密码" defaultVisibility={false} />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default PasswordModal;
