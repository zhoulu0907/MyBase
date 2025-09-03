import { Dropdown, Menu, Message, type FormInstance } from '@arco-design/web-react';
import { IconSettings } from '@arco-design/web-react/icon';
import { getPageSetId, RootParentPage, type GetPageSetIdReq } from '@onebase/app';
import { EDITOR_TYPES } from '@onebase/ui-kit';
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
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
  visible: boolean,
  menuCode: string;
  menuName: string;
  label: string;
  menuIcon: string;
  isGroup: boolean;
  onClick: () => void;
  triggerCreate: (formType: string) => void;
  triggerRename: () => void;
  triggerCopy: () => void;
  triggerHide: (menuID: string,visible: boolean) => void;
  triggerDelete: (menuID: string) => void;
  maxWidth: number;
  renameForm: FormInstance;
  copyForm: FormInstance;
  createForm: FormInstance;
}

const MyMenuItem: React.FC<MenuItemProps> = ({
  showOption,
  menuID,
  menuCode,
  visible,
  menuName,
  label,
  menuIcon,
  isGroup,
  onClick,
  triggerCreate,
  triggerRename,
  triggerCopy,
  triggerHide,
  triggerDelete,
  maxWidth,
  renameForm,
  copyForm,
  createForm
}) => {
  const navigate = useNavigate();

  const [popupVisible, setPopupVisible] = useState(false);

  const dropList = (
    <Menu style={{ padding: '10px 5px',maxHeight: 'none' }}>
      <MenuItem
        key="edit"
        onClick={(e) => {
          e.stopPropagation();
          handleEditPageSet();
        }}
      >
        {'编辑'}
      </MenuItem>
      <MenuItem
        key="rename"
        onClick={(e) => {
          e.stopPropagation();
          triggerRename();

          renameForm.resetFields();
          renameForm.setFieldValue('menuName', menuName);
          renameForm.setFieldValue('menuId', menuID);
        }}
      >
        {'重命名'}
      </MenuItem>
      <MenuItem
        key="visible"
        onClick={(e) => {
          e.stopPropagation();
          triggerHide(menuID,visible);
        }}
      >
        { !!visible ? '显示':'隐藏'}
      </MenuItem>
      {!isGroup && (
        <MenuItem
          key="copy"
          onClick={(e) => {
            e.stopPropagation();
            triggerCopy();
            copyForm.setFieldValue('menuName', menuName + '_副本');
            copyForm.setFieldValue('parentId', RootParentPage.id);
            copyForm.setFieldValue('menuId', menuID);
          }}
        >
          {'复制'}
        </MenuItem>
      )}
      {isGroup && (
        <MenuItem
          key="createPage"
          onClick={(e) => {
            e.stopPropagation();
            triggerCreate('page');

            createForm.setFieldValue('parentId', menuID);
          }}
        >
          {'新建页面'}
        </MenuItem>
      )}

      {isGroup && (
        <MenuItem
          key="createGroup"
          onClick={(e) => {
            e.stopPropagation();
            triggerCreate('group');

            createForm.setFieldValue('parentId', menuID);
          }}
        >
          {'新建分组'}
        </MenuItem>
      )}

      {/* <MenuItem
        key="hide"
        onClick={(e) => {
          e.stopPropagation();
          triggerHide();
        }}
      >
        {'隐藏'}
      </MenuItem> */}
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

  const handleEditPageSet = async () => {
    const req: GetPageSetIdReq = {
      menuId: menuID
    };
    const pageSetId = await getPageSetId(req);

    if (!pageSetId) {
      Message.error('请先创建页面集');
      return;
    }

    navigate(`/onebase/editor/${EDITOR_TYPES.FORM_EDITOR}?pageSetId=${pageSetId}`);
  };

  return (
    <div
      className={styles.myMenuItem}
      onContextMenu={(e) => {
        // 支持右键弹出菜单
        setPopupVisible(true);
        e.preventDefault();
      }}
      onClick={onClick}
      role="menuitem"
      tabIndex={0}
    >
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
          <Dropdown
            popupVisible={popupVisible}
            onVisibleChange={(visible) => {
              setPopupVisible(visible);
            }}
            droplist={dropList}
            trigger="click"
            position="bl"
          >
            <IconSettings onClick={(e) => e.stopPropagation()} />
          </Dropdown>
        </div>
      )}
    </div>
  );
};

export default MyMenuItem;
