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
  PageType,
  type Role,
  type ApplicationMenu,
  type ListApplicationMenuReq,
  type AuthRoleUsersPageRespVO
} from '@onebase/app';
import styles from './index.module.less';
import { debounce } from 'lodash-es';
import DynamicIcon from '@/components/DynamicIcon';
import { menuIconList } from '@/components/MenuIcon/const';

const MenuItem = Menu.Item;
const SubMenu = Menu.SubMenu;
const TabPane = Tabs.TabPane;
const RadioGroup = Radio.Group;
const InputSearch = Input.Search;

interface IProps {
  roleInfo: Role | undefined;
  memberList?: AuthRoleUsersPageRespVO[];
  memberTotal?: number;
}

type ManagerType = 'permission' | 'members';

// 角色面板
const RoleInfo = (props: IProps) => {
  const { roleInfo, memberList, memberTotal } = props;
  const { curAppId } = useAppStore();

  const [activeTab, setActiveTab] = useState('1'); // tabs
  const [activeMenuId, setActiveMenuId] = useState(''); // 选中菜单id
  const [activeMenuPageType, setActiveMenuPageType] = useState<number>(); // 选中菜单的Page Type
  const [managerType, setManagerType] = useState<ManagerType>('permission'); // 权限管理 or 成员管理
  const [menuList, setMuneList] = useState<ApplicationMenu[]>(); //菜单数据
  const [menuLoading, setMuneLoading] = useState<boolean>(false);
  const [openKeys, setOpenKeys] = useState<string[]>([]); // 展开的菜单项key数组

  useEffect(() => {
    const isAdmin = roleInfo?.roleType === RoleType.ADMIN;
    const type = isAdmin ? 'members' : 'permission';
    setManagerType(type);
    if (!isAdmin) {
      getApplicationMenu();
    }
  }, [roleInfo]);

  /* 获取菜单 */
  const getApplicationMenu = async (keywords?: string) => {
    setMuneLoading(true);
    const params: ListApplicationMenuReq = {
      applicationId: curAppId,
      name: keywords
    };
    const res = await listApplicationMenu(params);
    console.log('获取菜单 res:', res);
    setMuneList(res);
    setMuneLoading(false);

    const firstPage = findFirstPage(res);
    handleSelectMenu(firstPage.id);
    setActiveMenuPageType(firstPage.pagesetType);
  };

  /* 选择菜单获取权限数据 */
  const handleSelectMenu = async (value: string) => {
    setActiveTab('1');
    setActiveMenuId(value);
    // await getApplicationPermission(value);
  };

  // 处理菜单展开/收起
  const handleOpenChange = (keys: string[]) => {
    setOpenKeys(keys);
  };

  const findFirstPage: ApplicationMenu = (nodes: ApplicationMenu[]) =>
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
          <MenuItem key={menu.id} style={{ display: 'flex', alignItems: 'center' }} onClick={() => handleClick(menu)}>
            <DynamicIcon
              IconComponent={menuIconList.find((icon) => icon.code === menu.menuIcon)?.icon}
              theme="outline"
              size="18"
              fill={menu.id === activeMenuId ? 'rgb(var(--primary-6))' : '#333'}
              style={{ marginRight: 4 }}
            />
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

  const handleClick = (menu: any) => {
    setActiveMenuPageType(menu.pagesetType);
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
        <UserTable roleInfo={roleInfo} memberList={memberList} memberTotal={memberTotal} />
      ) : (
        <>
          {roleInfo?.roleType !== RoleType.ADMIN && (
            <div className={styles.user}>
              <div className={styles.left}>
                <div className={styles.search}>
                  <InputSearch placeholder="搜索分组或页面" onChange={debouncedSearch} />
                </div>
                <div className={styles.menu}>
                  <Spin className={styles.loading} loading={menuLoading}>
                    <Menu
                      openKeys={openKeys}
                      selectedKeys={[activeMenuId]}
                      defaultSelectedKeys={[findFirstPage(menuList)?.id]}
                      onClickMenuItem={handleSelectMenu}
                      onClickSubMenu={(_, openKeys) => handleOpenChange(openKeys)}
                    >
                      {renderMenuItems(menuList || [])}
                    </Menu>
                  </Spin>
                </div>
              </div>

              {roleInfo && roleInfo.id && (
                <div className={styles.right}>
                  <Tabs defaultActiveTab="1" destroyOnHide activeTab={activeTab} onChange={setActiveTab}>
                    <TabPane key="1" title="功能权限">
                      <FuncPermission
                        appId={curAppId}
                        menuId={activeMenuId}
                        roleId={roleInfo.id}
                        activeMenuPageType={activeMenuPageType}
                      />
                    </TabPane>
                    {activeMenuPageType !== PageType.WORKBENCH && (
                      <TabPane key="2" title="数据权限">
                        <DataPermission
                          appId={curAppId}
                          menuId={activeMenuId}
                          roleId={roleInfo.id}
                          roleType={roleInfo.roleType}
                        />
                      </TabPane>
                    )}
                    {activeMenuPageType !== PageType.WORKBENCH && (
                      <TabPane key="3" title="字段权限">
                        <FieldPermission appId={curAppId} menuId={activeMenuId} roleId={roleInfo.id} />
                      </TabPane>
                    )}
                  </Tabs>
                </div>
              )}
            </div>
          )}
          {/* TODO */}
          {!activeMenuId && (
            <div className={styles.permissionEmpty}>
              <IconEmpty fontSize={50} />
              暂无页面{activeTab === '1' ? '功能' : activeTab === '2' ? '数据' : '字段'}权限，请先添加页面
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default RoleInfo;
