import { useState } from 'react';
import { Modal, Input, Select, Radio } from '@arco-design/web-react';
import './style.less'

const Option = Select.Option;
const RadioGroup = Radio.Group;

const selOptions = ['Beijing', 'Shanghai', 'Guangzhou', 'Shenzhen', 'Chengdu', 'Wuhan'];

export default function TimeModal({timeModalShow, setTimeModalShow}:any) {
  const [timeoutVal, setTimeoutVal] = useState()
  const [timeoutEnd, setTimeoutEnd] = useState('hour')

  function handleItemChange(val:any, type:string) {
    console.log(val, type)
  }

  return <Modal
      title={<div style={{ textAlign: 'left' }}>超时处理</div>}
      className='time-modal'
      visible={timeModalShow}
      onOk={() => setTimeModalShow(false)}
      onCancel={() => setTimeModalShow(false)}
    >
      <div className='arco-form-item arco-form-layout-vertical'>
        <label className='arco-form-label-item'>
          <strong className='arco-form-item-symbol'>*</strong>超时时间
        </label>
        <div className='arco-form-item-control-wrapper form-item-wrapper'>
          <span>当流程到达审批节点</span>
          <Input value={timeoutVal} onChange={(e) => handleItemChange(e, 'timeoutVal')} placeholder='请输入' style={{width: '100px'}} />
          <Select onChange={(val) => handleItemChange(val, 'timeoutEnd')} value={timeoutEnd} style={{width: '100px'}}>
            <Option value="week">周</Option>
            <Option value="day">天</Option>
            <Option value="hour">时</Option>
          </Select>
          <span>后仍未处理时</span>
        </div>
      </div>
      <div className='arco-form-item arco-form-layout-vertical'>
        <label className='arco-form-label-item'>
          <strong className='arco-form-item-symbol'>*</strong>执行动作
        </label>
        <div className='arco-form-item-control-wrapper'>
          <RadioGroup onChange={(val) => handleItemChange(val, 'radioAction')} style={{ padding: '5px 0' }}>
                <Radio value='a'>自动提醒</Radio>
                <Radio value='b'>自动转交</Radio>
                <Radio value='c'>自动跳转</Radio>
                <Radio value='d'>自动同意</Radio>
                <Radio value='e'>自动拒绝</Radio>
          </RadioGroup>
        </div>
      </div>
      <section>
        <div className='arco-form-item arco-form-layout-vertical'>
          <label className='arco-form-label-item'>
            <strong className='arco-form-item-symbol'>*</strong>提醒人员
          </label>
          <div className='arco-form-item-control-wrapper'>
            <RadioGroup onChange={(val) => handleItemChange(val, 'radioCallPeople')} style={{ padding: '5px 0' }} direction='vertical'>
                  <Radio value='a'>当前审批人</Radio>
                  <Radio value='b'>指定成员/角色/字段
                    <Select
                      mode='multiple'
                      placeholder='请选择人员'
                      style={{width: '369px', position: 'absolute', left: '100%', top: 0, marginLeft: '16px'}}
                      filterOption={(inputValue, option) =>
                        option.props.children?.toLowerCase().indexOf(inputValue?.toLowerCase()) >= 0
                      }
                      value={['Shenzhen']}
                      allowClear
                    >
                      {selOptions.map((option) => (
                        <Option key={option} value={option}>
                          {option}
                        </Option>
                      ))}
                    </Select>
                  </Radio>
            </RadioGroup>
          </div>
        </div>
        <div className='arco-form-item arco-form-layout-vertical'>
          <label className='arco-form-label-item'>
            <strong className='arco-form-item-symbol'>*</strong>提醒形式
          </label>
          <div className='arco-form-item-control-wrapper'>
            <RadioGroup onChange={(val) => handleItemChange(val, 'radioMsgType')} style={{ padding: '5px 0' }}>
                  <Radio value='a'>邮件通知</Radio>
                  <Radio value='b'>短信通知</Radio>
            </RadioGroup>
          </div>
        </div>
        <div className='arco-form-item arco-form-layout-vertical'>
          <label className='arco-form-label-item'>
            <strong className='arco-form-item-symbol'>*</strong>通知内容
          </label>
          <div className='arco-form-item-control-wrapper'>
            <RadioGroup onChange={(val) => handleItemChange(val, 'radioMsgContent')} style={{ padding: '5px 0' }}>
                  <Radio value='a'>自定义</Radio>
                  <Radio value='b'>使用通知模板</Radio>
                  <Select value={timeoutEnd} style={{width: '316px'}}>
                    <Option value="week">周</Option>
                    <Option value="day">天</Option>
                    <Option value="hour">时</Option>
                  </Select>
            </RadioGroup>
          </div>
        </div>
        <div className='gray-outer'>
          <div className='arco-form-item arco-form-layout-vertical'>
            <label className='arco-form-label-item'>
              <strong className='arco-form-item-symbol'>*</strong>通知标题
            </label>
            <div className='arco-form-item-control-wrapper form-item-wrapper'>
              <Input value={timeoutVal} placeholder='请输入' style={{width: '100%'}} />
            </div>
          </div>
          <div className='arco-form-item arco-form-layout-vertical'>
            <label className='arco-form-label-item'>
              <strong className='arco-form-item-symbol'>*</strong>通知内容
            </label>
            <div className='arco-form-item-control-wrapper form-item-wrapper'>
              <Input.TextArea value={timeoutVal} placeholder='请输入' style={{width: '100%'}} 
                              maxLength={{ length: 500, errorOnly: true }}
                              showWordLimit/>
            </div>
          </div>
        </div>
      </section>
      <div className='arco-form-item-control-wrapper form-item-wrapper'>
        <span>转交至</span>
        <Select
          mode='multiple'  
          placeholder='请选择人员'
          style={{width: '80%'}}
          filterOption={(inputValue, option) =>
            option.props.children?.toLowerCase().indexOf(inputValue?.toLowerCase()) >= 0
          }
          value={['Shenzhen']}
          allowClear
        >
          {selOptions.map((option) => (
            <Option key={option} value={option}>
              {option}
            </Option>
          ))}
        </Select>
      </div>
      <div className='arco-form-item-control-wrapper form-item-wrapper'>
        <span>跳转至</span>
        <Select
          placeholder='请选择人员'
          style={{width: '80%'}}
          value='hour'
          allowClear
        >
          <Option value="week">周</Option>
          <Option value="day">天</Option>
          <Option value="hour">时</Option>
        </Select>
      </div>
  </Modal>
}