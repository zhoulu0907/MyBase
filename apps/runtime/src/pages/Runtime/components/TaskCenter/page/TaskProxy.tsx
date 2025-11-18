import { useState, type FC } from 'react';
import { Table, type TableColumnProps, Button, Tag, Radio, Space } from '@arco-design/web-react';
import { IconPlusCircle } from '@arco-design/web-react/icon'
import TableSearch from './TableSearch';
import EditProxyModal from '../modal/editProxyForm'
import '../style/tcPage.less'

const radioList = [
    {label: '全部', value: 'all'},
    {label: '代理中', value: '1'},
    {label: '待生效', value: '2'},
    {label: '已失效', value: '3'},
    {label: '已撤销', value: '4'}
]
let defaultCheck = 'all'

const WillDo:FC = () => {
    let [editVisible, setEditVisible] = useState(false)
    let [rowData, setRowData] = useState()
    const columns: TableColumnProps[] = [
        {
            title: '被代理人',
            dataIndex: 'name',
            render: (val, record) => (
                <span className='flex-bw-center'>
                    <div className='photo-img'>{record?.avatar && <img src={record?.avatar} />}</div>{val}
                </span>
            ),
        },
        {
            title: '代理人',
            dataIndex: 'salary',
            render: (val, record) => (
                <span className='flex-bw-center'>
                    <div className='photo-img'>{record?.avatar && <img src={record?.avatar} />}</div>{val}
                </span>
            ),
        },
        {
            title: '代理有效期',
            dataIndex: 'address',
        },
        {
            title: '代理状态',
            dataIndex: 'email1',
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
            title: '创建人',
            dataIndex: 'email',
            render: (val, record) => (
                <span className='flex-bw-center'>
                    <div className='photo-img'>{record?.avatar && <img src={record?.avatar} />}</div>{val}
                </span>
            ),
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
                <>
                    <Button type='text' onClick={() => {}}>撤消</Button>
                    <Button type='text' onClick={() => {setRowData(record); setEditVisible(true)}}>编辑</Button>
                </>
            ),
        },
    ];
    const data = [
        {
            key: '1',
            name: '1232',
            salary: '1234',
            address: '32 Park Road, London',
            email: '3jane.doe@example.com',
            email1: 'e@example.com',
            email2: 'ample.com',
        },
        {
            key: '2',
            name: '1233',
            salary: '1234',
            address: '35 Park Road, London',
            email: '6alisa.ross@example.com',
            email1: '12e@example.com',
            email2: '3333ample.com',
        },
        {
            key: '3',
            name: '1232',
            salary: '1235',
            address: '31 Park Road, London',
            email: '1kevin.sandra@example.com',
            email1: 'aaae@example.com',
            email2: 'bbbample.com',
        },
    ];

    function CreatedRadioChange(val:string) {
        console.log('radio ====', val)
    }

    return <section className='page-content-rgt'>
        <div className='normal-box-title'>
            <b>我已处理</b><span>可授权指定人员在有效期内代理您的流程处理事务</span>
        </div>
        <div className='table-title-box'>
            <Radio.Group defaultValue={defaultCheck} onChange={CreatedRadioChange} name='button-radio-group' className='created-radio-group task-proxy-radio'>
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
            <Space>
                <TableSearch uiConfig={{hasInput: true, hasFilter: false, hasSort: false, hasBatch: false}}/>
                <Button type='primary' onClick={() => {setRowData(undefined);setEditVisible(true)}}><IconPlusCircle style={{transform: 'scale(1.3)'}}/>新增代理</Button>
            </Space>
        </div>
        <Table className='task-tb-box task-proxy-tb' columns={columns} data={data} />
        {editVisible && <EditProxyModal visible={editVisible} setVisible={setEditVisible} handleModalForm={() => {}} initRowData={rowData}/>}
    </section>
}

export default WillDo;
