import { Modal, Button } from '@arco-design/web-react';
import React from 'react';
import styles from './index.module.less';

export interface DeleteConfirmModalProps {
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
  content = '确定要删除吗？删除后无法恢复。',
  okText = '删除',
  cancelText = '取消'
}) => {
  const handleCancel = () => {
    onVisibleChange(false);
  };

  return (
    <Modal
      visible={visible}
      onCancel={handleCancel}
      footer={null}
      autoFocus={false}
      focusLock={true}
      className={styles.deleteModal}
    >
      <div className={styles.modalContent}>
        <div className={styles.modalTitle}>{title}</div>
        <div className={styles.modalDesc}>{content}</div>
        <div className={styles.modalFooter}>
          <Button type="secondary" onClick={handleCancel}>
            {cancelText}
          </Button>
          <Button
            type="primary"
            status="danger"
            loading={confirmLoading}
            onClick={onConfirm}
          >
            {okText}
          </Button>
        </div>
      </div>
    </Modal>
  );
};

export default DeleteConfirmModal;