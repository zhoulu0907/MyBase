import { useEffect, useState, type FC } from 'react';
import { Button, Input, Select, Table, Pagination, Space } from '@arco-design/web-react';
import { IconPlus, IconSearch } from '@arco-design/web-react/icon';
import styles from './index.module.less';

const Option = Select.Option;

const DataSet: FC = () => {
  const [status, setStatus] = useState<number | string>('');
  const statusOptions = [
    {
      label: '全部状态',
      value: ''
    },
    {
      label: '开发中',
      value: 0
    },
    {
      label: '迭代中',
      value: 1
    },
    {
      label: '已发布',
      value: 2
    }
  ];
  const handleSearchChange = () => {};
  const handleAdd = () => {};

  interface DataTable {
    name: string;
    type: string;
    founder: string;
    emModifyRecord: string;
  }
  const handleEdit = (record: DataTable) => {
    console.log(record);
  };
  const columns = [
    { title: '名称', dataIndex: 'name' },
    { title: '类型', dataIndex: 'type', width: 140 },
    { title: '创建人', dataIndex: 'founder' },
    { title: '修改人/修改时间', dataIndex: 'emModifyRecord' },
    {
      title: '操作',
      width: 100,
      render: (_: any, record: any) => (
        <Space size="mini">
          <Button type="text" onClick={() => handleEdit(record)}>
            编辑
          </Button>
        </Space>
      )
    }
  ];
  const data = [
    {
      key: '001',
      name: 'zjl',
      type: 'form',
      founder: 'zjl',
      emModifyRecord: '2025'
    }
  ];
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [total, setTotal] = useState(1);
  useEffect(() => {}, []);

  return (
    <div className={styles.datasetPage}>
      <div className={styles.datasetTitle}>数据集</div>
      <div className={styles.dataFilter}>
        <Button type="primary" icon={<IconPlus />} onClick={handleAdd}>
          新建数据集
        </Button>
        <div>
          <Select
            placeholder="全部状态"
            bordered={false}
            style={{ width: 100 }}
            onChange={(value) => setStatus(value)}
            value={status}
          >
            {statusOptions.map((option, index) => (
              <Option key={index} value={option.value}>
                {option.label}
              </Option>
            ))}
          </Select>
          <Input
            className={styles.appInput}
            allowClear
            suffix={<IconSearch />}
            onChange={handleSearchChange}
            placeholder="搜索"
          />
        </div>
      </div>
      <Table rowKey="id" hover columns={columns} data={data} border={false} pagination={false} />
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
    </div>
  );
};
export default DataSet;
