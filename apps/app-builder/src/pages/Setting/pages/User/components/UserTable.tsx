import TablePagination from '@/components/TablePagination';
import ActionButtons from '@/components/ActionButtons';
import { PermissionButton as Button } from '@/components/PermissionControl';
import PlaceholderPanel from '@/components/PlaceholderPanel';
import StatusTag, { getStatusLabel } from '@/components/StatusTag';
import UserProfileAvatar from '@/components/UserProfileAvatar';
import { isSystemUser } from '@/utils';
import { Dropdown, Input, Menu, Message, Modal, Pagination, Select, Space, Table, Tag } from '@arco-design/web-react';
import { /* IconDownload, IconUpload, */ IconMoreVertical, IconPlus } from '@arco-design/web-react/icon';
import { type AuthRoleUsersPageRespVO } from '@onebase/app';
import {
  TENANT_USER_PERMISSION as ACTIONS,
  AddMembers,
  getPublicKey,
  hasAllPermissions,
  hasPermission,
  sm2Encrypt
} from '@onebase/common';
import type { PageParam, UpdateAdminOrDirectorReq, UserVO } from '@onebase/platform-center';
import {
  deleteUser,
  getDept,
  getSimpleUser,
  getUserListByName,
  getUserPage,
  PlatformTenantStatus,
  resetUserPassword,
  StatusEnum,
  updateAdminOrDirector,
  updateUserStatus
} from '@onebase/platform-center';
import { debounce } from 'lodash-es';
import { useCallback, useEffect, useState } from 'react';
import s from '../index.module.less';
import PasswordModal from './PasswordModal';
import UserFormModal from './UserFormModal';

interface DataItem {
  id: string;
  name: string;
  children?: DataItem[];
  [key: string]: any;
}

interface UserTableProps {
  selectedDeptId?: string;
  deptTree: DataItem[]; // 部门树数据
  deptLoading: boolean; // 部门数据加载状态
  onRefreshDept: () => void;
}

type UserRecord = Pick<UserVO, 'id' | 'username' | 'nickname'> & Partial<UserVO>;

interface SelectOptions {
  label: string;
  value: string | number;
}

const statusOptions: SelectOptions[] = [
  {
    label: '全部状态',
    value: ''
  },
  {
    label: '已启用',
    value: PlatformTenantStatus.enabled
  },
  {
    label: '已禁用',
    value: PlatformTenantStatus.disabled
  }
];

export enum UserRole {
  ADMIN = 'admin',
  DIRECTOR = 'director'
}

export const RoleLabelMap: Record<UserRole, string> = {
  [UserRole.ADMIN]: '部门接口人',
  [UserRole.DIRECTOR]: '主管'
};

export default function UserTable({
  selectedDeptId = undefined,
  deptTree,
  deptLoading,
  onRefreshDept
}: UserTableProps) {
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [status, setStatus] = useState<PlatformTenantStatus | ''>('');
  const [search, setSearch] = useState('');
  const [editingUser, setEditingUser] = useState<UserRecord | undefined>();
  const [data, setData] = useState<UserRecord[]>([]);
  const [total, setTotal] = useState(0);
  const [detailUser, setDetailUser] = useState<UserRecord | undefined>();
  const [resetPasswordUser, setResetPasswordUser] = useState<UserRecord | undefined>();

  const [userData, setUserData] = useState<{ userList: any[] }>();
  const [memberLoading, setMemberLoading] = useState<boolean>(false);
  const [selectedMembers, setSelectedMembers] = useState<any[]>([]);

  const [userModalVisible, setUserModalVisible] = useState(false);
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [resetPasswordModalVisible, setResetPasswordModalVisible] = useState(false);
  const [importModalVisible, setImportModalVisible] = useState(false); // 导入
  const [managerTypeModalVisible, setManagerTypeModalVisible] = useState<UserRole | null>(null); // 设置主管 or 管理员
  const [isMultiple, setIsMultiple] = useState<boolean>(false);
  // 查询用户列表
  const getUserList = useCallback(
    async (searchValue?: string) => {
      const params: PageParam = {
        pageNo: page,
        pageSize,
        status
      };
      if (selectedDeptId) params.deptId = selectedDeptId;
      if (searchValue) params.nickname = searchValue;
      const res = await getUserPage(params);
      setData(res.list || []);
      setTotal(res.total || 0);
    },
    [page, pageSize, selectedDeptId, status]
  );

  const debouncedSearch = useCallback(
    debounce((value: string) => {
      getUserList(value);
    }, 300),
    [getUserList]
  );

  useEffect(() => {
    getUserList();
  }, [selectedDeptId, page, pageSize, status]);

  const handleEdit = (record: UserRecord) => {
    setEditingUser(record);
    setUserModalVisible(true);
  };

  const handleCreate = () => {
    setEditingUser(undefined);
    setUserModalVisible(true);
  };

  const handleImport = () => {
    setImportModalVisible(true);
    // todo import
  };

  const handleExport = async () => {
    // try {
    //   const params = {
    //     pageNo: 1,
    //     pageSize: 10
    //   }
    //   await exportUser('用户数据', params);
    //   Message.success('用户导出成功');
    // } catch (error) {
    // }
  };

  // 搜索
  const handleSearch = useCallback(
    (value: string) => {
      setPage(1);
      setSearch(value);
      debouncedSearch(value);
    },
    [debouncedSearch]
  );

  const handleModalOk = () => {
    setUserModalVisible(false);
    getUserList();
  };

  // 重置密码
  const handleResetPassword = (record: UserRecord) => {
    setResetPasswordUser(record);
    setResetPasswordModalVisible(true);
  };

  const handleResetPasswordOk = async (password: string) => {
    if (!resetPasswordUser) return;

    try {
      setResetPasswordModalVisible(false);
      password = await sm2Encrypt(getPublicKey(), password);
      await resetUserPassword(resetPasswordUser.id, password);

      Message.success('密码已重置');
    } catch (error) {
      Message.error('密码重置失败');
    } finally {
      setResetPasswordUser(undefined);
    }
  };

  // 禁用用户，需确认
  const handleStatusUpdate = (record: UserRecord) => {
    const newStatus = record.status === StatusEnum.ENABLE ? StatusEnum.DISABLE : StatusEnum.ENABLE;
    const newLabel = getStatusLabel(newStatus);
    Modal.confirm({
      title: `${newLabel}账号（${record.nickname}）？`,
      content: newStatus === StatusEnum.DISABLE ? '禁用状态下，用户无法登录系统，再次启用时用户可恢复正常使用' : '',
      okButtonProps: { status: 'danger' },
      onOk: async () => {
        await updateUserStatus(record.id, newStatus);
        Message.success(`${newLabel}成功`);
        getUserList();
      }
    });
  };

  // 删除
  const handleDelete = (record: UserRecord) => {
    Modal.confirm({
      title: `确认要删除账号（${record.nickname}）吗？`,
      content: '删除用户后，用户将无法登录，用户数据将被永久删除，请谨慎操作。',
      okButtonProps: { status: 'danger' },
      onOk: async () => {
        await deleteUser(record.id);
        Message.success('删除成功');
        onRefreshDept();
        getUserList();
      }
    });
  };

  // 查看详情
  const handleViewDetail = (record: UserRecord) => {
    setDetailUser(record);
    setDetailModalVisible(true);
  };

  const renderRoleList = (record: UserRecord) => {
    const roleNameList = record.roles?.map((item) => item.name) || [];
    if (roleNameList.length === 1) {
      return roleNameList[0];
    } else if (roleNameList?.length > 1) {
      return roleNameList.join(',');
    } else {
      return [];
    }
  };

  const getColumns = (handleEdit: (record: UserRecord) => void) => {
    return [
      {
        title: '姓名',
        dataIndex: 'nickname',
        width: 160,
        ellipsis: true,
        render: (_: any, record: UserRecord) => (
          <>
            <UserProfileAvatar adminInfo={record} size={25} />
            <span className={s.tableColumnUsername} onClick={() => handleViewDetail(record)}>
              {record.nickname}
            </span>
            {isSystemUser(record) && (
              <Tag style={{ color: 'rgb(var(--primary-6))', backgroundColor: 'rgb(var(--primary-6), 0.15)', marginLeft: '8px' }}>
                系统
              </Tag>
            )}
          </>
        )
      },
      {
        title: '账号',
        dataIndex: 'username',
        width: 120,
        placeholder: '-',
        ellipsis: true
      },
      {
        title: '角色',
        dataIndex: 'roles',
        width: 130,
        ellipsis: true,
        render: (_: any, record: UserRecord) => <span>{renderRoleList(record)}</span>
      },
      { title: '手机号', dataIndex: 'mobile', width: 120 },
      {
        title: '邮箱',
        dataIndex: 'email',
        width: 150,
        placeholder: '-',
        ellipsis: true
      },
      {
        title: '状态',
        dataIndex: 'status',
        width: 70,
        render: (val: number) => <StatusTag status={val} />
      },
      {
        title: '操作',
        dataIndex: 'op',
        width: 150,
        render: (_: any, record: any) => (
          <ActionButtons>
            <Button permission={ACTIONS.UPDATE} type="text" onClick={() => handleEdit(record)}>
              编辑
            </Button>
            <Button permission={ACTIONS.RESETPWD} type="text" onClick={() => handleResetPassword(record)}>
              重置密码
            </Button>
            {hasAllPermissions([ACTIONS.DELETE, ACTIONS.STATUS]) ? (
              <Dropdown
                droplist={
                  <Menu>
                    <Menu.Item key="disable" disabled={isSystemUser(record)} onClick={() => handleStatusUpdate(record)}>
                      {getStatusLabel(record.status === StatusEnum.DISABLE ? StatusEnum.ENABLE : StatusEnum.DISABLE)}
                    </Menu.Item>
                    <Menu.Item key="del" disabled={isSystemUser(record)} onClick={() => handleDelete(record)}>
                      删除
                    </Menu.Item>
                  </Menu>
                }
                position="br"
                trigger="click"
              >
                <Button type="text" icon={<IconMoreVertical />}></Button>
              </Dropdown>
            ) : (
              <>
                <Button permission={ACTIONS.STATUS} type="text" onClick={() => handleStatusUpdate(record)}>
                  {getStatusLabel(record.status === StatusEnum.DISABLE ? StatusEnum.ENABLE : StatusEnum.DISABLE)}
                </Button>
                <Button
                  permission={ACTIONS.DELETE}
                  type="text"
                  disabled={isSystemUser(record)}
                  onClick={() => handleDelete(record)}
                >
                  删除
                </Button>
              </>
            )}
          </ActionButtons>
        )
      }
    ];
  };

  /**
   * 在树形数据中根据 ID 递归查找名称
   *
   * @param {Array<Object>} dataArray - 要搜索的树形数据数组
   * @param {string} targetId - 要查找的目标 ID
   * @returns {string | null} - 找到的名称，如果未找到则返回 null
   */
  const findNameById = (dataArray: DataItem[], targetId: string): string | null => {
    if (!Array.isArray(dataArray) || dataArray.length === 0) {
      return null;
    }

    for (const item of dataArray) {
      if (item.id === targetId) {
        return item.name;
      }

      if (item.children && Array.isArray(item.children) && item.children.length > 0) {
        const foundName = findNameById(item.children, targetId);
        if (foundName) {
          return foundName;
        }
      }
    }
    return null;
  };

  // 设置主管/管理员
  const handleSetAdminOrDirector = async (userRoleType: UserRole) => {
    if (!selectedDeptId) return Message.warning('请先选择部门');
    const userList = await getSimpleUsers({});
    const deptInfo = await getDeptInfo();
    // 设置主管、管理员时，设置默认选中的成员
    setManagerTypeModalVisible(userRoleType);
    if (userRoleType === UserRole.DIRECTOR) {
      if (deptInfo?.leaderUserId) {
        setSelectedMembers([
          {
            department: deptInfo?.name,
            key: deptInfo?.leaderUserId,
            name: deptInfo?.leaderUserName,
            avatar: userList?.find((item:any)=>item.id === deptInfo?.leaderUserId)?.avatar || undefined
          }
        ]);
      } else {
        setSelectedMembers([]);
      }
      setIsMultiple(false);
    } else if (userRoleType === UserRole.ADMIN) {
      if (deptInfo?.adminUserIds.length > 0) {
        const adminUserNames = deptInfo?.adminUserName.split(',');
        const adminUsers = deptInfo?.adminUserIds.map((id: string, index: number) => ({
          department: deptInfo?.name,
          key: id,
          name: adminUserNames?.[index] || '',
          avatar: userList?.find((item:any)=>item.id === id)?.avatar || undefined
        }));
        setSelectedMembers(adminUsers || []);
      } else {
        setSelectedMembers([]);
      }

      setIsMultiple(true);
    }
  };

  // 获取部门用户信息
  const getSimpleUsers = async ({ keywords = '' }: { keywords?: string }) => {
    setMemberLoading(true);
    try {
      if (!selectedDeptId) return;
      let res = null;
      if (keywords) {
        res = await getUserListByName(keywords);
      } else {
        res = await getSimpleUser(selectedDeptId, true);
      }
      setUserData({ userList: res });
      return res;
    } catch (error) {
      console.error('获取部门用户信息失败 error:', error);
    } finally {
      setMemberLoading(false);
    }
  };

  const getDeptInfo = async () => {
    if (!selectedDeptId) return;
    const res = await getDept(selectedDeptId);
    return res;
  };

  // 添加成员
  const handleAddUser = async (selectedMembers: any[]) => {
    console.log('添加成员 selectedMembers:', selectedMembers);
    if (!selectedDeptId || !managerTypeModalVisible) return;
    const keyIds = selectedMembers?.map((item) => item.key).filter(Boolean) || [];
    const params: UpdateAdminOrDirectorReq = {
      deptId: `${selectedDeptId}`,
      updateType: managerTypeModalVisible,
      adminUserIds: keyIds
    };
    await updateAdminOrDirector(params);
    setManagerTypeModalVisible(null);
    Message.success('添加成功');
  };

  const debouncedUpdate = useCallback(
    debounce((value) => {
      getSimpleUsers({ keywords: value });
    }, 500),
    [selectedDeptId]
  );

  useEffect(() => {
    return () => debouncedUpdate.cancel();
  }, [debouncedUpdate]);

  const handleUpdateSelectedMembers = useCallback((members: AuthRoleUsersPageRespVO[]) => {
    setSelectedMembers(members);
  }, []);

  return (
    <div>
      {/* 操作区 */}
      <div className={s.operationTop}>
        <div className={s.deptName}>{findNameById(deptTree, `${selectedDeptId}`)}</div>
        <Space>
          <Button permission={ACTIONS.ADMIN} onClick={() => handleSetAdminOrDirector(UserRole.DIRECTOR)}>
            设置主管
          </Button>
          <Button permission={ACTIONS.ADMIN} onClick={() => handleSetAdminOrDirector(UserRole.ADMIN)}>
            设置部门接口人
          </Button>
        </Space>
      </div>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 16 }}>
        <Space>
          <Input.Search
            className={s.inputSearch}
            style={{ width: 218, marginBottom: 0 }}
            placeholder="输入用户姓名"
            value={search}
            onChange={handleSearch}
            onPressEnter={(e) => handleSearch(e.target.value)}
            allowClear
          />
          <Select defaultValue={status} bordered={false} options={statusOptions} onChange={(val) => setStatus(val)} />
        </Space>
        <Space>
          <Button permission={ACTIONS.CREATE} type="primary" icon={<IconPlus />} onClick={handleCreate}>
            新建用户
          </Button>
          {/* <Button permission={ACTIONS.IMPORT} icon={<IconDownload />} onClick={handleImport}>
            导入
          </Button>
          <Button permission={ACTIONS.EXPORT} icon={<IconUpload />} onClick={handleExport}>
            导出
          </Button> */}
        </Space>
      </div>
      {/* 表格 */}
      <PlaceholderPanel hasPermission={hasPermission(ACTIONS.QUERY)}>
        <Table
          rowKey="id"
          hover
          stripe
          columns={getColumns(handleEdit)}
          data={data}
          pagination={false}
          scroll={{ y: 510 }}
        />
        {/* 页码 */}
        <TablePagination
          current={page}
          pageSize={pageSize}
          total={total}
          onChange={setPage}
          onPageSizeChange={setPageSize}
          sizeOptions={[10, 20, 50]}
        />
      </PlaceholderPanel>
      <UserFormModal
        visible={userModalVisible}
        initialValues={editingUser}
        mode={editingUser ? 'edit' : 'create'}
        onCancel={() => setUserModalVisible(false)}
        onOk={handleModalOk}
        deptTree={deptTree}
        deptLoading={deptLoading}
        onRefreshDept={onRefreshDept}
      />
      <UserFormModal
        visible={detailModalVisible}
        initialValues={detailUser}
        mode="edit"
        isDetail={true}
        onCancel={() => setDetailModalVisible(false)}
        onOk={() => setDetailModalVisible(false)}
        deptTree={deptTree}
        deptLoading={deptLoading}
        onRefreshDept={onRefreshDept}
      />
      <PasswordModal
        visible={resetPasswordModalVisible}
        onCancel={() => {
          setResetPasswordModalVisible(false);
          setResetPasswordUser(undefined);
        }}
        onOk={handleResetPasswordOk}
      />
      <AddMembers
        title={managerTypeModalVisible ? `设置${RoleLabelMap[managerTypeModalVisible]}` : ''}
        visible={!!managerTypeModalVisible}
        data={userData}
        loading={memberLoading}
        isMultiple={isMultiple}
        selectedMembers={selectedMembers || []}
        onSearch={debouncedUpdate}
        onConfirm={handleAddUser}
        onUpdateSelectedMembers={handleUpdateSelectedMembers}
        onCancel={() => {
          setManagerTypeModalVisible(null);
          setSelectedMembers([]);
        }}
      />
    </div>
  );
}
