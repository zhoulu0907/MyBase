import { PermissionButton as Button } from '@/components/PermissionControl';
import PlaceholderPanel from '@/components/PlaceholderPanel';
import { listToTree } from '@/utils/tree';
import { Input, Message, Modal, Space, Table } from '@arco-design/web-react';
import { IconCaretDown, IconCaretRight, IconPlus, IconSearch } from '@arco-design/web-react/icon';
import { TENANT_DEPT_PERMISSION as ACTIONS, hasAnyPermission, hasPermission } from '@onebase/common';
import { createDept, deleteDept, getDeptList, updateDept, type DeptForm, type DeptVO } from '@onebase/platform-center';
import dayjs from 'dayjs';
import { debounce } from 'lodash-es';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import OrganizationModal from './components/OrganizationModal';
import styles from './index.module.less';

const OrganizationPage: React.FC = () => {
  const [data, setData] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchValue, setSearchValue] = useState('');
  const [modalVisible, setModalVisible] = useState(false);
  const [modalLoading, setModalLoading] = useState(false);
  const [editRecord, setEditRecord] = useState<any>(null);
  const [isSubDept, setIsSubDept] = useState<boolean>(false); // 子部门
  const [modalType, setModalType] = useState<'create' | 'edit'>(); // 子部门

  const columns = [
    {
      title: '部门名称',
      dataIndex: 'name'
    },
    {
      title: '部门接口人',
      dataIndex: 'adminUserName',
      placeholder: '-'
    },
    {
      title: '部门主管',
      dataIndex: 'leaderUserName',
      placeholder: '-'
    },
    {
      title: '用户数量',
      dataIndex: 'userCount',
      placeholder: '-',
      sorter: (a: DeptVO, b: DeptVO) => a.userCount - b.userCount
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      placeholder: '-',
      render: (_: any, record: DeptVO) => dayjs(record?.createTime).format('YYYY-MM-DD HH:mm:ss')
    },
    {
      title: '操作',
      dataIndex: 'operations',
      width: 242,
      render: (_: any, record: any) => (
        <Space size="mini">
          <Button permission={ACTIONS.SUB_DEPT} type="text" onClick={(e) => handleAddSubDepart(e, record)}>
            添加子部门
          </Button>
          <Button permission={ACTIONS.UPDATE} type="text" onClick={(e) => handleEdit(e, record)}>
            编辑
          </Button>
          <Button permission={ACTIONS.DELETE} type="text" onClick={(e) => handleDelete(e, record)}>
            删除
          </Button>
        </Space>
      )
    }
  ];

  const filteredColumns = useMemo(() => {
    const allowOps = hasAnyPermission([ACTIONS.SUB_DEPT, ACTIONS.UPDATE, ACTIONS.DELETE]);
    if (allowOps) return columns;
    return columns.filter((column) => column.dataIndex !== 'operations');
  }, [columns]);

  const handleAddSubDepart = (e: any, record: DeptVO) => {
    e.stopPropagation();
    setIsSubDept(true);
    setModalType('create');
    setEditRecord({ parentId: record?.id });
    setModalVisible(true);
  };

  const handleEdit = (e: any, record: DeptVO) => {
    e.stopPropagation();
    setModalType('edit');
    setIsSubDept(record?.children.length === 0);
    setEditRecord(record);
    setModalVisible(true);
  };

  const handleDelete = (e: any, record: DeptVO) => {
    e.stopPropagation();
    Modal.confirm({
      title: `确认要删除部门（${record.name}）吗？`,
      content: '删除部门后，该部门下的用户将转移到其他部门，请谨慎操作。',
      okText: '确认',
      cancelText: '取消',
      okButtonProps: {
        status: 'danger'
      },
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
    setIsSubDept(false);
    setModalType('create');
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
            添加部门
          </Button>
        </div>
        <div className={styles.rightContent}>
          <Input
            className={styles.inputSearch}
            placeholder="请输入部门名称"
            prefix={<IconSearch />}
            value={searchValue}
            onChange={handleSearch}
            onPressEnter={(e) => handleSearch(e.target.value)}
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
        isSubDept={isSubDept}
        modalType={modalType}
      />
    </div>
  );
};

export default OrganizationPage;
