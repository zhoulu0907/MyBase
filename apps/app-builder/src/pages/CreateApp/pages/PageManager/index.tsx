import CreateGroupIcon from '@/assets/images/addfolder.svg';
import CreatePageIcon from '@/assets/images/addpage.svg';
import CreateWorkbenchIcon from '@/assets/images/addworkbench.svg';
import EditIcon from '@/assets/images/edit_menu_icon.svg';
import PageManagerGuide from '@/assets/images/page_manaager_guide.svg';
import { useI18n } from '@/hooks/useI18n';
import PreviewContainer from '@/pages/Runtime/components/preview';
import { useAppStore } from '@/store/store_app';
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
  RELATION_TYPE,
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
import { pagesRuntimeSignal } from '@onebase/common';
import { EDITOR_TYPES } from '@onebase/ui-kit';
import { currentEditorSignal } from '@onebase/ui-kit/src/signals/current_editor';
import { useSignals } from '@preact/signals-react/runtime';
import { debounce } from 'lodash-es';
import { useCallback, useEffect, useState, type FC } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { ReactSVG } from 'react-svg';
import { RELATIONSHIP_TYPE } from '../DataFactory/utils/types';
import CopyModal from './components/Modals/CopyModal';
import CreateModal from './components/Modals/CreateModal';
import CreateScreenModal from './components/Modals/CreateScreenModal';
import RenameModal from './components/Modals/RenameModal';
import MyMenuItem from './components/MyMenuItem';
import TaskCenterPage from './components/TaskCenter/TaskCenterPage';
import TaskCenterSide from './components/TaskCenter/taskTreeSide';
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

const menuStyles = {
  height: '40px',
  padding: '9px 12px'
};

const PageManagerPage: FC = () => {
  useSignals();

  const { t } = useI18n();
  const navigate = useNavigate();

  const { tenantId } = useParams();

  const { curAppId } = useAppStore();

  const [createForm] = Form.useForm();
  const [renameForm] = Form.useForm();
  const [copyForm] = Form.useForm();
  // 创建弹窗
  const [visibleCreateForm, setVisibleCreateForm] = useState('');
  // 创建大屏弹窗
  const [visibleCreateScreenForm, setVisibleCreateScreenForm] = useState('');
  // 重命名弹窗
  const [visibleRenameForm, setVisibleRenameForm] = useState(false);
  // 复制弹窗
  const [visibleCopyForm, setVisibleCopyForm] = useState(false);

  const [title, setTitle] = useState('');
  const [showGuide, setShowGuide] = useState<boolean>(false);
  const pageSetTypeOptions = [
    { label: '普通表单', value: PageType.NORMAL },
    { label: '流程表单', value: PageType.BPM },
    { label: '工作台', value: PageType.WORKBENCH }
  ];

  const [treeData, setTreeData] = useState<TreeNode[]>();
  const [entityListOptions, setEntityListOptions] = useState<Options[]>([]);

  const { curMenu, setCurMenu } = menuSignal;
  const { curPage } = pagesRuntimeSignal;
  const [parentPageOptions, setParentPageOptions] = useState<ApplicationMenu[]>([RootParentPage]);

  const [expandedKeys, setExpandedKeys] = useState<string[]>([]);
  const [searchResult, setSearchResult] = useState<boolean>(false); // 菜单搜索结果

  const initTreeItemWidth = 146;
  const cutTreeItemWidth = 25;

  const { clearEditMode } = currentEditorSignal;

  const findFirstPage: any = (nodes: ApplicationMenu[]) =>
    nodes.reduce((found, node) => {
      if (found) return found;
      if (Number(node.menuType) === MenuType.PAGE) return node;
      setExpandedKeys((prev) => [...prev, node.id]);
      return node.children ? findFirstPage(node.children) : undefined;
    }, undefined);

  useEffect(() => {
    setCurMenu({} as ApplicationMenu);
  }, [location.pathname]);

  useEffect(() => {
    if (curAppId !== '') {
      getMenuList();
      getEntityList();
    }
    clearEditMode();
  }, [curAppId]);

  useEffect(() => {
    if (searchResult) return;
    setShowGuide(treeData?.length === 0);
  }, [treeData, searchResult]);

  // 将接口返回的菜单数据（res）转换为 Tree 组件可用的 treeData 格式
  // TODO(mickey): showOption重构
  const convertMenuToTreeData = (
    menus: ApplicationMenu[],
    maxWidth: number,
    showOption: boolean = false,
    style: React.CSSProperties = menuStyles
  ): any[] => {
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
          style={style}
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

  const getMenuList = async (keywords?: string, menuId?: string) => {
    const req: ListApplicationMenuReq = {
      applicationId: curAppId,
      name: keywords
    };
    const res = await listApplicationMenu(req);
    // 为每个children元素补充parentId字段
    const processedRes = addParentIdToChildren(res, RootParentPage.id);
    setParentPageOptions([{ ...RootParentPage, children: processedRes }]);

    const treeData = convertMenuToTreeData(res, initTreeItemWidth, true, menuStyles);
    setTreeData(treeData);
    if (menuId) {
      const findCreateMenu = res.find((item: any) => item.id === menuId);
      setCurMenu(findCreateMenu);
      setSearchResult(false);
    } else if (res && res.length > 0) {
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

    const entityOptions = res
      .filter(
        (entity) =>
          // 过滤子表
          entity.relationType !== RELATION_TYPE.SLAVE ||
          (entity.relationType === RELATION_TYPE.SLAVE &&
            !entity.relationshipTypes.includes(RELATIONSHIP_TYPE.SUBTABLE_ONE_TO_MANY))
      )
      .map((entity) => ({
        label: entity.entityName,
        value: entity.entityUuid
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
        key="workbench"
        onClick={() => {
          setVisibleCreateForm('workbench');
          createForm.resetFields();
          setTitle(t('createApp.createWorkbench'));
        }}
      >
        <div className={styles.createItem}>
          <ReactSVG className={styles.customSvg} src={CreateWorkbenchIcon} />
          {t('createApp.createWorkbench')}
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
      <MenuItem
        key="screen"
        onClick={() => {
          setVisibleCreateScreenForm('screen');
          createForm.resetFields();
          setTitle(t('createApp.createScreen'));
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
          {t('createApp.createScreen')}
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
      const req: CreateApplicationMenuReq = {
        applicationId: curAppId,
        parentId:
          createForm.getFieldValue('parentId') === RootParentPage.id ? '' : createForm.getFieldValue('parentId'),
        pageSetType: createForm.getFieldValue('pageSetType'),
        menuName: createForm.getFieldValue('menuName'),
        menuType: MenuType.PAGE,
        menuIcon: createForm.getFieldValue('menuIcon'),
        entityUuid: visibleCreateForm === 'page' ? createForm.getFieldValue('entityUuid') : ''
      };

      if (visibleCreateForm === 'page') {
        req.menuType = MenuType.PAGE;
      }
      if (visibleCreateForm === 'group') {
        req.menuType = MenuType.GROUP;
      }
      if (visibleCreateForm === 'workbench') {
        req.menuType = MenuType.PAGE;
        req.pageType = 'workbench';
      }

      const menuResp = await createApplicationMenu(req);

      if (menuResp) {
        Message.success('创建成功');
      }
      setVisibleCreateForm('');
      getMenuList(undefined, menuResp.id);

      const pageSetId = await getPageSetId({
        menuId: menuResp.id
      });

      if (pageSetId && menuResp.menuType === MenuType.PAGE) {
        sessionStorage.setItem(
          'EDITOR_PAGE_INFO',
          JSON.stringify({ id: curMenu.value?.id, name: menuResp.menuName, icon: createForm.getFieldValue('menuIcon') })
        );

        // 根据页面类型跳转到对应的编辑器
        let editorType: string = EDITOR_TYPES.FORM_EDITOR;
        if (visibleCreateForm === 'workbench') {
          editorType = EDITOR_TYPES.WORKBENCH_EDITOR;
        } else {
          editorType = EDITOR_TYPES.FORM_EDITOR;
        }

        navigate(`/onebase/${tenantId}/editor/${editorType}?pageSetId=${pageSetId}&appId=${curAppId}`);
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

    const editorType =
      curPage.value?.pageSetType === PageType.WORKBENCH ? EDITOR_TYPES.WORKBENCH_EDITOR : EDITOR_TYPES.FORM_EDITOR;

    // 把编辑页菜单数据保存起来；
    sessionStorage.setItem('EDITOR_PAGE_INFO', JSON.stringify({ id: curMenu.value?.id, name, icon }));
    navigate(`/onebase/${tenantId}/editor/${editorType}?pageSetId=${pageSetId}&appId=${curAppId}`);
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

  const buildMenuTree = (nodes: TreeNode[]): { id: string; children: any[] }[] =>
    nodes.map((node) => ({
      id: node.key,
      children: node.children ? buildMenuTree(node.children) : []
    }));

  const findParentKey = (data: TreeNode[], childKey: string, parentKey: string | null = null): string | null => {
    for (const item of data) {
      if (item.key === childKey) {
        return parentKey;
      }
      if (item.children) {
        const result = findParentKey(item.children, childKey, item.key);
        if (result) return result;
      }
    }
    return null;
  };

  // 遍历工具函数
  const loop = (
    data: TreeNode[],
    key: string,
    callback: (item: TreeNode, index: number, arr: TreeNode[]) => void
  ): boolean => {
    return data.some((item, index, arr) => {
      if (item.key === key) {
        callback(item, index, arr);
        return true;
      }
      if (item.children) {
        return loop(item.children, key, callback);
      }
      return false;
    });
  };

  const handleDrop = async ({ dragNode, dropNode, dropPosition }: any) => {
    const data = [...treeData!];
    const dragType = dragNode.props.menuType;
    const dropType = dropNode.props.menuType;

    // ✅ 规则校验：只允许 PAGE → GROUP
    if (dropPosition === 0 && !(dragType === MenuType.PAGE && dropType === MenuType.GROUP)) {
      console.warn('❌ 只能将页面拖入分组下');
      return;
    }
    if (dropType === MenuType.PAGE && dropPosition === 0) {
      console.warn('❌ 分组不能拖入页面');
      return;
    }

    let dragItem: TreeNode | undefined;

    // 从原位置移除
    loop(data, dragNode.key, (item, index, arr) => {
      arr.splice(index, 1);
      dragItem = item;
    });

    if (!dragItem) return;

    // 新的 parentId（默认 null = 根节点）
    let newParentId: string | null = null;

    if (dropPosition === 0) {
      // 拖入节点内部
      loop(data, dropNode.key, (item) => {
        item.children = item.children || [];
        item.children.push(dragItem!);
      });
      newParentId = dropNode.key; // ✅ 设置新父节点 id
      setExpandedKeys([newParentId!]);
    } else {
      // 拖入节点前后
      loop(data, dropNode.key, (_item, index, arr) => {
        arr.splice(dropPosition < 0 ? index : index + 1, 0, dragItem!);
      });
      // 前后拖拽，父节点等于 dropNode 的父节点
      newParentId = findParentKey(treeData!, dropNode.key);
    }

    setTreeData([...data]);

    // 生成接口参数
    const payload: UpdateApplicationMenuOrderReq = {
      id: dragNode.key,
      parentId: newParentId ?? '0',
      menuTree: buildMenuTree(data)
    };

    console.debug('✅ 更新后的参数:', payload, data);
    await updateApplicationMenuOrder(payload);
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
          <TaskCenterSide
            curMenu={curMenu}
            setCurMenu={setCurMenu}
            styles_tree={styles.tree}
            curAppId={curAppId}
            triggerHide={triggerHide}
            findFirstPage={findFirstPage}
            setSearchResult={setSearchResult}
            searchResult={searchResult}
            setShowGuide={setShowGuide}
          />
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
              const dragNode = info.dragNode;
              const dropNode = info.dropNode;
              const dropPosition = info.dropPosition;
              const dragType = dragNode?.props?.menuType;
              const dropType = dropNode.props?.menuType;

              if (dropPosition === 0) {
                return dragType === MenuType.PAGE && dropType === MenuType.GROUP;
              }
              return true;
            }}
            onDrop={handleDrop}
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
                  {curMenu.value?.menuCode && curMenu.value?.menuCode?.indexOf('TASK-') < 0 && (
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
                  {curMenu?.value?.menuCode && curMenu?.value?.menuCode?.indexOf('TASK-') >= 0 && (
                    <TaskCenterPage curMenuId={curMenu.value?.menuCode} />
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
        treeData={convertMenuToTreeData(parentPageOptions, initTreeItemWidth, false, { height: '32px' })}
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
        pageSetTypeOptions={pageSetTypeOptions}
        visibleCreateForm={visibleCreateForm}
        initValue={{ pageType: PageType.NORMAL, menuName: '', parentId: RootParentPage.id }}
        treeData={convertMenuToTreeData(parentPageOptions, initTreeItemWidth, false, { height: '32px' })}
      />
      <CreateScreenModal
        title={title}
        handleCreate={handleCreate}
        onCancel={() => {
          setVisibleCreateScreenForm('');
        }}
        form={createForm}
        entityListOptions={entityListOptions}
        visibleCreateForm={visibleCreateScreenForm}
        initValue={{ pageType: PageType.NORMAL, menuName: '', parentId: RootParentPage.id }}
        treeData={convertMenuToTreeData(parentPageOptions, initTreeItemWidth, false, { height: '32px' })}
      />
    </div>
  );
};

export default PageManagerPage;
