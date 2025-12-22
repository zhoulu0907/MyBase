import { Form, Grid, Input, Modal } from '@arco-design/web-react';
import React, { useState } from 'react';
import styles from './SaveVersionModal.module.less';

const TextArea = Input.TextArea;

interface SaveVersionModalProps {
  visible: boolean;
  onCancel: () => void;
  onOk: (values: SaveVersionFormData) => void;
  loading?: boolean;
}

interface SaveVersionFormData {
  versionName: string;
  versionNumber: string;
  description: string;
}

const SaveVersionModal: React.FC<SaveVersionModalProps> = ({ visible, onCancel, onOk, loading = false }) => {
  const [form] = Form.useForm();
  const [formData] = useState<SaveVersionFormData>({
    versionName: '',
    versionNumber: 'V 2.0.4',
    description: ''
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
      title="保存版本"
      visible={visible}
      onOk={handleOk}
      onCancel={handleCancel}
      okText="保存"
      confirmLoading={loading}
      className={styles.saveVersionModal}
    >
      <Form form={form} layout="vertical" initialValues={formData} className={styles.form}>
        <Grid.Row gutter={16}>
          <Grid.Col span={16}>
            <Form.Item label="名称" field="versionName" rules={[{ required: true, message: '请输入名称' }]}>
              <Input placeholder="请输入" />
            </Form.Item>
          </Grid.Col>
          <Grid.Col span={8}>
            <Form.Item label="版本号" field="versionNumber" rules={[{ required: true, message: '请输入版本号' }]}>
              <Input value={formData.versionNumber} />
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

export default SaveVersionModal;
