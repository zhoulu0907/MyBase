import { useEffect, useState } from 'react';
import { v4 as uuidv4 } from 'uuid';
import { Modal, Divider, Input, Select, Message, Space, Link, Button } from '@arco-design/web-react';
import {IconClose} from '@arco-design/web-react/icon';
import SimpleMode from './SimpleMode';
import './inner.style.less'

const Option = Select.Option;

export default function ConditionModal({modalShow, setModalShow}:any) {
    const [conditionArr, setConditionArr] = useState([[{_key: uuidv4()}]])

    function handleAddSpace(type: string, itemArr?:Array<any>, pi?:number) {
        let _arr:Array<any> = []
        let condition_arr = _arr.concat(conditionArr)
        let _key = uuidv4()
        if (type === 'OR') {
            condition_arr.push([{_key}])
            setConditionArr(condition_arr)
        } else if (Array.isArray(itemArr) && typeof pi === 'number') {
            condition_arr[pi].push({_key})
            setConditionArr(condition_arr)
        } else {
            // nothing to do
        }
    }
    function handleDelSpace(itemArr:Array<any>, pi:number, ci: number) {
        if (Array.isArray(itemArr)) {
            let _arr:Array<any> = []
            let condition_arr = _arr.concat(conditionArr)
            if (Array.isArray(condition_arr[pi])) {
                condition_arr[pi].splice(ci, 1)
                // 如果itemArr里面的项都删除光了，那么这个空itemArr可能需要一起移除
                // 不能移除condition_arr为空的子项，会影响循环的key值
                // if (condition_arr[pi].length === 0 && condition_arr.length > 1) {
                //     condition_arr.splice(pi, 1)
                // }
                setConditionArr(condition_arr)
            }
        }
    }
    // 删除conditionArr为空的子项
    function filterArrItem(arr:Array<any>) {
        return arr.filter(item => {
            return item?.length > 0
        })
    }
    function handleOk() {
        let arr = filterArrItem(conditionArr)
    }
    
    return <Modal
        className='condition-modal-box'
        title={
          <div style={{ textAlign: 'left' }}>添加审批人</div>
        }
        visible={modalShow}
        onOk={() => handleOk()}
        onCancel={() => setModalShow(false)}
      >
        <SimpleMode />
        <Divider />
        <div className="arco-form-label-item no-form-item">
            <label><b className="arco-form-item-symbol">*</b>生效条件</label>
        </div>
        <div className='arco-btn-outline outer-border'>
            {conditionArr.map((itemArr, pi) => {
                if (!itemArr?.length) {
                    return <></>
                }
                return <section className='gray-part' key={pi} data-pi={pi}>
                    {
                        itemArr.map((item:any, ci:number) => {
                            return <Space key={item?._key}>
                                <Select
                                    placeholder='请选择'
                                    style={{ width: 120 }}
                                    onChange={(value) =>
                                        Message.info({
                                            content: `You select ${value}.`,
                                            showIcon: true,
                                        })
                                    }
                                >
                                    <Option value='1'>123132</Option>
                                    <Option value='2'>223231</Option>
                                </Select>
                                <Select
                                    placeholder='请选择'
                                    style={{ width: 120 }}
                                    onChange={(value) =>
                                        Message.info({
                                            content: `You select ${value}.`,
                                            showIcon: true,
                                        })
                                    }
                                >
                                    <Option value='1'>大于</Option>
                                    <Option value='2'>大于等于</Option>
                                </Select>
                                <Select
                                    placeholder='请选择'
                                    style={{ width: 120 }}
                                    onChange={(value) =>
                                        Message.info({
                                            content: `You select ${value}.`,
                                            showIcon: true,
                                        })
                                    }
                                >
                                    <Option value='1'>静态值</Option>
                                    <Option value='2'>大于等于</Option>
                                </Select>
                                <Input allowClear />
                                <Link onClick={() => handleDelSpace(itemArr, pi, ci)}><IconClose /></Link>
                            </Space>
                        })
                    }
                    <Link onClick={() => {handleAddSpace('AND', itemArr, pi)}}>+&nbsp;并且</Link>
                </section>
            })}
            <Button onClick={() => {handleAddSpace('OR')}} type="outline" size="mini" className='gray-outline-btn'>+&nbsp;或者</Button>
        </div>
    </Modal>
}