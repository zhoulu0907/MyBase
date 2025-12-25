import { useEffect, useState, type FC } from 'react';
import { Button, Modal, Table, Pagination, Space, type TableColumnProps } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import styles from './index.module.less';
import { DataSetParams, DelDataSetList } from '@onebase/platform-center';
import { useLocation } from 'react-router-dom';
import { format } from 'date-fns';
import { TokenManager } from '@onebase/common';

interface DataTable {
  name: string;
  type: string;
  creatBy: string;
  lastUpdateTime: string;
  id: string;
  updateBy: string;
}
const DataSet: FC = () => {
  const columns: TableColumnProps[] = [
    { title: '名称', dataIndex: 'name', key: 'name' },
    { title: '创建人', dataIndex: 'creatBy', key: 'creatBy' },
    { title: '修改人/修改时间', dataIndex: 'lastUpdateTime', key: 'lastUpdateTime' },
    {
      title: '操作',
      width: 100,
      key: 'operate',
      align: 'center',
      render: (_: DataTable, record: DataTable) => (
        <Space size="mini">
          <Button type="text" onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Button type="text" status="danger" onClick={() => handleDelete(record)}>
            删除
          </Button>
        </Space>
      )
    }
  ];
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [total, setTotal] = useState(1);
  const [dataSetList, setDataSetList] = useState<any[]>([]);
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const appId = searchParams.get('appId');
  //获取列表
  useEffect(() => {
    getDataSetList();
  }, [appId]);
  const getDataSetList = async () => {
    const { records, total } = await DataSetParams({ pageNum: currentPage, pageSize: pageSize, applicationId: appId });
    records.forEach((item: DataTable) => {
      const timedate = new Date(Number(item.lastUpdateTime));
      const formattedDate = format(timedate, 'yyyy-MM-dd HH:mm:ss');
      item.lastUpdateTime = item.updateBy + '/' + formattedDate;
    });
    setDataSetList(records);
    setTotal(total);
  };
  // 处理分页变化
  const handlePageChange = async (pageNum: number) => {
    try {
      setCurrentPage(pageNum);
      getDataSetList();
    } catch (error) {
      console.error(error);
    }
  };

  // 新建
  const handleAdd = async () => {
    const tokenInfo = TokenManager.getTokenInfo();
    window.open(
      `http://s25029301301.dev.internal.virtueit.net:81/v0/appdashboard/#/project/dataset-form?appId=${appId}&tenantId=${tokenInfo.tenantId}&userId=${tokenInfo.userId}`,
      '_blank'
    );
  };
  //编辑
  const handleEdit = async (record: DataTable) => {
    window.open(
      `http://s25029301301.dev.internal.virtueit.net:81/v0/appdashboard/#/project/dataset-form?editId=${record.id}`,
      '_blank'
    );
  };
  //删除
  const [deleteVisible, setDeleteVisible] = useState<boolean>(false);
  const [dataSetId, setDataSetId] = useState<string>('');
  const handleDelete = (record: DataTable) => {
    setDataSetId(record.id);
    setDeleteVisible(true);
  };
  const handleDeleteOk = async () => {
    await DelDataSetList(dataSetId);
    setDeleteVisible(false);
    getDataSetList();
  };
  return (
    <div className={styles.datasetPage}>
      <div className={styles.datasetTitle}>数据集</div>
      <div className={styles.dataFilter}>
        <Button type="primary" icon={<IconPlus />} onClick={handleAdd}>
          新建数据集
        </Button>
      </div>
      <Table rowKey="id" hover columns={columns} data={dataSetList} border={false} pagination={false} />
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'flex-end',
          marginTop: 12
        }}
      >
        <Pagination current={currentPage} pageSize={pageSize} total={total} onChange={handlePageChange} showTotal />
      </div>
      {/* 删除卡片弹框 */}
      <Modal
        visible={deleteVisible}
        onOk={handleDeleteOk}
        onCancel={() => setDeleteVisible(false)}
        autoFocus={false}
        focusLock={true}
        footer={
          <>
            <Button type="secondary" size="default" style={{ marginRight: 10 }} onClick={() => setDeleteVisible(false)}>
              取消
            </Button>
            <Button type="primary" status="danger" size="default" onClick={handleDeleteOk}>
              确认删除
            </Button>
          </>
        }
      >
        <p style={{ fontSize: 16, fontWeight: 500, color: '#1D2129' }}>您确定要删除该数据集吗？</p>
      </Modal>
    </div>
  );
};
export default DataSet;
