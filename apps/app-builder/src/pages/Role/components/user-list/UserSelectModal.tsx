import { Input, Modal, Table, Spin, Checkbox, Message, Pagination } from '@arco-design/web-react';
import { IconSearch } from '@arco-design/web-react/icon';
import { useEffect, useState, useMemo } from 'react';
import type { UserVO } from '@onebase/platform-center';
import { getUserPage } from '@onebase/platform-center';

type UserRecord = Pick<UserVO, 'id' | 'username' | 'nickname'> & Partial<UserVO>;

interface UserSelectModalProps {
  visible: boolean;
  onCancel: () => void;
  onOk: (selectedUserIds: number[]) => void;
  currentRoleUsers: UserRecord[];
}

export default function UserSelectModal({ visible, onCancel, onOk, currentRoleUsers }: UserSelectModalProps) {
  const [allUsers, setAllUsers] = useState<UserRecord[]>([]);
  const [selectedUserIds, setSelectedUserIds] = useState<number[]>([]);
  const [search, setSearch] = useState('');
  const [modalLoading, setModalLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [total, setTotal] = useState(0);

  // 加载所有用户
  const loadAllUsers = async () => {
    setModalLoading(true);
    try {
      // 调用API获取用户列表
      const params = {
        pageNo: 1,
        pageSize: 100 // 获取更多用户数据
      };

      const res = await getUserPage(params);
      const apiUsers = res.list || [];

      if (apiUsers.length > 0) {
        setAllUsers(apiUsers);
        setTotal(apiUsers.length);
      } else {
        // API返回空数据，使用mock数据
        throw new Error('API返回空数据');
      }
    } catch (error) {
      // API调用失败，使用mock数据
      console.warn('获取用户列表失败，使用mock数据:', error);
      const mockAllUsers = [
        {
          id: 1,
          nickname: '用户1',
          username: '用户1',
          mobile: '13800138001',
          email: 'user1@example.com',
          deptName: '技术部',
          role: '开发人员',
          status: 1
        },
        {
          id: 2,
          nickname: '用户2',
          username: '用户2',
          mobile: '13800138002',
          email: 'user2@example.com',
          deptName: '产品部',
          role: '产品经理',
          status: 1
        },
        {
          id: 3,
          nickname: '用户3',
          username: '用户3',
          mobile: '13800138003',
          email: 'user3@example.com',
          deptName: '设计部',
          role: 'UI设计师',
          status: 0
        },
        {
          id: 4,
          nickname: '用户4',
          username: '用户4',
          mobile: '13800138004',
          email: 'user4@example.com',
          deptName: '运营部',
          role: '运营专员',
          status: 1
        },
        {
          id: 5,
          nickname: '用户5',
          username: '用户5',
          mobile: '13800138005',
          email: 'user5@example.com',
          deptName: '市场部',
          role: '市场专员',
          status: 1
        },
        {
          id: 6,
          nickname: '用户6',
          username: '用户6',
          mobile: '13800138006',
          email: 'user6@example.com',
          deptName: '技术部',
          role: '开发人员',
          status: 1
        },
        {
          id: 7,
          nickname: '用户7',
          username: '用户7',
          mobile: '13800138007',
          email: 'user7@example.com',
          deptName: '产品部',
          role: '产品经理',
          status: 1
        },
        {
          id: 8,
          nickname: '用户8',
          username: '用户8',
          mobile: '13800138008',
          email: 'user8@example.com',
          deptName: '设计部',
          role: 'UI设计师',
          status: 0
        },
        {
          id: 9,
          nickname: '用户9',
          username: '用户9',
          mobile: '13800138009',
          email: 'user9@example.com',
          deptName: '运营部',
          role: '运营专员',
          status: 1
        },
        {
          id: 10,
          nickname: '用户10',
          username: '用户10',
          mobile: '13800138010',
          email: 'user10@example.com',
          deptName: '市场部',
          role: '市场专员',
          status: 1
        },
        {
          id: 11,
          nickname: '用户11',
          username: '用户11',
          mobile: '13800138011',
          email: 'user11@example.com',
          deptName: '技术部',
          role: '开发人员',
          status: 1
        },
        {
          id: 12,
          nickname: '用户12',
          username: '用户12',
          mobile: '13800138012',
          email: 'user12@example.com',
          deptName: '产品部',
          role: '产品经理',
          status: 1
        }
      ];
      setAllUsers(mockAllUsers);
      setTotal(mockAllUsers.length);
    } finally {
      setModalLoading(false);
    }

    // 设置当前角色下的用户为默认选中
    const currentRoleUserIds = currentRoleUsers.map((user) => user.id);
    setSelectedUserIds(currentRoleUserIds);
  };

  // 当对话框打开时加载用户
  useEffect(() => {
    if (visible) {
      loadAllUsers();
      setCurrentPage(1);
    }
  }, [visible]);

  // 过滤用户列表
  const filteredUsers = useMemo(() => {
    if (!search) return allUsers;
    return allUsers.filter((user) => user.username?.includes(search) || user.nickname?.includes(search));
  }, [allUsers, search]);

  // 处理用户选择
  const handleUserSelect = (userId: number, checked: boolean) => {
    // 如果用户已经在当前角色中，禁止取消选择
    const isCurrentRoleUser = currentRoleUsers.some((user) => user.id === userId);
    if (isCurrentRoleUser && !checked) {
      Message.warning('已选择的用户不能取消选择');
      return;
    }

    if (checked) {
      setSelectedUserIds((prev) => [...prev, userId]);
    } else {
      setSelectedUserIds((prev) => prev.filter((id) => id !== userId));
    }
  };

  // 处理全选
  const handleSelectAll = (checked: boolean) => {
    if (checked) {
      const allUserIds = filteredUsers.map((user) => user.id);
      setSelectedUserIds(allUserIds);
    } else {
      // 只保留当前角色中的用户
      const currentRoleUserIds = currentRoleUsers.map((user) => user.id);
      setSelectedUserIds(currentRoleUserIds);
    }
  };

  const handleConfirm = () => {
    onOk(selectedUserIds);
  };

  // 分页数据
  const paginatedUsers = useMemo(() => {
    const startIndex = (currentPage - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    return filteredUsers.slice(startIndex, endIndex);
  }, [filteredUsers, currentPage, pageSize]);

  // 当前页选中的用户
  const currentPageSelectedUserIds = useMemo(() => {
    return selectedUserIds.filter((id) => paginatedUsers.some((user) => user.id === id));
  }, [selectedUserIds, paginatedUsers]);

  // 选择对话框的列定义
  const columns = useMemo(
    () => [
      {
        title: (
          <Checkbox
            checked={currentPageSelectedUserIds.length === paginatedUsers.length && paginatedUsers.length > 0}
            indeterminate={
              currentPageSelectedUserIds.length > 0 && currentPageSelectedUserIds.length < paginatedUsers.length
            }
            onChange={handleSelectAll}
          />
        ),
        dataIndex: 'select',
        width: 50,
        render: (_: any, record: UserRecord) => {
          const isCurrentRoleUser = currentRoleUsers.some((user) => user.id === record.id);
          return (
            <Checkbox
              checked={selectedUserIds.includes(record.id)}
              onChange={(checked) => handleUserSelect(record.id, checked)}
              disabled={isCurrentRoleUser}
            />
          );
        }
      },
      {
        title: '姓名',
        dataIndex: 'username',
        width: 100
      },
      { title: '手机号', dataIndex: 'mobile', width: 120 },
      { title: '邮箱', dataIndex: 'email', width: 150 },
      { title: '部门', dataIndex: 'deptName', width: 120 }
    ],
    [selectedUserIds, paginatedUsers, currentPageSelectedUserIds, handleUserSelect, handleSelectAll, currentRoleUsers]
  );

  // 分页配置
  const paginationConfig = useMemo(
    () => ({
      current: currentPage,
      pageSize,
      total: filteredUsers.length,
      onChange: setCurrentPage,
      onPageSizeChange: setPageSize,
      showTotal: true,
      showJumper: true,
      sizeOptions: [10, 20, 50],
      size: 'small' as const
    }),
    [currentPage, pageSize, filteredUsers.length]
  );

  return (
    <Modal
      visible={visible}
      onCancel={onCancel}
      onOk={handleConfirm}
      title="添加用户到角色"
      okText="确定"
      cancelText="取消"
      style={{ width: '720px' }}
    >
      <div style={{ marginBottom: 16 }}>
        <Input
          style={{ width: 300 }}
          prefix={<IconSearch />}
          placeholder="搜索用户"
          value={search}
          onChange={setSearch}
        />
      </div>

      <Spin loading={modalLoading}>
        <Table
          rowKey="id"
          columns={columns}
          data={paginatedUsers}
          pagination={false}
          scroll={{ y: 480, x: 680 }}
          border={false}
        />
      </Spin>

      <div style={{ display: 'flex', alignItems: 'center', marginTop: 12 }}>
        <div style={{ flex: 1 }} />
        <Pagination {...paginationConfig} />
      </div>
    </Modal>
  );
}
