import { listToTree } from '@/utils/tree';
import { Checkbox, Input, Modal, Table } from '@arco-design/web-react';
import type { ColumnProps } from '@arco-design/web-react/es/Table';
import type { Permission } from '@onebase/platform-center';
import { getAllPermissions } from '@onebase/platform-center';
import React, { useEffect, useMemo, useState, useCallback } from 'react';
import { PERMISSION_TYPES } from '@/constants/permission';
import styles from '../../index.module.less'

const Search = Input.Search;
const CheckboxGroup = Checkbox.Group;

interface PermissionConfigModalProps {
  visible: boolean;
  onCancel: () => void;
  onConfirm: (values: any) => void;
  confirmLoading?: boolean;
  configuredPermissions: Permission[];
}

const PermissionConfigModal: React.FC<PermissionConfigModalProps> = ({
  visible,
  onCancel,
  onConfirm,
  confirmLoading = false,
  configuredPermissions = []
}) => {
  const [searchValue, setSearchValue] = useState('');
  const [selectedActions, setSelectedActions] = useState<Record<string, string[]>>({}); // 所选中的操作权限
  const [indeterminateMap, setIndeterminateMap] = useState<Record<string, boolean>>({}); // 权限功能的半选状态
  const [checkAllMap, setCheckAllMap] = useState<Record<string, boolean>>({}); // 权限功能的全选状态
  const [tableData, setTableData] = useState<Permission[]>([]);
  const [loading, setLoading] = useState(false);

  // 加载所有权限数据
  const loadPermissions = async () => {
    setLoading(true);
    try {
      const data = await getAllPermissions();
      // 接口返回三层：模块、功能、操作，需筛选出后两层数据
      const filteredData = data.filter(item => item.type !== PERMISSION_TYPES.MODULE);
      const tree = listToTree(filteredData);
      setTableData(tree as Permission[]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPermissions();
  }, [visible]);

  useEffect(() => {
    if (!visible) return;
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
    if (configuredPermissions?.length) {
      const initSelectedActions = configuredPermissions.reduce((acc, cur) => {
        if (cur.children?.length) {
          acc[cur.id] = cur.children.map(action => action.id);
        }
        return acc;
      }, {} as Record<string, string[]>);
      setSelectedActions(initSelectedActions);
    }
  }, [visible, configuredPermissions, tableData, filteredPermissions]);

  useEffect(() => {
    // 计算每个权限的选中状态
    const newIndeterminateMap: Record<string, boolean> = {};
    const newCheckAllMap: Record<string, boolean> = {};

    tableData.forEach((permission) => {
      const selectedActionIds = selectedActions[permission.id] || [];
      const actionIds = permission.children?.map((action) => action.id) || [];

      if (actionIds.length > 0) {
        const allSelected = actionIds.every((id) => selectedActionIds.includes(id));
        const someSelected = selectedActionIds.length > 0 && !allSelected;

        newCheckAllMap[permission.id] = allSelected;
        newIndeterminateMap[permission.id] = someSelected;
      } else if (selectedActions[permission.id]) {
        // 处理功能权限不包含子权限的情形
        newCheckAllMap[permission.id] = true;
        newIndeterminateMap[permission.id] = false;
      }
    });

    setIndeterminateMap(newIndeterminateMap);
    setCheckAllMap(newCheckAllMap);
  }, [selectedActions]);

  // 表格行全选处理
  const handleCheckAllChange = (permissionId: string, checked: boolean) => {
    const permission = tableData.find((p) => p.id === permissionId);
    if (!permission) return;

    const actionIds = permission.children?.map((action) => action.id);

    if (checked) {
      setSelectedActions((prev) => ({
        ...prev,
        [permissionId]: actionIds || []
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
    const allActionIds: Record<string, string[]> = {};

    if (checked) {
      tableData.forEach((permission) => {
        if (permission.children?.length) {
          allActionIds[permission.id] = permission.children.map((action) => action.id);
        } else {
          // 处理没有子权限的功能权限
          allActionIds[permission.id] = [];
        }
      });
      setSelectedActions(allActionIds);
    } else {
      setSelectedActions({});
    }
  };

  // 操作权限选择处理
  const handleActionChange = (permissionId: string, actionIds: string[]) => {
    setSelectedActions((prev) => ({
      ...prev,
      [permissionId]: actionIds
    }));
  };

  // 表头选择框状态
  const headerCheckState = useMemo(() => {
    if (filteredPermissions.length === 0) {
      return {
        checked: false,
        indeterminate: false
      };
    }

    const allChecked = filteredPermissions.every((permission) => checkAllMap[permission.id]);
    const hasChecked = filteredPermissions.some(
      (permission) => checkAllMap[permission.id] || indeterminateMap[permission.id]
    );

    return {
      checked: allChecked,
      indeterminate: !allChecked && hasChecked
    };
  }, [filteredPermissions, checkAllMap, indeterminateMap]);

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
        width: 140,
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
        title: '操作权限',
        dataIndex: 'children',
        width: 350,
        bodyCellStyle: { height: 'auto' },
        render: (_, record) => (
          <div>
            <CheckboxGroup
              value={selectedActions[record.id] || []}
              onChange={(value) => handleActionChange(record.id, value as string[])}
              options={(record.children || []).map((action) => ({
                label: action.name,
                value: action.id
              }))}
              style={{
                display: 'flex',
                flexWrap: 'wrap'
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

  const handleConfirm = useCallback(async () => {
    // 将选中的权限数据处理成扁平化数组并加上第一层父节点id传给接口
    const data = Object.keys(selectedActions).reduce((res: string[], key) => {
      res.push(key);
      res = res.concat(selectedActions[key]);
      const node = tableData.find((item) => item.id === key);
      node && res.push(node?.parentId);
      return res;
    }, []);
    onConfirm(data);
  }, [
    selectedActions
  ]);

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
            className={styles.permissionInput}
            placeholder="请输入权限名称"
            allowClear
            value={searchValue}
            onChange={(value) => setSearchValue(value)}
            onSearch={setSearchValue}
          />
        </div>
      }
      visible={visible}
      onCancel={onCancel}
      onOk={handleConfirm}
      confirmLoading={confirmLoading}
      style={{ width: 900 }}
    >
      <Table
        rowKey="id"
        childrenColumnName="actions"
        columns={columns}
        data={filteredPermissions}
        loading={loading}
      // pagination={false}
      />
    </Modal>
  );
};

export default PermissionConfigModal;
