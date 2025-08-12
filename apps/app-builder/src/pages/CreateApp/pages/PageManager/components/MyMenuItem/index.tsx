import { Dropdown, Menu, type FormInstance } from '@arco-design/web-react';
import { IconSettings } from '@arco-design/web-react/icon';
import { RootParentPage } from '@onebase/app';
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
  showOption: boolean;
  menuID: string;
  menuCode: string;
  menuName: string;
  label: string;
  menuIcon: string;
  isGroup: boolean;
  onClick: () => void;
  triggerRename: () => void;
  triggerCopy: () => void;
  triggerHide: () => void;
  triggerDelete: (menuID: string) => void;
  maxWidth: number;
  renameForm: FormInstance;
  copyForm: FormInstance;
}

const MyMenuItem: React.FC<MenuItemProps> = ({
  showOption,
  menuID,
  menuCode,
  menuName,
  label,
  menuIcon,
  isGroup,
  onClick,
  triggerRename,
  triggerCopy,
  triggerHide,
  triggerDelete,
  maxWidth,
  renameForm,
  copyForm
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
          renameForm.setFieldValue('menuID', menuID);
        }}
      >
        {'重命名'}
      </MenuItem>
      {!isGroup && (
        <MenuItem
          key="copy"
          onClick={(e) => {
            e.stopPropagation();
            triggerCopy();
            copyForm.setFieldValue('menuName', menuName + '_副本');
            copyForm.setFieldValue('parentCode', RootParentPage.menuCode);
            copyForm.setFieldValue('menuID', menuID);
          }}
        >
          {'复制'}
        </MenuItem>
      )}
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
      <div
        className={styles.menuName}
        style={{
          maxWidth: maxWidth + 'px'
        }}
      >
        <i className={`iconfont ${menuIcon}`} style={{ marginRight: '10px' }} />
        {label}
      </div>
      {showOption && (
        <div className={styles.dropdownContainer}>
          <Dropdown droplist={dropList} trigger="click" position="bl">
            <IconSettings onClick={(e) => e.stopPropagation()} />
          </Dropdown>
        </div>
      )}
    </div>
  );
};

export default MyMenuItem;
