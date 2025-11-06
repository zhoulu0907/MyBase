import styles from "./createBusiness.module.less";
import { useEffect, useMemo, useRef, useState } from 'react';
import { Steps, Button, Form, Space, Message } from '@arco-design/web-react';
import { useNavigate, useOutletContext } from "react-router-dom";
import { steps } from "./constants";
import { CreateSuccess } from "./components/createApp/createSuccess";
import { BasicInformation } from "./components/createApp/basicInformation";
import { AuthorizedApp } from "./components/createApp/authorizedApp";
import { AdminInformation } from "./components/createApp/adminInfomation";
import { CreateAppModal } from "./components/modal/createAppModal";
import type { AppItem, AuthorizedAppRef, OutletContextType } from "./types/appItem";
import EditAuthorizedTime from "./components/modal/editAuthorizedTime";
import {removeCorpAppApi, updateCorpAppApi,getCorpAuthorizedAppListApi, type updatedParams, type corpListParams} from "@onebase/platform-center";

const CreateBusinessPage: React.FC = () => {
    const authorizedAppRef = useRef<AuthorizedAppRef>(null);
    const { industryOptions } = useOutletContext<OutletContextType>(); 
    const [currentStep, setCurrentStep] = useState<number>(1);
    const navigate = useNavigate();
    const [loading, setLoading] = useState<boolean>(false);
    const [tableData, setTableData] = useState<AppItem[]>([]);
    const [visible, setVisible] = useState<boolean>(false);
    const [addAppModalVisible, setAddAppModalVisible] = useState<boolean>(false);
    const [editTimeVisible, setEditTimeVisible] = useState<boolean>(false);
    const [searchValue, setSearchValue] = useState<string>("");
    const [pageInation, setPagination] = useState({
        showTotal: true,
        total: 0,
        pageSize: 10,
        current: 1,
        sizeCanChange: true,
        pageSizeChangeResetCurrent: true
    });

    const fetchCorpAuthorizedList = async(pageNo = 1, pageSize = 10) => {
        setLoading(true);
        const params: corpListParams = {
            pageNo,
            pageSize
        };
        try {
        const res = await getCorpAuthorizedAppListApi(params);
        if (res && Array.isArray(res.list)) {
            setTableData(res.list);
            setPagination((prev) => ({ ...prev, current: pageNo, pageSize, total: res.total || 0 }));
        } else {
            console.warn('Invalid response format:', res);
        }
        }catch(error) {
            Message.error("获取企业授权应用列表失败");
        }finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        if(currentStep === 3) {
            fetchCorpAuthorizedList()
        }
    },[])

    //授权应用分页切换
    const handlePageChange = (current: number, pageSize: number) => {
        fetchCorpAuthorizedList(current, pageSize);
    };
   
    const handleRemoveAuthorizedApp = async(id: string) => {
    try {
        const res = await removeCorpAppApi(id);
        if(res) {
            await fetchCorpAuthorizedList(pageInation.current,pageInation.pageSize);
            Message.success("授权应用删除成功");
        }else {
            Message.success("未删除成功");
        }
        }catch(error) {
            Message.error("接口返回异常, 授权应用删除失败");
        }
    }

    const handleSearchChange = (searchValue: string) => {
        setSearchValue(searchValue);
    }

    const handleUpdateTime = async(params: updatedParams) => {
    try {
     const res =  await updateCorpAppApi(params);
     if(res) {
        await fetchCorpAuthorizedList(pageInation.current,pageInation.pageSize);
        Message.success("更新授权时间成功");
     }else {
       Message.success("接口返回数据异常");
     }
    }catch(error) {
      Message.error("更新授权时间失败");
    }finally {
      setVisible(false);
    }
    }

    const displayData = useMemo(()=>{
        if (!searchValue.trim()) return tableData
        const lowerSearch = searchValue.toLowerCase();
        return tableData.filter(item => 
            item.applicationName?.toLowerCase().includes(lowerSearch)
        )
    },[tableData, searchValue])

    // steps切换
    const handleNext = async () => {
        if(currentStep === 3) {
            const basicValues = await basicInfoForm.validate();
            const adminValues = await adminInfoForm.validate();
            console.log(basicValues, adminValues, "11");
        }else {
            setCurrentStep(currentStep + 1);
        }
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
                        <BasicInformation industryOptions={industryOptions}/>
                    )}
                    {/* 第二步：管理员信息 */}
                    {currentStep === 2 && (
                        <AdminInformation />
                    )}
                    {/* 第三步： 授权应用 */}
                    {currentStep === 3 && (
                        <AuthorizedApp 
                            visible={visible}
                            setVisible={setVisible}
                            pageination={pageInation} 
                            loading={loading} 
                            tableData={displayData} 
                            className ={styles.tabPanel} 
                            onSearch={handleSearchChange}
                            onChange={handlePageChange}
                            onUpdateTime={handleUpdateTime}
                            onRemoveAuthorizedApp={handleRemoveAuthorizedApp}
                        />
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