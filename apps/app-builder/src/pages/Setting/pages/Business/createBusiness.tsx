import { Button, Message, Space, Steps } from '@arco-design/web-react';
import { IconLoading } from '@arco-design/web-react/icon';
import {
  checkCorpAdminUserApi,
  checkCorpApi,
  createCorpApi,
  getCorpAuthorizedAppListApi,
  type checkCorpAdminUserParams,
  type checkCorpParams,
  type corpAppListParams,
  type CorpAppParams,
  type createCorpParams,
} from '@onebase/platform-center';
import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useOutletContext } from 'react-router-dom';
import { AdminInformation } from './components/createApp/adminInfomation';
import { AuthorizedApp } from './components/createApp/authorizedApp';
import { BasicInformation } from './components/createApp/basicInformation';
import { CreateSuccess } from './components/createApp/createSuccess';
import { steps } from './constants';
import styles from './createBusiness.module.less';
import type { AppItem, OutletContextType, successData, updatedParams } from './types/appItem';

const CreateBusinessPage: React.FC = () => {
  const [basicValues, setBasicData] = useState<Record<string, any>>({});
  const [adminValues, setAdminData] = useState<Record<string, any>>({});
  const { industryOptions, currentId } = useOutletContext<OutletContextType>();
  const [currentStep, setCurrentStep] = useState<number>(1);
  const navigate = useNavigate();
  const [loading, setLoading] = useState<boolean>(false);
  const [tableData, setTableData] = useState<AppItem[]>([]);
  const [visible, setVisible] = useState<boolean>(false);
  const [addAppModalVisible, setAddAppModalVisible] = useState<boolean>(false);
  const [searchValue, setSearchValue] = useState<string>('');
  const [createLoading, setCreateLoading] = useState<boolean>(false);
  const [pageInation, setPagination] = useState({
    showTotal: true,
    total: 0,
    pageSize: 10,
    current: 1,
    sizeCanChange: true,
    pageSizeChangeResetCurrent: true
  });
  const [successData, setSuccessData] = useState<successData | null>(null);

  const fetchCorpAuthorizedList = async (pageNo = 1, pageSize = 10) => {
    setLoading(true);
    const params: corpAppListParams = {
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
    } catch (error) {
      Message.error('获取企业授权应用列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (currentStep === 3) {
      // fetchCorpAuthorizedList()
    }
  }, [currentStep]);

  //授权应用分页切换
  const handlePageChange = (current: number, pageSize: number) => {
    fetchCorpAuthorizedList(current, pageSize);
  };

  const handleRemoveAuthorizedApp = async (id: string) => {
    const newData = tableData.filter((item) => item.id !== id);
    setTableData(newData);
  };

  const handleSearchChange = (searchValue: string) => {
    setSearchValue(searchValue);
  };

  const handleUpdateTime = async (params: updatedParams) => {
    const newData = tableData.map((item) => {
      if (item.id === params.id) {
        return {
          ...item,
          expiresTime: params.expiresTime,
          authorizationTime: params.authorizationTime
        };
      }
      return item;
    });
    setTableData(newData);
    setVisible(false);
    // try {
    // const res =  await updateCorpAppApi(params);
    // if(res) {
    //     await fetchCorpAuthorizedList(pageInation.current,pageInation.pageSize);
    //     Message.success("更新授权时间成功");
    // }else {
    // Message.success("接口返回数据异常");
    // }
    // }catch(error) {
    // Message.error("更新授权时间失败");
    // }finally {
    // setVisible(false);
    // }
  };

  const displayData = useMemo(() => {
    if (!searchValue.trim()) return tableData;
    const lowerSearch = searchValue.toLowerCase();
    return tableData.filter((item) => item.applicationName?.toLowerCase().includes(lowerSearch));
  }, [tableData, searchValue]);

  //点击创建应用的上一步button
  const handlePrev = () => {
    if (currentStep > 1) {
      setCurrentStep(currentStep - 1);
    } else {
      navigate('..');
    }
  };

  const handleBasicDataChange = (values: Record<string, any>) => {
    const newStatus = values?.status === true ? 1 : 0;
    let newValues = {
      ...values,
      status: newStatus
    };
    setBasicData(newValues);
  };

  const handleAdminDataChange = (values: Record<string, any>) => {
    setAdminData(values);
  };

  const handleNext = async () => {
    if (currentStep === 3) {
      setCreateLoading(true);
      const newTableData = tableData.map((item) => {
        return {
          ...item,
          authorizationTime: new Date(item.authorizationTime).getTime(),
          expiresTime: new Date(item.expiresTime).getTime()
        };
      });
      const params: createCorpParams = {
        appAuthTimeReqVO: newTableData,
        corpAdminReqVO: adminValues,
        corpReqVO: basicValues
      };
      try {
        const res = await createCorpApi(params);
        if (res) {
          setSuccessData(res);
          setCurrentStep(4);
          setBasicData({});
          setAdminData({});
          Message.success('创建企业成功');
        } else {
          Message.success('接口返回数据异常');
        }
        setCreateLoading(false);
      } catch (error) {
        Message.error('创建企业失败');
      } finally {
        setCreateLoading(false);
      }
    } else {
      if (currentStep === 1) {
        const params: checkCorpParams = {
          corpName: basicValues.corpName,
          corpCode: basicValues.corpCode,
          industryType: basicValues.industryType,
          status: basicValues.status,
          address: basicValues.address,
          userLimit: basicValues.userLimit
        };
        const res = await checkCorpApi(params);
        if (res === true) {
          setCurrentStep(currentStep + 1);
        }
      } else if (currentStep === 2) {
        const params: checkCorpAdminUserParams = {
          username: adminValues.username,
          email: adminValues.email,
          mobile: adminValues.mobile,
          nickname: adminValues.nickname
        };
        const res = await checkCorpAdminUserApi(params);
        if (res === true) {
          setCurrentStep(currentStep + 1);
        }
      } else {
        setCurrentStep(currentStep + 1);
      }
    }
  };

  const handleSubmitApp = (data: CorpAppParams) => {
    const newData = data.applicationIdList
      ?.map((item: any) => {
        return {
          ...item,
          authorizationTime: data.authorizationTime,
          expiresTime: data.expiresTime
        };
      })
      .filter(Boolean);
    setTableData((prev: any) => [...prev, ...newData]);
    setAddAppModalVisible(false);
  };

  const renderContent = (currentStep: number) => {
    return (
      <div className={currentStep === 3 ? '' : styles.content}>
        <div>
          {/* 第一步：基本信息 */}
          {currentStep === 1 && (
            <BasicInformation
              industryOptions={industryOptions}
              basicValues={basicValues}
              onDataChange={handleBasicDataChange}
            />
          )}
          {/* 第二步：管理员信息 */}
          {currentStep === 2 && <AdminInformation adminValues={adminValues} onDataChange={handleAdminDataChange} />}
          {/* 第三步： 授权应用 */}
          {currentStep === 3 && (
            <AuthorizedApp
              visible={visible}
              setVisible={setVisible}
              addAppModalVisible={addAppModalVisible}
              pageination={pageInation}
              loading={loading}
              tableData={displayData}
              className={styles.tabPanel}
              onSearch={handleSearchChange}
              onChange={handlePageChange}
              onUpdateTime={handleUpdateTime}
              onRemoveAuthorizedApp={handleRemoveAuthorizedApp}
              setAddAppModalVisible={setAddAppModalVisible}
              onSubmit={handleSubmitApp}
            />
          )}
          {/* 第四步：确认信息 */}
          {currentStep === 4 && (
            <CreateSuccess
              successData={successData}
              setCurrentStep={setCurrentStep}
              setAddAppModalVisible={setAddAppModalVisible}
            />
          )}
        </div>
      </div>
    );
  };

  return (
    <div className={styles.createBusinessConatiner}>
      {/* 导航条 */}
      <Steps current={currentStep}>
        {steps.map((step, index) => (
          <Steps.Step key={index} title={step.title} />
        ))}
      </Steps>
      {/* 内容展示区域 */}
      <div className={styles.BusinessInformation}>
        {/* 内容区域 */}
        {renderContent(currentStep)}
        {/* 底部操作按钮 */}
        {currentStep !== 4 && (
          <div className={styles.footerButton}>
            <Space size={16}>
              <Button onClick={handlePrev}>{currentStep === 1 ? '返回' : '上一步'}</Button>
              <Button type="primary" onClick={handleNext} disabled={currentStep === steps.length}>
                {currentStep === 3 && createLoading ? <IconLoading /> : '下一步'}
              </Button>
            </Space>
          </div>
        )}
      </div>
    </div>
  );
};

export default CreateBusinessPage;
