import { AppHeader } from '@/components/header';
import { useI18n } from '@/hooks/useI18n';
import { UserPermissionManager } from '@/utils/permission';
import { Input, Layout, Tree } from '@arco-design/web-react';
import { IconDown, IconSearch } from '@arco-design/web-react/icon';
import {
  menuSignal,
  MenuType,
  runtimeListApplicationBPMMenu,
  runtimeListApplicationMenu,
  VisibleType,
  type ApplicationMenu,
  type ListApplicationMenuReq
} from '@onebase/app';
import { TokenManager } from '@onebase/common';
import { getPermissionInfo } from '@onebase/platform-center';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams, useSearchParams } from 'react-router-dom';
import RuntimeMenuItem from './components/menuItem';
import PreviewContainer from './components/preview';
import './components/TaskCenter/style/taskSide.less';
import TaskCenterPage from './components/TaskCenter/TaskCenterPage';
import styles from './index.module.less';

const Sider = Layout.Sider;
const Content = Layout.Content;

/**
 * 树形数据节点接口
 */
interface TreeNode {
  key: string;
  value: string;
  title: string;
  isVisible: number;
  children?: TreeNode[];
}
/**
 * Runtime 运行时页面组件, 写于2025年8月13日凌晨两点
 * 作者：Mickey.Zhou
 * 此时此刻，我正在写这个组件，我也不知道我为什么要写这个组件，但是我知道为了OB3.0成功我必须写这个组件。
 */

const Runtime: React.FC = () => {
  useSignals();

  const navigate = useNavigate();
  const location = useLocation();

  //   const [appId, setAppId] = useState('');
  //   const [tenantId, setTenantId] = useState('');

  const [search] = useSearchParams();
  const curMenuId = search.get('curMenu');
  const { t } = useI18n();

  const [treeData, setTreeData] = useState<TreeNode[]>([]);

  const initTreeItemWidth = 155;
  const cutTreeItemWidth = 25;
  const { curMenu, setCurMenu } = menuSignal;
  const [expandedKeys, setExpandedKeys] = useState<string[]>([]);

  const [nickname, setNickname] = useState('U');

  //   useEffect(() => {
  //     // 从 window.location.hash 中解析 redirectURL，再从 redirectURL 解析 appId 和 tenantId
  //     const curAppId = getHashQueryParam('appId');
  //     const curTenantId = getHashQueryParam('tenantId');
  //     if (curAppId) {
  //       setAppId(curAppId);
  //     }

  //     if (curTenantId) {
  //       setTenantId(curTenantId);
  //     }
  //   }, []);

  const { appId, tenantId } = useParams();

  useEffect(() => {
    if (appId && tenantId) {
      TokenManager.setCurIdentifyId(`${appId}_${tenantId}`);
    } else {
      if (appId) {
        TokenManager.setCurIdentifyId(appId);
      }
      if (tenantId) {
        TokenManager.setCurIdentifyId(tenantId);
      }
    }
  }, [appId, tenantId]);

  useEffect(() => {
    if (appId) {
      getMenuList(appId);
    }
  }, [appId]);

  useEffect(() => {
    // TODO(多租户)：等马老师提供runtime的接口后打开
    // getUserInfo();
  }, []);

  const getUserInfo = async () => {
    const res = await getPermissionInfo();
    UserPermissionManager.setUserPermissionInfo(res);
    setNickname(res.user.nickname);
  };

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
    const res = await runtimeListApplicationMenu(req);
    console.log(res);
    const bpmRes = await runtimeListApplicationBPMMenu(req);
    console.log(bpmRes);
    let bpmData: any[] = [];
    if (bpmRes && bpmRes.length > 0) {
      bpmData = dealPage(bpmRes);
    }
    // 处理数据
    const resPageList: any[] = res && res.length > 0 ? dealPage(res) : [];
    console.log(resPageList);
    const pageList: any[] = bpmData.concat(resPageList);

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
      title: (
        <RuntimeMenuItem
          menuID={menu.id}
          menuIcon={menu.menuType === MenuType.BPM ? menu.menuCode : menu.menuIcon}
          maxWidth={maxWidth}
          label={menu.menuName}
          onClick={() => {
            if (menu.menuType == MenuType.PAGE || menu.menuType == MenuType.BPM) {
              handleCurMenuUrl(menu.id);
              setCurMenu(menu);
            }
          }}
        />
      ),
      children: menu.children ? convertMenuToTreeData(menu.children, maxWidth - cutTreeItemWidth) : []
    }));
  };

  return (
    <Layout className={styles.runtimePage}>
      <AppHeader />
      <Layout>
        <Sider className={styles.sider}>
          <div className={styles.siderHeader}>
            <div className={styles.siderHeaderInput}>
              <Input allowClear suffix={<IconSearch />} placeholder={t('app.searchPlaceHolder')} />
            </div>
          </div>
          <Tree
            blockNode
            draggable
            treeData={treeData}
            selectedKeys={[curMenu.value?.menuCode!]}
            expandedKeys={expandedKeys}
            onExpand={setExpandedKeys}
            className={`menuTree ${styles.tree}`}
            showLine={false}
            icons={{
              switcherIcon: <IconDown />,
              dragIcon: null
            }}
            actionOnClick={'expand'}
            style={{
              width: '200px',
              overflow: 'hidden',
              boxSizing: 'border-box',
              padding: '4px 8px',
              display: 'flex',
              flexDirection: 'column',
              gap: 8
            }}
          />
        </Sider>
        <Content className={styles.content}>
          {curMenu?.value?.menuCode && curMenu?.value?.menuCode?.indexOf('TASK-') >= 0 ? (
            <TaskCenterPage curMenuCode={curMenu.value.menuCode} />
          ) : (
            <div className={styles.contentBody}>
              <PreviewContainer menuId={curMenu.value?.id || ''} runtime={true} />
            </div>
          )}
        </Content>
      </Layout>
    </Layout>
  );
};

export default Runtime;
