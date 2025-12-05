import { Button, Input, Message, Modal, Pagination, Space, Spin, Table } from '@arco-design/web-react';
import { IconPlus, IconSearch } from '@arco-design/web-react/icon';
import type { PageParam, UserVO } from '@onebase/platform-center';
import { addRoleUsers, getUserPage, removeRoleUsers } from '@onebase/platform-center';
import { debounce } from 'lodash-es';
import { useCallback, useEffect, useMemo, useState } from 'react';
import UserSelectModal from './UserSelectModal';
import styles from '../../index.module.less';

interface UserListProps {
  selectedRoleId?: number;
}

type UserRecord = Pick<UserVO, 'id' | 'username' | 'nickname'> & Partial<UserVO>;

const UserList: React.FC<UserListProps> = ({ selectedRoleId = undefined }: UserListProps) => {
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchValue, setSearchValue] = useState('');
  const [userModalVisible, setUserModalVisible] = useState(false);
  const [data, setData] = useState<UserRecord[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);

  // 查询用户列表
  const getUserList = useCallback(
    async (keyword: string) => {
      if (!selectedRoleId) return;

      setLoading(true);
      const params: PageParam = {
        pageNo: page,
        pageSize
      };
      if (selectedRoleId) params.roleId = selectedRoleId;
      if (keyword) params.nickname = keyword;

      try {
        const res = await getUserPage(params);
        setData(res.list || []);
        setTotal(res.total || 0);
      } finally {
        setLoading(false);
      }
    },
    [selectedRoleId, page, pageSize]
  );

  useEffect(() => {
    getUserList(searchValue);
  }, [selectedRoleId, page, getUserList]);

  const debounceSearch = useCallback(
    debounce((keyword: string) => {
      getUserList(keyword);
    }, 300),
    [getUserList]
  );

  const handleSearch = useCallback(
    (value: string) => {
      setPage(1);
      setSearchValue(value);
      debounceSearch(value);
    },
    [debounceSearch]
  );

  // 为角色下添加用户
  const handleAdd = () => {
    setUserModalVisible(true);
  };

  // 添加用户确认
  const handleUserSelectOk = async (selectedUserIds: number[]) => {
    if (!selectedRoleId) {
      Message.error('角色ID不存在');
      return;
    }

    // 增量添加，仅传参新增的用户
    const currentUserIds = data.map((user) => user.id);
    const newUserIds = selectedUserIds.filter((id) => !currentUserIds.includes(id));

    if (newUserIds.length === 0) {
      Message.warning('没有新增的用户');
      setUserModalVisible(false);
      return;
    }

    try {
      await addRoleUsers(selectedRoleId, newUserIds);
      Message.success(`成功添加 ${newUserIds.length} 个用户到角色`);
      setUserModalVisible(false);
      getUserList(searchValue);
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
      title: `确认要移除角色用户（${record.nickname}）吗？`,
      content: '移除该角色用户后，该角色用户将失去该角色赋予的权限，请谨慎操作。',
      okButtonProps: { status: 'danger' },
      onOk: async () => {
        try {
          await removeRoleUsers(selectedRoleId, [record.id]);
          Message.success('用户移除成功');
          getUserList(searchValue);
        } catch (error) {
          Message.error('移除用户失败，请重试');
        }
      }
    });
  };

  const columns = useMemo(
    () => [
      {
        title: '姓名',
        dataIndex: 'nickname',
        width: 140,
        ellipsis: true
      },
      {
        title: '账号',
        dataIndex: 'username',
        width: 140,
        ellipsis: true
      },
      { title: '手机号', dataIndex: 'mobile', width: 140 },
      { title: '邮箱', dataIndex: 'email', placeholder: '-', ellipsis: true },
      { title: '部门', dataIndex: 'deptName', placeholder: '-', ellipsis: true },
      {
        title: '操作',
        dataIndex: 'op',
        width: 180,
        render: (_: any, record: any) => (
          <Button type="text" onClick={() => handleRemove(record)}>
            移除
          </Button>
        )
      }
    ],
    [handleRemove]
  );

  return (
    <div>
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: 16 }}>
        <Space>
          <Button type="primary" icon={<IconPlus />} onClick={handleAdd}>
            添加
          </Button>
        </Space>
        <div style={{ flex: 1 }} />
        <Input
          className={styles.userListInput}
          prefix={<IconSearch />}
          placeholder="输入用户名称"
          value={searchValue}
          onChange={handleSearch}
          onPressEnter={(e) => handleSearch(e.target.value)}
        />
      </div>

      <Spin loading={loading}>
        <Table rowKey="id" columns={columns} data={data} pagination={false} scroll={{ y: 510 }} border={false} />
      </Spin>
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
          onChange={(newPage) => {
            setPage(newPage);
          }}
          onPageSizeChange={(newPageSize) => {
            setPageSize(newPageSize);
            setPage(1);
          }}
          showTotal
          showJumper
          sizeOptions={[10, 20, 50]}
        />
      </div>

      {/* 添加用户对话框 */}
      {userModalVisible && (
        <UserSelectModal
          visible={userModalVisible}
          onCancel={() => {
            setUserModalVisible(false);
          }}
          onOk={handleUserSelectOk}
          currentRoleUsers={data}
          selectedRoleId={selectedRoleId}
        />
      )}
    </div>
  );
};

export default UserList;
