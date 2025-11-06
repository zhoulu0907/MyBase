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
import EditAuthorizedTime from "./components/modal/editAuthorizedTime";
import { useTableData } from "./hooks/useTable";


const CreateBusinessPage: React.FC<ICreateBusinessPageProps> = () => {
    const authorizedAppRef = useRef<AuthorizedAppRef>(null);
    const [currentStep, setCurrentStep] = useState<number>(1);
    const navigate = useNavigate();
    const [basicInfoForm] = Form.useForm();
    const [adminInfoForm] = Form.useForm();
    const [addAppModalVisible, setAddAppModalVisible] = useState<boolean>(false);
    const [editTimeVisible, setEditTimeVisible] = useState<boolean>(false);

    //点击创建应用的第三步中table的编辑button
    const handleEdit = (record?: AppItem) => {
        navigate(`${record?.appName}`)
    }

    const handleEditTime = () => {
        setEditTimeVisible(true);
    }

    const handleUpdateTime = () => {
        
    }

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
                        <AuthorizedApp ref={authorizedAppRef} onEdit={handleEditTime} setAddAppModalVisible={setAddAppModalVisible}/>
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
                {/* 编辑授权应用 */}
                <EditAuthorizedTime visible={editTimeVisible} setVisible={setEditTimeVisible} onUpdateData={handleUpdateTime} />
                {/* 创建应用modal */}
                <CreateAppModal visible={addAppModalVisible} onCloseAppModal={handleCloseModal} onSaveAppData={handleAddSubmit} />
            </div>
        </div>
    );
};

export default CreateBusinessPage;