import { Button, Pagination, Table } from "@arco-design/web-react";
import { type DictData } from "@onebase/platform-center";
import s from "../../index.module.less";
import { TableHeader } from "./TableHeader";
import StatusTag from "@/components/StatusTag";

interface DictionaryTableProps {
  data: DictData[];
  currentPage: number;
  pageSize: number;
  total: number;
  onPageChange: (page: number) => void;
  searchValue: string;
  onSearchChange: (value: string) => void;
  onAdd: () => void;
  onEdit: (item: DictData) => void;
  onDelete: (id: number) => void;
}

export default function DictionaryTable({
  data,
  currentPage,
  pageSize,
  total,
  onPageChange,
  searchValue,
  onSearchChange,
  onAdd,
  onEdit,
  onDelete,
}: DictionaryTableProps) {
  const columns = [
    { title: "字典值", dataIndex: "name" },
    { title: "字典值编码", dataIndex: "code" },
    { title: "显示顺序", dataIndex: "order" },
    {
      title: "状态",
      dataIndex: "status",
      render: (val: number) => <StatusTag status={val} />,
    },
    {
      title: "操作",
      dataIndex: "id",
      render: (_: any, record: DictData) => (
        <>
          <Button
            type="text"
            style={{ marginRight: 8 }}
            onClick={() => onEdit(record)}
          >
            编辑
          </Button>
          <Button type="text" onClick={() => onDelete(record.id!)}>
            删除
          </Button>
        </>
      ),
    },
  ];

  return (
    <>
      <TableHeader
        searchValue={searchValue}
        onSearchChange={onSearchChange}
        onAdd={onAdd}
      />
      <div className={s.tableContainer}>
        <Table
          rowKey="id"
          columns={columns}
          data={data}
          pagination={false}
          scroll={{ y: 510 }}
        />
        <div className={s.paginationContainer}>
          <Pagination
            total={total}
            pageSize={pageSize}
            current={currentPage}
            onChange={onPageChange}
            showTotal
            sizeCanChange
          />
        </div>
      </div>
    </>
  );
}
