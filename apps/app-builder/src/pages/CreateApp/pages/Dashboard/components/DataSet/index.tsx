import TablePagination from '@/components/TablePagination';
import ActionButtons from '@/components/ActionButtons';
import ResizableTable from '@/components/ResizableTable';
import DeleteConfirmModal from '@/components/DeleteConfirmModal';
import { Button, type TableColumnProps } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import styles from './index.module.less';
import { DataSetParams, DelDataSetList } from '@onebase/platform-center';
import { useLocation } from 'react-router-dom';
import { TokenManager, getDashBoardURL } from '@onebase/common';
import dayjs from 'dayjs';
import { useEffect, useState, type FC } from 'react';

interface DataTable {
  name: string;
  type: string;
  createTime: string;
  lastUpdateTime: string;
  id: string;
  updateBy: string;
}
const DataSet: FC = () => {
  const columns: TableColumnProps[] = [
    { title: '名称', dataIndex: 'name', key: 'name' },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime' },
    { title: '修改人/修改时间', dataIndex: 'lastUpdateTime', key: 'lastUpdateTime' },
    {
      title: '操作',
      width: 80,
      key: 'operate',
      render: (_: DataTable, record: DataTable) => (
        <ActionButtons>
          <Button type="text" onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Button type="text" status="danger" onClick={() => handleDelete(record)}>
            删除
          </Button>
        </ActionButtons>
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
      const formattedDate = dayjs(Number(item.lastUpdateTime)).format('YYYY-MM-DD HH:mm:ss');
      const formatCreateTime = dayjs(Number(item.createTime)).format('YYYY-MM-DD HH:mm:ss');
      item.lastUpdateTime = formattedDate;
      item.createTime = formatCreateTime;
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
  const resourceUrl = getDashBoardURL();
  // 新建
  const handleAdd = async () => {
    const tokenInfo = TokenManager.getTokenInfo();
    window.open(
      `${resourceUrl}project/dataset-form?appId=${appId}&tenantId=${tokenInfo?.tenantId}&userId=${tokenInfo?.userId}`,
      '_blank'
    );
  };
  //编辑
  const handleEdit = async (record: DataTable) => {
    window.open(`${resourceUrl}project/dataset-form?id=${record.id}`, '_blank');
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
      <ResizableTable rowKey="id" hover columns={columns} data={dataSetList} border={false} pagination={false} />
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'flex-end',
          marginTop: 12
        }}
      >
        <TablePagination current={currentPage} pageSize={pageSize} total={total} onChange={handlePageChange} />
      </div>
      {/* 删除卡片弹框 */}
      <DeleteConfirmModal
        visible={deleteVisible}
        onVisibleChange={setDeleteVisible}
        onConfirm={handleDeleteOk}
        content="您确定要删除该数据集吗？"
      />
    </div>
  );
};
export default DataSet;
