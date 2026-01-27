import { Checkbox, Form, Input, InputNumber, Select } from '@arco-design/web-react';
import { type CorpBasicInfo } from '@onebase/platform-center';
import type { industryTypeOption } from '../../types/appItem';

interface IBasicInfoProps {
  basicValues: CorpBasicInfo;
  industryOptions: industryTypeOption[];
  onDataChange: (values: Record<string, any>) => void;
}

const MAX_USER_LIMIT = 500000; // 最大用户上限
export const BasicInformation: React.FC<IBasicInfoProps> = ({ industryOptions, basicValues, onDataChange }) => {
  const [basicInfoForm] = Form.useForm();

  const convertIndustryOption = industryOptions.map(
    (option: industryTypeOption) => ({ label: option.label, value: option.id }) as industryTypeOption
  );

  const handleValuesChange = (_changedValues: Record<string, any>, allValues: Record<string, any>) => {
    onDataChange(allValues);
  };

  return (
    <Form
      onValuesChange={handleValuesChange}
      form={basicInfoForm}
      initialValues={{
        userLimit: basicValues?.userLimit || 5000,
        status: basicValues?.status === 0 ? false : true,
        corpName: basicValues?.corpName,
        corpCode: basicValues?.corpCode,
        industryType: basicValues?.industryType,
        address: basicValues?.address
      }}
    >
      <Form.Item label="企业名称" field="corpName" rules={[{ required: true, message: '请输入企业名称' }]}>
        <Input placeholder="输入企业名称" maxLength={50} />
      </Form.Item>
      <Form.Item label="企业编码" field="corpCode" rules={[{ required: true, message: '请输入企业编码' }]}>
        <Input placeholder="输入企业编码" />
      </Form.Item>
      <Form.Item label="行业类型" field="industryType" rules={[{ required: true, message: '请选择行业类型' }]}>
        <Select options={convertIndustryOption} placeholder="行业类型" />
      </Form.Item>
      <Form.Item label="联系地址" field="address">
        <Input.TextArea placeholder="请输入详细地址" autoSize={{ minRows: 2, maxRows: 6 }} />
      </Form.Item>
      <Form.Item label="用户上限" field="userLimit" rules={[{ required: true, message: '请输入用户上限' }]}>
        <InputNumber placeholder="请输入用户上限" max={MAX_USER_LIMIT} min={0} />
      </Form.Item>
      <Form.Item label="状态" field="status" triggerPropName="checked" rules={[{ required: true }]}>
        <Checkbox>启用</Checkbox>
      </Form.Item>
    </Form>
  );
};
