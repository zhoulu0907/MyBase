import { Checkbox, Form, Input, Select } from "@arco-design/web-react"
import type { industryTypeOption } from "../../types/appItem";
import {type CorpBasicInfo} from "@onebase/platform-center";

interface IBasicInfoProps {
    basicValues: CorpBasicInfo,
    industryOptions: industryTypeOption[];
    onDataChange: (values: Record<string, any>) => void;
}

export const BasicInformation:React.FC<IBasicInfoProps> = ({ industryOptions, basicValues, onDataChange }) => {
    const [ basicInfoForm ] = Form.useForm();

    const handleValuesChange = (changedValues: Record<string, any>, allValues: Record<string, any>) => {
        onDataChange(allValues);
    };


    return (
        <Form 
            onValuesChange={handleValuesChange} 
            form={basicInfoForm}  
            initialValues={{
                userLimit:"10000",
                status:true,
                corpName: basicValues?.corpName,
                corpId: basicValues?.corpId,
                industryType: basicValues?.industryType,
                address: basicValues?.address

            }}
        >
            <Form.Item
                label="企业名称"
                field="corpName"
                rules={[{ required: true, message: '请输入企业名称' }]}
            >
                <Input placeholder="输入企业名称" maxLength={50} />
            </Form.Item>
            <Form.Item
                label="企业ID"
                field="corpId"
                rules={[{ required: true, message: '请输入企业ID' }]}
            >
                <Input placeholder="输入企业ID" />
            </Form.Item>
            <Form.Item
                label="行业类型"
                field="industryType"
                rules={[{ required: true, message: '请选择行业类型' }]}
            >
                <Select
                    options={industryOptions}
                    placeholder="行业类型"
                />
            </Form.Item>
            <Form.Item label="联系地址" field="address">
                <Input.TextArea placeholder="请输入详细地址" autoSize={{ minRows: 2, maxRows: 6 }} />
            </Form.Item>
            <Form.Item label="用户上限" field="userLimit" rules={[{ required: true }]}>
                <Input />
            </Form.Item>
            <Form.Item label="状态" field="status" 
                triggerPropName="checked" 
                rules={[{ required: true }]}>
                <Checkbox>启用</Checkbox>
            </Form.Item>
        </Form>
    )
}