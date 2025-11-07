import { Form, Input } from "@arco-design/web-react"
import { type CorpAdminInfo} from "@onebase/platform-center";

interface IAdminInfoProps {
    adminValues: CorpAdminInfo;
    onDataChange: (values: Record<string, any>) => void;
}

export const AdminInformation:React.FC<IAdminInfoProps> = ({adminValues, onDataChange}) => {
    const [adminInfoForm] = Form.useForm();

    const handleValuesChange = (changedValues: Record<string, any>, allValues: Record<string, any>) => {
        onDataChange(allValues);
    };

    return (
        <Form 
            form={adminInfoForm} 
            onValuesChange={handleValuesChange} 
            initialValues={{
                username: adminValues?.username, 
                nickname:adminValues?.nickname,
                mobile: adminValues?.mobile,
                email: adminValues?.email
            }}
        >
            <Form.Item
                label="姓名"
                field="username"
                rules={[{ required: true, message: '请输入姓名' }]}
            >
                <Input placeholder="输入姓名" />
            </Form.Item>
            <Form.Item
                label="账号"
                field="nickname"
                rules={[{ required: true, message: '请输入账号' }]}
            >
                <Input placeholder="输入账号" />
            </Form.Item>
            <Form.Item
                label="手机号"
                field="mobile"
                rules={[
                    { required: true, message: '请输入手机号' },
                ]}
            >
                <Input placeholder="输入手机号" maxLength={11} />
            </Form.Item>
            <Form.Item
                label="邮箱"
                field="email"
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