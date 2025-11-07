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
import ellipse from '../../../../assets/images/home/ellipse.svg';
import curb from '../../../../assets/images/home/curb.png';
import topcates1 from '../../../../assets/images/home/topcates-1.svg';
import topcates2 from '../../../../assets/images/home/topcates-2.svg';
import topcates3 from '../../../../assets/images/home/topcates-3.svg';
import topcates4 from '../../../../assets/images/home/topcates-4.svg';

import styles from './index.module.less';
import AppsList from '../appsList';

interface TreeNode {
  key: string;
  value: string;
  title: string;
  isVisible: number;
  children?: TreeNode[];
}

const Home: React.FC<{ nickname: string }> = ({ nickname }) => {
  useSignals();

  const navigate = useNavigate();
  const location = useLocation();
  const { appId } = useParams<{ appId?: string }>();
  const [search] = useSearchParams();
  const curMenuId = search.get('curMenu');

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
  ];

  // 早上好，中午好，下午好，晚上好，根据当前时间判断
  const getGreekString = () => {
    const currentHour = new Date().getHours();
    let greekString = '';
    if (currentHour < 12) {
      greekString = '早上好！';
    } else if (currentHour < 13) {
      greekString = '中午好！';
    } else if (currentHour < 18) {
      greekString = '下午好！';
    } else {
      greekString = '晚上好！';
    }
    return nickname + '，' + greekString;
  }

  return (
    <div className={styles.home}>
      <div className={styles.topOut}>
        <img className={styles.ellipse} src={ellipse} alt="" />
        <img className={styles.curb} src={curb} alt="" />
        <p className={styles.greekString}>{getGreekString()}</p>
        <Grid
          className={styles.grid}
          columns={4}
          data={
            topCates.map((item, index) => ({
              key: item.menuCode,
              img: <img className={styles.topcatesImg} src={[topcates1, topcates2, topcates3, topcates4][index]} alt="" />,
              title: <span className={styles.topcatesTitle}>{item.menuName}</span>,
              itemStyle: { padding: 0 }
            }))}
        ></Grid>
      </div>
      <AppsList treeData={treeData} />
    </div>
  );
};

export default Home;
