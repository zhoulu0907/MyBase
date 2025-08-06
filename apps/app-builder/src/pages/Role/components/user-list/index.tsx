import { Button, Input, Message, Modal, Pagination, Space, Table, Spin } from '@arco-design/web-react';
import { IconSearch, IconPlus } from '@arco-design/web-react/icon';
import {
  getUserPage
} from '@onebase/platform-center';
import { deleteRoleUser, addRoleUsers } from '@onebase/platform-center';
import { useEffect, useState, useMemo } from 'react';
import type { UserVO } from '@onebase/platform-center';
import type { PageParam } from '@onebase/platform-center';
import UserSelectModal from './UserSelectModal';

interface UserListProps {
  selectedRoleId?: number;
}

type UserRecord = Pick<UserVO, 'id' | 'username' | 'nickname'> & Partial<UserVO>;

export default function UserTable({ selectedRoleId = undefined }: UserListProps) {
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [search, setSearch] = useState('');
  const [userModalVisible, setUserModalVisible] = useState(false);
  const [editingUser, setEditingUser] = useState<UserRecord | undefined>();
  const [data, setData] = useState<UserRecord[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);

  // 查询用户列表
  const getUserList = async () => {
    if (!selectedRoleId) return;
    
    setLoading(true);
    const params: PageParam = {
      pageNo: page,
      pageSize,
    };
    if (selectedRoleId) params.roleId = selectedRoleId;
    if (search) params.username = search;
    
    try {
      // TODO: 联调后移除mock数据
      // const res = await getUserPage(params)
      // setData(res.list || []);
      // setTotal(res.total || 0);
      
      await getUserPage(params);
      const mockData = [
        { id: 1, nickname: '用户1', username: '用户1', mobile: '13800138001', email: 'user1@example.com', deptName: '技术部', role: '开发人员', status: 1 },
        { id: 2, nickname: '用户2', username: '用户2', mobile: '13800138002', email: 'user2@example.com', deptName: '产品部', role: '产品经理', status: 1 },
        { id: 3, nickname: '用户3', username: '用户3', mobile: '13800138003', email: 'user3@example.com', deptName: '设计部', role: 'UI设计师', status: 0 },
      ];
      setData(mockData);
      setTotal(mockData.length);
    } catch (error) {
      // TODO 联调后移除
      const mockData = [
        { id: 1, nickname: '用户1', username: '用户1' },
        { id: 2, nickname: '用户2', status: 1, username: '用户2' }
      ];
      setData(mockData);
      setTotal(mockData.length);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    getUserList();
  }, [selectedRoleId, page, pageSize, search]);

  // 为角色下添加用户
  const handleAdd = () => {
    setEditingUser(undefined);
    setUserModalVisible(true);
  };


  const handleSearch = () => {
    setPage(1);
  };

  // 添加用户确认
  const handleUserSelectOk = async (selectedUserIds: number[]) => {
    if (!selectedRoleId) {
      Message.error('角色ID不存在');
      return;
    }

    // 增量添加，仅传参新增的用户
    const currentUserIds = data.map(user => user.id);
    const newUserIds = selectedUserIds.filter(id => !currentUserIds.includes(id));
    
    if (newUserIds.length === 0) {
      Message.warning('没有新增的用户');
      setUserModalVisible(false);
      return;
    }

    try {
      await addRoleUsers(selectedRoleId, newUserIds);
      Message.success(`成功添加 ${newUserIds.length} 个用户到角色`);
      setUserModalVisible(false);
      getUserList();
    } catch (error) {
      console.error('添加用户失败:', error);
      Message.error('添加用户失败，请重试');
    }
  };

  // 将用户从角色下移除
  const handleRemove = (record: UserRecord) => {
    if (!selectedRoleId) {
      Message.warning('请先选择一个角色');
      return;
    }

    Modal.confirm({
      title: `提示`,
      content: `确认要将用户 ${record.nickname} 从该角色下删除吗？`,
      onOk: async () => {
        try {
          await deleteRoleUser(selectedRoleId, record.id);
          Message.success('用户移除成功');
          getUserList();
        } catch (error) {
          Message.error('移除用户失败，请重试');
        }
      }
    });
  };

  const columns = useMemo(() => [
    {
      title: '姓名',
      dataIndex: 'username',
      width: 100
    },
    { title: '手机号', dataIndex: 'mobile', width: 140 },
    { title: '邮箱', dataIndex: 'email', width: 180 },
    { title: '部门', dataIndex: 'deptName', width: 180 },
    {
      title: '操作',
      dataIndex: 'op',
      width: 180,
      render: (_: any, record: any) => (
        <Button type='text' onClick={() => handleRemove(record)}>移除</Button>
      ),
    },
  ], [handleRemove]);

  const paginationConfig = useMemo(() => ({
    current: page,
    pageSize,
    total,
    onChange: setPage,
    onPageSizeChange: setPageSize,
    showTotal: true,
    showJumper: true,
    sizeOptions: [10, 20, 50],
    size: 'small' as const,
  }), [page, pageSize, total]);

  return (
    <div>
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: 16 }}>
        <Space>
          <Button type="primary" icon={<IconPlus />} onClick={handleAdd}>添加</Button>
        </Space>
        <div style={{ flex: 1 }} />
        <Input
          style={{ width: 220, marginRight: 12, borderRadius: 24 }}
          prefix={<IconSearch />}
          placeholder="输入用户名称"
          value={search}
          onChange={setSearch}
          onPressEnter={handleSearch}
        />
      </div>
      
      <Spin loading={loading}>
        <Table
          rowKey='id'
          columns={columns}
          data={data}
          pagination={false}
          scroll={{ y: 510 }}
          border={false}
        />
      </Spin>
      
      <div style={{ display: 'flex', alignItems: 'center', marginTop: 12 }}>
        <div style={{ flex: 1 }} />
        <span style={{ marginRight: 16 }}>共{total}条</span>
        <Pagination {...paginationConfig} />
      </div>
      
      {/* 添加用户对话框 */}
      <UserSelectModal
        visible={userModalVisible}
        onCancel={() => { setUserModalVisible(false) }}
        onOk={handleUserSelectOk}
        currentRoleUsers={data}
      />
    </div>
  );
}
