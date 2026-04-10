import ResizableTable from '@/components/ResizableTable';
import DeleteConfirmModal from '@/components/DeleteConfirmModal';
import ActionButtons from '@/components/ActionButtons';
import { listToTree } from '@/utils/tree';
import { Button, Message } from '@arco-design/web-react';
import { PERMISSION_TYPES } from '@onebase/common';
import type { Permission } from '@onebase/platform-center';
import { configureRolePermissions, getConfiguredPermissions, removeRolePermission, UserType } from '@onebase/platform-center';
import { useCallback, useEffect, useMemo, useState } from 'react';
import PermissionConfigModal from './PermissionModal';

interface PermissionListProps {
  selectedRoleId?: string;
  type?: number;
}

const PermissionList: React.FC<PermissionListProps> = ({ selectedRoleId, type }) => {
  const [permissions, setPermissions] = useState<Permission[]>([]);
  const [configModalVisible, setConfigModalVisible] = useState(false);
  const [configLoading, setConfigLoading] = useState(false);
  const [loading, setLoading] = useState(false);
  const [deleteModalVisible, setDeleteModalVisible] = useState(false);
  const [deleteTargetId, setDeleteTargetId] = useState<string | null>(null);

  // 获取已配置的权限数据
  const fetchPermissions = useCallback(async () => {
    if (selectedRoleId === undefined || selectedRoleId === null) {
      setPermissions([]);
      return;
    }

    setLoading(true);
    try {
      const response = (await getConfiguredPermissions(selectedRoleId)) || [];
      const filteredData = response.filter((item) => item.type !== PERMISSION_TYPES.MODULE);
      setPermissions(listToTree(filteredData) as Permission[]);
    } finally {
      setLoading(false);
    }
  }, [selectedRoleId]);

  useEffect(() => {
    fetchPermissions();
  }, [fetchPermissions]);

  // 找到要移除的权限及其所有子权限
  const getPermissionIds = (permissionId: string, data: any[]): string[] => {
    const permission = data.find((p) => p.id === permissionId);
    const ids = [permissionId];
    if (!permission) return [];
    if (permission.children) {
      permission.children.forEach((child: any) => {
        ids.push(...getPermissionIds(child.id, permission.children || []));
      });
    }
    return ids;
  };

  const handleRemove = useCallback(
    (id: string) => {
      setDeleteTargetId(id);
      setDeleteModalVisible(true);
    },
    []
  );

  const handleRemoveConfirm = async () => {
    if (!selectedRoleId || !deleteTargetId) return;
    const permissionIds = getPermissionIds(deleteTargetId, permissions);
    await removeRolePermission(selectedRoleId, permissionIds);
    setDeleteModalVisible(false);
    fetchPermissions();
    Message.success('操作成功');
  };

  // 权限配置
  const handleConfig = useCallback(() => {
    if (selectedRoleId === undefined || selectedRoleId === null) {
      Message.warning('请先选择一个角色');
      return;
    }
    setConfigModalVisible(true);
  }, [selectedRoleId]);

  // 权限配置
  const handleConfigConfirm = async (permissions: string[]) => {
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
        title: '操作权限',
        dataIndex: 'actions',
        render: (_: any, record: Permission) => {
          return record.children?.map((item) => item.name).join('，') || '-';
        }
      },
      {
        title: '操作',
        dataIndex: 'op',
        width: 120,
        render: (_: any, record: Permission) => (
          <ActionButtons>
            <Button type="text" size="small" disabled ={type === UserType.SYSTEM} onClick={() => handleRemove(record.id)}>
              移除
            </Button>
          </ActionButtons>
        )
      }
    ],
    [handleRemove]
  );

  const tableConfig = useMemo(
    () => ({
      rowKey: 'id',
      columns,
      data: permissions.filter((record) => record.children && record.children.length > 0),
      // pagination: false,
      scroll: { y: 500 },
      stripe: true,
      loading,
      childrenColumnName: 'actions'
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

      <ResizableTable {...tableConfig} />

      {configModalVisible && (
        <PermissionConfigModal
          visible={configModalVisible}
          onCancel={() => setConfigModalVisible(false)}
          onConfirm={handleConfigConfirm}
          configuredPermissions={permissions}
          confirmLoading={configLoading}
          type={type}
        />
      )}
      <DeleteConfirmModal
        visible={deleteModalVisible}
        onVisibleChange={setDeleteModalVisible}
        onConfirm={handleRemoveConfirm}
        title={deleteTargetId ? `确认要移除权限功能（${permissions.find((p) => p.id === deleteTargetId)?.name}）吗？` : '确认移除'}
        content="移除该权限功能后，关联该权限功能的角色用户将失去权限，请谨慎操作。"
      />
    </>
  );
};

export default PermissionList;
