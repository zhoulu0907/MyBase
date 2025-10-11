import { useEffect, useState, type FC } from 'react';
import { Tree } from '@arco-design/web-react';
import {
  MenuType,
  type ApplicationMenu,
} from '@onebase/app';
import MyMenuItem from '../MyMenuItem';
import { IconSettings, IconDragDotVertical } from '@arco-design/web-react/icon';
// import willdoIcon from '@/assets/images/task_center/willdo.svg'
import './taskSide.less'

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
  const [curMenu2, setCurMenu2] = useState<ApplicationMenu>();
  const [_activeMenu, setActiveMenu] = useState<ApplicationMenu>();
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
              setCurMenu2(menu);
              setCurMenu(menu);
            }
            setActiveMenu(menu);
          } } 
          triggerCreate={function (formType: string): void {
            throw new Error('Function not implemented.');
          } } 
          triggerRename={function (): void {
            throw new Error('Function not implemented.');
          } } 
          triggerCopy={function (): void {
            throw new Error('Function not implemented.');
          } } 
          triggerHide={function (menuID: string, isVisible: number): void {
            throw new Error('Function not implemented.');
          } } 
          triggerDelete={function (menuID: string): void {
            throw new Error('Function not implemented.');
          } } 
          renameForm={undefined} 
          copyForm={undefined}
          createForm={undefined}        
        />
      ),
      children: menu.children ? convertMenuToTreeData(menu.children, maxWidth - cutTreeItemWidth, showOption) : []
    }));
  };
  const [treeData, setTreeData] = useState<TreeNode[]>();

  useEffect(() => {
    let res:any = [
      {
        id: "TASK-ineedtodo",
        isVisible: 1,
        menuCode: "ineedtodo",
        menuIcon: "ineedtodo-icon",
        menuName: "待我处理",
        menuSort: 1,
        menuType: 1,
        parentId: "0"
      },
      {
        id: "TASK-ineedtodo2",
        isVisible: 1,
        menuCode: "ineedtodo2",
        menuIcon: "ineedtodo-icon2",
        menuName: "待我处理2",
        menuSort: 2,
        menuType: 1,
        parentId: "0"
      }
    ];
    const treeData = convertMenuToTreeData(res, 155, true);
    setTreeData(treeData);
  }, [])
  
  return <section className='task-center-box'>
    <Tree
      blockNode
      draggable={true}
      selectedKeys={[curMenu2?.id!]}
      treeData={treeData}
      className={styles_tree}
      showLine={false}
      icons={{
        switcherIcon: null,
        dragIcon: <IconDragDotVertical/>
      }}
      actionOnClick={'expand'}
      style={{
        width: '200px',
        overflow: 'hidden',
        boxSizing: 'border-box',
        padding: '0 8px'
      }}
    />
  </section>
}

export default TaskCenterTreeSide;