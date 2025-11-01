import AvatarSVG from '@/assets/images/avatar.svg';
import { useI18n } from '@/hooks/useI18n';
import { UserPermissionManager } from '@/utils/permission';
import { Dropdown, Input, Layout, Menu, Tree } from '@arco-design/web-react';
import { IconDown, IconSearch } from '@arco-design/web-react/icon';
import {
  listApplicationMenu,
  menuSignal,
  MenuType,
  TASKMENU_TYPE,
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
  const { appId } = useParams<{ appId?: string }>();
  const [search] = useSearchParams();
  const curMenuId = search.get('curMenu');
  const { t } = useI18n();

  const [treeData, setTreeData] = useState<TreeNode[]>([]);

  const initTreeItemWidth = 155;
  const cutTreeItemWidth = 25;
  const { curMenu, setCurMenu } = menuSignal;
  const [expandedKeys, setExpandedKeys] = useState<string[]>([]);

  const [nickname, setNickname] = useState('U');

  useEffect(() => {
    if (appId) {
      getMenuList(appId);
    }
  }, [appId]);

  useEffect(() => {
    getUserInfo();
  }, []);

  const getUserInfo = async () => {
    const res = await getPermissionInfo();
    UserPermissionManager.setUserPermissionInfo(res);
    // userPermissionSignal.setPermissionInfo(res);
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
    const res = await listApplicationMenu(req);

    // 处理数据
    const resPageList = res && res.length > 0 ? dealPage(res) : [];
    const pageList = getMenuArr().concat(resPageList);
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
          menuIcon={menu.menuIcon}
          maxWidth={maxWidth}
          label={menu.menuName}
          onClick={() => {
            if (menu.menuType == MenuType.PAGE) {
              handleCurMenuUrl(menu.id);
              setCurMenu(menu);
            }
          }}
        />
      ),
      children: menu.children ? convertMenuToTreeData(menu.children, maxWidth - cutTreeItemWidth) : []
    }));
  };

  function getMenuArr() {
    return [
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
      {
        id: TASKMENU_TYPE.TASKTASKPROXY,
        isVisible: 1,
        menuCode: 'taskproxy',
        menuIcon: 'taskproxy-icon',
        menuName: '流程代理',
        menuSort: 5,
        menuType: 1,
        parentId: '0'
      }
    ];
  }

  // 登出处理
  const handleLogout = () => {
    // 清除 token
    TokenManager.clearToken();
    UserPermissionManager.clearUserPermissionInfo();
    // 跳转到登录页
    navigate('/login', { replace: true });
  };
  // 用户菜单
  const userMenu = (
    <Menu>
      <Menu.Item key="logout" onClick={handleLogout} style={{ color: '#FF0000' }}>
        {t('header.logout')}
      </Menu.Item>
    </Menu>
  );

  return (
    <div className={styles.runtimePage}>
      <Layout style={{ height: '100%' }}>
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
            {curMenu.value?.id && (
              <div className={styles.contentHeader}>
                <div className={styles.contentTitle}>{curMenu.value?.menuName}</div>
                <div className={styles.userInfo}>
                  {nickname || '未登录'}

                  <Dropdown droplist={userMenu} position="bottom">
                    <div className={styles.userDropdown}>
                      <img src={AvatarSVG} alt="avatar" />
                    </div>
                  </Dropdown>
                </div>
              </div>
            )}
            {curMenu?.value?.id && curMenu?.value?.id?.indexOf('TASK-') >= 0 ? (
              <TaskCenterPage curMenuId={curMenu.value.id} />
            ) : (
              <div className={styles.contentBody}>
                <PreviewContainer menuId={curMenu.value?.id || ''} runtime={true} />
              </div>
            )}

            {/* <div className={styles.contentBody}>
              <PreviewContainer menuId={curMenu.value?.id || ''} runtime={true} />
            </div> */}
          </Content>
        </Layout>
      </Layout>
    </div>
  );
};

export default Runtime;
