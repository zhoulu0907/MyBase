
import { useState, forwardRef, useImperativeHandle } from 'react';
import { Radio, Form, Select } from '@arco-design/web-react';
import {IconQuestionCircle} from '@arco-design/web-react/icon';
import styles from './index.module.less';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const Option = Select.Option;

const SimpleMode = forwardRef((props, ref) => {
    const [selOptions, setSelOptions] = useState([
        {name: 'Beijing', userId: 'beijing'}, 
        {name: 'Shanghai', userId: 'shanghai'}, 
        {name: 'Guangzhou', userId: 'guangzhou'},
    ]);
    const [simpleCkType, setSimpleCkType] = useState<string>('user');
    const [form] = Form.useForm();
    // 校验规则
    const approverFormRules = {
        user: [{ required: true, message: '请选择审批人' }],
        role: [{ required: true, message: '请选择角色' }]
    };

    useImperativeHandle(ref, () => ({
        getModeData: () => {
            let formRes = form.getFieldsValue()
            console.log('formRes ===', formRes)
            return {
                approverType: simpleCkType,
                users: selOptions.filter((item:any) => {
                    if (Array.isArray(formRes[simpleCkType])) {
                        return formRes[simpleCkType].indexOf(item.userId) > -1
                    }
                    return false
                })
            }
        }
    }));

    return <>
        <RadioGroup className={styles.approverRadioGroup} value={simpleCkType} onChange={setSimpleCkType}>
            <Radio value="user">指定成员</Radio>
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
        >
            {simpleCkType === 'user' && <FormItem
                    className={styles.approverForm}
                    label="选择审批人"
                    field="user"
                    rules={approverFormRules.user}
                    wrapperCol={{style: {width: '100%'}}}
                >
                <Select mode="multiple" placeholder="选择审批人" defaultValue={['Beijing', 'Shenzhen']} allowClear>
                    {selOptions.map((option:any) => (
                        <Option key={option?.userId} value={option?.userId}>
                            {option.name}
                        </Option>
                    ))}
                </Select>
            </FormItem>}
            {simpleCkType === 'role' && <FormItem
                    className={styles.approverForm}
                    label="选择角色"
                    field="role"
                    rules={approverFormRules.role}
                    wrapperCol={{style: {width: '100%'}}}
                >
                <Select mode="multiple" placeholder="选择角色" defaultValue={['Beijing', 'Shenzhen']} allowClear>
                    {selOptions.map((option:any) => (
                        <Option key={option?.userId} value={option?.userId}>
                            {option.name}
                        </Option>
                    ))}
                </Select>
            </FormItem>}
        </Form>
    </>
})

export default SimpleMode;