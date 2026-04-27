import { Form, Input, Modal, TreeSelect, type FormInstance } from '@arco-design/web-react';
import React from 'react';

/**
 * CopyModal 组件
 * 用于页面管理器中复制弹窗的占位组件
 * 实际弹窗逻辑在 PageManagerPage 中实现
 */
interface CopyModalProps {
  title: string;
  visible: boolean;
  setVisible: (visible: boolean) => void;
  handleCopy: () => void;
  form: FormInstance;
  treeData: any[];
}

const CopyModal: React.FC<CopyModalProps> = ({ title, visible, handleCopy, setVisible, form, treeData }) => {
  return (
    <Modal
      title={title}
      visible={visible}
      onOk={handleCopy}
      onCancel={() => {
        setVisible(false);
      }}
      autoFocus={false}
      focusLock={true}
      unmountOnExit={true}
    >
      <Form
        layout="vertical"
        form={form}
        initialValues={{
          menuName: form.getFieldValue('menuName'),
          menuID: form.getFieldValue('menuId'),
          parentId: form.getFieldValue('parentId')
        }}
      >
        <Form.Item label="页面名称" field="menuName" rules={[{ required: true, message: '请输入页面名称' }]}>
          <Input placeholder="请输入页面名称" allowClear />
        </Form.Item>

        <Form.Item label="父级页面" field="parentId">
          <TreeSelect treeData={treeData} placeholder="请选择父级页面" allowClear />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default CopyModal;
