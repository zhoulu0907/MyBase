import StatusTag from '@/components/StatusTag';
import { Input, Pagination, Table } from '@arco-design/web-react';
import { type DictData } from '@onebase/platform-center';
import { TENANT_DICT_PERMISSION as ACTIONS } from '@/constants/permission';
import { PermissionButton as Button } from '@/components/PermissionControl';
import styles from '../../index.module.less';

interface DictionaryTableProps {
  data: DictData[];
  currentPage: number;
  pageSize: number;
  total: number;
  onPageChange: (page: number) => void;
  onPageSizeChange: (size: number) => void;
  searchValue: string;
  onSearchChange: (value: string) => void;
  onBatchConfig: () => void;
}

export default function DictionaryTable({
  data,
  currentPage,
  pageSize,
  total,
  onPageChange,
  onPageSizeChange,
  searchValue,
  onSearchChange,
  onBatchConfig
}: DictionaryTableProps) {
  const columns = [
    {
      title: '颜色标识',
      dataIndex: 'colorType',
      width: 120,
      render: (val: string) => <div style={{ width: 16, height: 16, borderRadius: 50, backgroundColor: val }} />
    },
    { title: '字典值', dataIndex: 'label' },
    { title: '字典值编码', dataIndex: 'value' },
    { title: '显示顺序', dataIndex: 'sort' },
    {
      title: '状态',
      dataIndex: 'status',
      render: (val: number) => <StatusTag status={val} />
    }
  ];

  return (
    <>
      <div className={styles.tableHeader}>
        <Button permission={ACTIONS.CREATE} type="primary" onClick={onBatchConfig}>
          字典值配置
        </Button>
        <Input.Search
          className={styles.searchInput}
          value={searchValue}
          onChange={onSearchChange}
          placeholder="输入字典值"
          allowClear
        />
      </div>
      <div className={styles.tableContainer}>
        <Table rowKey="id" columns={columns} data={data} pagination={false} scroll={{ y: 510 }} />
        <div className={styles.paginationContainer}>
          <Pagination
            total={total}
            pageSize={pageSize}
            current={currentPage}
            onChange={onPageChange}
            onPageSizeChange={onPageSizeChange}
            showTotal
            sizeCanChange
          />
        </div>
      </div>
    </>
  );
}
