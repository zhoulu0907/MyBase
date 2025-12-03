import React, { useEffect } from 'react';
import { Form, Input, Modal, Button, DatePicker, Select } from '@arco-design/web-react';

/**
 * EditProxyModal 组件
 * 用于页面管理器中重命名弹窗的占位组件
 * 实际弹窗逻辑在 PageManagerPage 中实现
 */
interface ModalProps {
  visible: boolean;
  setVisible: (visible: boolean) => void;
  handleModalForm: () => void;
  initRowData: any;
}

const Option = Select.Option;

const EditProxyModal: React.FC<ModalProps> = ({ visible, handleModalForm, setVisible, initRowData }) => {
    let [form] = Form.useForm()

    function handleFormOk() {
        console.log('1===', form.getFieldsValue())
        if (handleModalForm) {
            handleModalForm()
        }
    }

    useEffect(() => {
        if (initRowData) {
            form.setFieldsValue({
                menuName1: '1232',
                menuName2: '1235',
                menuName3: ["2025-10-23", "2025-11-28"]
            })
        } else {
            form.setFieldsValue({
                menuName1: undefined,
                menuName2: undefined,
                menuName3: undefined
            })
        }
    }, [])

    return (
        <Modal
            title={<div style={{ textAlign: 'left' }}>新增代理</div>}
            visible={visible}
            onOk={handleFormOk}
            onCancel={() => {
                setVisible(false);
            }}
            unmountOnExit={true}
        >
        <div>
            <Form
                layout="vertical"
                form={form}
            >
            <Form.Item label="被代理人" field="menuName1" rules={[{ required: true, message: '请选择被代理人' }]}>
                <Select placeholder='请选择被代理人' style={{ width: '100%' }}>
                    <Option value='1231'>123</Option>
                    <Option value='1232'>123</Option>
                    <Option value='1233'>123</Option>
                </Select>
            </Form.Item>
            <Form.Item label="代理人" field="menuName2" rules={[{ required: true, message: '请选择代理人' }]}>
                <Select placeholder='请选择代理人' style={{ width: '100%' }}>
                    <Option value='1234'>123</Option>
                    <Option value='1235'>123</Option>
                    <Option value='1236'>123</Option>
                </Select>
            </Form.Item>
            <Form.Item label="代理有效期" field="menuName3" rules={[{ required: true, message: '请选择代理有效期' }]}>
                <DatePicker.RangePicker style={{ width: '100%' }} />
            </Form.Item>
            </Form>
        </div>
        </Modal>
    );
};

export default EditProxyModal;
