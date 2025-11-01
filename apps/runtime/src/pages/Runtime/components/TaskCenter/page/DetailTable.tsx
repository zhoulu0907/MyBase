import { useState, type FC } from 'react';
import { Table, type TableColumnProps, Button } from '@arco-design/web-react';
import { IconDownload, IconEye } from '@arco-design/web-react/icon';
import '../style/tcPage.less'
import ExpendSp from '@/assets/images/task_center/expend-sp.svg'

const DetailTable:FC = () => {
    const columns: TableColumnProps[] = [
        {
            title: ' ',
            dataIndex: 'key',
        },
        {
            title: '物品名称',
            dataIndex: 'name',
        },
        {
            title: '申请数量',
            dataIndex: 'salary',
        },
        {
            title: '物品用途',
            dataIndex: 'address',
        }
    ];
    const data = [
        {
            key: '1',
            name: '便利贴',
            salary: 2,
            address: '贴便利贴',
            email: 'jane.doe@example.com',
        },
        {
            key: '2',
            name: '订书机',
            salary: 1,
            address: '订文件',
            email: 'alisa.ross@example.com',
        },
        {
            key: '3',
            name: '文件夹',
            salary: 3,
            address: '装文件',
            email: 'kevin.sandra@example.com',
        },
        {
            key: '4',
            name: '荧光笔',
            salary: 3,
            address: '标记重点',
            email: 'ed.hellen@example.com',
        },
        {
            key: '5',
            name: '笔记本',
            salary: 2,
            address: '记笔记',
            email: 'william.smith@example.com',
        },
    ];

    return <>
        {/* <Button type='outline' icon={<IconDownload />} className='left-export-btn'>导出Excel</Button> */}
        <Table className='detail-left-tb' columns={columns} data={data} />
        {/* <p className='gray-color photo-box'>附件</p>
        <ul className='fj-file-box'>
            <li className='flex-bw-center'>
                <span className='flex-bw-center'><img src={ExpendSp} alt='' />12313213</span>
                <span className='flex-bw-center fj-rgt-btns'><IconEye /><IconDownload /></span>
            </li>
            <li className='flex-bw-center'>
                <span className='flex-bw-center'><img src={ExpendSp} alt='' />12313213123132131231321312313213123132131231321312313213</span>
                <span className='flex-bw-center fj-rgt-btns'><IconEye /><IconDownload /></span>
            </li>
        </ul> */}
    </>
}
export default DetailTable;