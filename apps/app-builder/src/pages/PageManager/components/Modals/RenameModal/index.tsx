import { Form, Input, Modal, type FormInstance } from '@arco-design/web-react';
import React from 'react';

/**
 * RenameModal 组件
 * 用于页面管理器中重命名弹窗的占位组件
 * 实际弹窗逻辑在 PageManagerPage 中实现
 */
interface RenameModalProps {
  title: string;
  visible: boolean;
  setVisible: (visible: boolean) => void;
  handleRename: () => void;
  form: FormInstance;
  initValue: string;
}

const RenameModal: React.FC<RenameModalProps> = ({ title, visible, handleRename, setVisible, form, initValue }) => {
  return (
    <Modal
      title={title}
      visible={visible}
      onOk={handleRename}
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
          menuName: initValue
        }}
      >
        <Form.Item label="页面名称" field="menuName" rules={[{ required: true, message: '请输入页面名称' }]}>
          <Input placeholder="请输入页面名称" allowClear />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default RenameModal;
