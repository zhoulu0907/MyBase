import { useState, type FC } from 'react';
import { Table, type TableColumnProps, Button, Tag, Link } from '@arco-design/web-react';
import TableSearch from './TableSearch';
import DetailPop from './DetailPop'
import BatchApproveModal from '../modal/batchApprove';
import '../style/tcPage.less'

const WillDo:FC = () => {
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
            title: '当前节点状态',
            dataIndex: 'address',
            render: (val, record) => <Tag color='blue' size='medium'>{val}</Tag>,
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
            render: (val, record) => <Link status='warning'>{val}</Link>
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
    const data:any[] = [];
    let [tbRowSelection, setTbRowSelection] = useState<any>()
    const [selectedRowKeys, setSelectedRowKeys] = useState<any>();

    let [detailPopVisible, setPopVisible] = useState(false)
    let [approveVisible, setApproveVisible] = useState(false)

    function handleBatchClick(hasRowCheck: boolean) {
        console.log('batch click!', hasRowCheck)
        if (hasRowCheck) {
            setTbRowSelection({
                type: 'checkbox',
                selectedRowKeys,
                onChange: (selectedKeys:Array<string>, selectedRows:Array<any>) => {
                    console.log('onChange:', selectedKeys, selectedRows);
                    setSelectedRowKeys(selectedRowKeys);
                }
            })
        } else {
            setTbRowSelection(undefined)
        }
    }
    function handleBatch2Click() {
        setApproveVisible(true)
    }

    function handleDetailPage(row:any) {
        console.log('click to detail page === row ===', row)
        setPopVisible(true)
    }
    return <section className='page-content-rgt'>
        <div className='table-title-box'>
            <b>待我处理</b>
            <TableSearch  uiConfig={{hasInput: true, hasFilter: true, hasSort: true, hasBatch: true}} batchEvent={handleBatchClick}/>
        </div>
        {tbRowSelection && <div className='flex-bw-center title-batch-box'>
            <span>已选中3/20条</span>
            <div className='batch-btns'>
                <Button type='outline' onClick={() => setTbRowSelection(undefined)}>取消操作</Button>
                <Button type='outline' onClick={() => handleBatch2Click()}>批量拒绝</Button>
                <Button type='primary' onClick={() => handleBatch2Click()}>批量同意</Button>
            </div>
        </div>}
        <Table className='task-tb-box' rowKey='name' rowSelection={tbRowSelection} columns={columns} data={data} />
        {detailPopVisible && <DetailPop detailPopVisible={detailPopVisible} setPopVisible={setPopVisible}/>}
        {approveVisible && <BatchApproveModal approveVisible={approveVisible} setApproveVisible={setApproveVisible}/>}
    </section>
}

export default WillDo;