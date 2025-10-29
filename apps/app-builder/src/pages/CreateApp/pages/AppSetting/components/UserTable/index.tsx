import { Button, Message, Popconfirm, Table, type TableColumnProps } from '@arco-design/web-react';
import { IconPlusCircle } from '@arco-design/web-react/icon';
import {
  getDeptUser,
  getRoleUser,
  roleAddUser,
  roleDeleteUser,
  type GerRoleUserReq,
  type GetDeptUserReq,
  type Role,
  type RoleAddUserReq,
  type RoleDeleteUserReq,
  type AuthRoleUsersPageRespVO,
  type DeptAndUsersRespDTO
} from '@onebase/app';
import { debounce } from 'lodash-es';
import { useCallback, useEffect, useState } from 'react';
import { AddMembers } from '@onebase/common';
import styles from './index.module.less';

interface IProps {
  roleInfo: Role | undefined;
  memberList?: AuthRoleUsersPageRespVO[];
  memberTotal?: number;
}

// 用户成员列表
const UserMembers = (props: IProps) => {
  const { roleInfo, memberList, memberTotal } = props;

  const [deptData, setDeptData] = useState<DeptAndUsersRespDTO>();
  const [userList, setMuneList] = useState(memberList || []); // 用户列表
  const [pagination, setPagination] = useState({
    pageSize: 10,
    current: 1,
    total: memberTotal || 0
  });

  const [userLoading, setUserLoading] = useState<boolean>(false); // 用户列表加载状态
  const [memberLoading, setMemberLoading] = useState<boolean>(false);
  const [membersVisible, setMembersVisible] = useState<boolean>(false);
  const [selectedMembers, setSelectedMembers] = useState<AuthRoleUsersPageRespVO[]>([]);

  useEffect(() => {
    if (memberList && roleInfo?.id) {
      getRoleUserList();
    }
  }, [memberList]);

  /* 获取角色用户列表 */
  const getRoleUserList = async (pageNo = 1, pageSize = 10) => {
    setUserLoading(true);
    const params: GerRoleUserReq = {
      roleId: roleInfo?.id,
      pageNo,
      pageSize
    };
    const res = await getRoleUser(params);
    setMuneList(res.list || []);

    setPagination((prev) => ({ ...prev, current: pageNo, pageSize, total: res.total || 0 }));
    setUserLoading(false);
  };

  // 获取部门用户信息
  const getDeptUsers = async ({ deptId, keywords }: { deptId?: string; keywords?: string }) => {
    setMemberLoading(true);
    try {
      if (!roleInfo.id) return;
      const params: GetDeptUserReq = {
        roleId: roleInfo.id,
        deptId,
        keywords
      };
      const res = await getDeptUser(params);
      console.log('获取部门用户信息 res:', res);
      setDeptData(res);
    } catch (error) {
      console.error('获取部门用户信息失败 error:', error);
    } finally {
      setMemberLoading(false);
    }
  };

  const handleMembersVisible = async () => {
    await getDeptUsers({});
    setMembersVisible(true);
  };

  // 展开下级
  const handleExpand = async (deptId: string) => {
    await getDeptUsers({ deptId });
  };

  // 添加成员
  const handleAddUser = async (selectedMembers: AuthRoleUsersPageRespVO[]) => {
    console.log('添加成员 selectedMembers:', selectedMembers);
    if (!roleInfo.id) return;
    const userIds = selectedMembers.map((v) => v.key);
    const params: RoleAddUserReq = {
      roleId: roleInfo.id,
      userIds
    };
    console.log('添加成员 params:', params);
    await roleAddUser(params);
    await getRoleUserList();
    setMembersVisible(false);
    Message.success('添加成功');
  };

  // 移除角色用户
  const handleDeleteUser = async (userId: number) => {
    if (!roleInfo.id) return;
    const params: RoleDeleteUserReq = {
      roleId: roleInfo.id,
      userIds: [userId]
    };
    await roleDeleteUser(params);
    await getRoleUserList();
    Message.success('移除成功');
  };

  const columns: TableColumnProps[] = [
    {
      title: '序号',
      dataIndex: 'index',
      align: 'center',
      width: 100,
      render: (_value, _row, index) => index + 1
    },
    {
      title: '姓名',
      dataIndex: 'nickname',
      align: 'center'
    },
    {
      title: '手机号',
      dataIndex: 'mobile',
      align: 'center'
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      ellipsis: true,
      align: 'center'
    },
    {
      title: '部门',
      dataIndex: 'deptName',
      align: 'center'
    },
    {
      title: '操作',
      dataIndex: 'op',
      align: 'center',
      render: (_, _record) => (
        <Popconfirm
          focusLock
          title="移除成员"
          content="确定要移除这个成员吗？"
          onOk={() => {
            handleDeleteUser(_record.id);
          }}
        >
          <Button type="text">移除</Button>
        </Popconfirm>
      )
    }
  ];

  const debouncedUpdate = useCallback(
    debounce((value) => {
      getDeptUsers({ keywords: value });
    }, 500),
    []
  );

  useEffect(() => {
    return () => debouncedUpdate.cancel();
  }, [debouncedUpdate]);

  const handleUpdateSelectedMembers = useCallback((members: AuthRoleUsersPageRespVO[]) => {
    setSelectedMembers(members);
  }, []);

  const handlePageChange = (current: number, pageSize: number) => {
    getRoleUserList(current, pageSize);
  };

  return (
    <div className={styles.adminWrapper}>
      <div className={styles.header}>
        <Button type="primary" icon={<IconPlusCircle />} onClick={handleMembersVisible}>
          添加成员
        </Button>
      </div>
      <Table
        className={styles.table}
        columns={columns}
        data={userList}
        loading={userLoading}
        pagination={{ ...pagination, showTotal: true, onChange: handlePageChange }}
        rowKey="id"
      />
      <AddMembers
        visible={membersVisible}
        data={deptData}
        loading={memberLoading}
        selectedMembers={selectedMembers || []}
        onExpand={handleExpand}
        onSearch={debouncedUpdate}
        onConfirm={handleAddUser}
        onUpdateSelectedMembers={handleUpdateSelectedMembers}
        onCancel={() => {
          setMembersVisible(false);
          setSelectedMembers([]);
        }}
      />
    </div>
  );
};

export default UserMembers;
