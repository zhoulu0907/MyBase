import { Form, Message, Tree } from '@arco-design/web-react';
import { IconDragDotVertical } from '@arco-design/web-react/icon';
import {
  MenuType,
  listApplicationBPMMenu,
  updateApplicationMenu,
  updateApplicationMenuOrder,
  type ApplicationMenu,
  type ListApplicationMenuReq,
  type UpdateApplicationMenuNameReq,
  type UpdateApplicationMenuOrderReq
} from '@onebase/app';
import { useEffect, useState, type FC } from 'react';
import MyMenuItem from '../MyMenuItem';
import RenameForm from './modal/renameForm';

import './style/taskSide.less';

const TreeNode = Tree.Node;
/**
 * 树形数据节点接口
 */
interface TreeNode {
  key: string;
  value: string;
  title: string;
  children?: TreeNode[];
}

interface ComProps {
  setCurMenu: any;
  styles_tree: any;
  curAppId: any;
  curMenu: any;
  searchResult: any;
  setSearchResult: any;
  setShowGuide: any;
  triggerHide: (menuID: string, isVisible: number, platform: 'pc' | 'mobile') => void;
}

const TaskCenterTreeSide: FC<ComProps> = ({
  setCurMenu,
  curMenu,
  styles_tree,
  curAppId,
  searchResult,
  setSearchResult,
  setShowGuide,
  triggerHide
}) => {
  const [curMenu2, setCurMenu2] = useState<ApplicationMenu | any>();

  const convertMenuToTreeData = (menus: ApplicationMenu[], maxWidth: number, showOption: boolean = false): any[] => {
    return menus.map((menu, idx) => ({
      key: menu.id,
      parentId: menu.parentId,
      title: (
        <MyMenuItem
          showOption={showOption}
          menuID={menu.id || ''}
          menuInfo={menu}
          pagesetType={menu.pagesetType}
          isVisiblePc={menu.isVisiblePc}
          isVisibleMobile={menu.isVisibleMobile}
          menuCode={menu.menuCode}
          menuName={menu.menuName}
          menuIcon={menu.menuCode}
          menuType={menu.menuType}
          isGroup={true}
          maxWidth={maxWidth}
          label={menu.menuName}
          onClick={() => {
            if (menu.menuType == MenuType.BPM) {
              let _menu = {
                ...menu,
                _key: menu.id
              };
              setCurMenu2(_menu);
              setCurMenu(_menu);
            }
          }}
          triggerRename={() => setVisibleRenameForm(true)}
          triggerHide={onTriggerHide}
          renameForm={renameForm}
        />
      ),
      children: menu.children ? convertMenuToTreeData(menu.children, maxWidth - 25, showOption) : []
    }));
  };
  const [treeData, setTreeData] = useState<TreeNode[]>();

  // 重命名弹窗
  const [renameForm] = Form.useForm();
  const [visibleRenameForm, setVisibleRenameForm] = useState(false);

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
    getBpmMenuList();
  };
  const loop = (data: any, key: string, callback: (item: any, index: number, arr: any) => void) => {
    data.some((item: any, index: any, arr: any) => {
      if (item.key === key) {
        callback(item, index, arr);
        return true;
      }
      return false;
    });
  };
  const handleDrop = async ({ dragNode, dropNode, dropPosition }: any) => {
    if (dropPosition === 0) {
      //   Message.warning('仅支持拖拽到节点上下方调整顺序，禁止拖入节点内部');
      return;
    }
    if (!dropNode || dragNode.props._key === dropNode.props._key) {
      return;
    }
    const data = treeData ? [...treeData] : [];
    let dragItem: any;
    loop(data, dragNode.props._key, (item, index, arr) => {
      arr.splice(index, 1);
      dragItem = item;
    });

    if (!dragItem) return;

    loop(data, dropNode.props._key, (_, index, arr) => {
      arr.splice(dropPosition < 0 ? index : index + 1, 0, dragItem!);
    });
    setTreeData([...data]);
    const menuTree = data.map((node) => ({
      id: node.key
    }));
    const payload: UpdateApplicationMenuOrderReq = {
      id: dragNode.key,
      parentId: dragNode.parentId,
      menuTree
    };
    await updateApplicationMenuOrder(payload);
  };
  const getBpmMenuList = async () => {
    const req: ListApplicationMenuReq = {
      applicationId: curAppId
    };
    const res = await listApplicationBPMMenu(req);
    if (res && res.length > 0) {
      const sortedData = res?.sort((a: any, b: any) => a.menuSort - b.menuSort);
      const _treeData = convertMenuToTreeData(sortedData, 155, true);
      setTreeData(_treeData);
      if (!curMenu.value || Object.keys(curMenu.value).length === 0) {
        setCurMenu({ ...sortedData[0], _key: sortedData[0]?.id });
        setCurMenu2({ ...sortedData[0], _key: sortedData[0]?.id });
      }
      setSearchResult(false);
    }
  };
  const onTriggerHide = async (menuId: string, isVisible: number, platform: 'pc' | 'mobile') => {
    await triggerHide(menuId, isVisible, platform);
    getBpmMenuList();
  };

  useEffect(() => {
    if (curAppId !== '') {
      getBpmMenuList();
    }
  }, [curAppId]);

  useEffect(() => {
    if (searchResult) return;
    setShowGuide(treeData?.length === 0);
  }, [treeData, searchResult]);

  useEffect(() => {
    if (curMenu.value && curMenu.value?.menuType !== MenuType.BPM) {
      setCurMenu2(null);
    }
  }, [curMenu.value]);

  return (
    <section className="task-center-side">
      <Tree
        blockNode
        draggable={true}
        selectable
        selectedKeys={[curMenu2?._key]}
        treeData={treeData}
        className={styles_tree}
        showLine={false}
        icons={{
          switcherIcon: null,
          dragIcon: <IconDragDotVertical />
        }}
        actionOnClick={'expand'}
        onDrop={handleDrop}
        style={{
          width: '220px',
          overflow: 'hidden',
          boxSizing: 'border-box',
          padding: '0 8px'
        }}
      />
      <RenameForm
        visible={visibleRenameForm}
        handleRename={handleRename}
        setVisible={setVisibleRenameForm}
        form={renameForm}
      />
    </section>
  );
};

export default TaskCenterTreeSide;
