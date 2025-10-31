import { Checkbox, Input, Modal, Pagination, Spin, Table } from '@arco-design/web-react';
import { IconSearch } from '@arco-design/web-react/icon';
import type { PageParam, UserVO } from '@onebase/platform-center';
import { getUserPage } from '@onebase/platform-center';
import { debounce, difference, union } from 'lodash-es';
import { useCallback, useEffect, useMemo, useState } from 'react';

type UserRecord = Pick<UserVO, 'id' | 'username' | 'nickname'> & Partial<UserVO>;

interface UserSelectModalProps {
  visible: boolean;
  onCancel: () => void;
  onOk: (selectedUserIds: number[]) => void;
  currentRoleUsers: UserRecord[];
  selectedRoleId?: number;
}

const UserSelectModal: React.FC<UserSelectModalProps> = ({
  visible,
  onCancel,
  onOk,
  currentRoleUsers,
  selectedRoleId
}) => {
  const [userList, setUserList] = useState<UserRecord[]>([]);
  const [selectedUserIds, setSelectedUserIds] = useState<number[]>([]);
  const [searchValue, setSearchValue] = useState('');
  const [modalLoading, setModalLoading] = useState(false);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [total, setTotal] = useState(0);

  // 从接口加载用户列表
  const getUserList = useCallback(
    async (keyword?: string) => {
      setModalLoading(true);
      try {
        const params: PageParam = {
          pageNo: page,
          pageSize,
          excludRoleId: selectedRoleId // 仅筛选不属于当前角色的用户
        };

        if (keyword) {
          params.nickname = keyword;
        }

        if (keyword) {
          params.nickname = keyword;
        }

        const res = await getUserPage(params);
        const list = res.list || [];

        setUserList(list);
        setTotal(res.total || 0);
      } finally {
        setModalLoading(false);
      }
    },
    [page, pageSize]
  );

  // 当搜索、分页变化时重新加载数据
  useEffect(() => {
    if (visible) {
      setSearchValue('');
      getUserList();
    }
  }, [visible, page, pageSize, getUserList, currentRoleUsers]);

  useEffect(() => {
    if (visible) {
      // 设置当前角色下的用户为默认选中
      const currentRoleUserIds = currentRoleUsers.map((user) => user.id);
      setSelectedUserIds(currentRoleUserIds);
    }
  }, [visible]);

  const debounceSearch = useCallback(
    debounce((keyword: string) => {
      getUserList(keyword);
    }, 300),
    [getUserList]
  );

  const handleSearchChange = useCallback(
    (value: string) => {
      setSearchValue(value);
      setPage(1); // 搜索时重置到第一页
      debounceSearch(value);
    },
    [debounceSearch]
  );

  // 处理用户选择
  const handleUserSelect = (userId: number, checked: boolean) => {
    if (checked) {
      setSelectedUserIds((prev) => [...prev, userId]);
    } else {
      setSelectedUserIds((prev) => prev.filter((id) => id !== userId));
    }
  };

  // 处理全选
  const handleSelectAll = useCallback(
    (checked: boolean) => {
      const currentPageIds = userList.map((user) => user.id);
      if (checked) {
        // 全选则取当前页与已选的并集
        const unionedIds = union(selectedUserIds, currentPageIds);
        setSelectedUserIds(unionedIds);
      } else {
        // 取消全选从已选中去掉当前页数据
        const diffedIds = difference(selectedUserIds, currentPageIds);
        setSelectedUserIds(diffedIds);
      }
    },
    [userList, currentRoleUsers]
  );

  const handleConfirm = useCallback(() => {
    onOk(selectedUserIds);
  }, [onOk, selectedUserIds]);

  // 当前页选中的用户
  const currentPageSelectedUserIds = useMemo(() => {
    return selectedUserIds.filter((id) => userList.some((user) => user.id === id));
  }, [selectedUserIds, userList]);

  // 选择对话框的列定义
  const columns = useMemo(
    () => [
      {
        title: (
          <Checkbox
            checked={currentPageSelectedUserIds.length === userList.length && userList.length > 0}
            indeterminate={currentPageSelectedUserIds.length > 0 && currentPageSelectedUserIds.length < userList.length}
            onChange={handleSelectAll}
          />
        ),
        dataIndex: 'select',
        width: 50,
        render: (_: any, record: UserRecord) => {
          return (
            <Checkbox
              checked={selectedUserIds.includes(record.id)}
              onChange={(checked) => handleUserSelect(record.id, checked)}
            />
          );
        }
      },
      {
        title: '姓名',
        dataIndex: 'nickname',
        width: 120
      },
      { title: '手机号', dataIndex: 'mobile', width: 140 },
      { title: '邮箱', dataIndex: 'email', width: 160, placeholder: '-', ellipsis: true },
      { title: '部门', dataIndex: 'deptName', placeholder: '-' }
    ],
    [selectedUserIds, userList, currentPageSelectedUserIds, handleUserSelect, handleSelectAll]
  );

  return (
    <Modal
      visible={visible}
      onCancel={onCancel}
      onOk={handleConfirm}
      title="添加角色用户"
      okText="确定"
      cancelText="取消"
      style={{ width: '720px' }}
    >
      <div style={{ marginBottom: 16, float: 'right' }}>
        <Input
          style={{ width: 300 }}
          prefix={<IconSearch />}
          placeholder="搜索用户"
          value={searchValue}
          onChange={handleSearchChange}
          onPressEnter={(e) => handleSearchChange(e.target.value)}
        />
      </div>

      <Spin loading={modalLoading}>
        <Table
          rowKey="id"
          columns={columns}
          data={userList}
          pagination={false}
          scroll={{ y: 480, x: 680 }}
          border={false}
        />
      </Spin>

      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', marginTop: 12 }}>
        <Pagination
          size="small"
          current={page}
          pageSize={pageSize}
          total={total}
          onChange={setPage}
          onPageSizeChange={(pageSize) => {
            setPageSize(pageSize);
            setPage(1);
          }}
          showTotal
          showJumper
          sizeOptions={[10, 20, 50]}
        />
      </div>
    </Modal>
  );
};

export default UserSelectModal;
