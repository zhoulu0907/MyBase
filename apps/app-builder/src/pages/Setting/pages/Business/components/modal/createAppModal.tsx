import { DatePicker, Form, Modal, Select } from "@arco-design/web-react";

interface ICreateAppModal {
    visible: boolean;
    onCloseAppModal: () =>void;
    onSaveAppData: (data: any)=>void;
}
export const CreateAppModal:React.FC<ICreateAppModal> = ({visible, onCloseAppModal, onSaveAppData}) => {
    const [createNewAppForm] = Form.useForm();

    const handleSaveModal = async() => {
        const values = await createNewAppForm.validate();
        onSaveAppData(values);
    }
    return (
        <Modal
            title={
                <div style={{ textAlign: 'left' }}>
                添加应用
                </div>
            } 
            visible={visible}
            onCancel={onCloseAppModal}
            onOk={handleSaveModal}
            >
            <Form form={createNewAppForm}>
                <Form.Item
                    label="选择应用"
                    field="appName"
                    rules={[{ required: true, message: '请选择应用' }]}
                >
                    <Select
                        mode='multiple'
                        placeholder="选择应用"
                        allowClear
                    >
                        {["CM1","CM2"].map((option) => (
                        <Select.Option key={option} value={option}>
                            {option}
                        </Select.Option>
                        ))}
                    </Select>
                </Form.Item>
                <Form.Item
                    label="授权时间"
                    field="appTime"
                    rules={[{ required: true, message: '请选择授权时间' }]}
                    normalize={(value) => {
                    return {
                        effectTime: value && value[0],
                        expireTime: value && value[1]
                        };
                    }}
                    formatter={(value) => {
                    return value && value.effectTime ? [value.effectTime, value.expireTime] : [];
                    }}
                >
                    <DatePicker.RangePicker showTime />
                </Form.Item>
            </Form>
        </Modal>
    )
}