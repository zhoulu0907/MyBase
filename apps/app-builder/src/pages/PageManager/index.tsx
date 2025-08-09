import CreateGroupIcon from '@/assets/images/create_group.svg';
import CreatePageIcon from '@/assets/images/create_page.svg';
import { useAppStore, useBasicEditorStore } from '@/store';
import { Button, Dropdown, Form, Input, Layout, Menu, Message, Tree } from '@arco-design/web-react';
import { IconPlus, IconSearch } from '@arco-design/web-react/icon';
import {
  copyApplicationMenu,
  createApplicationMenu,
  deleteApplicationMenu,
  getPageSetCode,
  listApplicationMenu,
  MenuType,
  PageType,
  RootParentPage,
  updateApplicationMenuName,
  type ApplicationMenu,
  type CopyApplicationMenuReq,
  type CreateApplicationMenuReq,
  type DeleteApplicationMenuReq,
  type GetPageSetCodeReq,
  type ListApplicationMenuReq,
  type UpdateApplicationMenuNameReq
} from '@onebase/app';
import { useEffect, useState, type FC } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { EDITOR_TYPES } from '../Editor/components/const';
import CopyModal from './components/Modals/CopyModal';
import CreateModal from './components/Modals/CreateModal';
import RenameModal from './components/Modals/RenameModal';
import MyMenuItem from './components/MyMenuItem';
import styles from './index.module.less';

const TreeNode = Tree.Node;
const MenuItem = Menu.Item;
const Sider = Layout.Sider;
const Content = Layout.Content;

const iconStyle = {
  marginRight: 8,
  transform: 'translateY(5px)'
};

/**
 * 树形数据节点接口
 */
interface TreeNode {
  key: string;
  value: string;
  title: string;
  children?: TreeNode[];
}

const PageManagerPage: FC = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();

  const { curAppId } = useAppStore();

  const [createForm] = Form.useForm();
  const [renameForm] = Form.useForm();
  const [copyForm] = Form.useForm();
  // 创建弹窗
  const [visibleCreateForm, setVisibleCreateForm] = useState('');
  // 重命名弹窗
  const [visibleRenameForm, setVisibleRenameForm] = useState(false);
  // 复制弹窗
  const [visibleCopyForm, setVisibleCopyForm] = useState(false);

  const [title, setTitle] = useState('');
  const pageTypeOptions = [{ label: '普通表单', value: PageType.NORMAL }];

  const [treeData, setTreeData] = useState<TreeNode[]>([]);

  const [curEditMenuID, setCurEditMenuID] = useState<string>();
  const [curEditMenuName, setCurEditMenuName] = useState<string>();
  const [activeMenu, setActiveMenu] = useState<ApplicationMenu>();
  const [parentPageOptions, setParentPageOptions] = useState<ApplicationMenu[]>([RootParentPage]);

  const initTreeItemWidth = 155;
  const cutTreeItemWidth = 25;

  const { clearIsEditMode } = useBasicEditorStore();

  useEffect(() => {
    if (curAppId !== '') {
      getMenuList();
    }
    console.log('clearIsEditMode');
    clearIsEditMode();
  }, [curAppId]);

  /**
   * 递归为菜单项补充parentId字段
   * @param menuItems 菜单项数组
   * @param parentId 父级ID
   * @returns 处理后的菜单项数组
   */
  const addParentIdToChildren = (menuItems: ApplicationMenu[], parentId?: string): ApplicationMenu[] => {
    // 只保留 menuType 为 2（分组）的菜单项用于生成父级页面选择下拉框
    return menuItems
      .filter((menu) => menu.menuType == MenuType.GROUP)
      .map((menu) => ({
        ...menu,
        parentId: parentId,
        children: menu.children ? addParentIdToChildren(menu.children, menu.id) : []
      }));
  };

  // 将接口返回的菜单数据（res）转换为 Tree 组件可用的 treeData 格式
  const convertMenuToTreeData = (menus: ApplicationMenu[], maxWidth: number): any[] => {
    return menus.map((menu) => ({
      key: menu.id,
      title: (
        <MyMenuItem
          isGroup={menu.menuType == MenuType.GROUP}
          maxWidth={maxWidth}
          label={menu.menuName}
          dropList={settingMenuDropList}
          onClick={() => {
            if (menu.menuType == MenuType.PAGE) {
              setActiveMenu(menu);
            }
          }}
          settingOnClick={() => {
            console.log(menu.menuName);
            setCurEditMenuID(menu.id);
            setCurEditMenuName(menu.menuName);
          }}
        />
      ),
      children: menu.children ? convertMenuToTreeData(menu.children, maxWidth - cutTreeItemWidth) : []
    }));
  };

  const getMenuList = async () => {
    const req: ListApplicationMenuReq = {
      applicationId: curAppId
    };
    const res = await listApplicationMenu(req);
    console.log('res: ', res);

    // 为每个children元素补充parentId字段
    const processedRes = addParentIdToChildren(res, RootParentPage.id);
    setParentPageOptions([{ ...RootParentPage, children: processedRes }]);

    const treeData = convertMenuToTreeData(res, initTreeItemWidth);
    setTreeData(treeData);
  };

  const createMenuDropList = (
    <Menu style={{ padding: '10px 5px' }}>
      <MenuItem
        key="page"
        onClick={() => {
          setVisibleCreateForm('page');
          createForm.resetFields();
          setTitle(t('createApp.createPage'));
        }}
      >
        <img src={CreatePageIcon} style={iconStyle} />
        {t('createApp.createPage')}
      </MenuItem>
      <MenuItem
        key="group"
        onClick={() => {
          setVisibleCreateForm('group');
          createForm.resetFields();
          setTitle(t('createApp.createGroup'));
        }}
      >
        <img src={CreateGroupIcon} style={iconStyle} />
        {t('createApp.createGroup')}
      </MenuItem>
    </Menu>
  );

  const settingMenuDropList = (
    <Menu style={{ padding: '10px 5px' }}>
      <MenuItem
        key="rename"
        onClick={(e) => {
          e.stopPropagation();
          setVisibleRenameForm(true);
          renameForm.resetFields();
          renameForm.setFieldValue('menuName', curEditMenuName);
        }}
      >
        {'重命名'}
      </MenuItem>
      <MenuItem
        key="copy"
        onClick={(e) => {
          e.stopPropagation();
          setVisibleCopyForm(true);
          copyForm.resetFields();
          setTitle(t('createApp.copyPage'));
          console.log(activeMenu?.parentId || RootParentPage.id);
        }}
      >
        {'复制'}
      </MenuItem>
      <MenuItem
        key="hide"
        onClick={(e) => {
          e.stopPropagation();
        }}
      >
        {'隐藏'}
      </MenuItem>
      <MenuItem
        key="delete"
        onClick={(e) => {
          e.stopPropagation();
          handleDelete(activeMenu?.id);
        }}
        style={{ color: 'red' }}
      >
        {'删除'}
      </MenuItem>
    </Menu>
  );

  const handleCreate = async () => {
    let req: CreateApplicationMenuReq = {
      applicationId: curAppId,
      parentId: createForm.getFieldValue('parentId'),
      menuName: createForm.getFieldValue('menuName'),
      menuType: MenuType.PAGE,
      menuIcon: 'tmp'
    };

    if (visibleCreateForm === 'page') {
      req.menuType = MenuType.PAGE;
    }
    if (visibleCreateForm === 'group') {
      req.menuType = MenuType.GROUP;
    }

    const res = await createApplicationMenu(req);
    console.log('res: ', res);

    setVisibleCreateForm('');
    getMenuList();
  };

  const handleRename = async () => {
    if (!curEditMenuID) {
      Message.error('请选择要重命名的菜单');
      return;
    }
    console.log('curEditMenuID: ', curEditMenuID);
    const req: UpdateApplicationMenuNameReq = {
      id: curEditMenuID,
      menuName: renameForm.getFieldValue('menuName')
    };
    const res = await updateApplicationMenuName(req);
    console.log('res: ', res);
    setVisibleRenameForm(false);
    getMenuList();
  };

  const handleCopy = async () => {
    if (!activeMenu?.id) {
      Message.error('请选择要复制的菜单');
      return;
    }
    const req: CopyApplicationMenuReq = {
      id: activeMenu?.id,
      menuName: copyForm.getFieldValue('menuName'),
      parentUuid: copyForm.getFieldValue('parentId')
    };
    const res = await copyApplicationMenu(req);
    console.log('res: ', res);
    setVisibleCopyForm(false);
    getMenuList();
  };

  const handleDelete = async (id: string | undefined) => {
    if (!id) {
      Message.error('请选择要删除的菜单');
      return;
    }
    const req: DeleteApplicationMenuReq = {
      id: id
    };
    const res = await deleteApplicationMenu(req);
    console.log('res: ', res);
    getMenuList();
  };

  const handleGetPageSetCode = async () => {
    if (!activeMenu?.id) {
      Message.error('请选择菜单');
      return;
    }

    const req: GetPageSetCodeReq = {
      menuId: activeMenu?.id
    };
    const pageSetCode = await getPageSetCode(req);
    console.log('res: ', pageSetCode);

    if (!pageSetCode) {
      Message.error('请先创建页面集');
      return;
    }

    navigate(`/onebase/editor/${EDITOR_TYPES.FORM_EDITOR}?pageSetCode=${pageSetCode}`);
  };

  return (
    <div className={styles.pageManagerPage}>
      <Layout style={{ height: '100%' }}>
        <Layout>
          <Sider style={{ width: 225 }}>
            <div className={styles.siderHeader}>
              <Input
                style={{
                  width: 140,
                  border: '1px solid #dedede',
                  borderRadius: 3
                }}
                allowClear
                suffix={<IconSearch />}
                placeholder={t('common.search')}
              />
              <Dropdown droplist={createMenuDropList} trigger="click" position="bl">
                <Button type="primary" icon={<IconPlus />} />
              </Dropdown>
            </div>

            <Tree
              blockNode
              draggable
              treeData={treeData}
              className={styles.tree}
              showLine={false}
              icons={{
                switcherIcon: null,
                dragIcon: null
              }}
              actionOnClick={'expand'}
              style={{
                width: '210px',
                overflow: 'hidden',
                boxSizing: 'border-box'
              }}
            />
          </Sider>
          <Content className={styles.content}>
            {activeMenu?.id && (
              <div className={styles.contentHeader}>
                <div className={styles.contentTitle}>{activeMenu?.menuName}</div>
                <Button type="primary" onClick={() => handleGetPageSetCode()}>
                  {t('common.edit')}
                </Button>
              </div>
            )}
            <Content className={styles.content}>content</Content>
          </Content>
        </Layout>
      </Layout>

      {/* 重命名弹窗 */}
      <RenameModal
        title={title}
        visible={visibleRenameForm}
        handleRename={handleRename}
        setVisible={setVisibleRenameForm}
        form={renameForm}
        initValue={curEditMenuName || ''}
      />

      {/* 复制弹窗 */}
      <CopyModal
        title={title}
        visible={visibleCopyForm}
        handleCopy={handleCopy}
        setVisible={setVisibleCopyForm}
        form={copyForm}
        initValue={{ menuName: activeMenu?.menuName + '_副本', parentId: activeMenu?.parentId || RootParentPage.id }}
        treeData={convertMenuToTreeData(parentPageOptions, initTreeItemWidth)}
      />

      {/* 创建弹窗 */}
      <CreateModal
        title={title}
        handleCreate={handleCreate}
        onCancel={() => {
          setVisibleCreateForm('');
        }}
        form={createForm}
        pageTypeOptions={pageTypeOptions}
        visibleCreateForm={visibleCreateForm}
        initValue={{ pageType: PageType.NORMAL, menuName: '', parentId: RootParentPage.id }}
        treeData={convertMenuToTreeData(parentPageOptions, initTreeItemWidth)}
      />
    </div>
  );
};

export default PageManagerPage;
