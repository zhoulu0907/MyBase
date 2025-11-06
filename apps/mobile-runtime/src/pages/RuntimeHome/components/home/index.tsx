import { useI18n } from '@/hooks/useI18n';
import { Collapse, Grid } from '@arco-design/mobile-react';
import {
  listApplicationMenu,
  menuSignal,
  MenuType,
  TASKMENU_TYPE,
  VisibleType,
  type ApplicationMenu,
  type ListApplicationMenuReq
} from '@onebase/app';
import { useSignals } from '@preact/signals-react/runtime';
import { IconUser, IconHome } from '@arco-design/mobile-react/esm/icon';
import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams, useSearchParams } from 'react-router-dom';
import RuntimeMenuItem from '../menuItem';
import styles from './index.module.less';

interface TreeNode {
  key: string;
  value: string;
  title: string;
  isVisible: number;
  children?: TreeNode[];
}

const Home: React.FC = () => {
  useSignals();

  const navigate = useNavigate();
  const location = useLocation();
  const { appId } = useParams<{ appId?: string }>();
  const [search] = useSearchParams();
  const curMenuId = search.get('curMenu');
  const { t } = useI18n();

  const [treeData, setTreeData] = useState<TreeNode[]>([]);

  const initTreeItemWidth = 155;
  const cutTreeItemWidth = 25;
  const { curMenu, setCurMenu } = menuSignal;
  const [expandedKeys, setExpandedKeys] = useState<string[]>([]);

  useEffect(() => {
    if (appId) {
      getMenuList(appId);
    }
  }, [appId]);

  // 递归处理 去除隐藏的页面
  const dealPage = (array: ApplicationMenu[]) => {
    let treeList: ApplicationMenu[] = [];
    array.forEach((item: ApplicationMenu) => {
      if (item.isVisible === VisibleType.SHOW) {
        let childrenList: ApplicationMenu[] = [];
        if (item.children && item.children.length > 0) {
          childrenList = dealPage(item.children);
        }
        treeList.push({ ...item, children: childrenList });
      }
    });
    return treeList;
  };

  const getMenuList = async (appID: string) => {
    const req: ListApplicationMenuReq = {
      applicationId: appID
    };
    const res = await listApplicationMenu(req);

    // 处理数据
    const resPageList = res && res.length > 0 ? dealPage(res) : [];
    const pageList = resPageList; //getMenuArr().concat(resPageList);
    const treeData = convertMenuToTreeData(pageList, initTreeItemWidth);
    setTreeData(treeData);

    // 如果菜单列表不为空，默认选中第一个菜单
    if (pageList && pageList.length > 0) {
      // 初始化页面没有curMenuId就处理第一个菜单为分组的情况 分组里没有页面的情况
      const curMenuObj = curMenuId ? findMenuWithParents(pageList, [], curMenuId) : findMenuWithParents(pageList, []);
      if (curMenuObj) {
        setExpandedKeys(curMenuObj.parentIds);
        setCurMenu(curMenuObj.node);
      }
    }
  };

  //返回当前 menu 对象和链路上所有父节点 code
  const findMenuWithParents = (
    nodes: ApplicationMenu[],
    accIds: string[],
    targetId?: string
  ): { node: ApplicationMenu; parentIds: string[] } | null => {
    for (const n of nodes) {
      if (targetId ? n.id === targetId : n.menuType === MenuType.PAGE) {
        return { node: n, parentIds: accIds };
      }

      if (n.children && n.children.length) {
        const res = findMenuWithParents(n.children, accIds.concat(n.menuCode), targetId);
        if (res) return res;
      }
    }
    return null;
  };

  // 更新当前路由的 curMenu（不刷新页面）
  const handleCurMenuUrl = (curMenuId: string) => {
    const sp = new URLSearchParams(location.search);
    sp.set('curMenu', String(curMenuId));
    const to = `${location.pathname}?${sp.toString()}`;
    navigate(to, { replace: true });
  };

  const convertMenuToTreeData = (menus: ApplicationMenu[], maxWidth: number): any[] => {
    return menus.map((menu) => ({
      key: menu.menuCode,
      title: menu.menuName,
      // title1: (
      //   <RuntimeMenuItem
      //     menuID={menu.id}
      //     menuIcon={menu.menuIcon}
      //     maxWidth={maxWidth}
      //     label={menu.menuName}
      //     onClick={() => {
      //       if (menu.menuType == MenuType.PAGE) {
      //         handleCurMenuUrl(menu.id);
      //         setCurMenu(menu);
      //       }
      //     }}
      //   />
      // ),
      children: menu.children ? convertMenuToTreeData(menu.children, maxWidth - cutTreeItemWidth) : []
    }));
  };

  const topCates = [
      {
        id: TASKMENU_TYPE.TASKINEEDTODO,
        isVisible: 1,
        menuCode: 'ineedtodo',
        menuIcon: 'ineedtodo-icon',
        menuName: '待我处理',
        menuSort: 1,
        menuType: 1,
        parentId: '0'
      },
      {
        id: TASKMENU_TYPE.TASKIHAVEDONE,
        isVisible: 1,
        menuCode: 'ihavedone',
        menuIcon: 'ihavedone-icon',
        menuName: '我已处理',
        menuSort: 2,
        menuType: 1,
        parentId: '0'
      },
      {
        id: TASKMENU_TYPE.TASKICREATED,
        isVisible: 1,
        menuCode: 'icreated',
        menuIcon: 'icreated-icon',
        menuName: '我创建的',
        menuSort: 3,
        menuType: 1,
        parentId: '0'
      },
      {
        id: TASKMENU_TYPE.TASKICOPIED,
        isVisible: 1,
        menuCode: 'icopied',
        menuIcon: 'icopied-icon',
        menuName: '抄送我的',
        menuSort: 4,
        menuType: 1,
        parentId: '0'
      },
      // {
      //   id: TASKMENU_TYPE.TASKTASKPROXY,
      //   isVisible: 1,
      //   menuCode: 'taskproxy',
      //   menuIcon: 'taskproxy-icon',
      //   menuName: '流程代理',
      //   menuSort: 5,
      //   menuType: 1,
      //   parentId: '0'
      // }
    ];
  const getGroupItem = (itemData:TreeNode, level:number = 0) => {
    if (!itemData.children || itemData.children.length === 0) {
      return <div style={{ display: 'flex', alignItems: 'center' }}>
                   <IconUser className='header-icon-notice'/>
                   {itemData.title}
                 </div>
    }
    return <Collapse
             className={styles[`treeItem-${level}`]}
             header={
               <div style={{ display: 'flex', alignItems: 'center' }}>
                 <IconUser className='header-icon-notice'/>
                 {itemData.title}
               </div>
             }
             value={itemData.key}
             content={
               itemData.children.map((child) => getGroupItem(child, level + 1))
             }
          />
  }

  return (
    <div className={styles.home}>
      <div className={styles.topOut}>
        <p style={{fontSize: '0.3rem', color: '#fff'}}>金小可，晚上好！</p>
        <Grid
          className={styles.grid}
          columns={4}
          data={
            topCates.map((item) => ({
              key: item.menuCode,
              img: <div className={styles[item.menuIcon]}></div>,
              title: <span style={{fontSize: '0.2rem'}}>{item.menuName}</span>,
              itemStyle: {padding: 0}
            }))}
        ></Grid>
      </div>
      <div className={styles.label}>
        应用菜单
      </div>
      {
        treeData.map((item) => getGroupItem(item))
      }
    </div>
  );
};

export default Home;
