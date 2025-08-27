import { useState, useEffect, type FC } from 'react';
import { useSearchParams } from 'react-router-dom';
import { Menu, Popconfirm, Message } from '@arco-design/web-react';
import { IconEdit, IconPlus, IconUser, IconClose } from '@arco-design/web-react/icon';
import {
  listRole,
  createRole,
  deleteRole,
  RoleType,
  renameRole,
  type Role,
  type ListRoleReq,
  type CreateRoleReq,
  type DeleteRoleReq,
  type RenameRoleReq
} from '@onebase/app';
import RoleInfo from '../Role';
import InputRoleName from './inputRoleName';
import styles from './index.module.less';

const MenuItem = Menu.Item;

// 应用权限
const AppPermission: FC = () => {
  const [searchParams] = useSearchParams();
  const appId = searchParams.get('appId') || '';

  const [activeTab, setActiveTab] = useState<string>('');
  const [roleList, setRoleList] = useState<Role[]>([]);
  const [addRole, setAddRole] = useState<boolean>(false);
  const [updateRoleId, setUpdateRoleId] = useState<string>('');

  const adminData: Role | undefined = roleList?.find((role) => role.roleType === RoleType.ADMIN);
  const notAdminData: Role | undefined = roleList?.find(
    (role) => role.roleType !== RoleType.ADMIN && role.id === activeTab
  );

  useEffect(() => {
    getRoleList();
  }, []);

  // 获取角色列表
  const getRoleList = async () => {
    const params: ListRoleReq = {
      applicationId: appId
    };
    const res = await listRole(params);
    setRoleList(res || []);
    setActiveTab(res[0].id);
  };

  const handleSelectmenu = (val: string) => {
    if (val === 'add') {
      setAddRole(true);
      return;
    }
    setActiveTab(val);
  };

  // 回车新建自定义角色
  const handlePressEnter = async (e: any) => {
    const name = e.target.value;
    if (!name) return;
    /* 角色重命名 */
    if (updateRoleId) {
      handleRenameRole(name);
    } else if (addRole) {
      handleAddRole(name);
    }
  };

  /* 新建角色 */
  const handleAddRole = async (roleName: string) => {
    try {
      const params: CreateRoleReq = {
        applicationId: appId,
        roleName
      };
      const res = await createRole(params);
      setAddRole(false);
      setActiveTab(res.id);
      Message.success('角色创建成功');
      await getRoleList();
    } catch (error) {}
  };

  /* 角色重命名 */
  const handleRenameRole = async (roleName: string) => {
    try {
      const params: RenameRoleReq = {
        id: updateRoleId,
        name: roleName
      };
      const res = await renameRole(params);
      setAddRole(false);
      setActiveTab(res.id);
      setUpdateRoleId('');
      Message.success('角色重命名成功');
      await getRoleList();
    } catch (error) {}
  };

  /* 删除角色 */
  const handleDeleteRole = async (id: string) => {
    const params: DeleteRoleReq = {
      roleId: id
    };
    const res = await deleteRole(params);
    if (res) {
      Message.success('删除成功');
    }
    await getRoleList();
  };

  if (!adminData?.id) return null;

  return (
    <div className={styles.AppPermission}>
      <div className={styles.left}>
        <Menu className={styles.menu} defaultSelectedKeys={[adminData?.id!]} onClickMenuItem={handleSelectmenu}>
          {
            <div className={styles.user} key={adminData?.id}>
              <label>管理员角色</label>
              <MenuItem key={adminData?.id || ''}>
                <IconUser />
                {adminData?.roleName}
              </MenuItem>
            </div>
          }
          <div>
            <label>用户角色</label>
            {roleList
              ?.filter((role) => role.roleType !== RoleType.ADMIN)
              .map((role) => {
                return (
                  <MenuItem className={styles.menuItem} key={role.id}>
                    <div className={styles.custom}>
                      <IconUser className={styles.userIcon} />
                      {updateRoleId === role.id ? (
                        <InputRoleName
                          defaultValue={role.roleName}
                          onPressEnter={handlePressEnter}
                          onBlur={() => setUpdateRoleId('')}
                        />
                      ) : (
                        <>
                          {role.roleName}
                          {role.roleType === RoleType.CUSTOM && (
                            <IconEdit
                              onClick={() => setUpdateRoleId(role.id)}
                              style={{ marginLeft: 4, marginRight: 0 }}
                            />
                          )}
                        </>
                      )}
                    </div>
                    <Popconfirm
                      focusLock
                      title="删除角色"
                      content="确定要删除这个角色吗？"
                      onOk={() => handleDeleteRole(role.id)}
                    >
                      {role.roleType === RoleType.CUSTOM && <IconClose className={styles.deleteIcon} />}
                    </Popconfirm>
                  </MenuItem>
                );
              })}
          </div>
          {addRole && (
            <MenuItem key="ipput">
              <IconUser className={styles.iconRight4} />
              <InputRoleName onPressEnter={handlePressEnter} onBlur={() => setAddRole(false)} />
            </MenuItem>
          )}
          <MenuItem key="add" className={styles.add}>
            <IconPlus style={{ color: 'rgb(var(--primary-6))' }} />
            添加角色
          </MenuItem>
        </Menu>
      </div>
      <div className={styles.right}>
        <RoleInfo roleInfo={activeTab === adminData?.id ? adminData : notAdminData} />
      </div>
    </div>
  );
};

export default AppPermission;
