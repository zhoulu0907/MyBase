import { Form, Input } from "@arco-design/web-react"

interface IAdminInfoProps {
    adminInfoForm:any
}

export const AdminInformation:React.FC<IAdminInfoProps> = ({adminInfoForm}) => {
    return (
        <Form form={adminInfoForm}>
            <Form.Item
                label="姓名"
                field="adminName"
                rules={[{ required: true, message: '请输入姓名' }]}
            >
                <Input placeholder="输入姓名" />
            </Form.Item>
            <Form.Item
                label="账号"
                field=""
                rules={[{ required: true, message: '请输入账号' }]}
            >
                <Input placeholder="输入账号" />
            </Form.Item>
            <Form.Item
                label="手机号"
                field="adminPhone"
                rules={[
                    { required: true, message: '请输入手机号' },
                ]}
            >
                <Input placeholder="输入手机号" maxLength={11} />
            </Form.Item>
            <Form.Item
                label="邮箱"
                field="adminEmail"
                rules={[
                    { required: true, message: '请输入邮箱' },
                    { type: 'email', message: '请输入正确的邮箱格式' }
                ]}
            >
                <Input placeholder="输入邮箱" />
            </Form.Item>
        </Form>
    )
}