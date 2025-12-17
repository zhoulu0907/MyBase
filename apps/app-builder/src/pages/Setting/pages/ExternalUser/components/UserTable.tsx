import { PermissionButton as Button } from '@/components/PermissionControl';
import PlaceholderPanel from '@/components/PlaceholderPanel';
import StatusTag, { getStatusLabel } from '@/components/StatusTag';
import {
  Avatar,
  Dropdown,
  Input,
  Menu,
  Message,
  Modal,
  Pagination,
  Select,
  Space,
  Table,
  Tag
} from '@arco-design/web-react';
import { IconMoreVertical, IconPlus } from '@arco-design/web-react/icon';
import { TENANT_THIRD_PERMISSION as ACTIONS, hasAllPermissions, hasPermission } from '@onebase/common';
import type { externalUserListParams, updateExternalPwdParams } from '@onebase/platform-center';
import {
  deleteExternalUserApi,
  getExternalUserListApi,
  PlatformTenantStatus,
  StatusEnum,
  updateExternalUserPwdApi,
  updateStatusApi
} from '@onebase/platform-center';
import { debounce } from 'lodash-es';
import { useCallback, useEffect, useState } from 'react';
import s from '../index.module.less';
import UserFormModal from './UserFormModal';
import UserProfileAvatar from '@/components/UserProfileAvatar';
import { CreateSource, CreateSourceValue, statusOptions } from '../constant';
import type { CreateSourceKey, DataItem, externalUserRecord, userApplicationList, UserTableProps } from '../type';

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
  const [editingUser, setEditingUser] = useState<externalUserRecord | undefined>();
  const [data, setData] = useState<externalUserRecord[]>([]);
  const [total, setTotal] = useState(0);
  const [detailUser, setDetailUser] = useState<externalUserRecord | undefined>();
  const [userModalVisible, setUserModalVisible] = useState(false);
  const [detailModalVisible, setDetailModalVisible] = useState(false);

  // 查询用户列表
  const getUserList = useCallback(
    async (searchValue?: string) => {
      const params: externalUserListParams = {
        pageNo: page,
        pageSize
      };
      if (selectedDeptId) params.deptId = selectedDeptId;
      const res = await getExternalUserListApi(params);
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

  const handleEdit = (record: externalUserRecord) => {
    setEditingUser(record);
    setUserModalVisible(true);
  };

  const handleCreate = () => {
    setEditingUser(undefined);
    setUserModalVisible(true);
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
  const handleResetPassword = (record: externalUserRecord) => {
    Modal.confirm({
      title: `确定要重置用户(${record.nickName})的密码吗?`,
      content: '密码重置后，原密码失效，新密码将以短信形式发送至用户。',
      okButtonProps: { status: 'danger' },
      onOk: async () => {
        const params: updateExternalPwdParams = {
          id: record.id
        };
        await updateExternalUserPwdApi(params);
        Message.success('重置成功');
      }
    });
  };

  // 禁用用户，需确认
  const handleStatusUpdate = async (record: externalUserRecord) => {
    if (record.status === 0) {
      const params = { id: record.id, status: 1 };
      try {
        await updateStatusApi(params);
        await getUserList();
      } catch (error) {
        Message.error(`启用失败`);
      }
    } else {
      return Modal.confirm({
        title: `禁用企业(${record.nickName})? `,
        content: '禁用后企业用户无法登录，再次启用时企业可恢复正常使用',
        okButtonProps: {
          status: 'danger'
        },
        onOk: async () => {
          const params = { id: record.id, status: 0 };
          try {
            await updateStatusApi(params);
            await getUserList();
          } catch (error) {
            Message.error(`禁用失败`);
          }
        }
      });
    }
  };

  // 删除
  const handleDelete = (record: externalUserRecord) => {
    Modal.confirm({
      title: `确认要删除账号（${record.nickName}）吗？`,
      content: '删除用户后，用户将无法登录，用户数据将被永久删除，请谨慎操作。',
      okButtonProps: { status: 'danger' },
      onOk: async () => {
        await deleteExternalUserApi(record.id);
        Message.success('删除成功');
        onRefreshDept();
        getUserList();
      }
    });
  };

  // 查看详情
  const handleViewDetail = (record: externalUserRecord) => {
    setDetailUser(record);
    setDetailModalVisible(true);
  };

  const renderAuthorizedAppGroup = (applicationList: userApplicationList[]) => {
    return (
      <>
        {applicationList?.length > 0 ? (
          <div style={{ display: 'flex', alignItems: 'flex-end' }}>
            <Avatar.Group
              size={24}
              maxCount={5}
              maxPopoverTriggerProps={{
                disabled: true
              }}
            >
              {applicationList?.map((item, index) => {
                return (
                  <Avatar key={index} style={{ backgroundColor: item.iconColor }}>
                    {item.iconName}
                  </Avatar>
                );
              })}
            </Avatar.Group>
          </div>
        ) : (
          <></>
        )}
      </>
    );
  };

  const getColumns = (handleEdit: (record: externalUserRecord) => void) => {
    return [
      {
        title: '姓名',
        dataIndex: 'nickName',
        width: 140,
        ellipsis: true,
        render: (_: any, record: externalUserRecord) => (
          <>
            <UserProfileAvatar adminInfo={record} size={25} />
            <span className={s.tableColumnUsername} onClick={() => handleViewDetail(record)}>
              {record.nickName}
            </span>
          </>
        )
      },
      { title: '手机号', dataIndex: 'mobile', width: 140 },
      {
        title: '授权应用',
        width: 140,
        dataIndex: 'userApplicationList',
        render: (apps: userApplicationList[]) => <div>{renderAuthorizedAppGroup(apps)}</div>
      },
      {
        title: '来源',
        width: 100,
        dataIndex: 'createSource',
        render: (source: CreateSourceKey) => (
          <Tag color={source === CreateSourceValue.BACK ? "cyan" : 'blue'} size="small">
            {CreateSource[source]}
          </Tag>
        )
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
        width: 200,
        render: (_: any, record: any) => (
          <Space>
            <Button permission={ACTIONS.UPDATE} type="text" onClick={() => handleEdit(record)}>
              编辑
            </Button>
            <Button permission={ACTIONS.RESETPWD} type="text" onClick={() => handleResetPassword(record)}>
              重置密码
            </Button>
            {hasAllPermissions([ACTIONS.ENABLE, ACTIONS.DELETE]) ? (
              <Dropdown
                droplist={
                  <Menu>
                    <Menu.Item key="disable" onClick={() => handleStatusUpdate(record)}>
                      {getStatusLabel(record.status === StatusEnum.DISABLE ? StatusEnum.ENABLE : StatusEnum.DISABLE)}
                    </Menu.Item>
                    <Menu.Item key="del" onClick={() => handleDelete(record)}>
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
                <Button permission={ACTIONS.DELETE} type="text" onClick={() => handleDelete(record)}>
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

  return (
    <div>
      {/* 操作区 */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 16 }}>
        <Space>
          <Button permission={ACTIONS.CREATE} type="primary" icon={<IconPlus />} onClick={handleCreate}>
            新建用户
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
        tableData={deptTree}
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
        tableData={deptTree}
        deptLoading={deptLoading}
        onRefreshDept={onRefreshDept}
      />
    </div>
  );
}
