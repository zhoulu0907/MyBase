import { useState, type FC } from 'react';
import { Table, type TableColumnProps, Button, Link } from '@arco-design/web-react';
import TableSearch from './TableSearch';
import DetailPop from './DetailPop'

const IDone:FC = () => {
    const columns: TableColumnProps[] = [
        {
            title: '流程标题',
            dataIndex: 'name',
        },
        {
            title: '发起人',
            dataIndex: 'salary',
            render: (val, record) => (
                <span className='flex-bw-center'>
                    <img src="/src/assets/images/avatar.svg" />{val}
                </span>
            ),
        },
        {
            title: '处理操作',
            dataIndex: 'address',
            render: (val, record) => {
                if (record.key === '1') {
                    return <Link status='success'>{val}</Link>
                } else if (record.key === '2') {
                    return <Link status='error'>{val}</Link>
                } else {
                    return <Link status='warning'>{val}</Link>
                }
            },
        },
        {
            title: '处理时间',
            dataIndex: 'email',
            defaultSortOrder: 'ascend',
            sorter: (a, b) => {
                if (a.email > b.email) {
                    return 1;
                }
                if (a.email < b.email) {
                    return -1;
                }
                return 0;
            },
        },
        {
            title: '操作',
            dataIndex: 'op',
            align: 'center',
            render: (_, record) => (
                <Button type='text' status='success' onClick={() => {handleDetailPage(record)}}>详情</Button>
            ),
        },
    ];
    const data: any[] = [];
    let [detailPopVisible, setPopVisible] = useState(false)
    function handleDetailPage(row:any) {
        console.log('click to detail page === row ===', row)
        setPopVisible(true)
    }
    return <section className='page-content-rgt'>
        <div className='table-title-box'>
            <b>我已处理</b>
            <TableSearch uiConfig={{hasInput: true, hasFilter: true, hasSort: true, hasBatch: false}}/>
        </div>
        <Table className='task-tb-box created-tb' columns={columns} data={data} />
        {detailPopVisible && <DetailPop detailPopVisible={detailPopVisible} setPopVisible={setPopVisible}/>}
    </section>
}

export default IDone;