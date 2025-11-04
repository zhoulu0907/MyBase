import { Table } from '@arco-design/web-react';

interface ICommonTableProps {
    data: any[];
    columns: any[];
    loading: boolean;
    pageination: any;
    scroll?: {x: number}
}
export const CommonTable:React.FC<ICommonTableProps> = ({
    data,
    columns,
    pageination,
    scroll,
    loading,
    ...rest
}) => {
    const defaultPageination = {
        sizeCanChange: true,
        showTotal: true,
        total: 100,
        pageSize: 10,
        current: 1,
        pageSizeChangeResetCurrent: true
    }

    return (
        <Table
            rowKey="id"
            border={false}
            loading={loading}
            columns={columns}
            data={data}
            pagination={pageination ? pageination : defaultPageination}
            scroll={scroll}
            {...rest}
        />
    );
};