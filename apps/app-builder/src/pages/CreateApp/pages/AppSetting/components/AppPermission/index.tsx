import { Empty, Menu, Message, Popconfirm } from '@arco-design/web-react';
import { IconClose, IconEdit, IconPlus, IconUser } from '@arco-design/web-react/icon';
import {
  createRole,
  deleteRole,
  getRoleUser,
  listRole,
  renameRole,
  RoleType,
  type AuthRoleUsersPageRespVO,
  type CreateRoleReq,
  type DeleteRoleReq,
  type GerRoleUserReq,
  type ListRoleReq,
  type RenameRoleReq,
  type Role
} from '@onebase/app';
import { useEffect, useState, type FC } from 'react';
import { useSearchParams } from 'react-router-dom';
import RoleInfo from '../Role';
import styles from './index.module.less';
import InputRoleName from './inputRoleName';

const MenuItem = Menu.Item;

enum EditOpacity {
  hide = 0,
  show = 1
}
// 应用权限
const AppPermission: FC = () => {
  const [searchParams] = useSearchParams();
  const appId = searchParams.get('appId') || '';

  const [activeTab, setActiveTab] = useState<string>('');
  const [showEmpty, setShowEmpty] = useState<boolean>(false);
  const [roleList, setRoleList] = useState<Role[]>([]);
  const [addRole, setAddRole] = useState<boolean>(false);
  const [updateRoleId, setUpdateRoleId] = useState<string>('');
  const [memberList, setMemberList] = useState<AuthRoleUsersPageRespVO[]>([]);
  const [memberTotal, setMemberTotal] = useState<number>(0);
  const [hoveredRoleId, setHoveredRoleId] = useState<string>(''); // 新增状态用于跟踪鼠标悬停的角色ID

  const adminData: Role | undefined = roleList?.find((role) => role.roleType === RoleType.ADMIN);
  const notAdminData: Role | undefined = roleList?.find(
    (role) => role.roleType !== RoleType.ADMIN && role.id === activeTab
  );

  useEffect(() => {
    getRoleList();
  }, []);

  useEffect(() => {
    console.log('showEmpty', showEmpty, 'activeTab', !activeTab);
  }, [showEmpty, activeTab]);
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
    setShowEmpty(false);
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
    } catch (error) {
      console.error('创建角色失败 error:', error);
    }
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
    } catch (error) {
      console.error('角色重命名失败 error:', error);
    }
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
    setActiveTab('');
    setShowEmpty(true);
  };

  if (!adminData?.id) return null;

  return (
    <div className={styles.AppPermission}>
      <div className={styles.left}>
        {adminData && adminData?.id && (
          <Menu className={styles.menu} defaultSelectedKeys={[adminData.id]} onClickMenuItem={handleSelectmenu}>
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
                    <MenuItem
                      className={styles.menuItem}
                      key={role.id}
                      onMouseEnter={() => setHoveredRoleId(role.id)} // 鼠标进入时设置hoveredRoleId
                      onMouseLeave={() => setHoveredRoleId('')} // 鼠标离开时清空hoveredRoleId
                    >
                      <div className={styles.custom}>
                        <IconUser className={styles.userIcon} />
                        {updateRoleId === role.id ? (
                          <InputRoleName
                            defaultValue={role.roleName}
                            onPressEnter={handlePressEnter}
                            onBlur={(e) => {
                              handlePressEnter(e);
                              setUpdateRoleId('');
                            }}
                          />
                        ) : (
                          <>
                            {role.roleName}
                            {role.roleType === RoleType.CUSTOM && (
                              <IconEdit
                                onClick={() => setUpdateRoleId(role.id)}
                                className={styles.editIcon}
                                style={{
                                  opacity: hoveredRoleId === role.id ? EditOpacity.show : EditOpacity.hide,
                                  transition: 'opacity 0.2s'
                                }}
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
          </Menu>
        )}
        {addRole && (
          <div style={{ padding: '0 8px' }}>
            <InputRoleName
              onPressEnter={handlePressEnter}
              onBlur={(e) => {
                handlePressEnter(e);
                setAddRole(false);
              }}
            />
          </div>
        )}
        <div
          className={styles.addRoleBox}
          onClick={() => {
            setAddRole(true);
          }}
        >
          <IconPlus className={styles.addRole} />
          添加角色
        </div>
      </div>
      <div className={styles.right}>
        {!activeTab || showEmpty ? (
          <>
            <Empty />
          </>
        ) : (
          <RoleInfo
            roleInfo={activeTab === adminData?.id ? adminData : notAdminData}
            memberList={memberList}
            memberTotal={memberTotal}
          />
        )}
      </div>
    </div>
  );
};

export default AppPermission;
