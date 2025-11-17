import AvatarSVG from '@/assets/images/avatar.svg';
import { useI18n } from '@/hooks/useI18n';
import { menuSignal } from '@/store/menu';
import { UserPermissionManager } from '@/utils/permission';
import { Dropdown, Input, Layout, Menu, Tree } from '@arco-design/web-react';
import { IconDown, IconSearch } from '@arco-design/web-react/icon';
import {
  listApplicationMenu,
  MenuType,
  VisibleType,
  type ApplicationMenu,
  type ListApplicationMenuReq
} from '@onebase/app';
import { TokenManager } from '@onebase/common';
import { getPermissionInfo, runtimeLogout } from '@onebase/platform-center';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import RuntimeMenuItem from './components/menuItem';
import PreviewContainer from './components/preview';
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
  const { appId } = useParams<{ appId?: string }>();
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
    const pageList = res && res.length > 0 ? dealPage(res) : [];

    const treeData = convertMenuToTreeData(pageList, initTreeItemWidth);
    setTreeData(treeData);

    // 如果菜单列表不为空，默认选中第一个菜单
    if (pageList && pageList.length > 0) {
      // 处理第一个菜单为分组的情况 分组里没有页面的情况
      const currentMenu = dealMenu(pageList);
      if (currentMenu) {
        // 默认展开当前页面
        if (currentMenu.parentId) {
          const parentCode = dealCode(currentMenu.parentId, pageList);
          if (parentCode) {
            setExpandedKeys((prev) => [...prev, parentCode]);
          }
        }
        setCurMenu(currentMenu);
      }
    }
  };
  // 递归处理 获取第一个页面
  const dealMenu = (array: ApplicationMenu[]) => {
    // menu.menuType == MenuType.PAGE
    for (let item of array) {
      if (item.menuType == MenuType.PAGE) {
        return item;
      } else if (item.children && item.children.length) {
        return dealMenu(item.children);
      }
    }
  };

  const dealCode = (id: string, array: ApplicationMenu[]): any => {
    for (let item of array) {
      if (item.id == id) {
        return item.menuCode;
      } else if (item.children && item.children.length) {
        return dealMenu(item.children);
      }
    }
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
              setCurMenu(menu);
            }
          }}
        />
      ),
      children: menu.children ? convertMenuToTreeData(menu.children, maxWidth - cutTreeItemWidth) : []
    }));
  };

  // 登出处理
  const handleLogout = async () => {
    // 清除 token
    await runtimeLogout();
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

            <div className={styles.contentBody}>
              <PreviewContainer menuId={curMenu.value?.id || ''} runtime={true} />
            </div>
          </Content>
        </Layout>
      </Layout>
    </div>
  );
};

export default Runtime;
