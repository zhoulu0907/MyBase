import { DatePicker, Form, Modal } from "@arco-design/web-react";
import type { AppItem } from "../../types/appItem";

interface IEditAuthorizedModal {
    visible: boolean;
    initialFormData: AppItem | null;
    setVisible: (visible: boolean) =>void;
    onUpdateData: (data: any)=>void;
}
export const EditAuthorizedTime:React.FC<IEditAuthorizedModal> = ({initialFormData, visible, setVisible, onUpdateData}) => {
    const [editForm] = Form.useForm();

    const handleUpdateData = async() => {
        const values = await editForm.validate();
        onUpdateData(values);
    }
    return (
        <Modal
            title={
                <div style={{ textAlign: 'left' }}>
                编辑授权时间
                </div>
            } 
            visible={visible}
            onCancel={()=>setVisible(false)}
            onOk={handleUpdateData}
            >
            <Form form={editForm} initialValues={{
                appTime: {
                    authorizationTime: initialFormData?.authorizationTime,
                    expiresTime: initialFormData?.expiresTime
                }
            }}>
                <Form.Item
                    label="授权时间"
                    field="appTime"
                    rules={[{ required: true, message: '请选择授权时间' }]}
                    normalize={(value) => {
                    return {
                        authorizationTime: value && value[0],
                        expiresTime: value && value[1]
                        };
                    }}
                    formatter={(value) => {
                    return value && value.authorizationTime ? [value.authorizationTime, value.expiresTime] : [];
                    }}
                >
                    <DatePicker.RangePicker showTime />
                </Form.Item>
            </Form>
        </Modal>
    )
}

export default EditAuthorizedTime;