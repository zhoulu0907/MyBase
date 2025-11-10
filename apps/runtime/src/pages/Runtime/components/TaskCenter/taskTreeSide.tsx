import { useEffect, useState, type FC } from 'react';
import { Tree, Form } from '@arco-design/web-react';
import {
  MenuType,
  type ApplicationMenu,
} from '@onebase/app';
import MyMenuItem from '../menuItem/index';
import { IconSettings, IconDragDotVertical } from '@arco-design/web-react/icon';
import RenameForm from './modal/renameForm'

import './style/taskSide.less'

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
}
const TaskCenterTreeSide:FC<ComProps> = ({setCurMenu, styles_tree}) => {
  const [curMenu2, setCurMenu2] = useState<ApplicationMenu | any>();
  // const [_activeMenu, setActiveMenu] = useState<ApplicationMenu>();

  // 将接口返回的菜单数据（res）转换为 Tree 组件可用的 treeData 格式
  // TODO(mickey): showOption重构
  const convertMenuToTreeData = (menus: ApplicationMenu[], maxWidth: number, showOption: boolean = false): any[] => {
    return menus.map((menu, idx) => ({
      key: menu.id + '-' + idx,
      title: (
        <MyMenuItem
          showOption={showOption}
          menuID={menu.id}
          isVisible={menu.isVisible}
          menuCode={menu.menuCode}
          menuName={menu.menuName}
          menuIcon={menu.menuIcon}
          isGroup={true}
          maxWidth={maxWidth}
          label={menu.menuName}
          onClick={() => {
            if (menu.menuType == MenuType.PAGE) {
              let _menu = {
                ...menu,
                _key: menu.id + '-' + idx
              };
              setCurMenu2(_menu);
              setCurMenu(_menu);
            }
            // setActiveMenu(menu);
          } }
          triggerRename = {() => setVisibleRenameForm(true)}
          triggerHide = {() => triggerHide(menu.id, idx, menu.isVisible)}
          renameForm = {renameForm}
        />
      ),
      children: menu.children ? convertMenuToTreeData(menu.children, maxWidth - 25, showOption) : []
    }));
  };
  const [treeData, setTreeData] = useState<TreeNode[]>();

  // 重命名弹窗
  const [renameForm] = Form.useForm();
  const [visibleRenameForm, setVisibleRenameForm] = useState(false);

  function getMenuArr() {
    return [
      {
        id: "TASK-ineedtodo",
        isVisible: 1,
        menuCode: "ineedtodo",
        menuIcon: "ineedtodo-taskicon",
        menuName: "待我处理",
        menuSort: 1,
        menuType: 1,
        parentId: "0"
      },
      {
        id: "TASK-ihavedone",
        isVisible: 1,
        menuCode: "ihavedone",
        menuIcon: "ihavedone-taskicon",
        menuName: "我已处理",
        menuSort: 2,
        menuType: 1,
        parentId: "0"
      },
      {
        id: "TASK-icreated",
        isVisible: 1,
        menuCode: "icreated",
        menuIcon: "icreated-taskicon",
        menuName: "我创建的",
        menuSort: 3,
        menuType: 1,
        parentId: "0"
      },
      {
        id: "TASK-icopied",
        isVisible: 1,
        menuCode: "icopied",
        menuIcon: "icopied-taskicon",
        menuName: "抄送我的",
        menuSort: 4,
        menuType: 1,
        parentId: "0"
      },
      {
        id: "TASK-taskproxy",
        isVisible: 1,
        menuCode: "taskproxy",
        menuIcon: "taskproxy-taskicon",
        menuName: "流程代理",
        menuSort: 5,
        menuType: 1,
        parentId: "0"
      }
    ];
  }
  function handleRename() {
    console.log('handle re name function.')
  }
  // 更新应用菜单可见性  显示/隐藏
  function triggerHide(menuId: string | number, arrIdx: number, isVisible: number) {
    console.log('trigger hide ===', menuId, arrIdx)
    let res:Array<any> = getMenuArr()
    res[arrIdx].isVisible = isVisible === 0 ? 1 : 0;
    const treeData = convertMenuToTreeData(res, 155, true);
    setTreeData(treeData);
  }

  useEffect(() => {
    let res:Array<any> = getMenuArr()
    const _treeData = convertMenuToTreeData(res, 155, true);
    setTreeData(_treeData);
  }, [])

  // useEffect(() => {
  //   console.log(curMenu2)
  // }, [curMenu2])
  
  return <section className='task-center-side'>
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
        dragIcon: <IconDragDotVertical/>
      }}
      actionOnClick={'expand'}
      onDrop={({ dragNode, dropNode, dropPosition }: any) => {
        const loop = (data: Array<TreeNode>, key: string, callback: Function) => {
            data.some((item: TreeNode, index: number, arr: Array<any>) => {
              if (item.key === key) {
                callback(item, index, arr);
                return true;
              }

              if (item.children) {
                return loop(item.children, key, callback);
              }
            });
          };

          const data = treeData? [...treeData] : [];
          let dragItem:TreeNode | any;
          loop(data, dragNode.props._key, (item: TreeNode, index: number, arr: Array<any>) => {
            arr.splice(index, 1);
            dragItem = item;
            dragItem.className = 'tree-node-dropover';
          });

          if (dropPosition === 0) {
            loop(data, dropNode.props._key, (item: TreeNode, index: number, arr: Array<any>) => {
              item.children = item.children || [];
              item.children.push(dragItem);
            });
          } else {
            loop(data, dropNode.props._key, (item: TreeNode, index: number, arr: Array<any>) => {
              arr.splice(dropPosition < 0 ? index : index + 1, 0, dragItem);
            });
          }

          setTreeData([...data]);
          setTimeout(() => {
            dragItem.className = '';
            setTreeData([...data]);
          }, 1000);
      }}
      style={{
        width: '220px',
        overflow: 'hidden',
        boxSizing: 'border-box',
        padding: '0 8px',
      }}
    />
    <RenameForm
        visible={visibleRenameForm}
        handleRename={handleRename}
        setVisible={setVisibleRenameForm}
        form={renameForm}/>
  </section>
}

export default TaskCenterTreeSide;