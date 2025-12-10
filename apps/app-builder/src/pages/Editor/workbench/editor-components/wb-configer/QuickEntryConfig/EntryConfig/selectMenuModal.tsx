import { useEffect, useState } from 'react';
import { Modal, Input, Tree } from '@arco-design/web-react';
import { IconDown } from '@arco-design/web-react/icon';
import { listApplicationMenu, type ApplicationMenu } from '@onebase/app';
import { useAppStore } from '@/store/store_app';
import { listToTree, treeFilter } from '@onebase/common';

interface SelectMenuModalProps {
  visible: boolean;
  onCancel: () => void;
  onOk: (menuIds: ApplicationMenu[]) => void;
}

const SelectMenuModal = ({ visible, onCancel, onOk }: SelectMenuModalProps) => {
  const { curAppId } = useAppStore();
  const [menuList, setMenuList] = useState<ApplicationMenu[]>([]);
  const [inputValue, setInputValue] = useState('');
  const [checkedKeys, setCheckedKeys] = useState<string[]>([]);

  const handleCheck = (checkedKeys: string[]) => {
    setCheckedKeys(checkedKeys);
  };

  const handleCancel = () => {
    setCheckedKeys([]);
    onCancel();
  };

  const handleOk = () => {
    const menuIds = menuList.filter((menu) => checkedKeys.includes(menu.id));
    onOk(menuIds);
    setCheckedKeys([]);
  };

  useEffect(() => {
    listApplicationMenu({ applicationId: curAppId }).then((res) => {
      const treeData = listToTree(res, {
        key: 'id',
        // parentKey: 'parentId',
        children: 'children',
        label: 'menuName'
      });
      setMenuList(treeData as ApplicationMenu[]);
    });
  }, []);

  useEffect(() => {
    if (!inputValue) {
      setMenuList(menuList);
    } else {
      const result = treeFilter(menuList, inputValue, {
        children: 'children',
        label: 'menuName'
      });
      setMenuList(result as ApplicationMenu[]);
    }
  }, [inputValue]);

  return (
    <Modal title="选择菜单" visible={visible} onCancel={handleCancel} onOk={handleOk}>
      {/* <div>
        <div>选择菜单</div>
        <Select options={menuList.map((menu) => ({ label: menu.menuName, value: menu.id }))}></Select>
      </div> */}

      <div>选择菜单</div>
      <Input.Search placeholder="搜索菜单" onChange={setInputValue} />
      <Tree
        treeData={menuList}
        // showLine
        checkable
        checkedKeys={checkedKeys}
        onCheck={handleCheck}
        icons={{
          switcherIcon: <IconDown />
        }}
      ></Tree>
    </Modal>
  );
};

export default SelectMenuModal;
