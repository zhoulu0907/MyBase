import { Modal } from '@arco-design/web-react';
import MenuComp from '@/components/MenuIcon';

interface IconSelectorModalProps {
  visible: boolean;
  onClose: () => void;
  onSelected: (iconCode: string) => void;
}

export const IconSelectorModal = ({ visible, onClose, onSelected }: IconSelectorModalProps) => {
  return (
    <Modal title="选择图标" visible={visible} onCancel={onClose} footer={null} style={{ width: '800px' }}>
      <div style={{ padding: 0, height: '600px', overflow: 'hidden' }}>
        <MenuComp
          style={{ position: 'relative', transform: 'none', height: '100%' }}
          onSelected={onSelected}
          handleBack={onClose}
        />
      </div>
    </Modal>
  );
};

