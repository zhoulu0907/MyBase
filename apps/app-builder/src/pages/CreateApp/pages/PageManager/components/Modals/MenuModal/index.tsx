import React from 'react';
import { Modal } from '@arco-design/web-react';
import { IconLeft } from '@arco-design/web-react/icon';
import MenuComp from '@/components/MenuIcon';

interface CreateModalProps {
  handleSelect: (val: string) => void;
  onCancel: () => void;
  visible: boolean;
}

const CreateModal: React.FC<CreateModalProps> = ({ handleSelect, onCancel, visible }) => {
  return (
    <Modal
      title={
        <div style={{ textAlign: 'left', cursor: 'pointer' }}>
          <IconLeft onClick={onCancel} />
          菜单图标选择
        </div>
      }
      simple
      footer={null}
      closable={false}
      visible={visible}
      onCancel={onCancel}
      autoFocus={false}
      focusLock={true}
      unmountOnExit={true}
      style={{ width: 800 }}
    >
      <MenuComp onSelected={handleSelect} />
    </Modal>
  );
};

export default CreateModal;
