import { Button, Input, Modal, Form, DatePicker } from "@arco-design/web-react";
import styles from "./index.module.less";
import type { CreateLicenseFileReq } from "@onebase/platform-center";
import { formatTimestamp } from "@/utils/date";

interface DownloadLicenseModal {
  visible: boolean;
  handleCancel: () => void;
  handleSubmit: (values: CreateLicenseFileReq) => void;
}

const DownloadLicenseModal = (props: DownloadLicenseModal) => {
  const { visible, handleCancel, handleSubmit } = props;
  const [form] = Form.useForm();

  const onSubmit = async () => {
    try {
      const values = await form.validate();
      console.log('onSubmit values.expireTime:', values.expireTime);
      // 将日期转换为时间戳
      if (values.expireTime) {
        const timestamp = new Date(values.expireTime).getTime();
        values.expireTime = timestamp;
      }
      handleSubmit(values);
    } catch (error) {
      console.error('表单验证失败:', error);
    }
  };

  return (
    <>
      <Modal
        title="下载生成License文件"
        visible={visible}
        onOk={onSubmit}
        onCancel={handleCancel}
        footer={
          <div className={styles.downloadLicenseModalFooter}>
            <Button onClick={() => handleCancel()}>取消</Button>
            <Button type="primary" onClick={onSubmit}>确定</Button>
          </div>
        }
      >
        <Form form={form} layout="vertical">
          <Form.Item 
            label="License租户上限数" 
            field="tenantLimit"
            rules={[{ required: true, message: '请输入租户上限数' }]}
          >
            <Input placeholder="请输入License租户数量" defaultValue="3000" />
          </Form.Item>
          <Form.Item 
            label="License用户上限数" 
            field="userLimit"
            rules={[{ required: true, message: '请输入用户上限数' }]}
          >
            <Input placeholder="请输入License用户数量" defaultValue="3000" />
          </Form.Item>
          <Form.Item 
            label="License到期时间" 
            field="expireTime"
            rules={[{ required: true, message: '请选择到期时间' }]}
          >
            <DatePicker placeholder="请选择到期时间" defaultPickerValue={new Date()} />
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}

export default DownloadLicenseModal;