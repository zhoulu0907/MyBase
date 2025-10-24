/**
 * 审批人
 */
import { Radio, Divider, Form, Select, Space, Tag } from '@arco-design/web-react';
import styles from './index.module.less';
import { useState } from 'react';
const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const Option = Select.Option;
const options = ['Beijing', 'Shanghai', 'Guangzhou', 'Shenzhen', 'Chengdu', 'Wuhan'];

export default function Approver() {
  const [configMode, setConfigMode] = useState<string>('simple');
  const [approverType, setApproverType] = useState<string>('member');
  const [form] = Form.useForm();
  // 校验规则
  const approverFormRules = {
    approver: [{ required: true, message: '请选择审批人' }]
  };
  return (
    <div className={styles.approverConfig}>
      <div className={styles.configTitle}>审批人设置</div>
      <div className={styles.configMode}>
        <span>配置模式：</span>
        <RadioGroup value={configMode} onChange={setConfigMode}>
          <Radio value="simple">简易模式</Radio>
          <Radio value="condition">条件模式</Radio>
        </RadioGroup>
      </div>

      {configMode === 'simple' && (
        <>
          <Divider />
          <RadioGroup className={styles.approverRadioGroup} value={approverType} onChange={setApproverType}>
            <Radio value="member">指定成员</Radio>
            <Radio value="role">指定角色</Radio>
            <Radio value="deptManager">部门负责人</Radio>
            <Radio value="multistageManager">多级主管</Radio>
            <Radio value="directManager">直属主管</Radio>
            <Radio value="deptContact">部门接口人</Radio>
            <Radio value="initiator">发起人本人</Radio>
            <Radio value="initiatorChoice">发起人自选</Radio>
            <Radio value="formMember">表单内成员字段</Radio>
          </RadioGroup>
        </>
      )}
      <div className={styles.configTitle}></div>
      <Form
        form={form}
        autoComplete="off"
        onValuesChange={(v, vs) => {
          console.log(v, vs);
        }}
        onSubmit={(v) => {
          console.log(v);
        }}
      >
        <FormItem
          className={styles.approverForm}
          label="选择审批人"
          field="approver"
          rules={approverFormRules.approver}
        >
          <Select mode="multiple" placeholder="选择审批人" defaultValue={['Beijing', 'Shenzhen']} allowClear>
            {options.map((option) => (
              <Option key={option} value={option}>
                {option}
              </Option>
            ))}
          </Select>
        </FormItem>
      </Form>
    </div>
  );
}
