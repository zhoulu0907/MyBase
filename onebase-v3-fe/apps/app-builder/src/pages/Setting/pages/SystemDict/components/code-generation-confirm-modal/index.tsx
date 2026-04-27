import React from 'react';
import { Modal, Button, Space } from '@arco-design/web-react';
import { IconExclamationCircle } from '@arco-design/web-react/icon';
import styles from './index.module.less';

interface CodeGenerationConfirmModalProps {
  visible: boolean;
  onConfirm: () => void;
  onCancel: () => void;
  loading?: boolean;
}

/**
 * 编码生成确认对话框
 * 提示用户生成编码的后果，让用户确认是否继续
 */
const CodeGenerationConfirmModal: React.FC<CodeGenerationConfirmModalProps> = ({
  visible,
  onConfirm,
  onCancel,
  loading = false
}) => {
  return (
    <Modal
      className={styles.confirmModal}
      title={
        <div className={styles.modalTitle}>
          <IconExclamationCircle className={styles.warningIcon} />
          <span>确认生成编码</span>
        </div>
      }
      visible={visible}
      onCancel={onCancel}
      footer={
        <Space>
          <Button onClick={onCancel} disabled={loading}>
            取消
          </Button>
          <Button type="primary" onClick={onConfirm} loading={loading} status="warning">
            确认生成
          </Button>
        </Space>
      }
      style={{ width: 480 }}
      closable={!loading}
      maskClosable={!loading}
    >
      <div className={styles.modalContent}>
        <div className={styles.warningText}>将为未设置编码的字典值项自动生成编码，生成后不可修改，是否继续？</div>
      </div>
    </Modal>
  );
};

export default CodeGenerationConfirmModal;
