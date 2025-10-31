import { Modal, Checkbox } from '@arco-design/web-react';
import {IconClose} from '@arco-design/web-react/icon'
import { useEffect, useState } from 'react';

const CheckboxGroup = Checkbox.Group;

export default function FieldModal({fmVisible, setFmVisible, curKeyArr, isEdit, mergeDataToTable}: any) {
    const [ckedKey, setCkedKey] = useState(curKeyArr)
    const [checkedItem, setCheckedItem] = useState([])
    const ckOptions = [
        {
            label: 'Jane Doe',
            value: '1'
        },
        {
            value: '2',
            label: 'Alisa Ross',
        },
        {
            label: '字段1',
            value: 'fid1',
        },
        {
            label: '字段2',
            value: 'fid2',
        },
        {
            label: '字段3',
            value: 'fid3',
        },
        {
            label: '字段4',
            value: 'fid4',
        },
    ];
    function handleCheckChange(keyArr:Array<any>) {
        setCkedKey(keyArr)
    }
    function handleDelCked(item: any) {
        let key = item?.value;
        if (key !== undefined) {
            let key_arr:Array<any> = [];
            key_arr = key_arr.concat(ckedKey)
            let idx = key_arr.indexOf(key);
            if (idx > -1) {
                key_arr.splice(idx, 1)
                setCkedKey(key_arr)
            }
        }
    }
    function handleSubmit() {
        if (Array.isArray(checkedItem)) {
            let resData: Array<any> = []
            checkedItem.forEach((item:any) => {
                resData.push({
                    fieldId: item.value,
                    fieldName: item.label,
                    fieldPermType: isEdit ? 'write' : 'hidden'
                })
            })
            mergeDataToTable && mergeDataToTable(resData)
            setFmVisible(false)
        } else {
            console.error('选择的数据结构不对')
        }
    }

    useEffect(() => {
        let ckedArr:any = ckOptions.filter(item => {
            return ckedKey.indexOf(item.value) > -1
        })
        setCheckedItem(ckedArr)
    }, [ckedKey])

    return <Modal
        className='field-modal'
        title={<div style={{textAlign: 'left'}}>{isEdit?'添加可编辑字段':'添加可隐藏字段'}</div>}
        visible={fmVisible}
        onOk={handleSubmit}
        onCancel={() => setFmVisible(false)}
      >
        <div className='out-line-box flex-btw'>
            <section className='left-part'>
                <div>字段列表</div>
                <CheckboxGroup
                    className='check-group-outer'
                    options={ckOptions}
                    value={ckedKey}
                    onChange={handleCheckChange}
                />
            </section>
            <section className='right-part'>
                <div className='flex-btw'>
                    <span>字段列表</span>
                    <span onClick={() => setCkedKey([])} style={{color: 'rgb(var(--primary-6))', cursor: 'pointer'}}>清空</span>
                </div>
                <div className='check-group-outer'>
                    {checkedItem.map((item:any, i:number) => {
                        return <div className='flex-btw arco-checkbox li' key={i}>
                            <span>{item?.label}</span><IconClose onClick={() => handleDelCked(item)}/>
                        </div>
                    })}
                    {/* <div className='flex-btw arco-checkbox li'>
                        <span>sdfsfs</span><IconClose />
                    </div> */}
                </div>
            </section>
        </div>
    </Modal>
}