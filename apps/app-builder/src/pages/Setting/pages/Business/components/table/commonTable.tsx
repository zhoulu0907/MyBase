import { Table } from '@arco-design/web-react';

interface ICommonTableProps {
    data: any[];
    columns: any[];
    pageination: any;
}
export const CommonTable:React.FC<ICommonTableProps> = ({
    data,
    columns,
    pageination
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
            rowKey="key"
            border={false}
            columns={columns}
            data={data}
            pagination={pageination ? pageination : defaultPageination}
        />
    );
};