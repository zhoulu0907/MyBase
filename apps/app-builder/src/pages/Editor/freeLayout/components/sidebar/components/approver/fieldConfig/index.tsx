import { useEffect, useState } from 'react';
import { Switch, Button, Table, type TableColumnProps } from '@arco-design/web-react';
import {IconQuestionCircle, IconPlus} from '@arco-design/web-react/icon'
import FieldModal from './FieldModal';

import './style.less'

function FieldTable({editable}:any) {
    // keyArr是专门给FieldModal弹窗用的，帮助弹窗反选
    let [curKeyArr, setCurKeyArr] = useState([])
    let [selectRowArr, setSelectRowArr] = useState([])
    let [fmVisible, setFmVisible] = useState(false)

    const columns: TableColumnProps[] = [
        {
            title: '字段名称',
            dataIndex: 'name',
        },
        {
            title: '操作',
            width: 95,
            render: (_:any, row:any) => {
                return <Button type='text'>删除</Button>
            }
        },
    ];
    const [tbData, setTbData] = useState([
        {
            key: '1',
            name: 'Jane Doe',
            salary: 23000,
            address: '32 Park Road, London',
            email: 'jane.doe@example.com',
        },
        {
            key: '2',
            name: 'Alisa Ross',
            salary: 25000,
            address: '35 Park Road, London',
            email: 'alisa.ross@example.com',
        },
    ]);

    function handleTbSelect(keyArr: any, rowArr: any) {
        console.log(keyArr, rowArr)
        setSelectRowArr(rowArr)
    }
    function handleAddFiled() {
        setFmVisible(true)
    }

    useEffect(() => {
        if (Array.isArray(tbData)) {
            let cur_key_arr:any = []
            tbData.forEach(item => {
                cur_key_arr.push(item.key)
            })
            setCurKeyArr(cur_key_arr)
        }
    }, [tbData])
    return <>
        <p style={{paddingBottom: '6px'}}>{editable?'可编辑字段': '隐藏字段'}</p>
        <div className='flex-btw'>
            <Button onClick={handleAddFiled} type='primary' icon={<IconPlus />} >添加字段</Button>
            {selectRowArr?.length > 0 && <Button type='primary' className='gray-btn' >批量删除</Button>}
        </div>
        <Table  className='field-table-wrapper'
                columns={columns} 
                data={tbData} 
                pagination={false} 
                rowSelection = {
                    {
                        type: 'checkbox', 
                        onChange: (keyArr: any, rowArr: any) => handleTbSelect(keyArr, rowArr)
                    }
                }
        />
        {fmVisible && <FieldModal fmVisible={fmVisible} setFmVisible={setFmVisible} isEdit={editable} curKeyArr={curKeyArr}/>}
    </>
}

export default function FieldConfig() {
    let [nodeSwitch, setNodeSwitch] = useState(true)

    function changeNodeSwitch(flag: boolean) {
        setNodeSwitch(flag)
    }
    return <div className='field-config'>
        <div className='title-box'>
            <p className='p-title'>字段权限</p>
            <p style={{fontSize: 'small', color: 'rgba(134, 144, 156, 1)'}}>字段默认为只读状态，如需设为“可编辑”或“隐藏”，请在下方添加配置</p>
            <div className='right-switch'>
                <p>节点独立配置</p>
                <p className='switch-outer'>
                    <IconQuestionCircle /><Switch onChange={changeNodeSwitch} checked={nodeSwitch}/>
                </p>
            </div>
        </div>
        <FieldTable editable={true}/>
        <div style={{height: 24}}></div>
        <FieldTable editable={false}/>
    </div>
}