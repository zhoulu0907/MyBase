import { useI18n } from '@/hooks/useI18n';
import { getHashQueryParam } from '@/utils/router';
import { Input, Layout, Tree } from '@arco-design/web-react';
import { IconSearch } from '@arco-design/web-react/icon';
import { listApplicationMenu, MenuType, type ApplicationMenu, type ListApplicationMenuReq } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import RuntimeMenuItem from './components/menuItem';
import PreviewContainer from './components/preview';
import styles from './index.module.less';

interface RuntimeProps {}

const Sider = Layout.Sider;
const Content = Layout.Content;

/**
 * 树形数据节点接口
 */
interface TreeNode {
  key: string;
  value: string;
  title: string;
  children?: TreeNode[];
}

/**
 * Runtime 运行时页面组件, 写于2025年8月13日凌晨两点
 * 作者：Mickey.Zhou
 * 此时此刻，我正在写这个组件，我也不知道我为什么要写这个组件，但是我知道为了OB3.0成功我必须写这个组件。
 */
const Runtime: React.FC<RuntimeProps> = ({}) => {
  const { t } = useI18n();

  const [treeData, setTreeData] = useState<TreeNode[]>([]);

  const initTreeItemWidth = 155;
  const cutTreeItemWidth = 25;
  const [curMenu, setCurMenu] = useState<ApplicationMenu>();

  useEffect(() => {
    const appId = getHashQueryParam('appId');
    if (appId) {
      getMenuList(appId);
    }
  }, [window.location.hash]);

  const getMenuList = async (appID: string) => {
    const req: ListApplicationMenuReq = {
      applicationId: appID
    };
    const res = await listApplicationMenu(req);

    const treeData = convertMenuToTreeData(res, initTreeItemWidth, true);
    setTreeData(treeData);

    // 如果菜单列表不为空，默认选中第一个菜单
    if (res && res.length > 0) {
      // TODO(Mickey): 处理第一个菜单为分组的情况
      setCurMenu(res[0]);
    }
  };

  const convertMenuToTreeData = (menus: ApplicationMenu[], maxWidth: number, showOption: boolean = false): any[] => {
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

  return (
    <div className={styles.runtimePage}>
      <Layout style={{ height: '100%' }}>
        <Layout>
          {/* <Sider style={{ width: 225 }}> */}
          <Sider className={styles.sider}>
            <div className={styles.siderHeader}>
              <Input
                style={{
                  width: 120,
                  border: '1px solid #dedede',
                  borderRadius: 3
                }}
                allowClear
                suffix={<IconSearch />}
                placeholder={t('common.search')}
              />
            </div>

            <Tree
              blockNode
              draggable
              treeData={treeData}
              className={styles.tree}
              showLine={false}
              icons={{
                switcherIcon: null,
                dragIcon: null
              }}
              actionOnClick={'expand'}
              style={{
                width: '200px',
                overflow: 'hidden',
                boxSizing: 'border-box'
              }}
            />
          </Sider>
          <Content className={styles.content}>
            {curMenu?.id && (
              <div className={styles.contentHeader}>
                <div className={styles.contentTitle}>{curMenu?.menuName}</div>
              </div>
            )}
            <div className={styles.contentBody}>
              <PreviewContainer menuId={curMenu?.id || ''} runtime={true} />
            </div>
          </Content>
        </Layout>
      </Layout>
    </div>
  );
};

export default Runtime;
