import { listToTree } from '@/utils/tree';
import { Input, Message, Modal, Space, Table } from '@arco-design/web-react';
import { IconPlus, IconSearch, IconCaretDown, IconCaretRight } from '@arco-design/web-react/icon';
import { createDept, deleteDept, getDeptList, updateDept, type DeptForm, type DeptVO } from '@onebase/platform-center';
import { debounce } from 'lodash-es';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import OrganizationModal from './components/OrganizationModal';
import PlaceholderPanel from '@/components/PlaceholderPanel';
import { PermissionButton as Button } from '@/components/PermissionControl';
import { hasAnyPermission, hasPermission } from '@/utils/permission';
import { TENANT_DEPT_PERMISSION as ACTIONS } from '@/constants/permission';
import styles from './index.module.less';

const OrganizationPage: React.FC = () => {
  const [data, setData] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchValue, setSearchValue] = useState('');
  const [modalVisible, setModalVisible] = useState(false);
  const [modalLoading, setModalLoading] = useState(false);
  const [editRecord, setEditRecord] = useState<any>(null);

  const columns = [
    {
      title: '部门名称',
      dataIndex: 'name'
    },
    {
      title: '部门描述',
      dataIndex: 'remark',
      placeholder: '-'
    },
    {
      title: '管理员',
      dataIndex: 'leaderUserName',
      placeholder: '-'
    },
    {
      title: '用户数量',
      dataIndex: 'userCount',
      align: 'center' as const
    },
    {
      title: '操作',
      dataIndex: 'operations',
      width: 150,
      render: (_: any, record: any) => (
        <Space size="mini">
          <Button permission={ACTIONS.UPDATE} type="text" onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Button permission={ACTIONS.DELETE} type="text" onClick={() => handleDelete(record)}>
            删除
          </Button>
        </Space>
      )
    }
  ];

  const filteredColumns = useMemo(() => {
    const allowOps = hasAnyPermission([ACTIONS.UPDATE, ACTIONS.DELETE]);
    if (allowOps) return columns;
    return columns.filter((column) => column.dataIndex !== 'operations');
  }, [columns]);

  const handleEdit = (record: any) => {
    setEditRecord(record);
    setModalVisible(true);
  };

  const handleDelete = (record: DeptVO) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除部门 "${record.name}" 吗？此操作不可恢复。`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        deleteDept(record.id).then(() => {
          Message.success('删除成功');
          fetchDeptList(searchValue);
        });
      }
    });
  };

  const handleAdd = () => {
    setEditRecord(null);
    setModalVisible(true);
  };

  // 添加/编辑 提交
  const handleModalConfirm = async (values: DeptForm) => {
    setModalLoading(true);
    try {
      if (editRecord?.id) {
        await updateDept({ ...values, id: editRecord.id });
        Message.success(`编辑成功`);
      } else {
        await createDept(values);
        Message.success(`添加成功`);
      }
      handleSearch(searchValue);
      setModalVisible(false);
    } catch (error) {
      console.error('操作失败，请重试');
      setModalLoading(false);
    } finally {
      setModalLoading(false);
    }
  };

  const fetchDeptList = async (name?: string) => {
    setLoading(true);
    try {
      const res = await getDeptList({ name });
      // 将平铺的部门列表数据转换为树形结构
      const treeData = listToTree(res);
      setData(treeData);
    } finally {
      setLoading(false);
    }
  };

  const debouncedSearch = useCallback(
    debounce((value: string) => {
      fetchDeptList(value);
    }, 300),
    [fetchDeptList]
  );

  const handleSearch = useCallback(
    (value: string) => {
      setSearchValue(value);
      debouncedSearch(value);
    },
    [debouncedSearch]
  );

  useEffect(() => {
    fetchDeptList();
  }, []);

  return (
    <div className={styles.organizationPage}>
      <div className={styles.pageHeader}>
        <div className={styles.leftContent}>
          <Button permission={ACTIONS.CREATE} type="primary" onClick={handleAdd} icon={<IconPlus />}>
            添加
          </Button>
        </div>
        <div className={styles.rightContent}>
          <Input
            placeholder="请输入部门名称"
            prefix={<IconSearch />}
            value={searchValue}
            onChange={handleSearch}
            onPressEnter={(e) => handleSearch(e.target.value)}
            style={{ width: 300 }}
          />
        </div>
      </div>
      <PlaceholderPanel hasPermission={hasPermission(ACTIONS.QUERY)}>
        <Table
          loading={loading}
          columns={filteredColumns}
          data={data}
          rowKey="id"
          childrenColumnName="children"
          pagination={false}
          virtualized={true}
          scroll={{ y: 600 }}
          expandProps={{
            expandRowByClick: true,
            strictTreeData: true,
            icon: ({ expanded }) =>
              expanded ? <IconCaretDown style={{ marginRight: 8 }} /> : <IconCaretRight style={{ marginRight: 8 }} />,
            width: 12
          }}
        />
      </PlaceholderPanel>

      <OrganizationModal
        visible={modalVisible}
        onCancel={() => setModalVisible(false)}
        onConfirm={handleModalConfirm}
        loading={modalLoading}
        initialValues={editRecord || undefined}
      />
    </div>
  );
};

export default OrganizationPage;
