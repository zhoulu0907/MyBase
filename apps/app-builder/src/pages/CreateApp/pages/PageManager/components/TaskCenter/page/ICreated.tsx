import { useState, type FC } from 'react';
import { Table, type TableColumnProps, Button, Tag, Space, Radio } from '@arco-design/web-react';
import TableSearch from './TableSearch';
import DetailPop from './DetailPop'
import '../style/tcPage.less'

const radioList = [
    {label: '全部', value: 'all'},
    {label: '草稿', value: '1'},
    {label: '审批中', value: '2'},
    {label: '已通过', value: '3'},
    {label: '已拒绝', value: '4'},
    {label: '已撤回', value: '5'},
    {label: '已终止', value: '6'}
]
let defaultCheck = 'all'

const ICreated:FC = () => {
    const columns: TableColumnProps[] = [
        {
            title: '流程标题',
            dataIndex: 'name',
        },
        {
            title: '流程状态',
            dataIndex: 'email',
            render: (val, record) => {
                if (record.key === '1') {
                    return <Tag color='green' size='medium'>{val}</Tag>
                } else if (record.key === '2') {
                    return <Tag color='blue' size='medium'>{val}</Tag>
                } else {
                    return <Tag color='gray' size='medium'>{val}</Tag>
                }
            },
        },
        {
            title: '当前节点处理人',
            dataIndex: 'salary',
            render: (val, record) => (
                <span className='flex-bw-center'>
                    <img src="/src/assets/images/avatar.svg" />{val}
                </span>
            ),
        },
        {
            title: '发起时间',
            dataIndex: 'email1',
        },
        {
            title: '创建时间',
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
    const data: any[] = [];
    let [detailPopVisible, setPopVisible] = useState(false)

    function CreatedRadioChange(val:string) {
        console.log('radio ====', val)
    }

    function handleDetailPage(row:any) {
        console.log('click to detail page === row ===', row)
        setPopVisible(true)
    }

    return <section className='page-content-rgt'>
        <div className='table-title-box'>
            <div>
                <b style={{marginRight: '8px'}}>我创建的</b>
                <Radio.Group defaultValue={defaultCheck} onChange={CreatedRadioChange} name='button-radio-group' className='created-radio-group'>
                    {radioList.map((item) => {
                        return (
                        <Radio key={item.value} value={item.value}>
                            {({ checked }) => {
                                return (
                                    <Button key={item.value} type='text' className={`${checked ? 'rdo-checked' : ''}`}>
                                        {item.label}
                                    </Button>
                                );
                            }}
                        </Radio>
                        );
                    })}
                </Radio.Group>
            </div>
            <TableSearch uiConfig={{hasInput: true, hasFilter: true, hasSort: true, hasBatch: false}}/>
        </div>
        <Table className='task-tb-box' columns={columns} data={data} />
        {detailPopVisible && <DetailPop detailPopVisible={detailPopVisible} setPopVisible={setPopVisible}/>}
    </section>
}

export default ICreated;