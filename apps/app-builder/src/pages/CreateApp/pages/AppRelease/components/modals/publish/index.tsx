import { Form, Grid, Input, Modal, Tag } from '@arco-design/web-react';
import { onlineApplication, OperationType, type OnlineApplicationReq } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';

const TextArea = Input.TextArea;

interface PublishVersionModalProps {
  applicationId: string;
  appName?: string;
  visible: boolean;
  onCancel: () => void;
  onOk: (values: PublishVersionFormData) => void;
}

interface PublishVersionFormData {
  versionName: string;
  versionNumber: string;
  description: string;
  environment: string;
}

const PublishVersionModal: React.FC<PublishVersionModalProps> = ({
  applicationId,
  appName,
  visible,
  onCancel,
  onOk
}) => {
  const [loading, setLoading] = useState(false);

  const [form] = Form.useForm();
  const [formData] = useState<PublishVersionFormData>({
    versionName: '',
    versionNumber: '',
    description: '',
    environment: '正式环境'
  });

  // 当弹窗打开时，将应用名称填充到名称字段
  useEffect(() => {
    if (visible) {
      // 重置表单到初始值
      form.resetFields();
      // 如果有应用名称，则设置到表单
      // 使用 setTimeout 确保 setFieldsValue 在 resetFields 之后执行
      if (appName) {
        setTimeout(() => {
          form.setFieldsValue({
            versionName: appName
          });
        }, 0);
      }
    }
  }, [visible, appName, form]);

  const handleOk = async () => {
    try {
      const values = await form.validate();
      setLoading(true);

      const req: OnlineApplicationReq = {
        applicationId: applicationId,
        versionName: values.versionName,
        versionNumber: values.versionNumber,
        versionDescription: values.description,
        environment: '正式环境',
        operationType: OperationType.PUBLISH
      };
      const res = await onlineApplication(req);
      console.log(res);

      setLoading(false);
      form.resetFields();
      onOk(values);
    } catch (error) {
      console.error('表单验证失败:', error);
      setLoading(false);
    }
  };

  const handleCancel = () => {
    form.resetFields();
    onCancel();
  };

  return (
    <Modal
      title="发布版本 "
      visible={visible}
      onOk={handleOk}
      onCancel={handleCancel}
      confirmLoading={loading}
      okText="发布"
      className={styles.publishVersionModal}
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

        <Form.Item label="发布环境" field="environment" rules={[{ required: true, message: '请选择发布环境' }]}>
          <Tag color="green" bordered className={styles.environmentTag}>
            正式环境
          </Tag>
        </Form.Item>

        {/* <Form.Item label="版本比对" field="versionComparison" rules={[{ required: true, message: '请查看版本比对' }]}>
          <div className={styles.versionComparison}>
            <Typography.Text>与上一发布版本进行比对,变更清单如下:</Typography.Text>
            <div className={styles.comparisonList}>
              <Typography.Text>• xxx、xxx等元数据资源被修改,共计88项</Typography.Text>
              <Typography.Text>• xxx、xxx等页面资源被修改,共计123项</Typography.Text>
            </div>
            <Button type="text" className={styles.viewDetailsButton}>
              查看详情
            </Button>
          </div>
        </Form.Item> */}
      </Form>
    </Modal>
  );
};

export default PublishVersionModal;
