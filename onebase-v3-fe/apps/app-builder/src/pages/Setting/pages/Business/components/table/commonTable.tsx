import ResizableTable from '@/components/ResizableTable';

interface ICommonTableProps {
  data: any[];
  columns: any[];
  loading: boolean;
  pageination: any;
  scroll?: { x: number }
  onChange: (pageNo: number, pageSize: number) => void;
}
export const CommonTable: React.FC<ICommonTableProps> = ({
  data,
  columns,
  pageination,
  scroll,
  loading,
  onChange,
  ...rest
}) => {
  return (
    <ResizableTable
      rowKey="id"
      stripe
      loading={loading}
      columns={columns}
      data={data}
      pagination={{ ...pageination, onChange: onChange }}
      scroll={scroll}
      {...rest}
    />
  );
};