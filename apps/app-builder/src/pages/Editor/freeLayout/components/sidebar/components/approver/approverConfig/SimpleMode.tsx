
import { useState } from 'react';
import { Radio, Form, Select } from '@arco-design/web-react';
import {IconQuestionCircle} from '@arco-design/web-react/icon';
import styles from './index.module.less';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const Option = Select.Option;
const options = ['Beijing', 'Shanghai', 'Guangzhou', 'Shenzhen', 'Chengdu', 'Wuhan'];

export default function SimpleMode() {
    const [simpleCkType, setSimpleCkType] = useState<string>('member');
    const [form] = Form.useForm();
    // 校验规则
    const approverFormRules = {
        approver: [{ required: true, message: '请选择审批人' }],
        roler: [{ required: true, message: '请选择角色' }]
    };
    return <>
        <RadioGroup className={styles.approverRadioGroup} value={simpleCkType} onChange={setSimpleCkType}>
            <Radio value="member">指定成员</Radio>
            <Radio value="role">指定角色<IconQuestionCircle /></Radio>
            <Radio value="deptManager" disabled>部门负责人</Radio>
            <Radio value="multistageManager" disabled>多级主管</Radio>
            <Radio value="directManager" disabled>直属主管</Radio>
            <Radio value="deptContact" disabled>部门接口人</Radio>
            <Radio value="initiator" disabled>发起人本人</Radio>
            <Radio value="initiatorChoice" disabled>发起人自选</Radio>
            <Radio value="formMember" disabled>表单内成员字段</Radio>
        </RadioGroup>
        <div className={styles.configTitle}></div>
        <Form
            form={form}
            layout="vertical"
            autoComplete="off"
            onValuesChange={(v, vs) => {
            console.log(v, vs);
            }}
            onSubmit={(v) => {
            console.log(v);
            }}
        >
            {simpleCkType === 'member' && <FormItem
            className={styles.approverForm}
            label="选择审批人"
            field="approver"
            rules={approverFormRules.approver}
            wrapperCol={{style: {width: '100%'}}}
            >
            <Select mode="multiple" placeholder="选择审批人" defaultValue={['Beijing', 'Shenzhen']} allowClear>
                {options.map((option) => (
                <Option key={option} value={option}>
                    {option}
                </Option>
                ))}
            </Select>
            </FormItem>}
            {simpleCkType === 'role' && <FormItem
            className={styles.approverForm}
            label="选择角色"
            field="roler"
            rules={approverFormRules.roler}
            wrapperCol={{style: {width: '100%'}}}
            >
            <Select mode="multiple" placeholder="选择角色" defaultValue={['Beijing', 'Shenzhen']} allowClear>
                {options.map((option) => (
                <Option key={option} value={option}>
                    {option}
                </Option>
                ))}
            </Select>
            </FormItem>}
        </Form>
    </>
}