import { useState, type FC } from 'react';
import { Table, type TableColumnProps, Button, Tag, Space, Radio } from '@arco-design/web-react';
import TableSearch from './TableSearch';
import DetailPop from './DetailPop'
import TaskList from './TaskList';
import avatar from '@assets/images/avatar.svg';
import '../style/tcPage.less'

const radioList = [
    {label: '未读', value: '0'},
    {label: '已读', value: '1'},
    {label: '全部', value: 'all'}
]
let defaultCheck = 'all'

const ICopied:FC = () => {
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
                    <img src={avatar} />{val}
                </span>
            ),
        },
        {
            title: '流程状态',
            dataIndex: 'email2',
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

    function CreatedRadioChange(val:string) {
        console.log('radio ====', val)
    }

    function handleDetailPage(row:any) {
        console.log('click to detail page === row ===', row)
        setPopVisible(true)
    }

    const fetchFormData = async () => {
        return {
            total: data.length,
            list: data
        }
    }

    const newTask = true;
    if (newTask) {
        return (
        <section className="page-will-do">
            <TaskList 
            title="我创建的"
            dataFetch={fetchFormData}
            columns={columns}
            />
        </section>
        );
    }
    return <section className='page-content-rgt'>
        <div className='table-title-box'>
            <div>
                <b style={{marginRight: '8px'}}>抄送我的</b>
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

export default ICopied;