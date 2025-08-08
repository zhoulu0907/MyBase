import { Button, Pagination, Table, Popconfirm, Input } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import { type DictData, StatusEnum } from '@onebase/platform-center';
import s from '../../index.module.less';
import StatusTag, { getStatusLabel } from '@/components/StatusTag';

interface DictionaryTableProps {
  data: DictData[];
  currentPage: number;
  pageSize: number;
  total: number;
  onPageChange: (page: number) => void;
  onPageSizeChange: (size: number) => void;
  searchValue: string;
  onSearchChange: (value: string) => void;
  onAdd: () => void;
  onEdit: (item: DictData) => void;
  onDelete: (id: number) => void;
  onUpdateStatus: (id: number, status: number) => void;
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
  onAdd,
  onEdit,
  onDelete,
  onUpdateStatus
}: DictionaryTableProps) {
  const getNextStatus = (status: number) => {
    return status === StatusEnum.ENABLE ? StatusEnum.DISABLE : StatusEnum.ENABLE;
  };
  const handleStatusUpdate = (record: DictData) => {
    onUpdateStatus(record.id, getNextStatus(record.status))
  }

  const getNextStatusLabel = (status: StatusEnum) => {
    const nextStatus = getNextStatus(status);
    return getStatusLabel(nextStatus);
  }
  const columns = [
    { title: '字典值', dataIndex: 'label' },
    { title: '字典值编码', dataIndex: 'value' },
    { title: '显示顺序', dataIndex: 'sort' },
    {
      title: '状态',
      dataIndex: 'status',
      render: (val: number) => <StatusTag status={val} />
    },
    {
      title: '操作',
      dataIndex: 'id',
      render: (_: any, record: DictData) => (
        <>
          <Button type="text" style={{ marginRight: 8 }} onClick={() => onEdit(record)}>
            编辑
          </Button>
          <Button type="text" onClick={() => onDelete(record.id!)}>
            删除
          </Button>
           <Popconfirm
              focusLock
              title={ `确定要${getNextStatusLabel(record.status)}这条数据吗？`}
              onOk={() => {
                handleStatusUpdate(record)
              }}
            >
              <Button type='text'>
                {getStatusLabel(record.status === StatusEnum.DISABLE ? StatusEnum.ENABLE : StatusEnum.DISABLE)}
              </Button>
          </Popconfirm>
        </>
      )
    }
  ];

  return (
    <>
      <div className={s.tableHeader}>
        <Button type="primary" onClick={onAdd}>
          <IconPlus />
          添加
        </Button>
        <Input.Search
          value={searchValue}
          onChange={onSearchChange}
          placeholder="搜索字典值"
          style={{ width: 200 }}
          allowClear
        />
      </div>
      <div className={s.tableContainer}>
        <Table rowKey="id" columns={columns} data={data} pagination={false} scroll={{ y: 510 }} />
        <div className={s.paginationContainer}>
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
