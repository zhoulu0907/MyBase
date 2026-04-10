import CreateGroupIcon from '@/assets/images/create_group_icon.svg';
import CreateNormalPageIcon from '@/assets/images/create_normal_page_icon.svg';
import CreateBpmPageIcon from '@/assets/images/create_bpm_page_icon.svg';
import CreateWorkbenchIcon from '@/assets/images/create_workbench_icon.svg';
import CreateScreenIcon from '@/assets/images/create_screen_icon.svg';
import DeleteMenuIcon from '@/assets/images/app_delete.svg';
import CopyIcon from '@/assets/images/copy_comp_icon.svg';
import EditIcon from '@/assets/images/edit_menu_icon.svg';
import RenameIcon from '@/assets/images/edit_page_name_icon.svg';
import HiddenIcon from '@/assets/images/eye_off_icon.svg';
import VisibleIcon from '@/assets/images/eye_on_icon.svg';
import { useAppStore } from '@/store';
import { Dropdown, Menu, Message, Tooltip, Divider, type FormInstance } from '@arco-design/web-react';
import { IconEyeInvisible, IconMoreVertical, IconSettings } from '@arco-design/web-react/icon';
import {
  CREATE_MENU_CATEGORIES,
  CreateMenuCategoryLabelMap,
  getPageSetId,
  menuSignal,
  MenuType,
  PageType,
  RootParentPage,
  VisibleType,
  TASKMENU_TYPE,
  type ApplicationMenu,
  type CreateMenuCategory,
  type GetPageSetIdReq
} from '@onebase/app';
import { EDITOR_TYPES, webMenuIcons } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { ReactSVG } from 'react-svg';
import ineedtodoSvg from '@/assets/images/task_center/willdo.svg';
import ihavedoneSvg from '@/assets/images/task_center/idone.svg';
import icreatedSvg from '@/assets/images/task_center/icreated.svg';
import icopiedSvg from '@/assets/images/task_center/icopied.svg';
import taskproxySvg from '@/assets/images/task_center/taskproxy.svg';
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
  isVisiblePc: number;
  isVisibleMobile: number;
  menuCode: string;
  menuName: string;
  label: string;
  menuIcon: string;
  isGroup: boolean;
  menuType?: number;
  pagesetType: number;
  onClick?: () => void;
  triggerCreate?: (formType: CreateMenuCategory, pageSetType?: PageType) => void;
  triggerRename?: () => void;
  triggerCopy?: () => void;
  triggerHide?: (menuID: string, isVisible: number, platform: 'pc' | 'mobile') => void;
  triggerDelete?: (menuID: string, menuName: string) => void;
  maxWidth: number;
  renameForm: FormInstance;
  copyForm?: FormInstance;
  createForm?: FormInstance;
  style?: React.CSSProperties;
  menuInfo: ApplicationMenu;
}

const MyMenuItem: React.FC<MenuItemProps> = ({
  showOption,
  menuID,
  isVisiblePc,
  isVisibleMobile,
  menuName,
  label,
  menuIcon,
  menuCode,
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
  style,
  menuInfo
}) => {
  const allWebMenuIcons = webMenuIcons.map((ele) => ele.children).reduce((acc, current) => acc.concat(current), []);
  useSignals();
  const navigate = useNavigate();

  const { curAppId } = useAppStore();

  const { curMenu, setCurMenu } = menuSignal;
  const { tenantId } = useParams();

  const [popupVisible, setPopupVisible] = useState(false);

  const taskIconList = [
    { key: TASKMENU_TYPE.TASKINEEDTODO, value: ineedtodoSvg },
    { key: TASKMENU_TYPE.TASKIHAVEDONE, value: ihavedoneSvg },
    { key: TASKMENU_TYPE.TASKICREATED, value: icreatedSvg },
    { key: TASKMENU_TYPE.TASKICOPIED, value: icopiedSvg },
    { key: TASKMENU_TYPE.TASKTASKPROXY, value: taskproxySvg }
  ];

  const groupCreateMenus = [
    {
      key: 'createNormalPage',
      formType: CREATE_MENU_CATEGORIES.NORMAL_FORM,
      label: CreateMenuCategoryLabelMap[CREATE_MENU_CATEGORIES.NORMAL_FORM],
      icon: CreateNormalPageIcon
    },
    {
      key: 'createBpmPage',
      formType: CREATE_MENU_CATEGORIES.BPM_FORM,
      label: CreateMenuCategoryLabelMap[CREATE_MENU_CATEGORIES.BPM_FORM],
      icon: CreateBpmPageIcon
    },
    {
      key: 'createWorkbench',
      formType: CREATE_MENU_CATEGORIES.WORKBENCH,
      label: CreateMenuCategoryLabelMap[CREATE_MENU_CATEGORIES.WORKBENCH],
      icon: CreateWorkbenchIcon
    },
    {
      key: 'createScreen',
      formType: CREATE_MENU_CATEGORIES.SCREEN,
      label: CreateMenuCategoryLabelMap[CREATE_MENU_CATEGORIES.SCREEN],
      icon: CreateScreenIcon
    },
    {
      key: 'createGroup',
      formType: CREATE_MENU_CATEGORIES.GROUP,
      label: CreateMenuCategoryLabelMap[CREATE_MENU_CATEGORIES.GROUP],
      icon: CreateGroupIcon
    }
  ] as const;

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

      <Divider className={styles.divider} />

      <MenuItem
        className={styles.menuContent}
        key="visible-web"
        onClick={(e) => {
          e.stopPropagation();
          triggerHide && triggerHide(menuID, isVisiblePc, 'pc');
        }}
      >
        {isVisiblePc === VisibleType.HIDDEN ? (
          <img src={VisibleIcon} alt="隐藏web端" />
        ) : (
          <img src={HiddenIcon} alt="取消隐藏web端" />
        )}
        {isVisiblePc === VisibleType.HIDDEN ? '取消隐藏web端' : '隐藏web端'}
      </MenuItem>
      <MenuItem
        className={styles.menuContent}
        key="visible-mobile"
        onClick={(e) => {
          e.stopPropagation();
          triggerHide && triggerHide(menuID, isVisibleMobile, 'mobile');
        }}
      >
        {isVisibleMobile === VisibleType.HIDDEN ? (
          <img src={VisibleIcon} alt="隐藏移动端" />
        ) : (
          <img src={HiddenIcon} alt="取消隐藏移动端" />
        )}
        {isVisibleMobile === VisibleType.HIDDEN ? '取消隐藏移动端' : '隐藏移动端'}
      </MenuItem>

      {isGroup && menuType !== MenuType.BPM && <Divider className={styles.divider} />}

      {isGroup &&
        createForm &&
        triggerCreate &&
        groupCreateMenus.map((menu) => (
          <MenuItem
            className={styles.menuContent}
            key={menu.key}
            onClick={(e) => {
              e.stopPropagation();
              triggerCreate(menu.formType);
              createForm.setFieldValue('parentId', menuID);
            }}
          >
            <img src={menu.icon} alt={menu.label} />
            {menu.label}
          </MenuItem>
        ))}

      {triggerDelete && <Divider className={styles.divider} />}

      {triggerDelete && (
        <MenuItem
          className={styles.menuContent}
          key="delete"
          onClick={(e) => {
            e.stopPropagation();
            triggerDelete(menuID, menuName);
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

    setCurMenu(menuInfo);

    const editorType = pagesetType == PageType.WORKBENCH ? EDITOR_TYPES.WORKBENCH_EDITOR : EDITOR_TYPES.FORM_EDITOR;

    sessionStorage.setItem('EDITOR_PAGE_INFO', JSON.stringify({ id: menuID, name: menuName, icon: menuIcon }));
    navigate(`/onebase/${tenantId}/editor/${editorType}?pageSetId=${pageSetId}&appId=${curAppId}`);
  };

  return (
    <div
      className={`${styles.myMenuItem} ${isVisiblePc === VisibleType.HIDDEN || isVisibleMobile === VisibleType.HIDDEN ? 'menu-hidden' : ''}`}
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
            <ReactSVG
              className={styles.menuIcon}
              src={taskIconList.find((ele) => ele.key === menuIcon)?.value || ''}
              beforeInjection={(svg) => {
                const fillColor = curMenu.value?.id === menuID ? 'rgb(var(--primary-6))' : '#333';
                svg.querySelectorAll('*').forEach((el) => {
                  if (el.getAttribute('fill') === 'black' || el.getAttribute('fill') === '#4E5969') {
                    el.setAttribute('fill', fillColor);
                  }
                  if (el.getAttribute('stroke') === 'black' || el.getAttribute('stroke') === '#4E5969') {
                    el.setAttribute('stroke', fillColor);
                  }
                });
                svg.setAttribute('width', '18px');
                svg.setAttribute('height', '18px');
              }}
            />
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
      {(isVisiblePc === VisibleType.HIDDEN || isVisibleMobile === VisibleType.HIDDEN) && (
        <div className={styles.eyeVisible}>
          <IconEyeInvisible />
        </div>
      )}
      {showOption && (
        <div className={styles.dropdownContainer} style={{ marginRight: isGroup ? 24 : 8 }}>
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
              <IconSettings color="rgb(var(--primary-6))" style={{ stroke: 'rgb(var(--primary-6))' }} />
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
