import emailIcon from '@/assets/images/login/email.svg';
import { Form, Input, Button, Popup } from '@arco-design/mobile-react';
import { type IFormInstance } from '@arco-design/mobile-react/esm/form';
import { IconArrowBack, IconUser } from '@arco-design/mobile-react/esm/icon';
import { ValidatorType } from '@arco-design/mobile-utils';
import { useRef, useState } from 'react';
import { TokenManager } from '@onebase/common';
import { createExternalUserApp, type createExternalUserAppParams } from '@onebase/platform-center';
import styles from '../../index.module.less';

interface IConfirmInfoProps {
  visible: boolean;
  tenantId: string;
  appId: string;
  onOk: () => void;
  onCancel: () => void;
}

interface FormRef {
  dom: HTMLFormElement;
  form: IFormInstance;
}

const ConfirmInfoForm: React.FC<IConfirmInfoProps> = ({ visible, appId, tenantId, onOk, onCancel }) => {
  const formRef = useRef<FormRef>(null);
  const [loading, setLoading] = useState(false);
  const tokenInfo = TokenManager.getTokenInfo();
  // 校验规则
  const rules = {
    nickName: [
      {
        type: ValidatorType.Custom,
        validator: (val: string, callback: (error?: string) => void) => {
          if (!val) {
            callback('请输入姓名');
          }
          callback();
        }
      }
    ],
    email: [
      {
        type: ValidatorType.Custom,
        validator: (val: string, callback: (error?: string) => void) => {
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
      const params: createExternalUserAppParams = {
        userId: tokenInfo?.userId || '',
        applicationIdList: [appId],
        email: values?.email || '',
        nickName: values?.nickName || ''
      };
      const response = await createExternalUserApp(params);
      if (response) {
        onOk();
      }
    } catch (err) {
      console.log(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Popup visible={visible} close={onCancel} direction="bottom" maskClosable={false} className={styles.popup}>
      <div className={styles.popupContent}>
        <div className={styles.popupBack}>
          <IconArrowBack onClick={onCancel} />
        </div>

        <div className={styles.popupHeader}>
          <div className={styles.popupTitle}>请确认用户信息</div>
          <div className={styles.popupDescribe}>检测到您的预留信息，确认身份即可登录</div>
        </div>
        <Form ref={formRef} layout="vertical" className={styles.popupForm}>
          <Form.Item label="姓名" field="nickName" rules={rules.nickName}>
            <Input label={<IconUser />} placeholder="请输入姓名" />
          </Form.Item>
          <Form.Item label="邮箱(选填)" field="email" rules={rules.email}>
            <Input
              label={<img src={emailIcon} alt="email" className={styles.popupFormIcon} />}
              placeholder="请输入邮箱(选填)"
            />
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

export default ConfirmInfoForm;
