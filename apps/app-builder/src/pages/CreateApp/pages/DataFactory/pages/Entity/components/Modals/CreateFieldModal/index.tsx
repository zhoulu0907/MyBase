import type { EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { ENTITY_FIELD_TYPE, FIELD_TYPE } from '@onebase/ui-kit';
import { useAppStore } from '@/store/store_app';
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

const dataTypes = Object.entries(ENTITY_FIELD_TYPE).map(([key, value]) => ({
  label: value,
  value: key
}));

const CreateFieldModal: React.FC<{
  visible: boolean;
  setVisible: (visible: boolean) => void;
  entity: EntityNode;
  successCallback: () => void;
}> = ({ visible, setVisible, entity, successCallback }) => {
  const { curAppId } = useAppStore();
  const [form] = Form.useForm<FieldFormValues>();
  // 提交
  const handleFinish = () => {
    form.validate().then(async (values) => {
      const res = await createField({
        entityId: entity.entityId,
        displayName: entity.entityName,
        applicationId: curAppId,
        ...values,
        isSystemField: FIELD_TYPE.CUSTOM
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
      className={styles.createEntityModal}
      title="添加数据字段"
      visible={visible}
      onOk={handleFinish}
      onCancel={() => setVisible(false)}
      okText="创建"
      cancelText="取消"
    >
      <Form form={form} layout="vertical" onSubmit={handleFinish}>
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
