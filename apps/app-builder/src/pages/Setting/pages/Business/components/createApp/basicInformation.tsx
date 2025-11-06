import { Checkbox, Form, Input, Select } from "@arco-design/web-react"
import { industryOptions, noLabelLayout } from "../../constants"
import { type CorpBasicInfo } from "@onebase/platform-center";


export const BasicInformation:React.FC<CorpBasicInfo> = ({basicInfoForm}) => {
    return (
        <Form 
            requiredSymbol={{ position: 'end' }}
            form={basicInfoForm}  
            initialValues={{
                userLimit:"10000",
                status:true
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
            <Form.Item label="联系地址" field="address" rules={[{ required: true }]}>
                <Select placeholder="请选择" />
            </Form.Item>
            <Form.Item label="" field="address" {...noLabelLayout}>
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