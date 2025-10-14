import { useState, type FC } from 'react';
import { Table, type TableColumnProps, Button } from '@arco-design/web-react';
import TableSearch from './TableSearch';
import DetailPop from './DetailPop'

const WillDo:FC = () => {
    const columns: TableColumnProps[] = [
        {
            title: '流程标题',
            dataIndex: 'name',
        },
        {
            title: '发起人',
            dataIndex: 'salary',
        },
        {
            title: '当前节点状态',
            dataIndex: 'address',
        },
        {
            title: '表单摘要',
            dataIndex: 'email1',
        },
        {
            title: '到达时间',
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
            title: '发起时间',
            dataIndex: 'email2',
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
    const data = [
        {
            key: '1',
            name: 'Jane Doe',
            salary: 23000,
            address: '32 Park Road, London',
            email: '3jane.doe@example.com',
            email1: 'e@example.com',
            email2: 'ample.com',
        },
        {
            key: '2',
            name: 'Alisa Ross',
            salary: 25000,
            address: '35 Park Road, London',
            email: '6alisa.ross@example.com',
            email1: '12e@example.com',
            email2: '3333ample.com',
        },
        {
            key: '3',
            name: 'Kevin Sandra',
            salary: 22000,
            address: '31 Park Road, London',
            email: '1kevin.sandra@example.com',
            email1: 'aaae@example.com',
            email2: 'bbbample.com',
        },
    ];
    let [detailPopVisible, setPopVisible] = useState(false)
    function handleDetailPage(row:any) {
        console.log('click to detail page === row ===', row)
        setPopVisible(true)
    }
    return <section className='page-content-rgt'>
        <div className='table-title-box'>
            <b>待我处理</b>
            <TableSearch />
        </div>
        <Table columns={columns} data={data} />
        {detailPopVisible && <DetailPop detailPopVisible={detailPopVisible} setPopVisible={setPopVisible}/>}
    </section>
}

export default WillDo;