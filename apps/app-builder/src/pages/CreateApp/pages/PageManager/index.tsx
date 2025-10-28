import CreateGroupIcon from '@/assets/images/addfolder.svg';
import CreatePageIcon from '@/assets/images/addpage.svg';
import EditIcon from '@/assets/images/edit_menu_icon.svg';
import PageManagerGuide from '@/assets/images/page_manaager_guide.svg';
import { useI18n } from '@/hooks/useI18n';
import PreviewContainer from '@/pages/Runtime/components/preview';
import { useAppStore } from '@/store/store_app';
import { useBasicEditorStore } from '@/store/store_editor';
import { addParentIdToChildren } from '@/utils/menu';
import { Button, Dropdown, Form, Input, Layout, Menu, Message, Tree } from '@arco-design/web-react';
import { IconDown, IconEmpty, IconPlus, IconSearch } from '@arco-design/web-react/icon';
import {
  copyApplicationMenu,
  createApplicationMenu,
  deleteApplicationMenu,
  getEntityListByApp,
  getPageSetId,
  listApplicationMenu,
  menuSignal,
  MenuType,
  PageType,
  RootParentPage,
  updateApplicationMenu,
  updateApplicationMenuOrder,
  updateApplicationMenuVisible,
  VisibleType,
  type ApplicationMenu,
  type CopyApplicationMenuReq,
  type CreateApplicationMenuReq,
  type DeleteApplicationMenuReq,
  type GetPageSetIdReq,
  type ListApplicationMenuReq,
  type MetadataEntityPair,
  type UpdateApplicationMenuNameReq,
  type UpdateApplicationMenuOrderReq,
  type UpdateApplicationMenuVisibleReq
} from '@onebase/app';
import { EDITOR_TYPES } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import { debounce } from 'lodash-es';
import { useCallback, useEffect, useState, type FC } from 'react';
import { useNavigate } from 'react-router-dom';
import { ReactSVG } from 'react-svg';
import CopyModal from './components/Modals/CopyModal';
import CreateModal from './components/Modals/CreateModal';
import RenameModal from './components/Modals/RenameModal';
import MyMenuItem from './components/MyMenuItem';
import styles from './index.module.less';

const TreeNode = Tree.Node;
const MenuItem = Menu.Item;
const Sider = Layout.Sider;
const Content = Layout.Content;

/**
 * 树形数据节点接口
 */
interface TreeNode {
  key: string;
  value: string;
  title: string;
  menuType?: MenuType;
  children?: TreeNode[];
}

interface Options {
  label: string;
  value: string;
}

const PageManagerPage: FC = () => {
  useSignals();

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

  const { curMenu, setCurMenu } = menuSignal;
  const [parentPageOptions, setParentPageOptions] = useState<ApplicationMenu[]>([RootParentPage]);

  const [expandedKeys, setExpandedKeys] = useState<string[]>([]);
  const [searchResult, setSearchResult] = useState<boolean>(false); // 菜单搜索结果

  const initTreeItemWidth = 146;
  const cutTreeItemWidth = 25;

  const { clearIsEditMode } = useBasicEditorStore();

  const findFirstPage: any = (nodes: ApplicationMenu[]) =>
    nodes.reduce((found, node) => {
      if (found) return found;
      if (Number(node.menuType) === MenuType.PAGE) return node;
      setExpandedKeys((prev) => [...prev, node.id]);
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
          isVisible={menu.isVisible}
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
      menuType: menu.menuType,
      props: {
        // ✅ 显式传入 props 让 dragNode.props.menuType 能取到
        menuType: menu.menuType
      },
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
    const appId: string = curAppId;
    const res: MetadataEntityPair[] = await getEntityListByApp(appId);
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
        <div className={styles.createItem}>
          <ReactSVG
            className={styles.customSvg}
            src={CreatePageIcon}
            beforeInjection={(svg) => {
              svg.querySelectorAll('*').forEach((el) => el.removeAttribute('fill'));
              svg.setAttribute('fill', '#4E5969');
              svg.setAttribute('width', '16px');
              svg.setAttribute('height', '16px');
            }}
          />
          {t('createApp.createPage')}
        </div>
      </MenuItem>
      <MenuItem
        key="group"
        onClick={() => {
          setVisibleCreateForm('group');
          createForm.resetFields();
          setTitle(t('createApp.createGroup'));
        }}
      >
        <div className={styles.createItem}>
          <ReactSVG
            className={styles.customSvg}
            src={CreateGroupIcon}
            beforeInjection={(svg) => {
              svg.querySelectorAll('*').forEach((el) => el.removeAttribute('fill'));
              svg.setAttribute('fill', '#4E5969');
              svg.setAttribute('width', '16px');
              svg.setAttribute('height', '16px');
            }}
          />
          {t('createApp.createGroup')}
        </div>
      </MenuItem>
    </Menu>
  );

  const triggerRename = () => {
    setVisibleRenameForm(true);
    setTitle(t('createApp.rename'));
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

  // 更新应用菜单可见性  显示/隐藏
  const triggerHide = async (menuID: string, isVisible: number) => {
    const req: UpdateApplicationMenuVisibleReq = {
      id: menuID,
      visible: isVisible === VisibleType.HIDDEN ? VisibleType.SHOW : VisibleType.HIDDEN
    };
    const res = await updateApplicationMenuVisible(req);
    if (res) {
      Message.success(`${isVisible === VisibleType.HIDDEN ? '取消隐藏' : '隐藏'}成功`);
    }
    getMenuList();
  };

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
        sessionStorage.setItem(
          'EDITOR_PAGE_INFO',
          JSON.stringify({ id: curMenu.value?.id, name: menuResp.menuName, icon: createForm.getFieldValue('menuIcon') })
        );
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
      menuName: renameForm.getFieldValue('menuName'),
      menuIcon: renameForm.getFieldValue('menuIcon')
    };
    const res = await updateApplicationMenu(req);
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
      setCurMenu({} as ApplicationMenu);
    }

    getMenuList();
  };

  const handleEditPageSet = async (name: string, icon: string) => {
    if (!curMenu.value?.id) {
      Message.error('请选择菜单');
      return;
    }

    const req: GetPageSetIdReq = {
      menuId: curMenu.value?.id
    };
    const pageSetId = await getPageSetId(req);

    if (!pageSetId) {
      Message.error('请先创建页面集');
      return;
    }

    // 把编辑页菜单数据保存起来；
    sessionStorage.setItem('EDITOR_PAGE_INFO', JSON.stringify({ id: curMenu.value?.id, name, icon }));
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

  // 菜单节点拖拽排序
  const moveNode = (dragNode: TreeNode, dropNode: TreeNode | null, dropPosition: number): void => {
    // 移除节点
    const removeNode = (nodes: TreeNode[], key: string): TreeNode | null => {
      for (let i = 0; i < nodes.length; i++) {
        if (nodes[i].key === key) return nodes.splice(i, 1)[0];
        if (nodes[i].children) {
          const removed = removeNode(nodes[i].children!, key);
          if (removed) return removed;
        }
      }
      return null;
    };

    const dragItem = treeData ? removeNode(treeData, dragNode.key) : null;
    if (!dragItem) return;

    // 插入节点
    const insertNode = (
      nodes: TreeNode[],
      key: string | null,
      nodeToInsert: TreeNode,
      position: number,
      parentNodes: TreeNode[] = nodes
    ): boolean => {
      for (let i = 0; i < nodes.length; i++) {
        if (nodes[i].key === key) {
          const dropType = nodes[i].menuType;
          const dragType = nodeToInsert.menuType;

          // 页面节点不能放到页面节点内部
          if (dragType === MenuType.PAGE && dropType === MenuType.PAGE && position === 0) {
            return false;
          }

          if (dropType === MenuType.GROUP && position === 0) {
            // 拖到分组节点上 -> 插入 children
            if (!nodes[i].children) nodes[i].children = [];
            nodes[i].children!.push(nodeToInsert);
            setExpandedKeys((prev) => [...prev, nodes[i].key]);
          } else if (position === -1) {
            // 放在目标节点前面
            const index = parentNodes.indexOf(nodes[i]);
            parentNodes.splice(index, 0, nodeToInsert);
          } else if (position === 1) {
            // 放在目标节点后面
            const index = parentNodes.indexOf(nodes[i]);
            parentNodes.splice(index + 1, 0, nodeToInsert);
          }

          return true;
        }

        if (nodes[i].children && insertNode(nodes[i].children!, key, nodeToInsert, position, nodes[i].children)) {
          return true;
        }
      }

      // 拖到空白处，插入顶层
      if (key === null && parentNodes === nodes) {
        nodes.push(nodeToInsert);
        return true;
      }

      return false;
    };

    insertNode(treeData!, dropNode ? dropNode.key : null, dragItem, dropPosition);
    setTreeData([...treeData!]);
  };

  const buildMenuTree = (nodes: TreeNode[]): { id: string; children: any[] }[] =>
    nodes.map((node) => ({
      id: node.key,
      children: node.children ? buildMenuTree(node.children) : []
    }));

  const findParentNode = (list: TreeNode[], key: string): TreeNode | null => {
    for (const node of list) {
      if (node.children?.some((c) => c.key === key)) {
        return node;
      }
      const found = findParentNode(node.children || [], key);
      if (found) return found;
    }
    return null;
  };

  return (
    <div className={styles.pageManagerPage}>
      <Layout style={{ height: '100%' }}>
        <Sider className={styles.sider} style={{ width: 220 }}>
          <div className={styles.siderTitle}>
            所有页面
            <Dropdown droplist={createMenuDropList} trigger="click" position="bl">
              <Button type="text" icon={<IconPlus />} style={{ padding: 6 }}>
                新建
              </Button>
            </Dropdown>
          </div>
          <div className={styles.siderHeader}>
            <div className={styles.siderHeaderInput}>
              <Input
                allowClear
                suffix={<IconSearch />}
                placeholder={t('createApp.searchPlaceHolder')}
                onChange={debouncedSearch}
              />
            </div>
          </div>

          <Tree
            blockNode
            draggable
            selectedKeys={[curMenu.value?.id!]}
            treeData={treeData}
            className={`menuTree ${styles.tree}`}
            showLine={false}
            icons={{
              switcherIcon: <IconDown />,
              dragIcon: null
            }}
            expandedKeys={expandedKeys}
            onExpand={setExpandedKeys}
            actionOnClick={'expand'}
            style={{
              width: '220px',
              overflow: 'hidden',
              boxSizing: 'border-box',
              padding: '4px 8px',
              display: 'flex',
              flexDirection: 'column',
              gap: 8
            }}
            allowDrop={(info: any) => {
              const { dragNode, dropNode, dropPosition } = info;

              const dragParent = dragNode?.props?.parentKey;
              const dropParent = dropNode?.props?.parentKey;
              const sameParent = dragParent === dropParent;

              // 仅允许同级间上下移动
              return sameParent && dropPosition !== 0;
            }}
            onDrop={async (info: any) => {
              const dragNode = info.dragNode;
              const dropNode = info.dropNode;
              const dropPosition = info.dropPosition;
              const dropNodeParent = findParentNode(treeData!, dropNode.key);

              moveNode(dragNode, dropNode, dropPosition);

              // 生成接口参数
              const payload: UpdateApplicationMenuOrderReq = {
                id: dragNode.key,
                parentId: dropNodeParent?.key || '0',
                menuTree: buildMenuTree(treeData!)
              };

              console.debug('✅ 更新后的参数:', payload);
              await updateApplicationMenuOrder(payload);
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
                  {curMenu.value?.id && (
                    <>
                      <div className={styles.contentHeader}>
                        <div className={styles.contentTitle}>{curMenu.value?.menuName}</div>
                        <Button
                          className={styles.editButton}
                          type="primary"
                          icon={<img src={EditIcon} alt="编辑页面" />}
                          onClick={() => handleEditPageSet(curMenu.value?.menuName, curMenu.value?.menuIcon)}
                        >
                          {t('createApp.editPage')}
                        </Button>
                      </div>
                      <div className={styles.contentBody}>
                        <PreviewContainer menuId={curMenu.value?.id} runtime={true} />
                      </div>
                    </>
                  )}
                </>
              )}
            </>
          )}
        </Content>
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
