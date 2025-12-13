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

  const handleCancel = () => {
    setSelectedKeys([]);
    onCancel();
  };

  const handleOk = () => {
    const selectedMenus = selectedKeys.map((key) => findMenuInList(menuList, key)).filter(Boolean) as ApplicationMenu[];
    onOk(selectedMenus);
    setSelectedKeys([]);
  };

  // 递归查找菜单项
  const findMenuInList = (menus: ApplicationMenu[], id: string): ApplicationMenu | null => {
    for (const menu of menus) {
      if (menu.id === id) {
        return menu;
      }
      if (menu.children && menu.children.length > 0) {
        const found = findMenuInList(menu.children, id);
        if (found) return found;
      }
    }
    return null;
  };

  const handleMenuChange = (value: string | string[], selectedMenus: ApplicationMenu | ApplicationMenu[]) => {
    setSelectedKeys(Array.isArray(value) ? value : [value]);
  };

  return (
    <Modal title="选择菜单" visible={visible} onCancel={handleCancel} onOk={handleOk}>
      <MenuSelector mode="multiple" value={selectedKeys} onChange={handleMenuChange} />
    </Modal>
  );
};

export default SelectMenuModal;
