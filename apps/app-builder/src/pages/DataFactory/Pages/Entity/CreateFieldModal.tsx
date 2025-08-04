import React from 'react';
import { Form, Input, Select, Message, Modal } from '@arco-design/web-react';
import styles from './index.module.less';
import type { EntityField, EntityNode } from '../../utils/interface';

interface EntityFormValues {
  code: string;
  name: string;
  description: string;
  type: string;
}

const dataTypes = [
  { label: '常规短文本', value: 'TEXT' },
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
  { label: '聚合统计', value: 'AGGREGATE' },
];

const CreateFieldModal: React.FC<{ visible: boolean, setVisible: (visible: boolean) => void, setRefreshEntityList: (refresh: boolean) => void, entityId: string, successCallback: () => void }> = ({ visible, setVisible, entityId, successCallback }) => {
  const [form] = Form.useForm<EntityFormValues>();
  // 提交
  const handleFinish = () => {
    // TODO: 提交表单数据
    form.validate().then(values => {
      const { nodes } = JSON.parse(localStorage.getItem('entityFormValues') || JSON.stringify({ nodes: [] }));
      const entity = nodes.find((node: EntityNode) => node.id === entityId);
      if (entity) {
        if (entity.fields.find((field: EntityField) => field.id === values.code)) {
          Message.error('字段编码已存在');
          return;
        }
        entity.fields.push({
          id: values.code,
          code: values.code,
          name: values.name,
          type: values.type,
          isSystem: false,
        });
      }

      localStorage.setItem('entityFormValues', JSON.stringify({ nodes }));
      // console.log(values);
      Message.success('保存成功');
      setVisible(false);
      successCallback();
    });
  };

  return (
    <Modal
      className={styles['create-entity-modal']}
      title='添加数据字段' visible={visible}
      onOk={handleFinish}
      onCancel={() => setVisible(false)}
      okText='创建'
      cancelText='取消'
    >
      <Form
        form={form}
        layout="vertical"
        onSubmit={handleFinish}
        className={styles['entity-form']}
      >

        <Form.Item
          label="字段编码"
          field="code"
          rules={[
            { required: true, message: '请输入字段编码' },
            { max: 40, message: '字段编码不能超过40个字符' }
          ]}
        >
          <Input maxLength={40} placeholder="请输入字段编码，由字母、数字、下划线组合，须以字母开头，不超过40个字符" />
        </Form.Item>

        <Form.Item
          label="字段名称"
          field="name"
          rules={[
            { required: true, message: '请输入字段名称' },
            { max: 50, message: '字段名称不能超过50个字符' }
          ]}
        >
          <Input maxLength={50} placeholder="请输入字段名称，不超过50个字符" />
        </Form.Item>

        <Form.Item
          label="字段描述"
          field="description"
        >
          <Input.TextArea
            placeholder="请输入描述（选填）"
            rows={4}
            maxLength={500}
            showWordLimit
          />
        </Form.Item>

        <Form.Item
          label="数据类型"
          field="type"
        >
          <Select placeholder="请选择数据类型" options={dataTypes} />
        </Form.Item>

      </Form>
    </Modal>
  );
};

export default CreateFieldModal;
