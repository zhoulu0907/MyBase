import { useCallback, useEffect, useState } from 'react';
import { Input, Menu, Tabs, Radio, Spin } from '@arco-design/web-react';
import { IconEmpty } from '@arco-design/web-react/icon';
import { useAppStore } from '@/store';
import FuncPermission from '../FuncPermission';
import FieldPermission from '../FieldPermission';
import DataPermission from '../DataPermission';
import UserTable from '../UserTable';
import {
  RoleType,
  listApplicationMenu,
  MenuType,
  type Role,
  type ApplicationMenu,
  type ListApplicationMenuReq
} from '@onebase/app';
import styles from './index.module.less';
import { debounce } from 'lodash-es';

const MenuItem = Menu.Item;
const SubMenu = Menu.SubMenu;
const TabPane = Tabs.TabPane;
const RadioGroup = Radio.Group;
const InputSearch = Input.Search;

interface IProps {
  roleInfo: Role | undefined;
}

type ManagerType = 'permission' | 'members';

// 角色面板
const RoleInfo = (props: IProps) => {
  const { roleInfo } = props;
  const { curAppId } = useAppStore();

  const [activeTab, setActiveTab] = useState('1'); // tabs
  const [activeMenuId, setActiveMenuId] = useState(''); // 选中菜单id
  const [managerType, setManagerType] = useState<ManagerType>('permission'); // 权限管理 or 成员管理
  const [menuList, setMuneList] = useState<ApplicationMenu[]>(); //菜单数据
  const [menuLoading, setMuneLoading] = useState<boolean>(false);

  useEffect(() => {
    const isAdmin = roleInfo?.roleType === RoleType.ADMIN;
    const type = isAdmin ? 'members' : 'permission';
    setManagerType(type);
    !isAdmin && getApplicationMenu();
  }, [roleInfo]);

  /* 获取菜单 */
  const getApplicationMenu = async (keywords?: string) => {
    setMuneLoading(true);
    const params: ListApplicationMenuReq = {
      applicationId: curAppId,
      name: keywords
    };
    const res = await listApplicationMenu(params);
    setMuneList(res);
    setMuneLoading(false);
    handleSelectMenu(findFirstPage(res).id);
  };

  /* 选择菜单获取权限数据 */
  const handleSelectMenu = async (value: string) => {
    setActiveTab('1');
    setActiveMenuId(value);
    // await getApplicationPermission(value);
  };

  const firstGroupIndex = menuList?.findIndex((menu: ApplicationMenu) => menu.menuType === MenuType.GROUP); // 第一个菜单为分组时的索引
  const firstGroupCode = (firstGroupIndex === 0 && menuList?.[firstGroupIndex]?.id) || ''; // 第一个菜单为分组时的code

  const findFirstPage: any = (nodes: ApplicationMenu[]) =>
    nodes?.reduce((found, node) => {
      if (found) return found;
      if (Number(node.menuType) === MenuType.PAGE) return node;
      return node.children ? findFirstPage(node.children) : undefined;
    }, undefined);

  /* 菜单渲染 */
  const renderMenuItems = (data: ApplicationMenu[]) => {
    return data.map((menu: ApplicationMenu) => {
      const hasChildren = menu.children && menu.children.length > 0;
      if (!hasChildren) {
        return (
          <MenuItem key={menu.id}>
            <i className={`iconfont ${menu.menuIcon}`} style={{ marginRight: 4 }} />
            {menu.menuName}
          </MenuItem>
        );
      }
      return (
        <SubMenu key={menu.id} title={menu.menuName}>
          {renderMenuItems(menu.children)}
        </SubMenu>
      );
    });
  };

  const debouncedSearch = useCallback(
    debounce((value) => {
      getApplicationMenu(value);
    }, 500),
    []
  );

  useEffect(() => {
    return () => debouncedSearch.cancel();
  }, [debouncedSearch]);

  return (
    <div className={styles.userWrapper}>
      <div className={styles.header}>
        <div className={styles.headerTitle}>{roleInfo?.roleName || '-'}</div>
        {roleInfo?.roleType !== RoleType.ADMIN && (
          <RadioGroup type="button" name="lang" defaultValue="permission" value={managerType} onChange={setManagerType}>
            <Radio value="permission">权限管理</Radio>
            <Radio value="members">成员管理</Radio>
          </RadioGroup>
        )}
      </div>

      {managerType === 'members' ? (
        <UserTable roleInfo={roleInfo} />
      ) : (
        <>
          {roleInfo?.roleType !== RoleType.ADMIN && (
            <div className={styles.user}>
              <div className={styles.left}>
                <div className={styles.search}>
                  <InputSearch placeholder="搜索分组或页面" onChange={debouncedSearch} />
                </div>
                <div className={styles.menu}>
                  {menuList?.length === 0 ? (
                    <div className={styles.menuEmpty}>
                      <IconEmpty fontSize={36} />
                      暂无数据
                    </div>
                  ) : (
                    <Spin className={styles.loading} loading={menuLoading}>
                      <Menu
                        openKeys={[firstGroupCode]}
                        selectedKeys={[activeMenuId]}
                        defaultSelectedKeys={[findFirstPage(menuList)?.id]}
                        onClickMenuItem={handleSelectMenu}
                      >
                        {renderMenuItems(menuList || [])}
                      </Menu>
                    </Spin>
                  )}
                </div>
              </div>

              <div className={styles.right}>
                <Tabs defaultActiveTab="1" destroyOnHide activeTab={activeTab} onChange={setActiveTab}>
                  <TabPane key="1" title="功能权限">
                    <FuncPermission appId={curAppId} menuId={activeMenuId} roleId={roleInfo?.id!} />
                  </TabPane>
                  <TabPane key="2" title="数据权限">
                    <DataPermission appId={curAppId} menuId={activeMenuId} roleId={roleInfo?.id!} />
                  </TabPane>
                  <TabPane key="3" title="字段权限">
                    <FieldPermission appId={curAppId} menuId={activeMenuId} roleId={roleInfo?.id!} />
                  </TabPane>
                </Tabs>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default RoleInfo;
