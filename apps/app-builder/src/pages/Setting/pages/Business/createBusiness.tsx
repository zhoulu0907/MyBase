import styles from "./createBusiness.module.less";
import { useState } from 'react';
import {
    Steps,
    Input,
    Select,
    Checkbox,
    Button,
    Form,
    Space,
    Typography,
    Message
} from '@arco-design/web-react';
import { useNavigate } from "react-router-dom";


// 步骤配置
const steps = [
    { title: '基本信息' },
    { title: '管理员信息' },
    { title: '授权应用' },
    { title: '完成' }
];

// 行业类型选项
const industryOptions = [
    { label: '工业', value: 'industry' },
    { label: '金融', value: 'finance' },
    { label: '教育', value: 'education' },
    { label: '医疗', value: 'medical' },
];

// 联系地址选项（省/市层级示例）
const addressOptions = [
    { label: '请选择', value: '' },
    { label: '北京市', value: 'beijing' },
    { label: '上海市', value: 'shanghai' },
    { label: '广东省', value: 'guangdong' },
];

const CreateBusinessPage: React.FC = () => {
    const [currentStep, setCurrentStep] = useState(1);
    const navigate = useNavigate();
    const [form] = Form.useForm();

    const noLabelLayout = {
        wrapperCol: {
            span: 19,
            offset: 5,
        },
    };
    // 步骤切换
    const handleNext = () => {
        if (currentStep < steps.length - 1) {
            setCurrentStep(currentStep + 1);
        }
    };

    const handlePrev = () => {
        if (currentStep > 1) {
            setCurrentStep(currentStep - 1);
        }else {
            navigate("..");
        }
    };

    const handleSubmit = async () => {
        const values = await form.validate();
        Message.success({
            content: '表单提交成功',
        });
        console.log('最终表单数据:', values);
    };

    const renderContent = (currentStep: number) => {
        return (
            <div className={styles.content}>
                <Form form={form}>
                    {/* 第一步：基本信息 */}
                    {currentStep === 1 && (
                        <>
                        <Form.Item
                            label="企业名称"
                            field="enterpriseName"
                            rules={[{ required: true, message: '请输入企业名称' }]}
                        >
                            <Input placeholder="输入企业名称" maxLength={50}/>
                        </Form.Item>
                        <Form.Item
                            label="企业ID"
                            field="enterpriseId"
                            rules={[{ required: true, message: '请输入企业ID' }]}
                        >
                            <Input placeholder="输入企业ID" />
                        </Form.Item>
                        <Form.Item
                            label="行业类型"
                            field="industry"
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
                        <Form.Item label="" field="detailAddress" {...noLabelLayout}>
                            <Input.TextArea placeholder="请输入详细地址" autoSize={{ minRows: 2, maxRows: 6 }} />
                        </Form.Item>
                        <Form.Item label="用户上限" field="userLimit" rules={[{ required: true }]}>
                            <Input value="2000" />
                        </Form.Item>
                        <Form.Item label="状态" field="status">
                            <Checkbox defaultChecked>启用</Checkbox>
                        </Form.Item>
                        </>
                    )}

                    {/* 第二步：管理员信息 */}
                    {currentStep === 2 && (
                        <>
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
                        </>
                    )}

                    {/* 第三步：确认信息 */}
                    {currentStep === 3 && (
                        <div style={{ backgroundColor: '#f5f7fa', padding: '20px', borderRadius: 4 }}>
                            <Typography.Title level={5} style={{ marginBottom: 16 }}>
                                确认以下信息无误
                            </Typography.Title>

                            <div style={{ display: 'grid', gridTemplateColumns: '120px 1fr', gap: '12px 0' }}>
                                <Typography.Text type="secondary">企业名称：</Typography.Text>
                                <Typography.Text>{form.getFieldValue('enterpriseName') || '-'}</Typography.Text>

                                <Typography.Text type="secondary">企业ID：</Typography.Text>
                                <Typography.Text>{form.getFieldValue('enterpriseId') || '-'}</Typography.Text>

                                <Typography.Text type="secondary">行业类型：</Typography.Text>
                                <Typography.Text>
                                    {industryOptions.find(item => item.value === form.getFieldValue('industry'))?.label || '-'}
                                </Typography.Text>

                                <Typography.Text type="secondary">管理员：</Typography.Text>
                                <Typography.Text>{form.getFieldValue('adminName') || '-'}</Typography.Text>
                            </div>
                        </div>
                    )}
                </Form>
            </div>
        )
    }

    return (
        <div className={styles.createBusinessConatiner}>
            {/* 导航条 */}
            <Steps
                current={currentStep}
            >
                {steps.map((step, index) => (
                    <Steps.Step
                        key={index}
                        title={step.title}
                    />
                ))}
            </Steps>
            <div className={styles.BusinessInformation}>
                {/* 内容区域 */}
                {renderContent(currentStep)}
                {/* 底部操作按钮 */}
                <div className={styles.footerButton}>
                    <Space size={16}>
                        <Button
                            onClick={handlePrev}
                            disabled={currentStep === 0}
                        >
                            {currentStep === 1 ? "返回" : "上一步"}
                        </Button>
                        <Button
                            type="primary"
                            onClick={handleNext}
                            disabled={currentStep === steps.length - 1}
                        >
                            下一步
                        </Button>
                    </Space>
                </div>
            </div>
        </div>
    );
};

export default CreateBusinessPage;