import { DatePicker, Form, Message, Modal, Select } from "@arco-design/web-react";
import { getCorpAppSimpleListApi } from "@onebase/platform-center";
import type { authorizedAppList, ICreateAppModal } from "../../types/appItem";
import { useEffect, useState } from "react";

export const CreateAppModal:React.FC<ICreateAppModal> = ({visible, onCloseAppModal, onSaveAppData}) => {
    const [createNewAppForm] = Form.useForm();
    const [dropdownList, setDropdownList] = useState<authorizedAppList[]>([]);

    const getApplicationIdResult = async() => {
        try{
           const res: authorizedAppList[]= await getCorpAppSimpleListApi();
           setDropdownList(res ? res : [])
        }catch(error) {
            Message.error("获取列表失败")
        }
    }

    useEffect(() => {
        getApplicationIdResult();
    },[])

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
                    field="applicationIdList"
                    rules={[{ required: true, message: '请选择应用' }]}
                >
                    <Select
                        mode='multiple'
                        placeholder="选择应用"
                        allowClear
                    >
                        {dropdownList.map((option) => (
                        <Select.Option key={option.id} value={option.corpId}>
                            {option.corpName}
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