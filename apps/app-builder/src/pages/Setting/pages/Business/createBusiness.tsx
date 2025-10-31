import styles from "./createBusiness.module.less";
import { useMemo, useRef, useState } from 'react';
import { Steps, Button, Form, Space, Tag, Message } from '@arco-design/web-react';
import { useNavigate } from "react-router-dom";
import { steps } from "./constants";
import { CreateSuccess } from "./components/createApp/createSuccess";
import { BasicInformation } from "./components/createApp/basicInformation";
import { AuthorizedApp } from "./components/createApp/authorizedApp";
import { AdminInformation } from "./components/createApp/adminInfomation";
import { CreateAppModal } from "./components/modal/createAppModal";
import type { AppItem, AuthorizedAppRef } from "./types/appItem";

interface applicationTableData {
    key: number;
    appName: string;
    appId: string;
    version: string;
    effectTime: string;
    expireTime: string;
}

interface ICreateBusinessPageProps {
}

const CreateBusinessPage: React.FC<ICreateBusinessPageProps> = () => {
    // 1. 创建 ref 关联 AuthorizedApp 组件
    const authorizedAppRef = useRef<AuthorizedAppRef>(null);
    const [currentStep, setCurrentStep] = useState<number>(1);
    const [currentPage, setCurrentPage] = useState(1);
    const [tableData, setTableData] = useState<applicationTableData[]>([]);
    const [searchInputValue, setSearchInputValue] = useState<string>(''); // 输入框显示的值
    const navigate = useNavigate();
    const [basicInfoForm] = Form.useForm();
    const [adminInfoForm] = Form.useForm();
    const [addAppModalVisible, setAddAppModalVisible] = useState<boolean>(false);

    //点击创建应用的第三步中table的编辑button
    const handleEdit = (name: string) => {
        navigate(`${name}`)
    }

    const handleDelete = (key: number) => {
        if (!key) return;
        const newTableData = tableData.filter(item => item.key !== key);
        setTableData(newTableData);
        Message.success('移除成功');
    }

    const columns = [
        {
            title: '应用名称',
            dataIndex: 'appName',
            width: 180,
            render: (text: string) => (
                <Space size={12} align="center">{text}</Space>
            ),
        },
        {
            title: '应用ID',
            dataIndex: 'appId',
            width: 180,
        },
        {
            title: '版本号',
            dataIndex: 'version',
            width: 100,
            render: (text: string) => (
                <Tag color="gray" size="small">{text}</Tag>
            ),
        },
        {
            title: '授权启效时间',
            dataIndex: 'effectTime',
            width: 200,
        },
        {
            title: '过期时间',
            dataIndex: 'expireTime',
            width: 200,
        },
        {
            title: '操作',
            width: 140,
            render: (_: any, record: any) => (
                <Space size="mini">
                    <Button type="text" onClick={() => handleEdit(record)}>
                        编辑
                    </Button>
                    <Button type="text" onClick={() => handleDelete(record.key)}>
                        移除
                    </Button>
                </Space>
            ),
        },
    ];

    // steps切换
    const handleNext = async () => {
        if (currentStep < steps.length) {
            setCurrentStep(currentStep + 1);
        }
        const values = await basicInfoForm.validate();
    };

    //点击创建应用的上一步button
    const handlePrev = () => {
        if (currentStep > 1) {
            setCurrentStep(currentStep - 1);
        } else {
            navigate("..");
        }
    };

    // 处理分页变化
    const handlePageChange = async (pageNo: number) => {
        try {
            setCurrentPage(pageNo);
        } catch (error) {
            console.error(error);
        }
    };

    //点击搜索
    const handleSearchInput = (searchValue: string) => {
        setSearchInputValue(searchValue);
    }

    const displayData = useMemo(() => {
        if (!searchInputValue.trim()) {
        return tableData;
        }
        // 有搜索值时，过滤原始数据
        const lowerKey = searchInputValue.toLowerCase();
        return tableData.filter(item => 
        item.appName.toLowerCase().includes(lowerKey)
        );
    }, [tableData, searchInputValue]); 

    //点击modal的取消按钮
    const handleCloseModal = () => {
        setAddAppModalVisible(false);
    }
    // 提交新应用（弹窗确认后调用）
    const handleAddSubmit = (newAppData: any) => {
        const newData: AppItem = {
            key: displayData.length + 1,
            appId: "113",
            effectTime: newAppData.appTime.effectTime,
            expireTime: newAppData.appTime.expireTime,
            appName: newAppData.appName[0],
            version: "V2.3"
        };
        authorizedAppRef.current?.addNewApp(newData);
        setAddAppModalVisible(false);
    };

    const renderContent = (currentStep: number) => {
        return (
            <div className={currentStep === 3 ? "" : styles.content}>
                <div>
                    {/* 第一步：基本信息 */}
                    {currentStep === 1 && (
                        <BasicInformation basicInfoForm={basicInfoForm} />
                    )}
                    {/* 第二步：管理员信息 */}
                    {currentStep === 2 && (
                        <AdminInformation adminInfoForm={adminInfoForm} />
                    )}
                    {/* 第三步： 授权应用 */}
                    {currentStep === 3 && (
                        <AuthorizedApp ref={authorizedAppRef} onEdit={handleEdit} setAddAppModalVisible={setAddAppModalVisible}/>
                    )}
                    {/* 第四步：确认信息 */}
                    {currentStep === 4 && (
                        <CreateSuccess 
                         basicInfoForm={basicInfoForm} 
                         setCurrentStep={setCurrentStep} 
                         setAddAppModalVisible={setAddAppModalVisible}
                        />
                    )}
                </div>
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
            {/* 内容展示区域 */}
            <div className={styles.BusinessInformation}>
                {/* 内容区域 */}
                {renderContent(currentStep)}
                {/* 底部操作按钮 */}
                {currentStep !== 4 &&
                    <div className={styles.footerButton}>
                        <Space size={16}>
                            <Button
                                onClick={handlePrev}
                            >
                                {currentStep === 1 ? "返回" : "上一步"}
                            </Button>
                            <Button
                                type="primary"
                                onClick={handleNext}
                                disabled={currentStep === steps.length}
                            >
                                下一步
                            </Button>
                        </Space>
                    </div>}
                            {/* 创建应用modal */}
                <CreateAppModal visible={addAppModalVisible} onCloseAppModal={handleCloseModal} onSaveAppData={handleAddSubmit} />
            </div>
        </div>
    );
};

export default CreateBusinessPage;