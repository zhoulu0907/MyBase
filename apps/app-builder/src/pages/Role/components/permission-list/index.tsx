import { Button, Table, Modal, Message } from "@arco-design/web-react";
import { useState, useCallback, useMemo, useEffect } from "react";
import {
  getConfiguredPermissions,
  configureRolePermissions,
  removeRolePermission,
} from "@onebase/platform-center";
import PermissionConfigModal from "./PermissionModal";

interface Permission {
  id: number;
  name: string;
  type: string;
  remark?: string;
  actions?: PermissionAction[];
}

interface PermissionAction {
  id: number;
  name: string;
}

interface PermissionListProps {
  selectedRoleId?: number;
}

const PermissionList: React.FC<PermissionListProps> = ({ selectedRoleId }) => {
  const [permissions, setPermissions] = useState<Permission[]>([]);
  const [configModalVisible, setConfigModalVisible] = useState(false);
  const [configLoading, setConfigLoading] = useState(false);
  const [loading, setLoading] = useState(false);

  // 生成mock权限数据
  const mockPermissions = useMemo(
    () => [
      {
        id: 1,
        name: "用户管理",
        type: "user:manage",
        remark: "管理系统用户信息",
        actions: [
          { id: 1, name: "查看" },
          { id: 2, name: "编辑" },
          { id: 3, name: "删除" },
        ],
      },
      {
        id: 2,
        name: "角色管理",
        type: "role:manage",
        remark: "管理系统角色配置",
        actions: [
          { id: 4, name: "查看" },
          { id: 5, name: "编辑" },
          { id: 6, name: "删除" },
          { id: 7, name: "分配" },
        ],
      },
      {
        id: 3,
        name: "权限管理",
        type: "permission:manage",
        remark: "管理系统权限配置",
        actions: [
          { id: 8, name: "查看" },
          { id: 9, name: "编辑" },
        ],
      },
      {
        id: 4,
        name: "系统配置",
        type: "system:config",
        remark: "管理系统基础配置",
        actions: [
          { id: 10, name: "查看" },
          { id: 11, name: "编辑" },
          { id: 12, name: "删除" },
        ],
      },
    ],
    [],
  );

  // 获取已配置的权限数据
  const fetchPermissions = useCallback(async () => {
    if (selectedRoleId === undefined || selectedRoleId === null) {
      setPermissions(mockPermissions);
      return;
    }

    setLoading(true);
    try {
      const response = await getConfiguredPermissions(selectedRoleId);
      setPermissions(response.data || []);
    } catch (error) {
      // TODO: 联调后移除mock data
      setPermissions(mockPermissions);
    } finally {
      setLoading(false);
    }
  }, [selectedRoleId, mockPermissions]);

  useEffect(() => {
    fetchPermissions();
  }, [fetchPermissions]);

  // 处理移除权限
  const handleRemove = useCallback(
    (id: number) => {
      Modal.confirm({
        title: "确认移除",
        content: `确定要移除权限 "${permissions.find((p) => p.id === id)?.name}" 吗？`,
        okText: "确认",
        cancelText: "取消",
        onOk: () => {
          removeRolePermission(selectedRoleId!, id).then(() => {
            Message.success("操作成功");
          });
        },
      });
    },
    [permissions],
  );

  // 权限配置
  const handleConfig = useCallback(() => {
    if (selectedRoleId === undefined || selectedRoleId === null) {
      Message.warning("请先选择一个角色");
      return;
    }
    setConfigModalVisible(true);
  }, [selectedRoleId]);

  // 权限配置
  const handleConfigConfirm = async (permissions: Record<number, number[]>) => {
    if (!selectedRoleId) {
      Message.error("角色ID不存在");
      return;
    }

    setConfigLoading(true);
    try {
      await configureRolePermissions(selectedRoleId, permissions);
      Message.success("权限配置成功");
      setConfigModalVisible(false);
      fetchPermissions();
    } catch (error) {
      Message.error("权限配置失败，请重试");
    } finally {
      setConfigLoading(false);
    }
  };

  const columns = useMemo(
    () => [
      {
        title: "权限功能",
        dataIndex: "name",
        width: 200,
      },
      {
        title: "描述",
        dataIndex: "remark",
      },
      {
        title: "操作权限",
        dataIndex: "actions",
        render: (_: any, record: Permission) => {
          return record.actions?.map((item) => item.name).join("，") || "-";
        },
      },
      {
        title: "操作",
        dataIndex: "op",
        width: 120,
        render: (_: any, record: Permission) => (
          <Button
            type="text"
            size="small"
            onClick={() => handleRemove(record.id)}
          >
            移除
          </Button>
        ),
      },
    ],
    [handleRemove],
  );

  const tableConfig = useMemo(
    () => ({
      rowKey: "id",
      columns,
      data: permissions,
      pagination: false,
      scroll: { y: 400 },
      border: false,
      loading,
    }),
    [columns, permissions, loading],
  );

  return (
    <>
      <div style={{ display: "flex", alignItems: "center", marginBottom: 16 }}>
        <Button type="primary" onClick={handleConfig}>
          权限配置
        </Button>
      </div>

      <Table {...tableConfig} />

      <PermissionConfigModal
        visible={configModalVisible}
        onCancel={() => setConfigModalVisible(false)}
        onConfirm={handleConfigConfirm}
        configuredPermissions={{} as Record<number, number[]>}
        confirmLoading={configLoading}
      />
    </>
  );
};

export default PermissionList;
