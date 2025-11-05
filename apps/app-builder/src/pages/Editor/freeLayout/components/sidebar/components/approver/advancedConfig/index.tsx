import { useState } from 'react';
import { Checkbox, Radio } from '@arco-design/web-react';
import TimeModal from './TimeModal';
import './style.less'

const CheckboxGroup = Checkbox.Group;
const RadioGroup = Radio.Group;

const autoCheckArr = [
  {
    label: '发起人自动审批（当前节点人员为发起人时，自动审批）',
    value: 'auto_approval',
  },
  {
    label: '重复人员自动审批（当前节点人员已在前置审批节点中进行过操作时，自动审批）',
    value: 'repeat_approval',
  },
  {
    label: '相邻节点重复人员自动审批（当前节点人员已在上一审批节点中进行过操作时，自动审批）',
    value: 'neighbor_approval',
  },
];

export default function AdvancedConfig() {
    const [timeModalShow, setTimeModalShow] = useState(false)
    function handleCheckChange(val:any) {
        console.log(val)
    }
    return <section className='heigher-config'>
        <div className='title-box'>自动审批</div>
        <CheckboxGroup onChange={handleCheckChange} direction='vertical' options={autoCheckArr} />
        <div className='title-box' style={{padding: '32px 0 0'}}>审批人为空时</div>
        <RadioGroup onChange={handleCheckChange} style={{ marginBottom: 20 }}>
            <Radio value='a'>流程暂停（即不允许为空）</Radio>
            <Radio value='b'>自动跳过节点</Radio>
            <Radio value='c'>转交给应用管理员</Radio>
            <Radio value='d'>转交给指定成员</Radio>
      </RadioGroup>
      {timeModalShow && <TimeModal timeModalShow={timeModalShow} setTimeModalShow={setTimeModalShow}/>}
    </section>
}