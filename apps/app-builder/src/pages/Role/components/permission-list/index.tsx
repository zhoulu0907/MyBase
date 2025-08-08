import { Button, Table, Modal, Message } from '@arco-design/web-react';
import { useState, useCallback, useMemo, useEffect } from 'react';
import { getConfiguredPermissions, configureRolePermissions, removeRolePermission } from '@onebase/platform-center';
import PermissionConfigModal from './PermissionModal';

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

  // 获取已配置的权限数据
  const fetchPermissions = useCallback(async () => {
    if (selectedRoleId === undefined || selectedRoleId === null) {
      setPermissions([]);
      return;
    }

    setLoading(true);
    try {
      const response = await getConfiguredPermissions(selectedRoleId); // TODO: 待接口修改后联调
      setPermissions(response.data || []);
    } finally {
      setLoading(false);
    }
  }, [selectedRoleId]);

  useEffect(() => {
    fetchPermissions();
  }, [fetchPermissions]);

  // 处理移除权限
  const handleRemove = useCallback(
    (id: number) => {
      Modal.confirm({
        title: '确认移除',
        content: `确定要移除权限 "${permissions.find((p) => p.id === id)?.name}" 吗？`,
        okText: '确认',
        cancelText: '取消',
        onOk: () => {
          removeRolePermission(selectedRoleId!, id).then(() => {
            Message.success('操作成功');
          });
        }
      });
    },
    [permissions]
  );

  // 权限配置
  const handleConfig = useCallback(() => {
    if (selectedRoleId === undefined || selectedRoleId === null) {
      Message.warning('请先选择一个角色');
      return;
    }
    setConfigModalVisible(true);
  }, [selectedRoleId]);

  // 权限配置
  const handleConfigConfirm = async (permissions: Record<number, number[]>) => {
    if (!selectedRoleId) {
      Message.error('角色ID不存在');
      return;
    }

    setConfigLoading(true);
    try {
      await configureRolePermissions(selectedRoleId, permissions);
      Message.success('权限配置成功');
      setConfigModalVisible(false);
      fetchPermissions();
    } catch (error) {
      Message.error('权限配置失败，请重试');
    } finally {
      setConfigLoading(false);
    }
  };

  const columns = useMemo(
    () => [
      {
        title: '权限功能',
        dataIndex: 'name',
        width: 200
      },
      {
        title: '描述',
        dataIndex: 'remark'
      },
      {
        title: '操作权限',
        dataIndex: 'actions',
        render: (_: any, record: Permission) => {
          return record.actions?.map((item) => item.name).join('，') || '-';
        }
      },
      {
        title: '操作',
        dataIndex: 'op',
        width: 120,
        render: (_: any, record: Permission) => (
          <Button type="text" size="small" onClick={() => handleRemove(record.id)}>
            移除
          </Button>
        )
      }
    ],
    [handleRemove]
  );

  const tableConfig = useMemo(
    () => ({
      rowKey: 'id',
      columns,
      data: permissions,
      pagination: false,
      scroll: { y: 400 },
      border: false,
      loading
    }),
    [columns, permissions, loading]
  );

  return (
    <>
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: 16 }}>
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
