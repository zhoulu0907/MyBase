import type { EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { Form, Input, Message, Modal, Select } from '@arco-design/web-react';
import { createField } from '@onebase/app';
import React from 'react';
import styles from '../modal.module.less';
interface FieldFormValues {
  fieldCode: string;
  fieldName: string;
  description: string;
  fieldType: string;
}

const dataTypes = [
  { label: '常规文本', value: 'TEXT' },
  { label: '长文本内容', value: 'LONG_TEXT' },
  { label: '邮箱地址', value: 'EMAIL' },
  { label: '电话号码', value: 'PHONE' },
  { label: '网址链接', value: 'URL' },
  { label: '详细地址', value: 'ADDRESS' },
  { label: '通用数字', value: 'NUMBER' },
  { label: '货币金额', value: 'CURRENCY' },
  { label: '日期', value: 'DATE' },
  { label: '日期时间', value: 'DATETIME' },
  { label: '布尔值', value: 'BOOLEAN' },
  { label: '单选列表', value: 'PICKLIST' },
  { label: '多选列表', value: 'MULTI_PICKLIST' },
  { label: '自动编码', value: 'AUTO_CODE' },
  { label: '用户引用', value: 'USER' },
  { label: '部门引用', value: 'DEPARTMENT' },
  { label: '数据选择', value: 'DATA_SELECTION' },
  { label: '关联关系', value: 'RELATION' },
  { label: '结构化对象', value: 'STRUCTURE' },
  { label: '数组列表', value: 'ARRAY' },
  { label: '文件', value: 'FILE' },
  { label: '图片', value: 'IMAGE' },
  { label: '地理位置', value: 'GEOGRAPHY' },
  { label: '密码', value: 'PASSWORD' },
  { label: '加密字段', value: 'ENCRYPTED' },
  { label: '聚合统计', value: 'AGGREGATE' }
];

const CreateFieldModal: React.FC<{
  visible: boolean;
  setVisible: (visible: boolean) => void;
  entity: EntityNode;
  successCallback: () => void;
}> = ({ visible, setVisible, entity, successCallback }) => {
  const [form] = Form.useForm<FieldFormValues>();
  // 提交
  const handleFinish = () => {
    // TODO: 提交表单数据
    form.validate().then(async (values) => {
      const res = await createField({
        entityId: entity.entityId,
        displayName: entity.entityName,
        appId: '1',
        ...values,
        isSystemField: false
      });

      console.log('createField', res);

      form.resetFields();
      Message.success('保存成功');
      setVisible(false);
      successCallback();
    });
  };

  return (
    <Modal
      className={styles['create-entity-modal']}
      title="添加数据字段"
      visible={visible}
      onOk={handleFinish}
      onCancel={() => setVisible(false)}
      okText="创建"
      cancelText="取消"
    >
      <Form form={form} layout="vertical" onSubmit={handleFinish} className={styles['entity-form']}>
        <Form.Item
          label="字段编码"
          field="fieldCode"
          rules={[
            { required: true, message: '请输入字段编码' },
            { max: 40, message: '字段编码不能超过40个字符' }
          ]}
        >
          <Input maxLength={40} placeholder="请输入字段编码，由字母、数字、下划线组合，须以字母开头，不超过40个字符" />
        </Form.Item>

        <Form.Item
          label="字段名称"
          field="fieldName"
          rules={[
            { required: true, message: '请输入字段名称' },
            { max: 50, message: '字段名称不能超过50个字符' }
          ]}
        >
          <Input maxLength={50} placeholder="请输入字段名称，不超过50个字符" />
        </Form.Item>

        <Form.Item label="字段描述" field="description">
          <Input.TextArea placeholder="请输入描述（选填）" rows={4} maxLength={500} showWordLimit />
        </Form.Item>

        <Form.Item label="数据类型" field="fieldType">
          <Select placeholder="请选择数据类型" options={dataTypes} />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default CreateFieldModal;
