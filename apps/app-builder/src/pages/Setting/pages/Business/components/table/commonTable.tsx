import { Table } from '@arco-design/web-react';

interface ICommonTableProps {
    data: any[];
    columns: any[];
    pageination: any;
    scroll?: {x: number}
}
export const CommonTable:React.FC<ICommonTableProps> = ({
    data,
    columns,
    pageination,
    scroll,
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
            rowKey="key"
            border={false}
            columns={columns}
            data={data}
            pagination={pageination ? pageination : defaultPageination}
            scroll={scroll}
            {...rest}
        />
    );
};