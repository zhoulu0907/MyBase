import {
  Button,
  Dropdown,
  Input,
  Menu,
  Message,
  Popconfirm,
  Select,
  Table,
  Tooltip,
  type TableColumnProps
} from '@arco-design/web-react';
import { IconDown, IconPlus } from '@arco-design/web-react/icon';
import {
  getRoleMembers,
  getDeptUser,
  roleAddUser,
  roleDeleteMember,
  type GetDeptUserReq,
  type Role,
  type RoleAddUserReq,
  type AuthRoleUsersPageRespVO,
  type DeptAndUsersRespDTO,
  type getRoleMembersReq,
  type RoleDeleteMemberReq,
  type UserMembers,
  roleAddDept,
  type RoleAddDeptReq
} from '@onebase/app';
import { getDeptsById, type GetDeptsByIdReq } from '@onebase/platform-center';
import { debounce } from 'lodash-es';
import { useCallback, useEffect, useRef, useState } from 'react';
import { AddMembers } from '@onebase/common';
import styles from './index.module.less';

interface IProps {
  roleInfo: Role | undefined;
  memberList?: AuthRoleUsersPageRespVO[];
  memberTotal?: number;
}

const ROLE = {
  USER: 'user',
  DEPT: 'dept'
} as const;
type RoleType = (typeof ROLE)[keyof typeof ROLE];

const ADDTYPE = {
  USER: 'specifiedPerson',
  DEPT: 'specifiedDepartment'
} as const;

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

  // Search
  const [searchType, setSearchType] = useState<RoleType>(ROLE.USER);
  const [addSelect, setAddSelect] = useState<string>(ADDTYPE.USER); //添加
  const [selectRowkeyArr, setSelectRowKeyArr] = useState<any[]>([]);
  const cacheDeptListRef = useRef<Record<string, string>>({});
  const loadingDeptRef = useRef<Record<string, boolean>>({});
  const [, forceUpdate] = useState(0);

  const dropList = (
    <Menu
      onClickMenuItem={(key) => {
        setAddSelect(key);
      }}
    >
      <Menu.Item key={ADDTYPE.USER}>
        <IconPlus /> 添加成员
      </Menu.Item>
      <Menu.Item key={ADDTYPE.DEPT}>
        <IconPlus /> 添加部门
      </Menu.Item>
    </Menu>
  );

  useEffect(() => {
    if (roleInfo?.id) {
      getRoleUserList();
    }
  }, [roleInfo?.id]);

  /* 获取角色用户列表 */
  const getRoleUserList = async (pageNo = 1, pageSize = 10, memberName?: string, memberType?: string) => {
    setUserLoading(true);
    const params: getRoleMembersReq = {
      roleId: roleInfo?.id || '',
      pageNo,
      pageSize,
      memberName,
      memberType
    };
    const res = await getRoleMembers(params);
    setMuneList(res.list || []);
    if (selectRowkeyArr.length > 0) {
      const curMemberIdSet = new Set(res.list.map((u: any) => u.id));
      const cur = selectRowkeyArr.filter((item: any) => curMemberIdSet.has(item.id));
      setSelectRowKeyArr(cur);
    }
    setPagination((prev) => ({ ...prev, current: pageNo, pageSize, total: res.total || 0 }));
    setUserLoading(false);
  };

  /* 列表搜索 */
  const debouncedSearch = useCallback(
    debounce((value) => {
      if (value) {
        getRoleUserList(1, 10, value, searchType);
      } else {
        getRoleUserList();
      }
    }, 500),
    [searchType]
  );

  // 获取部门用户信息
  const getDeptUsers = async ({ deptId, keywords }: { deptId?: string; keywords?: string }) => {
    setMemberLoading(true);
    try {
      if (!roleInfo?.id) return;
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
  const handleAddUser = async (selectedMembers: AuthRoleUsersPageRespVO[], isIncludeChild?: boolean) => {
    console.log('添加成员 selectedMembers:', selectedMembers);
    if (!roleInfo?.id) return;
    const userIds = selectedMembers.map((v) => v.key);
    if (addSelect === ADDTYPE.DEPT) {
      const params: RoleAddDeptReq = {
        roleId: roleInfo.id,
        deptIds: userIds,
        isIncludeChild: isIncludeChild ? +isIncludeChild : Number(isIncludeChild)
      };
      console.log('添加部门 params:', params);
      await roleAddDept(params);
    } else {
      const params: RoleAddUserReq = {
        roleId: roleInfo.id,
        userIds
      };
      console.log('添加成员 params:', params);
      await roleAddUser(params);
    }
    await getRoleUserList();
    setMembersVisible(false);
    setSelectedMembers([]);
    Message.success('添加成功');
  };

  // 移除角色用户
  const handleDeleteUser = async (members: any[]) => {
    if (!roleInfo?.id) return;
    const formatMembers = members.map((member) => ({
      id: member.id,
      memberId: member.memberId,
      memberName: member.name,
      memberType: member.type
    }));
    const params: RoleDeleteMemberReq = {
      roleId: roleInfo.id,
      members: formatMembers
    };
    await roleDeleteMember(params);
    await getRoleUserList();
    Message.success('移除成功');
  };

  // 获取部门层级
  const fetchDeptData = async (id: string, idType: string) => {
    if (loadingDeptRef.current[id]) return; // 已在加载中，防止重复请求

    loadingDeptRef.current[id] = true;
    forceUpdate((s) => s + 1);

    try {
      const params: GetDeptsByIdReq = { id, idType };
      const res = await getDeptsById(params);
      const deptListName = leafParentStrings(res);
      cacheDeptListRef.current[id] = deptListName;
    } catch (err: any) {
      cacheDeptListRef.current[id] = '加载失败';
    } finally {
      loadingDeptRef.current[id] = false;
      forceUpdate((s) => s + 1);
    }
  };

  const leafParentStrings = (items: any[]): string => {
    const map = new Map(items.map((i) => [i.id, i]));
    const parentIdSet = new Set(items.map((i) => i.parentId));
    const leaves = items.filter((i) => !parentIdSet.has(i.id));
    if (leaves.length === 0) return '';
    const leaf = leaves[0]; // 只会有一条链路，取第一个叶子
    const parent = map.get(leaf.parentId);
    return parent ? `${parent.name} / ${leaf.name}` : leaf.name;
  };

  const columns: TableColumnProps[] = [
    {
      title: '成员或部门',
      dataIndex: 'name',
      ellipsis: true,
      align: 'center'
    },
    {
      title: '类型',
      dataIndex: 'typeName',
      align: 'center'
    },
    {
      title: '部门',
      dataIndex: 'deptName',
      ellipsis: true,
      align: 'center',
      render: (deptName: string, item: any) => (
        <Tooltip
          getPopupContainer={() => document.body}
          content={
            loadingDeptRef.current[item.memberId] ? '加载中...' : (cacheDeptListRef.current[item.memberId] ?? '未加载')
          }
          onVisibleChange={(visible) => {
            // 按当前行的 id 去判断是否需要请求，而不是全局 content
            if (visible && !cacheDeptListRef.current[item.memberId] && !loadingDeptRef.current[item.memberId]) {
              fetchDeptData(item.memberId, item.type);
            }
          }}
        >
          <span>{deptName}</span>
        </Tooltip>
      )
    },
    {
      title: '操作',
      dataIndex: 'op',
      align: 'center',
      render: (_, record) => (
        <Popconfirm
          focusLock
          title={`移除${record.type === ROLE.USER ? '成员' : '部门'}`}
          content={`确定要移除这个${record.type === ROLE.USER ? '成员' : '部门'}吗？`}
          onOk={() => {
            handleDeleteUser([record]);
          }}
        >
          <Button type="text" status="danger">
            移除
          </Button>
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

  useEffect(() => {
    return () => debouncedSearch.cancel();
  }, [debouncedSearch]);

  const handleUpdateSelectedMembers = useCallback((members: AuthRoleUsersPageRespVO[]) => {
    setSelectedMembers(members);
  }, []);

  const handlePageChange = (current: number, pageSize: number) => {
    getRoleUserList(current, pageSize);
  };

  return (
    <div className={styles.adminWrapper}>
      <div className={styles.header}>
        <Input
          style={{ width: 284 }}
          addBefore={
            <Select
              className={styles.searchSelect}
              value={searchType}
              onChange={(type) => {
                setSearchType(type);
              }}
            >
              <Select.Option value={ROLE.USER}>搜索成员</Select.Option>
              <Select.Option value={ROLE.DEPT}>搜索部门</Select.Option>
            </Select>
          }
          allowClear={true}
          placeholder={`请输入${searchType === ROLE.USER ? '成员姓名' : '部门名称'}`}
          onChange={(value) => debouncedSearch(value)}
        />
        <div>
          {selectRowkeyArr.length > 0 && (
            <Popconfirm
              focusLock
              title="移除部门或成员"
              content="是否确认移除这些部门或成员？"
              onOk={() => {
                handleDeleteUser(selectRowkeyArr);
              }}
            >
              <Button type="outline" status="danger" style={{ marginRight: '16px' }}>
                批量删除
              </Button>
            </Popconfirm>
          )}
          <Dropdown.Button type="primary" droplist={dropList} icon={<IconDown />} onClick={handleMembersVisible}>
            {addSelect === ADDTYPE.USER ? '添加成员' : '添加部门'}
          </Dropdown.Button>
        </div>
      </div>
      <Table
        className={styles.table}
        columns={columns}
        data={userList}
        loading={userLoading}
        pagination={{ ...pagination, showTotal: true, onChange: handlePageChange }}
        rowKey="id"
        rowSelection={{
          type: 'checkbox',
          onChange: (_keyArr: any, selectedRows: any[]) => setSelectRowKeyArr(selectedRows)
        }}
      />
      <AddMembers
        visible={membersVisible}
        data={deptData}
        title={addSelect}
        loading={memberLoading}
        selectedMembers={selectedMembers || []}
        isFromPermission={true}
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
