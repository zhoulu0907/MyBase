import CreateGroupIcon from '@/assets/images/create_group.svg';
import CreatePageIcon from '@/assets/images/create_page.svg';
import { useI18n } from '@/hooks/useI18n';
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
  const { t } = useI18n();
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

  const [curMenu, setCurMenu] = useState<ApplicationMenu>();
  const [activeMenu, setActiveMenu] = useState<ApplicationMenu>();
  const [parentPageOptions, setParentPageOptions] = useState<ApplicationMenu[]>([RootParentPage]);

  const initTreeItemWidth = 155;
  const cutTreeItemWidth = 25;

  const { clearIsEditMode } = useBasicEditorStore();

  useEffect(() => {
    if (curAppId !== '') {
      getMenuList();
    }
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
          menuID={menu.id}
          menuName={menu.menuName}
          isGroup={menu.menuType == MenuType.GROUP}
          maxWidth={maxWidth}
          label={menu.menuName}
          onClick={() => {
            if (menu.menuType == MenuType.PAGE) {
              setCurMenu(menu);
            }
            setActiveMenu(menu);
          }}
          triggerRename={triggerRename}
          triggerCopy={triggerCopy}
          triggerHide={triggerHide}
          triggerDelete={triggerDelete}
          renameForm={renameForm}
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

  const triggerRename = () => {
    setVisibleRenameForm(true);
  };

  const triggerCopy = () => {
    setVisibleCopyForm(true);
    copyForm.resetFields();
    setTitle(t('createApp.copyPage'));
  };

  const triggerHide = () => {};

  const triggerDelete = (menuID: string) => {
    handleDelete(menuID);
  };

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
    if (res) {
      Message.success('创建成功');
    }
    setVisibleCreateForm('');
    getMenuList();
  };

  const handleRename = async () => {
    if (!activeMenu?.id) {
      Message.error('请选择要重命名的菜单');
      return;
    }
    const req: UpdateApplicationMenuNameReq = {
      id: activeMenu?.id,
      menuName: renameForm.getFieldValue('menuName')
    };
    const res = await updateApplicationMenuName(req);
    if (res) {
      Message.success('重命名成功');
    }
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
    if (res) {
      Message.success('复制成功');
    }
    setVisibleCopyForm(false);
    getMenuList();
  };

  const handleDelete = async (id: string) => {
    if (!id) {
      Message.error('请选择要删除的菜单');
      return;
    }
    const req: DeleteApplicationMenuReq = {
      id: id
    };
    const res = await deleteApplicationMenu(req);
    if (res) {
      Message.success('删除成功');
      setActiveMenu(undefined);
    }
    getMenuList();
  };

  const handleEditPageSet = async () => {
    if (!curMenu?.id) {
      Message.error('请选择菜单');
      return;
    }

    const req: GetPageSetCodeReq = {
      menuId: curMenu?.id
    };
    const pageSetCode = await getPageSetCode(req);

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
            {curMenu?.id && (
              <div className={styles.contentHeader}>
                <div className={styles.contentTitle}>{curMenu?.menuName}</div>
                <Button type="primary" onClick={() => handleEditPageSet()}>
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
