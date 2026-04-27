import { Form, Grid, Input, Modal } from '@arco-design/web-react';
import React, { useState } from 'react';
import styles from './EditVersionModal.module.less';

const TextArea = Input.TextArea;

interface EditVersionModalProps {
  visible: boolean;
  onCancel: () => void;
  onOk: (values: EditVersionFormData) => void;
  loading?: boolean;
  initialData?: EditVersionFormData;
}

interface EditVersionFormData {
  versionName: string;
  versionNumber: string;
  description: string;
}

const EditVersionModal: React.FC<EditVersionModalProps> = ({
  visible,
  onCancel,
  onOk,
  loading = false,
  initialData
}) => {
  const [form] = Form.useForm();
  const [formData] = useState<EditVersionFormData>({
    versionName: initialData?.versionName || '回退至OB3.0_V2版本',
    versionNumber: initialData?.versionNumber || 'V 2.0.4',
    description: initialData?.description || '这是一段描述'
  });

  const handleOk = async () => {
    try {
      const values = await form.validate();
      onOk(values);
    } catch (error) {
      console.error('表单验证失败:', error);
    }
  };

  const handleCancel = () => {
    form.resetFields();
    onCancel();
  };

  return (
    <Modal
      title="编辑"
      visible={visible}
      onOk={handleOk}
      onCancel={handleCancel}
      confirmLoading={loading}
      className={styles.editVersionModal}
      okText="保存"
    >
      <Form form={form} layout="vertical" initialValues={formData} className={styles.form}>
        <Grid.Row gutter={16}>
          <Grid.Col span={16}>
            <Form.Item label="名称" field="versionName" rules={[{ required: true, message: '请输入名称' }]}>
              <Input placeholder="请输入" />
            </Form.Item>
          </Grid.Col>
          <Grid.Col span={8}>
            <Form.Item label="版本号" field="versionNumber">
              <Input value={formData.versionNumber} disabled />
            </Form.Item>
          </Grid.Col>
        </Grid.Row>

        <Form.Item label="版本描述" field="description">
          <TextArea placeholder="请简要表述当前版本情况,便于区分不同的版本" rows={4} />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default EditVersionModal;
