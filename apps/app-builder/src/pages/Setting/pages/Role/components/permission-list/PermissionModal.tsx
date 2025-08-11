import { listToTree } from '@/utils/tree';
import { Checkbox, Input, Modal, Table } from '@arco-design/web-react';
import type { ColumnProps } from '@arco-design/web-react/es/Table';
import type { Permission } from '@onebase/platform-center';
import { getAllPermissions } from '@onebase/platform-center';
import React, { useEffect, useMemo, useState } from 'react';

const Search = Input.Search;
const CheckboxGroup = Checkbox.Group;

interface PermissionConfigModalProps {
  visible: boolean;
  onCancel: () => void;
  onConfirm: (values: any) => void;
  confirmLoading?: boolean;
  configuredPermissions?: Record<number, number[]>;
}

const PermissionConfigModal: React.FC<PermissionConfigModalProps> = ({
  visible,
  onCancel,
  onConfirm,
  confirmLoading = false,
  configuredPermissions = {}
}) => {
  const [searchValue, setSearchValue] = useState('');
  const [selectedActions, setSelectedActions] = useState<Record<number, number[]>>({}); // 所选中的操作选线
  const [indeterminateMap, setIndeterminateMap] = useState<Record<number, boolean>>({}); // 权限功能的半选状态
  const [checkAllMap, setCheckAllMap] = useState<Record<number, boolean>>({}); // 权限功能的全选状态
  const [tableData, setTableData] = useState<Permission[]>([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10
  });

  // 加载权限数据
  const loadPermissions = async () => {
    setLoading(true);
    try {
      const data = await getAllPermissions(); // TODO: 待接口修改后联调
      const tree = listToTree(data);
      setTableData(tree as Permission[]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPermissions();
  }, [visible, searchValue]);

  useEffect(() => {
    if (!visible) return;
    setPagination((prev) => ({
      ...prev,
      current: 1
    }));
    loadPermissions();
  }, [visible]);

  const filteredPermissions = useMemo(() => {
    if (!searchValue) {
      return tableData;
    }

    const search = searchValue.toLowerCase();
    return tableData.filter(
      (permission) =>
        permission.name.toLowerCase().includes(search) || (permission.remark?.toLowerCase().includes(search) ?? false)
    );
  }, [tableData, searchValue]);

  useEffect(() => {
    // 初始化选中状态
    if (configuredPermissions && Object.keys(configuredPermissions).length > 0) {
      setSelectedActions(configuredPermissions);
    }

    // 计算每个权限的选中状态
    const newIndeterminateMap: Record<number, boolean> = {};
    const newCheckAllMap: Record<number, boolean> = {};

    tableData.forEach((permission) => {
      const selectedActionIds = selectedActions[permission.id] || [];
      const actionIds = permission.children?.map((action) => action.id) || [];

      if (actionIds.length > 0) {
        const allSelected = actionIds.every((id) => selectedActionIds.includes(id));
        const someSelected = selectedActionIds.length > 0 && !allSelected;

        newCheckAllMap[permission.id] = allSelected;
        newIndeterminateMap[permission.id] = someSelected;
      }
    });

    setIndeterminateMap(newIndeterminateMap);
    setCheckAllMap(newCheckAllMap);

    setPagination((prev) => ({
      ...prev,
      total: filteredPermissions.length
    }));
  }, [visible, configuredPermissions, tableData, selectedActions, filteredPermissions]);

  const handleSearch = () => {
    setPagination((prev) => ({
      ...prev,
      current: 1
    }));
  };

  // 权限全选处理
  const handleCheckAllChange = (permissionId: number, checked: boolean) => {
    const permission = tableData.find((p) => p.id === permissionId);
    if (!permission || !permission.children) return;

    const actionIds = permission.children.map((action) => action.id);

    if (checked) {
      setSelectedActions((prev) => ({
        ...prev,
        [permissionId]: actionIds
      }));
    } else {
      setSelectedActions((prev) => {
        const newSelected = { ...prev };
        delete newSelected[permissionId];
        return newSelected;
      });
    }
  };

  // 表头全选处理
  const handleHeaderCheckAllChange = (checked: boolean) => {
    const allActionIds: Record<number, number[]> = {};

    tableData.forEach((permission) => {
      if (permission.children) {
        allActionIds[permission.id] = permission.children.map((action) => action.id);
      }
    });

    if (checked) {
      setSelectedActions(allActionIds);
    } else {
      setSelectedActions({});
    }
  };

  // 操作权限选择处理
  const handleActionChange = (permissionId: number, actionIds: number[]) => {
    setSelectedActions((prev) => ({
      ...prev,
      [permissionId]: actionIds
    }));
  };

  // 表头选择框状态
  const headerCheckState = useMemo(() => {
    const startIndex = (pagination.current - 1) * pagination.pageSize;
    const endIndex = startIndex + pagination.pageSize;
    const currentPageData = filteredPermissions.slice(startIndex, endIndex);

    if (currentPageData.length === 0) {
      return {
        checked: false,
        indeterminate: false
      };
    }

    const allChecked = currentPageData.every((permission) => checkAllMap[permission.id]);
    const hasChecked = currentPageData.some(
      (permission) => checkAllMap[permission.id] || indeterminateMap[permission.id]
    );

    return {
      checked: allChecked,
      indeterminate: !allChecked && hasChecked
    };
  }, [pagination.current, pagination.pageSize, filteredPermissions, checkAllMap, indeterminateMap]);

  const columns: ColumnProps<Permission>[] = useMemo(
    () => [
      {
        title: (
          <div style={{ display: 'flex', alignItems: 'center' }}>
            <Checkbox
              style={{ marginRight: 8 }}
              indeterminate={headerCheckState.indeterminate}
              checked={headerCheckState.checked}
              onChange={(checked) => handleHeaderCheckAllChange(checked as boolean)}
            />
            权限功能
          </div>
        ),
        dataIndex: 'name',
        width: 200,
        render: (_, record) => (
          <div style={{ display: 'flex', alignItems: 'center' }}>
            <Checkbox
              style={{ marginRight: 8 }}
              indeterminate={indeterminateMap[record.id]}
              checked={checkAllMap[record.id]}
              onChange={(checked) => handleCheckAllChange(record.id, checked as boolean)}
            />
            {record.name}
          </div>
        )
      },
      {
        title: '描述',
        dataIndex: 'remark',
        width: 150,
        render: (text) => <span>{text || '-'}</span>
      },
      {
        title: '操作权限',
        dataIndex: 'children',
        width: 350,
        render: (_, record) => (
          <div>
            <CheckboxGroup
              value={selectedActions[record.id] || []}
              onChange={(value) => handleActionChange(record.id, value as number[])}
              options={(record.children || []).map((action) => ({
                label: action.name,
                value: action.id
              }))}
              style={{
                display: 'flex',
                flexWrap: 'wrap',
                maxHeight: 100,
                overflowY: 'auto'
              }}
            />
          </div>
        )
      }
    ],
    [
      headerCheckState,
      indeterminateMap,
      checkAllMap,
      selectedActions,
      handleHeaderCheckAllChange,
      handleCheckAllChange,
      handleActionChange
    ]
  );

  const currentPageData = useMemo(() => {
    const startIndex = (pagination.current - 1) * pagination.pageSize;
    const endIndex = startIndex + pagination.pageSize;
    return filteredPermissions.slice(startIndex, endIndex);
  }, [pagination.current, pagination.pageSize, filteredPermissions]);

  return (
    <Modal
      title={
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center'
          }}
        >
          <span>权限配置</span>
          <Search
            placeholder="请输入权限名称"
            style={{ width: 240 }}
            allowClear
            value={searchValue}
            onChange={(value) => setSearchValue(value)}
            onSearch={handleSearch}
          />
        </div>
      }
      visible={visible}
      onCancel={onCancel}
      onOk={() => onConfirm(selectedActions)}
      confirmLoading={confirmLoading}
      style={{ width: 900 }}
    >
      <Table
        rowKey="id"
        childrenColumnName="actions"
        columns={columns}
        data={currentPageData}
        loading={loading}
        pagination={false}
      />
    </Modal>
  );
};

export default PermissionConfigModal;
