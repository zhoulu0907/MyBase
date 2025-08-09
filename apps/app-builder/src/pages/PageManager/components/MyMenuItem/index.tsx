import { Dropdown, Menu, type FormInstance } from '@arco-design/web-react';
import { IconFolder, IconSettings } from '@arco-design/web-react/icon';
import React from 'react';
import styles from './index.module.less';

const MenuItem = Menu.Item;

/**
 * MenuItem 组件
 * 用于在页面管理器中渲染单个菜单项
 * @param props.label 菜单项显示文本
 * @param props.icon 可选，菜单项图标
 * @param props.onClick 点击事件处理函数
 */
interface MenuItemProps {
  menuID: string;
  menuName: string;
  label: string;
  icon?: React.ReactNode;
  isGroup: boolean;
  onClick: () => void;
  triggerRename: () => void;
  triggerCopy: () => void;
  triggerHide: () => void;
  triggerDelete: (menuID: string) => void;
  maxWidth: number;
  renameForm: FormInstance;
}

const MyMenuItem: React.FC<MenuItemProps> = ({
  menuID,
  menuName,
  label,
  icon,
  isGroup,
  onClick,
  triggerRename,
  triggerCopy,
  triggerHide,
  triggerDelete,
  maxWidth,
  renameForm
}) => {
  const dropList = (
    <Menu style={{ padding: '10px 5px' }}>
      <MenuItem
        key="rename"
        onClick={(e) => {
          e.stopPropagation();
          triggerRename();

          renameForm.resetFields();
          renameForm.setFieldValue('menuName', menuName);
        }}
      >
        {'重命名'}
      </MenuItem>
      <MenuItem
        key="copy"
        onClick={(e) => {
          e.stopPropagation();
          triggerCopy();
          //   setVisibleCopyForm(true);
          //   copyForm.resetFields();
          //   setTitle(t('createApp.copyPage'));
          //   console.log(activeMenu?.parentId || RootParentPage.id);
        }}
      >
        {'复制'}
      </MenuItem>
      <MenuItem
        key="hide"
        onClick={(e) => {
          e.stopPropagation();
          triggerHide();
        }}
      >
        {'隐藏'}
      </MenuItem>
      <MenuItem
        key="delete"
        onClick={(e) => {
          e.stopPropagation();
          triggerDelete(menuID);
        }}
        style={{ color: 'red' }}
      >
        {'删除'}
      </MenuItem>
    </Menu>
  );

  return (
    <div className={styles.myMenuItem} onClick={onClick} role="menuitem" tabIndex={0}>
      {/* {icon && <span className="mr-2">{icon}</span>} */}
      <div
        className={styles.menuName}
        style={{
          maxWidth: maxWidth + 'px'
        }}
      >
        {isGroup ? <IconFolder style={{ marginRight: '10px' }} /> : null}

        {label}
      </div>
      <div className={styles.dropdownContainer}>
        <Dropdown droplist={dropList} trigger="click" position="bl">
          <IconSettings onClick={(e) => e.stopPropagation()} />
        </Dropdown>
      </div>
    </div>
  );
};

export default MyMenuItem;
