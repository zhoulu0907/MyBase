import CreateGroupIcon from '@/assets/images/addfolder.svg';
import CreatePageIcon from '@/assets/images/addpage.svg';
import PageManagerGuide from '@/assets/images/page_manaager_guide.svg';
import { useI18n } from '@/hooks/useI18n';
import PreviewContainer from '@/pages/Runtime/components/preview';
import { useAppStore } from '@/store/store_app';
import { useBasicEditorStore } from '@/store/store_editor';
import { addParentIdToChildren } from '@/utils/menu';
import { Button, Dropdown, Form, Input, Layout, Menu, Message, Tree } from '@arco-design/web-react';
import { IconEmpty, IconPlus, IconSearch } from '@arco-design/web-react/icon';
import {
  copyApplicationMenu,
  createApplicationMenu,
  deleteApplicationMenu,
  getEntityListByApp,
  getPageSetId,
  listApplicationMenu,
  MenuType,
  PageType,
  RootParentPage,
  updateApplicationMenuName,
  type ApplicationMenu,
  type CopyApplicationMenuReq,
  type CreateApplicationMenuReq,
  type DeleteApplicationMenuReq,
  type GetPageSetIdReq,
  type ListApplicationMenuReq,
  type MetadataEntityPair,
  type UpdateApplicationMenuNameReq
} from '@onebase/app';
import { EDITOR_TYPES } from '@onebase/ui-kit';
import { debounce } from 'lodash-es';
import { useCallback, useEffect, useState, type FC } from 'react';
import { useNavigate } from 'react-router-dom';
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

interface Options {
  label: string;
  value: string;
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
  const [showGuide, setShowGuide] = useState<boolean>(false);
  const pageTypeOptions = [{ label: '普通表单', value: PageType.NORMAL }];

  const [treeData, setTreeData] = useState<TreeNode[]>();
  const [entityListOptions, setEntityListOptions] = useState<Options[]>([]);

  const [curMenu, setCurMenu] = useState<ApplicationMenu>();
  const [_activeMenu, setActiveMenu] = useState<ApplicationMenu>();
  const [parentPageOptions, setParentPageOptions] = useState<ApplicationMenu[]>([RootParentPage]);

  const [expandedKeys, setExpandedKeys] = useState<string[]>([]);
  const [searchResult, setSearchResult] = useState<boolean>(false); // 菜单搜索结果

  const initTreeItemWidth = 155;
  const cutTreeItemWidth = 25;

  const { clearIsEditMode } = useBasicEditorStore();

  const findFirstPage: any = (nodes: ApplicationMenu[]) =>
    nodes.reduce((found, node) => {
      if (found) return found;
      if (Number(node.menuType) === MenuType.PAGE) return node;
      setExpandedKeys((prev) => [...prev, node.menuCode]);
      return node.children ? findFirstPage(node.children) : undefined;
    }, undefined);

  useEffect(() => {
    if (curAppId !== '') {
      getMenuList();
      getEntityList();
    }
    clearIsEditMode();
  }, [curAppId]);

  useEffect(() => {
    if (searchResult) return;
    setShowGuide(treeData?.length === 0);
  }, [treeData, searchResult]);

  // 将接口返回的菜单数据（res）转换为 Tree 组件可用的 treeData 格式
  // TODO(mickey): showOption重构
  const convertMenuToTreeData = (menus: ApplicationMenu[], maxWidth: number, showOption: boolean = false): any[] => {
    return menus.map((menu) => ({
      key: menu.id,
      title: (
        <MyMenuItem
          showOption={showOption}
          menuID={menu.id}
          menuCode={menu.menuCode}
          menuName={menu.menuName}
          menuIcon={menu.menuIcon}
          isGroup={menu.menuType == MenuType.GROUP}
          maxWidth={maxWidth}
          label={menu.menuName}
          onClick={() => {
            if (menu.menuType == MenuType.PAGE) {
              setCurMenu(menu);
            }
            setActiveMenu(menu);
          }}
          triggerCreate={triggerCreate}
          triggerRename={triggerRename}
          triggerCopy={triggerCopy}
          triggerHide={triggerHide}
          triggerDelete={triggerDelete}
          renameForm={renameForm}
          copyForm={copyForm}
          createForm={createForm}
        />
      ),
      children: menu.children ? convertMenuToTreeData(menu.children, maxWidth - cutTreeItemWidth, showOption) : []
    }));
  };

  const getMenuList = async (keywords?: string) => {
    const req: ListApplicationMenuReq = {
      applicationId: curAppId,
      name: keywords
    };
    const res = await listApplicationMenu(req);

    // 为每个children元素补充parentId字段
    const processedRes = addParentIdToChildren(res, RootParentPage.id);
    setParentPageOptions([{ ...RootParentPage, children: processedRes }]);

    const treeData = convertMenuToTreeData(res, initTreeItemWidth, true);
    setTreeData(treeData);

    if (res && res.length > 0) {
      setCurMenu(findFirstPage(res));
      setSearchResult(false);
    }

    if (keywords) {
      setSearchResult(res.length === 0);
    } else {
      setShowGuide(res.length === 0);
    }
  };

  const getEntityList = async () => {
    // TODO(mickey): 等xiaoyi完成后 写活
    // const appId: string = '1';
    const appId: string = curAppId;
    const res: MetadataEntityPair[] = await getEntityListByApp(appId);
    console.log(res);
    const entityOptions = res.map((entity) => ({
      label: entity.entityName,
      value: entity.entityId
    }));
    setEntityListOptions(entityOptions);
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

  const triggerCreate = (formType: string) => {
    setVisibleCreateForm(formType);
    createForm.resetFields();
    if (formType == 'page') {
      setTitle(t('createApp.createPage'));
    }

    if (formType == 'group') {
      setTitle(t('createApp.createGroup'));
    }
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
    createForm.validate(async (error) => {
      if (error !== null) return;
      let req: CreateApplicationMenuReq = {
        applicationId: curAppId,
        parentId:
          createForm.getFieldValue('parentId') === RootParentPage.id ? '' : createForm.getFieldValue('parentId'),
        menuName: createForm.getFieldValue('menuName'),
        menuType: MenuType.PAGE,
        menuIcon: createForm.getFieldValue('menuIcon'),
        entityId: visibleCreateForm === 'page' ? createForm.getFieldValue('entityId') : ''
      };

      if (visibleCreateForm === 'page') {
        req.menuType = MenuType.PAGE;
      }
      if (visibleCreateForm === 'group') {
        req.menuType = MenuType.GROUP;
      }

      const menuResp = await createApplicationMenu(req);

      if (menuResp) {
        Message.success('创建成功');
      }
      setVisibleCreateForm('');
      getMenuList();

      const pageSetId = await getPageSetId({
        menuId: menuResp.id
      });

      if (pageSetId && menuResp.menuType === MenuType.PAGE) {
        navigate(`/onebase/editor/${EDITOR_TYPES.FORM_EDITOR}?pageSetId=${pageSetId}`);
      }
    });
  };

  const handleRename = async () => {
    if (!renameForm.getFieldValue('menuId')) {
      Message.error('请选择要重命名的菜单');
      return;
    }
    const req: UpdateApplicationMenuNameReq = {
      id: renameForm.getFieldValue('menuId'),
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
    if (!copyForm.getFieldValue('menuId')) {
      Message.error('请选择要复制的菜单');
      return;
    }

    const req: CopyApplicationMenuReq = {
      id: copyForm.getFieldValue('menuId'),
      menuName: copyForm.getFieldValue('menuName'),
      parentId: copyForm.getFieldValue('parentId') === RootParentPage.id ? '' : copyForm.getFieldValue('parentId')
    };

    // console.log('req: ', req);

    const res = await copyApplicationMenu(req);
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
      setCurMenu(undefined);
    }

    getMenuList();
  };

  const handleEditPageSet = async (name: string) => {
    if (!curMenu?.id) {
      Message.error('请选择菜单');
      return;
    }

    const req: GetPageSetIdReq = {
      menuId: curMenu?.id
    };
    const pageSetId = await getPageSetId(req);

    if (!pageSetId) {
      Message.error('请先创建页面集');
      return;
    }

    // 把编辑页菜单数据保存起来；
    sessionStorage.setItem('EDITOR_PAGE_INFO', JSON.stringify({ id: curMenu?.id, name }));
    navigate(`/onebase/editor/${EDITOR_TYPES.FORM_EDITOR}?pageSetId=${pageSetId}`);
  };

  // 菜单搜索
  const debouncedSearch = useCallback(
    debounce((value) => {
      getMenuList(value);
    }, 500),
    [curAppId]
  );

  useEffect(() => {
    return () => debouncedSearch.cancel();
  }, [debouncedSearch]);

  return (
    <div className={styles.pageManagerPage}>
      <Layout style={{ height: '100%' }}>
        <Layout>
          <Sider className={styles.sider}>
            <div className={styles.siderHeader}>
              <Input
                style={{
                  width: 120,
                  border: '1px solid #dedede',
                  borderRadius: 3
                }}
                allowClear
                suffix={<IconSearch />}
                placeholder={t('common.search')}
                onChange={debouncedSearch}
              />
              <Dropdown droplist={createMenuDropList} trigger="click" position="bl">
                <Button type="primary" icon={<IconPlus />} />
              </Dropdown>
            </div>

            <Tree
              blockNode
              draggable
              selectedKeys={[curMenu?.menuCode!]}
              treeData={treeData}
              className={styles.tree}
              showLine={false}
              icons={{
                switcherIcon: null,
                dragIcon: null
              }}
              expandedKeys={expandedKeys}
              onExpand={setExpandedKeys}
              actionOnClick={'expand'}
              style={{
                width: '200px',
                overflow: 'hidden',
                boxSizing: 'border-box',
                paddingRight: 12
              }}
            />
          </Sider>
          <Content className={styles.content}>
            {showGuide ? (
              <div className={styles.guide}>
                <div
                  className={styles.guideImg}
                  style={{ background: `url(${PageManagerGuide})no-repeat center / cover` }}
                >
                  <div
                    className={styles.guideButton}
                    onClick={() => {
                      setTitle(t('createApp.createPage'));
                      setVisibleCreateForm('page');
                    }}
                  />
                </div>
              </div>
            ) : (
              <>
                {searchResult ? (
                  <div className={styles.contentEmpty}>
                    <IconEmpty fontSize={56} />
                    暂无数据
                  </div>
                ) : (
                  <>
                    {curMenu?.id && (
                      <>
                        <div className={styles.contentHeader}>
                          <div className={styles.contentTitle}>{curMenu?.menuName}</div>
                          <Button type="primary" onClick={() => handleEditPageSet(curMenu?.menuName)}>
                            {t('common.edit')}
                          </Button>
                        </div>
                        <div className={styles.contentBody}>
                          <PreviewContainer menuId={curMenu?.id} runtime={false} />
                        </div>
                      </>
                    )}
                  </>
                )}
              </>
            )}
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
        entityListOptions={entityListOptions}
        pageTypeOptions={pageTypeOptions}
        visibleCreateForm={visibleCreateForm}
        initValue={{ pageType: PageType.NORMAL, menuName: '', parentId: RootParentPage.id }}
        treeData={convertMenuToTreeData(parentPageOptions, initTreeItemWidth)}
      />
    </div>
  );
};

export default PageManagerPage;
