import { useEffect, useState, forwardRef, useImperativeHandle, useRef } from 'react';
import { Switch, Button, Table, type TableColumnProps } from '@arco-design/web-react';
import {IconQuestionCircle, IconPlus} from '@arco-design/web-react/icon'
import FieldModal from './FieldModal';

import './style.less'

const FieldTable = forwardRef(({editable}: any, ref) => {
    // keyArr是专门给FieldModal弹窗用的，帮助弹窗反选
    let [curKeyArr, setCurKeyArr] = useState([])
    let [selectRowkeyArr, setSelectRowKeyArr] = useState([])
    let [fmVisible, setFmVisible] = useState(false)

    const columns: TableColumnProps[] = [
        {
            title: '字段名称',
            dataIndex: 'fieldName',
        },
        {
            title: '操作',
            width: 95,
            dataIndex: 'fieldId',
            render: (_:any, row:any) => {
                return <Button type='text' onClick={() => handleDelRow(_)}>删除</Button>
            }
        },
    ];
    const [tbData, setTbData] = useState([
        {
            fieldId: '1',
            fieldName: 'Jane Doe',
            fieldPermType: editable ? 'write' : 'hidden'
        },
        {
            fieldId: '2',
            fieldName: 'Alisa Ross',
            fieldPermType: editable ? 'write' : 'hidden'
        },
    ]);

    function handleTbSelect(keyArr: any, rowArr: any) {
        // console.log(keyArr, rowArr)
        setSelectRowKeyArr(keyArr)
    }
    function handleAddFiled() {
        setFmVisible(true)
    }
    function handleDelRow(fid: any) {
        console.log(typeof fid, fid)
        let _data = [...tbData]
        if (typeof fid === 'string') {
            _data = _data.filter(item => {
                return item.fieldId !== fid
            })
        } else if (Array.isArray(fid)) {
            _data = _data.filter((item) => {
                return fid.indexOf(item.fieldId) < 0
            })
        }
        setTbData(_data)
    }
    function mergeDataToTable(arr: Array<any>) {
        setTbData(arr)
    }

    useEffect(() => {
        if (Array.isArray(tbData)) {
            let cur_key_arr:any = []
            tbData.forEach((item:any) => {
                cur_key_arr.push(item.fieldId)
            })
            setCurKeyArr(cur_key_arr)
        }
    }, [tbData])

    useImperativeHandle(ref, () => ({
        getTbData: () => tbData
    }));

    return <>
        <p style={{paddingBottom: '6px'}}>{editable?'可编辑字段': '隐藏字段'}</p>
        <div className='flex-btw'>
            <Button onClick={handleAddFiled} type='primary' icon={<IconPlus />} >添加字段</Button>
            {selectRowkeyArr?.length > 0 && <Button type='primary' className='gray-btn' onClick={() => handleDelRow(selectRowkeyArr)}>批量删除</Button>}
        </div>
        <Table  className='field-table-wrapper'
                rowKey="fieldId"
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
        {fmVisible && <FieldModal fmVisible={fmVisible} setFmVisible={setFmVisible} isEdit={editable} curKeyArr={curKeyArr} mergeDataToTable={mergeDataToTable}/>}
    </>
})

// 定义 ref 的类型接口
interface ChildComponentRef {
  getTbData: () => any[];
}

export default function FieldConfig({setApprovalConfigData}: any) {
    let [nodeSwitch, setNodeSwitch] = useState(true)
    let editRef = useRef<ChildComponentRef>()
    let hiddenRef = useRef<ChildComponentRef>()

    function changeNodeSwitch(flag: boolean) {
        setNodeSwitch(flag)
    }

    function testclick() {
        let editTable:any = editRef?.current?.getTbData() || []
        let hiddenTable:any = hiddenRef?.current?.getTbData() || []
        let fieldPermConfig = {
            useNodeConfig: nodeSwitch,
            fieldConfig: [
                ...editTable,
                ...hiddenTable
            ]
        }
        console.log(fieldPermConfig)
    }

    return <div className='field-config'>
        <div className='title-box'>
            <p className='p-title' onClick={testclick}>字段权限</p>
            <p style={{fontSize: 'small', color: 'rgba(134, 144, 156, 1)'}}>字段默认为只读状态，如需设为“可编辑”或“隐藏”，请在下方添加配置</p>
            <div className='right-switch'>
                <p>节点独立配置</p>
                <p className='switch-outer'>
                    <IconQuestionCircle /><Switch onChange={changeNodeSwitch} checked={nodeSwitch}/>
                </p>
            </div>
        </div>
        <FieldTable editable={true} ref={editRef}/>
        <div style={{height: 24}}></div>
        <FieldTable editable={false} ref={hiddenRef}/>
    </div>
}