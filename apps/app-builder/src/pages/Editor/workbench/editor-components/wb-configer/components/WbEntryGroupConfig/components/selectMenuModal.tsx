import { useState } from 'react';
import { Modal } from '@arco-design/web-react';
import { type ApplicationMenu } from '@onebase/app';
import MenuSelector from '@/pages/Editor/workbench/components/MenuSelector';

interface SelectMenuModalProps {
  visible: boolean;
  onCancel: () => void;
  onOk: (menuIds: ApplicationMenu[]) => void;
}

const SelectMenuModal = ({ visible, onCancel, onOk }: SelectMenuModalProps) => {
  const [selectedKeys, setSelectedKeys] = useState<string[]>([]);
  const [selectedMenusCache, setSelectedMenusCache] = useState<ApplicationMenu[]>([]);

  const handleCancel = () => {
    setSelectedKeys([]);
    setSelectedMenusCache([]);
    onCancel();
  };

  const handleOk = () => {
    onOk(selectedMenusCache);
    setSelectedKeys([]);
    setSelectedMenusCache([]);
  };

  const handleMenuChange = (value: string | string[], selectedMenus: ApplicationMenu | ApplicationMenu[]) => {
    const keys = Array.isArray(value) ? value : [value];
    const menus = Array.isArray(selectedMenus) ? selectedMenus : [selectedMenus];
    setSelectedKeys(keys);
    setSelectedMenusCache(menus);
  };

  return (
    <Modal title="选择菜单" visible={visible} onCancel={handleCancel} onOk={handleOk}>
      <MenuSelector mode="multiple" value={selectedKeys} onChange={handleMenuChange} />
    </Modal>
  );
};

export default SelectMenuModal;
