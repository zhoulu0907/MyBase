import { Form, Input, Button, Typography, Link } from '@arco-design/web-react';
import styles from './register.module.less';
import { useState } from 'react';
import { createExternalUserApp, type createExternalUserAppParams } from '@onebase/platform-center';
import { getHashQueryParam, TokenManager } from '@onebase/common';
import { useNavigate } from 'react-router-dom';

interface IConfirmInfoProps {
  tenantId: string;
  appId: string;
  onGoBack: () => void;
}
const ConfirmInfoForm:React.FC<IConfirmInfoProps> = ({ appId, tenantId, onGoBack}) => {
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const [loading, setLoading] = useState<boolean>(false);
  const tokenInfo = TokenManager.getTokenInfo();

    const handleSubmit = async () => {
    try {
      setLoading(true);
      // 先验证表单
      const values = await form.validate();
      const params: createExternalUserAppParams = {
        userId: tokenInfo?.userId || '',
        applicationIdList:[appId],
        email: values?.email || '',
        nickName: values?.nickName || ''
      };
      const response = await createExternalUserApp(params);

      if (response) {
        const redirectURL = getHashQueryParam('redirectURL');
        if (redirectURL) {
          navigate(`/onebase/${tenantId}/${appId}/runtime`);
        } else {
          // 跳转到首页
          navigate(`/onebase/${tenantId}/runtime/`);
        }
      }
    } catch (error) {
      console.log('error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.registerPage}>
      {/* 返回按钮 */}
      <Link className={styles.back} onClick={onGoBack}>← 返回</Link>

      {/* 标题 */}
      <Typography.Title heading={2}>请确认用户信息</Typography.Title>
      <Typography.Text type='secondary'>检测到您的预留信息，确认身份即可登录</Typography.Text>

      {/* 表单区域 */}
      <Form layout="vertical" style={{marginTop:'40px'}} 
        form={form} 
        initialValues={{
          nickName: tokenInfo?.nickName,
          email: tokenInfo?.email
        }}
      >
        {/* 姓名输入框 */}
        <Form.Item label="姓名" field="nickName" required rules={[{ required: true, message: '请输入姓名' }]}>
          <Input placeholder="请输入姓名" />
        </Form.Item>

        {/* 邮箱输入框（选填） */}
        <Form.Item label="邮箱(选填)" field="email">
          <Input placeholder="请输入邮箱" />
        </Form.Item>
          <Button type="primary" long size="large" className={styles.loginButton} loading={loading} onClick={handleSubmit}>
            确认
          </Button>
      </Form>
    </div>
  );
};

export default ConfirmInfoForm;
