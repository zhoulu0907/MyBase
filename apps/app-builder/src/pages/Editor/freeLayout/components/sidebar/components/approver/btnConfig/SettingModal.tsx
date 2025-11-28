import { useState } from 'react';
import { Modal, Radio, Select, Message, Tooltip } from '@arco-design/web-react';
import { IconQuestionCircle } from '@arco-design/web-react/icon';

const RadioGroup = Radio.Group;
const Option = Select.Option;

export default function SettingModal({settingsShow, setSettingShow}:any) {
    const [radioVal, setRadioVal] = useState('LAST-NODE')

    function handleRadio(val:any) {
        setRadioVal(val)
    }

    function handleOK() {
        setSettingShow(false)
    }

    return <Modal
        title={null}
        closable={false}
        visible={settingsShow}
        onOk={handleOK}
        onCancel={() => setSettingShow(false)}
        autoFocus={false}
        focusLock={true}
      >
        <p><b>退回节点配置</b></p>
        <div className='arco-row'>
            <span style={{paddingRight: '16px'}}>可回退至</span>
            <RadioGroup defaultValue='LAST-NODE' style={{ marginBottom: 20 }} onChange={handleRadio}>
                <Radio value='LAST-NODE'>上一节点
                    <Tooltip
                        position="top"
                        trigger="hover"
                        content="流转到上一个人工处理节点，而非流程图中的上一个节点"
                    >
                        <IconQuestionCircle style={{ fontSize: '15px', position: 'relative', left: '4px', top: '1px', color: '#AAAEB3' }} />
                    </Tooltip>
                </Radio>
                <Radio value='SELECT-NODE'>指定节点</Radio>
            </RadioGroup>
        </div>
        {radioVal === 'SELECT-NODE' && <Select
            placeholder='请选择'
            onChange={(value) =>
                Message.info({
                    content: `You select ${value}.`,
                    showIcon: true,
                })
            }
        >
            <Option value='123'>1231bb</Option>
            <Option value='1233'>1231aa</Option>
        </Select>}
    </Modal>
}