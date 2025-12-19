import { Form, Input, Button, Typography, Link } from '@arco-design/web-react';
import styles from './register.module.less';

interface IConfirmInfoProps {
    onGoBack: () => void;
}
const ConfirmInfoForm:React.FC<IConfirmInfoProps> = ({onGoBack}) => {
  const [form] = Form.useForm();

  return (
    <div className={styles.registerPage}>
      {/* 返回按钮 */}
      <Link className={styles.back} onClick={onGoBack}>← 返回</Link>

      {/* 标题 */}
      <Typography.Title heading={2}>请确认用户信息</Typography.Title>
      <Typography.Text type='secondary'>检测到您的预留信息，确认身份即可登录</Typography.Text>

      {/* 表单区域 */}
      <Form layout="vertical" style={{marginTop:'40px'}}>
        {/* 姓名输入框 */}
        <Form.Item label="姓名" field="nickname" required rules={[{ required: true, message: '请输入姓名' }]}>
          <Input placeholder="请输入姓名" />
        </Form.Item>

        {/* 邮箱输入框（选填） */}
        <Form.Item label="邮箱(选填)" field="email" required rules={[{ required: true, message: '请输入邮箱' }]}>
          <Input placeholder="请输入邮箱" />
        </Form.Item>
          <Button type="primary" long size="large" className={styles.loginButton}>
            确认
          </Button>
      </Form>
    </div>
  );
};

export default ConfirmInfoForm;
