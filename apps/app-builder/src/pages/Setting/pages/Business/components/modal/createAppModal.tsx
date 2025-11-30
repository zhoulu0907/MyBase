import { Avatar, DatePicker, Form,  Modal, Select, Space, Typography } from "@arco-design/web-react";
import { formatTimeYMDHMS } from "@onebase/common";
import type { ICreateAppModal } from "../../types/appItem";
import styles from "./index.module.less";


export const CreateAppModal:React.FC<ICreateAppModal> = ({visible, dropdownList, tableData, onCloseAppModal, onSaveAppData}) => {
    const [createNewAppForm] = Form.useForm();
    const handleCancel = () => {
        onCloseAppModal();
        createNewAppForm.resetFields();
    }

    const handleSaveModal = async() => {
        const values = await createNewAppForm.validate();
        const formattedList = values.applicationIdList.map((value: string) => {
            const match = dropdownList.find(item => item.id === value);
            return {
                value,
                applicationName: match?.appName || "",
                versionNumber: match?.versionNumber || "",
                applicationCode: match?.appCode  || "",
                id: match?.id || ""
            };
        });
        onSaveAppData({ ...values, applicationIdList: formattedList });
        createNewAppForm.resetFields();
    }

    const filterOptions = dropdownList.filter(data => !tableData?.some(item => item.id === data.id));

    return (
        <Modal
            title={
                <div style={{ textAlign: 'left' }}>
                添加应用
                </div>
            } 
            visible={visible}
            onCancel={handleCancel}
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
                        showSearch
                        renderFormat ={(option:any, value: any)=>{
                            const selectedOption = dropdownList.find(item => item.id === value);
                            return selectedOption ? selectedOption.appName : value;
                        }}
                    >
                        {filterOptions.map((option) => (
                        <Select.Option key={option.id} value={option.id}>
                            <Space align="center" size={12}>
                                <Avatar style={{ backgroundColor: option.iconColor }}>{option.iconName}</Avatar>
                                <div className={styles.authorizedOption}>
                                    <Typography.Text>{option.appName}</Typography.Text>
                                    <Typography.Text type='secondary'>
                                        {option.versionNumber} · {formatTimeYMDHMS(option.createTime)}
                                    </Typography.Text>
                                </div>
                            </Space>
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