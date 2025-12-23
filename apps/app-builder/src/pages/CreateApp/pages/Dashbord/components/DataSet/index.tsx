import { useEffect, useState, type FC } from 'react';
import { Button, Modal, Table, Pagination, Space, type TableColumnProps } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import styles from './index.module.less';
import { DataSetList, DelDataSetList } from '@onebase/platform-center';
interface DataTable {
  name: string;
  type: string;
  founder: string;
  emModifyRecord: string;
  id: string;
}
const DataSet: FC = () => {
  const columns: TableColumnProps[] = [
    { title: '名称', dataIndex: 'name', key: 'name' },
    { title: '创建人', dataIndex: 'founder', key: 'founder' },
    { title: '修改人/修改时间', dataIndex: 'emModifyRecord', key: 'emModifyRecord' },
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

  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [total, setTotal] = useState(1);
  const [dataSet, setDataSet] = useState<any[]>([]);
  //获取列表
  useEffect(() => {
    getDataSetList();
  }, []);
  const getDataSetList = async () => {
    const res = await DataSetList({ busiFlag: 'dataset' });
    setDataSet(res[0].children);
  };
  // 新建
  const handleAdd = () => {
    window.open(`http://s25029301301.dev.internal.virtueit.net:81/v0/appdashboard/#/project/dataset-form`, '_blank');
  };
  //编辑
  const handleEdit = async (record: DataTable) => {
    window.open(
      `http://s25029301301.dev.internal.virtueit.net:81/v0/appdashboard/#/project/dataset-form?id=${record.id}`,
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
    getDataSetList();
    setDeleteVisible(false);
  };
  return (
    <div className={styles.datasetPage}>
      <div className={styles.datasetTitle}>数据集</div>
      <div className={styles.dataFilter}>
        <Button type="primary" icon={<IconPlus />} onClick={handleAdd}>
          新建数据集
        </Button>
      </div>
      <Table rowKey="id" hover columns={columns} data={dataSet} border={false} pagination={false} />
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'flex-end',
          marginTop: 12
        }}
      >
        <Pagination
          size="small"
          current={page}
          pageSize={pageSize}
          total={total}
          onChange={setPage}
          onPageSizeChange={setPageSize}
          showTotal
          showJumper
          sizeOptions={[10, 20, 50]}
        />
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
