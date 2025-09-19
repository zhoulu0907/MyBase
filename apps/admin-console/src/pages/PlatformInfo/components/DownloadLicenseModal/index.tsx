import { Button, Input, Modal, Form, DatePicker, Select } from "@arco-design/web-react";
import styles from "./index.module.less";
import type { CreateLicenseFileReq } from "@onebase/platform-center";
import { formatTimestamp } from "@/utils/date";
import { useEffect } from "react";

interface DownloadLicenseModal {
  visible: boolean;
  handleCancel: () => void;
  handleSubmit: (values: CreateLicenseFileReq) => void;
}

const DownloadLicenseModal = (props: DownloadLicenseModal) => {
  const { visible, handleCancel, handleSubmit } = props;
  const [form] = Form.useForm();

  useEffect(() => {
    if (visible) {
      form.setFieldsValue({
        enterpriseName: "上海移动有限公司",
        enterpriseCode: "F200090910001",
        enterpriseAddress: "上海市浦东金桥开发区",
        platformType: '私有化部署',
        tenantLimit: 3000,
        userLimit: 1000,
        expireTime: formatTimestamp(new Date().getTime())
      });
    }
  }, [visible, form])

  const platformTypeOptions = [
    { value: "私有化部署", label: "私有化部署" }
  ];
  const onSubmit = async () => {
    try {
      const values = await form.validate();
      // 将日期转换为时间戳（第二天的0点）
      if (values.expireTime) {
        values.expireTime = handleSelectToday(values.expireTime);
      }
      handleSubmit(values);
    } catch (error) {
      console.error('表单验证失败:', error);
    }
  };

  // 选择当前日期的方法
  const handleSelectToday = (value: string) => { 
    const date = new Date(value);
    date.setDate(date.getDate() + 1);
    date.setHours(0, 0, 0, 0);
    return date.getTime();
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
            label="企业名称" 
            field="enterpriseName"
            rules={[{ required: true, message: '请输入企业名称' }]}
          >
            <Input placeholder="请输入企业名称"/>
          </Form.Item>
          <Form.Item 
            label="企业编号" 
            field="enterpriseCode"
            rules={[{ required: true, message: '请输入企业编号' }]}
          >
            <Input placeholder="请输入企业编号"/>
          </Form.Item>
          <Form.Item 
            label="企业地址" 
            field="enterpriseAddress"
            rules={[{ required: true, message: '请输入企业地址' }]}
          >
            <Input placeholder="请输入企业地址"/>
          </Form.Item>
          <Form.Item
            label="平台类型" 
            field="platformType"
            rules={[{ required: true, message: '请输入企业地址' }]}
          >
            <Select defaultValue="私有化部署">
              {platformTypeOptions.map((item) => (
                <Select.Option value={item.value}>{item.label}</Select.Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item 
            label="License租户上限数" 
            field="tenantLimit"
            rules={[{ required: true, message: '请输入租户上限数' }]}
          >
            <Input placeholder="请输入License租户数量"/>
          </Form.Item>
          <Form.Item 
            label="License用户上限数" 
            field="userLimit"
            rules={[{ required: true, message: '请输入用户上限数' }]}
          >
            <Input placeholder="请输入License用户数量"/>
          </Form.Item>
          <Form.Item 
            label="License到期时间" 
            field="expireTime"
            rules={[
              { required: true, message: '请选择到期时间' },
              {
                validator: (value, callback) => {
                  if (value && handleSelectToday(value) <= new Date().getTime()) {
                    callback('到期时间必须大于当前时间');
                  }
                  callback();
                }
              }
            ]}
          >
            <DatePicker placeholder="请选择到期时间"/>
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}

export default DownloadLicenseModal;