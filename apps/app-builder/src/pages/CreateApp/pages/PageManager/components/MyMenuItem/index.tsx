import { menuEditorSignal } from '@/store/singals/menu_editor';
import { Dropdown, Menu, Message, Tooltip, type FormInstance } from '@arco-design/web-react';
import { IconEyeInvisible, IconMoreVertical } from '@arco-design/web-react/icon';
import { getPageSetId, RootParentPage, VisibleType, type GetPageSetIdReq } from '@onebase/app';
import { EDITOR_TYPES } from '@onebase/ui-kit';
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './index.module.less';
import DynamicIcon from '@/components/DynamicIcon';
import { menuIconList } from '@/components/MenuIcon/const';
import { useSignals } from '@preact/signals-react/runtime';

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
  isVisible: number;
  menuCode: string;
  menuName: string;
  label: string;
  menuIcon: string;
  isGroup: boolean;
  onClick: () => void;
  triggerCreate: (formType: string) => void;
  triggerRename: () => void;
  triggerCopy: () => void;
  triggerHide: (menuID: string, isVisible: number) => void;
  triggerDelete: (menuID: string) => void;
  maxWidth: number;
  renameForm: FormInstance;
  copyForm: FormInstance;
  createForm: FormInstance;
}

const MyMenuItem: React.FC<MenuItemProps> = ({
  showOption,
  menuID,
  isVisible,
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
  useSignals()
  const navigate = useNavigate();
  const { curMenuId } = menuEditorSignal;

  const [popupVisible, setPopupVisible] = useState(false);

  const dropList = (
    <Menu style={{ padding: '10px 5px', maxHeight: 'none' }}>
      {!isGroup && (
        <MenuItem
          key="edit"
          onClick={(e) => {
            e.stopPropagation();
            handleEditPageSet();
          }}
        >
          {'编辑'}
        </MenuItem>
      )}
      <MenuItem
        key="rename"
        onClick={(e) => {
          e.stopPropagation();
          triggerRename();

          renameForm.resetFields();
          renameForm.setFieldValue('menuName', menuName);
          renameForm.setFieldValue('menuId', menuID);
          renameForm.setFieldValue('menuIcon', menuIcon);
        }}
      >
        {'重命名'}
      </MenuItem>
      <MenuItem
        key="visible"
        onClick={(e) => {
          e.stopPropagation();
          triggerHide(menuID, isVisible);
        }}
      >
        {isVisible === VisibleType.HIDDEN ? '取消隐藏' : '隐藏'}
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

    sessionStorage.setItem('EDITOR_PAGE_INFO', JSON.stringify({ id: menuID, name: menuName, icon: menuIcon }));
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
      <Tooltip content={menuName} position="top">
        <div
          className={styles.menuName}
          style={{
            maxWidth: maxWidth + 'px'
          }}
        >
          <DynamicIcon
            IconComponent={menuIconList.find(icon => icon.code === menuIcon)?.icon}
            theme="outline"
            size="18"
            fill={curMenuId.value === menuID ? 'rgb(var(--primary-6))' : '#333'}
            style={{ marginRight: 16 }}
          />
          {label}
        </div>
      </Tooltip>
      {isVisible === VisibleType.HIDDEN && (
        <div className={styles.eyeVisible}>
          <IconEyeInvisible />
        </div>
      )}
      {showOption && (
        <div className={styles.dropdownContainer} style={{ marginRight: isGroup ? 22 : 12 }}>
          <Dropdown
            popupVisible={popupVisible}
            onVisibleChange={(visible) => {
              setPopupVisible(visible);
            }}
            droplist={dropList}
            trigger="click"
            position="bl"
          >
            <IconMoreVertical width={16} height={16} onClick={(e) => e.stopPropagation()} />
          </Dropdown>
        </div>
      )}
    </div>
  );
};

export default MyMenuItem;
