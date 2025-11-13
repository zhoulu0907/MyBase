import { useAppStore } from '@/store/store_app';
import { Button, Message, Modal, Space, Table, type TableColumnProps } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import { deleteDatasource, getDatasource, getDatasourcePage, type DatasourceSaveReqVO } from '@onebase/app';
import { useEffect, useState } from 'react';
import styles from '../index.module.less';
import EditDsDrawer from './EditDsDrawer';

// 数据源记录类型
interface DatasourceRecord {
  id: number;
  datasourceName: string;
  code: string;
  datasourceType: string;
  description: string;
  runMode: number;
  appId: string;
}

const DataSourceTable = ({ handlePageType }: { handlePageType: (tab: string) => void }) => {
  const { curAppId } = useAppStore();
  const [dataSourceList, setDataSourceList] = useState<DatasourceRecord[]>([]);
  const [deleteModalVisible, setDeleteModalVisible] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState(false);
  const [currentDeleteId, setCurrentDeleteId] = useState<number | null>(null);
  const [editDrawerVisible, setEditDrawerVisible] = useState(false);
  const [currentDataSource, setCurrentDataSource] = useState<DatasourceSaveReqVO>();
  const [tableLoading, setTableLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState({
    pageNo: 1,
    pageSize: 10
  });

  const getTableData = async () => {
    setTableLoading(true);
    const params = {
      pageNo: page.pageNo,
      pageSize: page.pageSize,
      // datasourceName: '',
      // datasourceType: '',
      // code: '',
      // runMode: 0,
      appId: curAppId
    };
    const res = await getDatasourcePage(params);

    console.log('getTableData res', res);
    if (res?.list?.length > 0) {
      setDataSourceList(res?.list || []);
      setTotal(res?.total || 0);
    } else {
      handlePageType('empty-ds');
    }
    setTableLoading(false);
  };

  useEffect(() => {
    getTableData();
  }, []);

  const gotoEdit = async (id: number) => {
    console.log('handleEdit id', id);
    try {
      const res = await getDatasource(id);
      console.log('handleEdit res', res);
      if (res) {
        setCurrentDataSource(res);
        setEditDrawerVisible(true);
      }
    } catch (error) {
      console.error('获取数据源详情失败:', error);
    }
  };

  const handleEditSuccess = () => {
    setEditDrawerVisible(false);
    getTableData();
  };

  const handleDelete = (id: number) => {
    setCurrentDeleteId(id);
    setDeleteModalVisible(true);
  };

  const confirmDelete = async () => {
    if (!currentDeleteId) return;

    setDeleteLoading(true);
    try {
      const res = await deleteDatasource(currentDeleteId);
      if (res) {
        Message.success('删除成功');
        setDeleteModalVisible(false);
        setCurrentDeleteId(null);
        // 重新获取数据
        await getTableData();
      } else {
        Message.error(res.msg || '删除失败');
      }
    } catch (error) {
      Message.error('删除失败，请稍后重试');
      console.error('删除数据源失败:', error);
    } finally {
      setDeleteLoading(false);
    }
  };

  const cancelDelete = () => {
    setDeleteModalVisible(false);
    setCurrentDeleteId(null);
  };

  const columns: TableColumnProps[] = [
    {
      title: '序号',
      dataIndex: 'id',
      width: 100,
      render: (_, __, index) => index + 1
    },
    {
      title: '数据源名称',
      dataIndex: 'datasourceName'
    },
    {
      title: '数据源编码',
      dataIndex: 'code'
    },
    {
      title: '数据源类型',
      dataIndex: 'datasourceType'
    },
    {
      title: '描述',
      dataIndex: 'description'
    },
    {
      title: '操作',
      dataIndex: 'operation',
      render: (_, record: DatasourceRecord, index) => (
        // 默认数据源（第一个返回值）不可编辑删除
        <Space>
          <Button
            type="text"
            size="mini"
            style={{ marginRight: 8 }}
            onClick={() => gotoEdit(record.id)}
            disabled={index === 0}
          >
            编辑
          </Button>
          <Button
            type="text"
            size="mini"
            status="danger"
            onClick={() => handleDelete(record.id)}
            disabled={index === 0}
          >
            删除
          </Button>
        </Space>
      ),
      fixed: 'right',
      width: 120
    }
  ];

  return (
    <div className={styles.dataSourceContainer}>
      <div className={styles.dataSourceContent}>
        <div className={styles.operationHeader}>
          <div className={styles.operationHeaderLeft}>数据源管理</div>

          <Button
            type="primary"
            hidden={true}
            onClick={() => {
              handlePageType('create-ds');
            }}
          >
            <IconPlus />
            创建数据源
          </Button>
        </div>
        <Table
          columns={columns}
          data={dataSourceList}
          pagination={{
            total,
            pageSize: page.pageSize,
            current: page.pageNo,
            onChange: (current, pageSize) => {
              setPage({ pageNo: current, pageSize });
              getTableData();
            }
          }}
          loading={tableLoading}
          style={{ margin: '0 16px' }}
          rowKey="id"
        />
      </div>

      {/* 删除确认对话框 */}
      <Modal
        title="确认删除"
        visible={deleteModalVisible}
        onOk={confirmDelete}
        onCancel={cancelDelete}
        confirmLoading={deleteLoading}
        okText="确认删除"
        cancelText="取消"
      >
        <p>确定要删除这个数据源吗？删除后无法恢复。</p>
      </Modal>

      <EditDsDrawer
        visible={editDrawerVisible}
        onClose={() => setEditDrawerVisible(false)}
        dataSource={currentDataSource}
        onSuccess={handleEditSuccess}
      />
    </div>
  );
};

export default DataSourceTable;
