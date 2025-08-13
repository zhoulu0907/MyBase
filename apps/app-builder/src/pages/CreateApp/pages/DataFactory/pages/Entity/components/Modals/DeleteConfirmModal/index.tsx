import { Modal } from '@arco-design/web-react';
import React from 'react';

interface DeleteConfirmModalProps {
  visible: boolean;
  onVisibleChange: (visible: boolean) => void;
  onConfirm: () => void;
  confirmLoading?: boolean;
  title?: string;
  content?: string;
  okText?: string;
  cancelText?: string;
}

const DeleteConfirmModal: React.FC<DeleteConfirmModalProps> = ({
  visible,
  onVisibleChange,
  onConfirm,
  confirmLoading = false,
  title = '确认删除',
  content = '确定要删除这个项目吗？删除后无法恢复。',
  okText = '确认删除',
  cancelText = '取消'
}) => {
  const handleCancel = () => {
    onVisibleChange(false);
  };

  return (
    <Modal
      title={title}
      visible={visible}
      onOk={onConfirm}
      onCancel={handleCancel}
      confirmLoading={confirmLoading}
      okText={okText}
      cancelText={cancelText}
      okButtonProps={{ status: 'danger' }}
    >
      <p>{content}</p>
    </Modal>
  );
};

export default DeleteConfirmModal; 