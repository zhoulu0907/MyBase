import { PermissionButton as Button } from '@/components/PermissionControl';
import PlaceholderPanel from '@/components/PlaceholderPanel';
import StatusTag, { getStatusLabel } from '@/components/StatusTag';
import { Dropdown, Input, Menu, Message, Modal, Pagination, Select, Space, Table, Tag } from '@arco-design/web-react';
import { IconDownload, IconMoreVertical, IconPlus, IconUpload } from '@arco-design/web-react/icon';
import { type AuthRoleUsersPageRespVO } from '@onebase/app';
import { CORP_USER_PERMISSION as ACTIONS, AddMembers, hasAllPermissions, hasPermission } from '@onebase/common';
import type { PageParam, UpdateAdminOrDirectorReq, UserVO } from '@onebase/platform-center';
import {
  deleteUser,
  getSimpleUser,
  getUserListByName,
  getUserPage,
  PlatformTenantStatus,
  resetUserPassword,
  StatusEnum,
  updateCorpAdminOrDirector,
  updateUserStatus,
  UserType
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

  const [userData, setUsertData] = useState<{ userList: any[] }>();
  const [memberLoading, setMemberLoading] = useState<boolean>(false);
  const [selectedMembers, setSelectedMembers] = useState<AuthRoleUsersPageRespVO[]>([]);

  const [userModalVisible, setUserModalVisible] = useState(false);
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [resetPasswordModalVisible, setResetPasswordModalVisible] = useState(false);
  const [importModalVisible, setImportModalVisible] = useState(false); // 导入
  const [managerTypeModalVisible, setManagerTypeModalVisible] = useState<UserRole | null>(null); // 设置主管 or 管理员

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

  const isSystemUser = (record: UserRecord) => {
    return record.adminType === UserType.SYSTEM;
  };

  const getColumns = (handleEdit: (record: UserRecord) => void) => {
    return [
      {
        title: '姓名',
        dataIndex: 'nickname',
        width: 120,
        ellipsis: true,
        render: (_: any, record: UserRecord) => (
          <>
            <span className={s.tableColumnUsername} onClick={() => handleViewDetail(record)}>
              {record.nickname}
            </span>
            {isSystemUser(record) && (
              <Tag color="cyan" style={{ marginLeft: '8px' }}>
                系统
              </Tag>
            )}
          </>
        )
      },
      { title: '手机号', dataIndex: 'mobile', width: 140 },
      {
        title: '邮箱',
        dataIndex: 'email',
        width: 150,
        placeholder: '-',
        ellipsis: true
      },
      {
        title: '账号',
        dataIndex: 'username',
        width: 140,
        placeholder: '-',
        ellipsis: true
      },
      // {
      //   title: '部门',
      //   dataIndex: 'deptName',
      //   width: 180,
      //   placeholder: '-',
      //   ellipsis: true
      // },
      {
        title: '状态',
        dataIndex: 'status',
        width: 80,
        render: (val: number) => <StatusTag status={val} />
      },
      {
        title: '操作',
        dataIndex: 'op',
        width: 200,
        render: (_: any, record: any) => (
          <Space>
            <Button permission={ACTIONS.UPDATE} type="text" onClick={() => handleEdit(record)}>
              编辑
            </Button>
            <Button permission={ACTIONS.RESET} type="text" onClick={() => handleResetPassword(record)}>
              重置密码
            </Button>
            {hasAllPermissions([ACTIONS.DELETE, ACTIONS.STATUS]) ? (
              <Dropdown
                droplist={
                  <Menu>
                    <Menu.Item key="disable" onClick={() => handleStatusUpdate(record)}>
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
                <Button permission={ACTIONS.UPDATE} type="text" onClick={() => handleStatusUpdate(record)}>
                  {getStatusLabel(record.status === StatusEnum.DISABLE ? StatusEnum.ENABLE : StatusEnum.DISABLE)}
                </Button>
                <Button
                  permission={ACTIONS.RESET}
                  type="text"
                  disabled={isSystemUser(record)}
                  onClick={() => handleDelete(record)}
                >
                  删除
                </Button>
              </>
            )}
          </Space>
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
  const handleSetAdminOrDirector = async (updateType: UserRole) => {
    if (!selectedDeptId) return Message.warning('请先选择部门');
    await getSimpleUsers({});
    setManagerTypeModalVisible(updateType);
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
      setUsertData({ userList: res });
    } catch (error) {
      console.error('获取部门用户信息失败 error:', error);
    } finally {
      setMemberLoading(false);
    }
  };

  // 添加成员
  const handleAddUser = async (selectedMembers: any[]) => {
    console.log('添加成员 selectedMembers:', selectedMembers);
    if (!selectedDeptId || !managerTypeModalVisible) return;
    const params: UpdateAdminOrDirectorReq = {
      deptId: `${selectedDeptId}`,
      updateType: managerTypeModalVisible,
      userId: selectedMembers[0].key
    };
    await updateCorpAdminOrDirector(params);
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
          <Button permission={ACTIONS.CREATE} onClick={() => handleSetAdminOrDirector(UserRole.DIRECTOR)}>
            设置主管
          </Button>
          <Button permission={ACTIONS.CREATE} onClick={() => handleSetAdminOrDirector(UserRole.ADMIN)}>
            设置部门接口人
          </Button>
        </Space>
      </div>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 16 }}>
        <Space>
          <Button permission={ACTIONS.CREATE} type="primary" icon={<IconPlus />} onClick={handleCreate}>
            新建用户
          </Button>
          <Button permission={ACTIONS.IMPORT} icon={<IconDownload />} onClick={handleImport}>
            导入
          </Button>
          <Button permission={ACTIONS.EXPORT} icon={<IconUpload />} onClick={handleExport}>
            导出
          </Button>
        </Space>
        <Space>
          <Select defaultValue={status} bordered={false} options={statusOptions} onChange={(val) => setStatus(val)} />
          <Input.Search
            className={s.inputSearch}
            style={{ width: 218, marginBottom: 0 }}
            placeholder="输入用户名称"
            value={search}
            onChange={handleSearch}
            onPressEnter={(e) => handleSearch(e.target.value)}
            allowClear
          />
        </Space>
      </div>
      {/* 表格 */}
      <PlaceholderPanel hasPermission={hasPermission(ACTIONS.QUERY)}>
        <Table
          rowKey="id"
          hover
          columns={getColumns(handleEdit)}
          data={data}
          pagination={false}
          scroll={{ y: 510 }}
          border={false}
        />
        {/* 页码 */}
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'flex-end',
            marginTop: 12
          }}
        >
          <Pagination
            size="small"
            current={page}
            pageSize={pageSize}
            total={total}
            onChange={setPage}
            onPageSizeChange={setPageSize}
            showTotal
            showJumper
            sizeOptions={[10, 20, 50]}
          />
        </div>
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
