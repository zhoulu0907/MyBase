import StatusTag, { getStatusLabel } from "@/components/StatusTag";
import {
  Button,
  Dropdown,
  Form,
  Input,
  Menu,
  Message,
  Modal,
  Pagination,
  Space,
  Table,
  TreeSelect,
  Typography,
} from "@arco-design/web-react";
import {
  IconMoreVertical,
  IconSearch,
  IconPlus,
} from "@arco-design/web-react/icon";
import {
  deleteUser,
  exportUser,
  getUserPage,
  resetUserPassword,
  updateUserStatus,
} from "@onebase/platform-center";
import { useEffect, useState, useCallback } from "react";
import s from "../index.module.less";
import UserFormModal from "./UserFormModal";
import type { UserVO, PageParam } from "@onebase/platform-center";
import { StatusEnum } from "@onebase/platform-center";
import { debounce } from "lodash-es";

interface UserTableProps {
  selectedDeptId?: number;
  onTotalUserCountChange: (count: number) => void;
  deptTree: any[]; // 部门树数据
  deptLoading: boolean; // 部门数据加载状态
}

type UserRecord = Pick<UserVO, "id" | "username" | "nickname"> &
  Partial<UserVO>;

export default function UserTable({
  selectedDeptId = undefined,
  onTotalUserCountChange,
  deptTree,
  deptLoading,
}: UserTableProps) {
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [search, setSearch] = useState("");
  const [userModalVisible, setUserModalVisible] = useState(false);
  const [editingUser, setEditingUser] = useState<UserRecord | undefined>();
  const [data, setData] = useState<UserRecord[]>([]);
  const [total, setTotal] = useState(0);
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [detailUser, setDetailUser] = useState<UserRecord | undefined>();
  const [exportModalVisible, setExportModalVisible] = useState(false);
  const [exportForm] = Form.useForm();

  // 查询用户列表
  const getUserList = useCallback(
    async (searchValue?: string) => {
      const params: PageParam = {
        pageNo: page,
        pageSize,
      };
      if (selectedDeptId) params.deptId = selectedDeptId;
      console.log(searchValue);
      if (searchValue) params.nickname = searchValue;
      const res = await getUserPage(params);
      setData(res.list || []);
      setTotal(res.total || 0);
      onTotalUserCountChange(res.total || 0);
    },
    [page, pageSize, selectedDeptId, onTotalUserCountChange],
  );

  const debouncedSearch = useCallback(
    debounce((value: string) => {
      getUserList(value);
    }, 300),
    [getUserList],
  );

  useEffect(() => {
    getUserList();
  }, [selectedDeptId, page, pageSize]);

  const handleEdit = (record: UserRecord) => {
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
    [debouncedSearch],
  );

  // 导出功能 本期暂不实现
  // const handleExport = () => {
  //   setExportModalVisible(true);
  // }

  // 处理导出确认
  const handleExportOk = async () => {
    try {
      const values = await exportForm.validate();
      await exportUser("用户列表", {
        deptIds: values.deptIds,
        username: search || undefined,
      });
      Message.success("导出成功");
      setExportModalVisible(false);
    } catch (error) {
      console.error("导出失败:", error);
      Message.error("导出失败");
    }
  };

  const handleModalOk = () => {
    setUserModalVisible(false);
    getUserList();
  };

  // 重置密码
  const handleResetPassword = (record: UserRecord) => {
    Modal.confirm({
      title: `确定重置账号 ${record.nickname} 的密码？`,
      content: "密码重置后，原密码失效，请将新密码发送至用户。",
      onOk: async () => {
        // TODO: 待接口修改后验证
        const res = await resetUserPassword(record.id);
        Modal.success({
          title: "重置成功",
          okText: "我已知晓",
          content: <Typography.Text copyable>新密码为：{res}</Typography.Text>,
        });
      },
    });
  };

  // 禁用用户，需确认
  const handleStatusUpdate = (record: UserRecord) => {
    const newStatus =
      record.status === StatusEnum.ENABLE
        ? StatusEnum.DISABLE
        : StatusEnum.ENABLE;
    const newLabel = getStatusLabel(newStatus);
    Modal.confirm({
      title: `确定要${newLabel}账号 ${record.nickname} 吗？`,
      content:
        newStatus === StatusEnum.DISABLE
          ? "禁用状态下，用户无法登录系统，再次启用时用户可恢复正常使用"
          : "",
      onOk: async () => {
        // TODO: 待接口修改后验证
        await updateUserStatus(record.id, newStatus);
        Message.success(`${newLabel}成功`);
        getUserList();
      },
    });
  };

  // 删除
  const handleDelete = (record: UserRecord) => {
    Modal.confirm({
      title: `确认要删除用户 ${record.nickname} 吗？`,
      content: "删除用户后，用户将无法登录，用户数据将被永久删除，请谨慎操作。",
      onOk: async () => {
        await deleteUser(record.id);
        Message.success("删除成功");
        getUserList();
      },
    });
  };

  // 查看详情
  const handleViewDetail = (record: UserRecord) => {
    setDetailUser(record);
    setDetailModalVisible(true);
  };

  const getColumns = (handleEdit: (record: UserRecord) => void) => {
    return [
      {
        title: "姓名",
        dataIndex: "nickname",
        width: 120,
        ellipsis: true,
        render: (_: any, record: UserRecord) => (
          <span
            className={s.tableColumnUsername}
            onClick={() => handleViewDetail(record)}
          >
            {record.nickname}
          </span>
        ),
      },
      { title: "手机号", dataIndex: "mobile", width: 140 },
      {
        title: "邮箱",
        dataIndex: "email",
        width: 180,
        placeholder: "-",
        ellipsis: true,
      },
      {
        title: "部门",
        dataIndex: "deptName",
        width: 180,
        placeholder: "-",
        ellipsis: true,
      },
      {
        title: "状态",
        dataIndex: "status",
        width: 80,
        render: (val: number) => <StatusTag status={val} />,
      },
      {
        title: "操作",
        dataIndex: "op",
        width: 180,
        render: (_: any, record: any) => (
          <Space>
            <Button type="text" onClick={() => handleEdit(record)}>
              编辑
            </Button>
            <Button type="text" onClick={() => handleResetPassword(record)}>
              重置密码
            </Button>
            <Dropdown
              droplist={
                <Menu>
                  <Menu.Item
                    key="disable"
                    onClick={() => handleStatusUpdate(record)}
                  >
                    {getStatusLabel(
                      record.status === StatusEnum.DISABLE
                        ? StatusEnum.ENABLE
                        : StatusEnum.DISABLE,
                    )}
                  </Menu.Item>
                  <Menu.Item key="del" onClick={() => handleDelete(record)}>
                    删除
                  </Menu.Item>
                </Menu>
              }
              position="br"
              trigger="click"
            >
              <a style={{ cursor: "pointer" }}>
                <IconMoreVertical />
              </a>
            </Dropdown>
          </Space>
        ),
      },
    ];
  };

  return (
    <div>
      {/* 操作区 */}
      <div style={{ display: "flex", alignItems: "center", marginBottom: 16 }}>
        <Space>
          <Button type="primary" icon={<IconPlus />} onClick={handleCreate}>
            新建
          </Button>
        </Space>
        <div style={{ flex: 1 }} />
        <Input
          style={{ width: 220, marginRight: 12, borderRadius: 24 }}
          prefix={<IconSearch />}
          placeholder="输入用户名称"
          value={search}
          onChange={handleSearch}
          onPressEnter={handleSearch}
          allowClear
        />
        {/* 导出本期暂不实现 */}
        {/* <Button icon={<IconDownload />} onClick={handleExport}>导出</Button> */}
      </div>
      {/* 表格 */}
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
          display: "flex",
          alignItems: "center",
          justifyContent: "flex-end",
          marginTop: 12,
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
      <UserFormModal
        visible={userModalVisible}
        initialValues={editingUser}
        mode={editingUser ? "edit" : "create"}
        onCancel={() => setUserModalVisible(false)}
        onOk={handleModalOk}
        deptTree={deptTree}
        deptLoading={deptLoading}
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
      />
      {/* 导出对话框 */}
      <Modal
        title="导出用户"
        visible={exportModalVisible}
        onOk={handleExportOk}
        onCancel={() => setExportModalVisible(false)}
        unmountOnExit
      >
        <Form form={exportForm} layout="vertical">
          <Form.Item
            label="选择部门"
            field="deptIds"
            rules={[{ required: true, message: "请选择部门" }]}
          >
            <TreeSelect
              placeholder="请选择部门"
              treeData={deptTree}
              multiple
              allowClear
              treeCheckable
              showSearch
              loading={deptLoading}
              disabled={deptLoading}
            />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
