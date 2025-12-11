import CreateGroupIcon from '@/assets/images/addfolder.svg';
import CreatePageIcon from '@/assets/images/addpage.svg';
import DeleteMenuIcon from '@/assets/images/app_delete.svg';
import CopyIcon from '@/assets/images/copy_comp_icon.svg';
import EditIcon from '@/assets/images/edit_menu_icon.svg';
import RenameIcon from '@/assets/images/edit_page_name_icon.svg';
import HiddenIcon from '@/assets/images/eye_off_icon.svg';
import VisibleIcon from '@/assets/images/eye_on_icon.svg';
import SettingIcon from '@/assets/images/task_center/setting-on.svg';
import { useAppStore } from '@/store';
import { Dropdown, Menu, Message, Tooltip, type FormInstance } from '@arco-design/web-react';
import { IconEyeInvisible, IconMoreVertical } from '@arco-design/web-react/icon';
import {
  getPageSetId,
  menuSignal,
  MenuType,
  PageType,
  RootParentPage,
  VisibleType,
  type GetPageSetIdReq
} from '@onebase/app';
import { pagesRuntimeSignal } from '@onebase/common';
import { EDITOR_TYPES, webMenuIcons } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { ReactSVG } from 'react-svg';
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
  isVisible: number;
  menuCode: string;
  menuName: string;
  label: string;
  menuIcon: string;
  isGroup: boolean;
  menuType?: number;
  pagesetType: number;
  onClick?: () => void;
  triggerCreate?: (formType: string) => void;
  triggerRename?: () => void;
  triggerCopy?: () => void;
  triggerHide?: (menuID: string, isVisible: number) => void;
  triggerDelete?: (menuID: string) => void;
  maxWidth: number;
  renameForm: FormInstance;
  copyForm?: FormInstance;
  createForm?: FormInstance;
  style?: React.CSSProperties;
}

const MyMenuItem: React.FC<MenuItemProps> = ({
  showOption,
  menuID,
  isVisible,
  menuName,
  label,
  menuIcon,
  isGroup,
  menuType,
  pagesetType,
  onClick,
  triggerCreate,
  triggerRename,
  triggerCopy,
  triggerHide,
  triggerDelete,
  maxWidth,
  renameForm,
  copyForm,
  createForm,
  style
}) => {
  const allWebMenuIcons = webMenuIcons.map((ele) => ele.children).reduce((acc, current) => acc.concat(current), []);
  useSignals();
  const navigate = useNavigate();

  const { curAppId } = useAppStore();

  const { curMenu } = menuSignal;
  const { curPage } = pagesRuntimeSignal;
  const { tenantId } = useParams();

  const [popupVisible, setPopupVisible] = useState(false);

  const dropList = (
    <Menu style={{ padding: '10px 5px', maxHeight: 'none' }}>
      {!isGroup && (
        <MenuItem
          className={styles.menuContent}
          key="edit"
          onClick={(e) => {
            e.stopPropagation();
            handleEditPageSet();
          }}
        >
          <img src={EditIcon} alt="编辑" />
          编辑
        </MenuItem>
      )}
      {renameForm && triggerRename && (
        <MenuItem
          className={styles.menuContent}
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
          <img src={RenameIcon} alt="重命名" />
          重命名
        </MenuItem>
      )}
      <MenuItem
        className={styles.menuContent}
        key="visible"
        onClick={(e) => {
          e.stopPropagation();
          triggerHide && triggerHide(menuID, isVisible);
        }}
      >
        {isVisible === VisibleType.HIDDEN ? (
          <img src={VisibleIcon} alt="隐藏" />
        ) : (
          <img src={HiddenIcon} alt="取消隐藏" />
        )}
        {isVisible === VisibleType.HIDDEN ? '取消隐藏' : '隐藏'}
      </MenuItem>
      {!isGroup && copyForm && triggerCopy && (
        <MenuItem
          className={styles.menuContent}
          key="copy"
          onClick={(e) => {
            e.stopPropagation();
            triggerCopy();
            copyForm.setFieldValue('menuName', menuName + '_副本');
            copyForm.setFieldValue('parentId', RootParentPage.id);
            copyForm.setFieldValue('menuId', menuID);
          }}
        >
          <img src={CopyIcon} alt="复制" />
          复制
        </MenuItem>
      )}
      {isGroup && createForm && triggerCreate && (
        <MenuItem
          className={styles.menuContent}
          key="createPage"
          onClick={(e) => {
            e.stopPropagation();
            triggerCreate('page');

            createForm.setFieldValue('parentId', menuID);
          }}
        >
          <img src={CreatePageIcon} alt="新建页面" />
          新建页面
        </MenuItem>
      )}

      {isGroup && createForm && triggerCreate && (
        <MenuItem
          className={styles.menuContent}
          key="createGroup"
          onClick={(e) => {
            e.stopPropagation();
            triggerCreate('group');

            createForm.setFieldValue('parentId', menuID);
          }}
        >
          <img src={CreateGroupIcon} alt="新建分组" />
          新建分组
        </MenuItem>
      )}
      {triggerDelete && (
        <MenuItem
          className={styles.menuContent}
          key="delete"
          onClick={(e) => {
            e.stopPropagation();
            triggerDelete(menuID);
          }}
          style={{ color: 'red' }}
        >
          <img src={DeleteMenuIcon} alt="删除" />
          删除
        </MenuItem>
      )}
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

    const editorType = pagesetType == PageType.WORKBENCH ? EDITOR_TYPES.WORKBENCH_EDITOR : EDITOR_TYPES.FORM_EDITOR;

    sessionStorage.setItem('EDITOR_PAGE_INFO', JSON.stringify({ id: menuID, name: menuName, icon: menuIcon }));
    navigate(`/onebase/${tenantId}/editor/${editorType}?pageSetId=${pageSetId}&appId=${curAppId}`);
  };

  return (
    <div
      className={`${styles.myMenuItem} ${isVisible === VisibleType.HIDDEN ? 'menu-hidden' : ''}`}
      onContextMenu={(e) => {
        // 支持右键弹出菜单
        setPopupVisible(true);
        e.preventDefault();
      }}
      onClick={onClick}
      role="menuitem"
      tabIndex={0}
      style={style}
    >
      <Tooltip content={menuName} position="top">
        <div className={styles.menuName}>
          {menuIcon.includes('TASK-') ? (
            // xxx-taskicon 是工作流程任务中心菜单的icon
            <i className={`iconfont ${menuIcon}`} style={{ marginRight: '16px' }} />
          ) : (
            // 正常菜单 icon
            <ReactSVG
              className={styles.menuIcon}
              src={
                allWebMenuIcons.find((ele) => ele.code === menuIcon)?.icon ||
                allWebMenuIcons.find((ele) => ele.code === 'FormPage')?.icon ||
                ''
              }
              beforeInjection={(svg) => {
                const fillColor = curMenu.value?.id === menuID ? 'rgb(var(--primary-6))' : '#333';
                svg.querySelectorAll('*').forEach((el) => el.removeAttribute('fill'));
                svg.setAttribute('fill', fillColor);
                svg.setAttribute('width', '18px');
                svg.setAttribute('height', '18px');
              }}
            />
          )}
          <span
            className={styles.name}
            style={{
              maxWidth: maxWidth + 'px',
              color: curMenu.value?.id === menuID ? 'rgb(var(--primary-6))' : '#333'
            }}
          >
            {label}
          </span>
        </div>
      </Tooltip>
      {isVisible === VisibleType.HIDDEN && (
        <div className={styles.eyeVisible}>
          <IconEyeInvisible />
        </div>
      )}
      {showOption && (
        <div className={styles.dropdownContainer} style={{ marginRight: isGroup ? 26 : 0 }}>
          <Dropdown
            popupVisible={popupVisible}
            onVisibleChange={(visible) => {
              setPopupVisible(visible);
            }}
            droplist={dropList}
            trigger="click"
            position="bl"
          >
            {menuType === MenuType.BPM ? (
              <img src={SettingIcon} alt="" />
            ) : (
              <IconMoreVertical className={styles.moreIcon} onClick={(e) => e.stopPropagation()} />
            )}
          </Dropdown>
        </div>
      )}
    </div>
  );
};

export default MyMenuItem;
