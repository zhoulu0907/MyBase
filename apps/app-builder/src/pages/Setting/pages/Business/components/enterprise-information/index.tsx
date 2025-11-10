import React, { useEffect, useMemo, useState } from 'react';
import { Tabs, Button, Card, Input, Descriptions, Checkbox, Select, Upload, Space, Message, Typography } from '@arco-design/web-react';
import { IconEdit } from '@arco-design/web-react/icon';
import EditableFormItem from '../formItem';
import styles from "./index.module.less";
import { AuthorizedApp } from '../createApp/authorizedApp';
import { useOutletContext, useParams } from 'react-router-dom';
import type { AppItem, cropItem, industryTypeOption, OutletContextType, updatedParams } from '../../types/appItem';
import { getDetailsApi, updateCorpApi, getCorpAuthorizedAppListApi,createCorpAppApi, removeCorpAppApi, updateCorpAppApi,type CorpAppParams, type corpListParams } from "@onebase/platform-center";

const EnterpriseInfoPage: React.FC = () => {
  const {activeTab} = useParams();
  const { currentId, industryOptions} = useOutletContext<OutletContextType>();
  const [addAppModalVisible, setAddAppModalVisible] = useState<boolean>(false);
  const [visible, setVisible] = useState<boolean>(false);
  const [currentTab, setCurrentTab] = useState(activeTab ==="授权应用" ? "authorized" : "basic");
  const [isEdited, setIsEdited] = React.useState(false);
  const [tableData, setTableData] = useState<AppItem[]>([]);
  const [formData, setFormData] = useState<cropItem | null>(null);
  const [originalInfo, setOriginalInfo] = useState<cropItem | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [searchValue, setSearchValue] = useState<string>("");
  const [pageInation, setPagination] = useState({
        showTotal: true,
        total: 0,
        pageSize: 10,
        current: 1,
        sizeCanChange: true,
        pageSizeChangeResetCurrent: true
    });

  const fetchCorpDetail = async() => {
    try {
      const res = await getDetailsApi(currentId);
      setFormData(res && res || null);
      setOriginalInfo(res && res || null);
    }catch(error) {
      Message.error("获取详情失败");
    }
  }

  useEffect(()=>{
    fetchCorpDetail();
  },[])

  const fetchCorpAuthorizedList = async(pageNo = 1, pageSize = 10) => {
    setLoading(true);
    const params: corpListParams = {
        pageNo,
        pageSize,
        corpId
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
    fetchCorpAuthorizedList()
  },[])

  //授权应用分页切换
  const handlePageChange = (current: number, pageSize: number) => {
    fetchCorpAuthorizedList(current, pageSize);
  };

  const handleChange = (field: string, value: number | string | boolean) => {
    let newValue = value;
    if(field === "status") {
      newValue = newValue === true ? 1 : 0;
    }
    setFormData((prev: any) => ({ ...prev, [field]: newValue }));
  };

  const toggleEdit = () => {
    setIsEdited(!isEdited);
  };

  const handleCancel = () => {
    setFormData(originalInfo);
    setIsEdited(false);
  }

  const handleSubmitInfo = async() => {
    try {
      await updateCorpApi({...formData, id: currentId});
      Message.success("企业基本信息保存成功");
    }catch(error) {
      Message.error("企业基本信息保存失败");
    }finally {
      setIsEdited(false);
    }
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

  const handleSubmitApp = async(data:CorpAppParams) => {
    const IdList = data.applicationIdList?.map((item: any) => item.id).filter(Boolean);
    data.applicationIdList = IdList;
    try {
        const res = await createCorpAppApi(data);
        if(res) {
          await fetchCorpAuthorizedList(pageInation.current,pageInation.pageSize)
          Message.success("创建授权应用成功");
        }else {
          Message.error("接口返回异常");
        }
    }catch(error) {
        Message.error("创建授权应用失败");
    }finally {
        setAddAppModalVisible(false);
    }
  }

  const displayData = useMemo(()=>{
      if (!searchValue.trim()) return tableData
      const lowerSearch = searchValue.toLowerCase();
      return tableData.filter(item => 
          item.applicationName?.toLowerCase().includes(lowerSearch)
      )
  },[tableData, searchValue])

  const data = [
    {
      label:"企业Logo", 
      value: <EditableFormItem
          value = {
            <Button type='dashed' style={{width:"160px", height:"80px", backgroundColor:"#F2F3F5"}}>中国移动</Button>
          }
          label="logo"
          onChange={handleChange.bind(null, "corpLogo")}
          isEdit={isEdited}
          component={Upload}
          componentProps={{listType: 'picture-list',multiple: false, headers: {authorization: 'authorization-text'}}}
          logoContent={<Space style={{display:"flex", alignItems:"flex-end"}}><Button type='primary'>重新上传</Button><Typography.Text type='secondary'>建议比例2:1</Typography.Text></Space>}
      />
    },
    {
      label:"企业名称", 
      value: <EditableFormItem
          value = {formData?.corpName}
          onChange={handleChange.bind(null, "corpName")}
          isEdit={isEdited}
          component={Input}
          componentProps={{ placeholder: '请输入企业名称' }}
      />
    },
    {
      label:"企业ID", 
      value: <EditableFormItem
          value = {formData?.corpCode}
          onChange={handleChange.bind(null, "corpCode")}
          isEdit={isEdited}
          component={Input}
          componentProps={{ placeholder: '请输入企业ID' }}
      />
    },
     {
      label:"行业类型", 
      value: <EditableFormItem
          value = {formData?.industryType}
          onChange={handleChange.bind(null, "industryType")}
          isEdit={isEdited}
          component={Select}
          label="industryType"
          componentProps={{
            placeholder: '请选择行业',
            options: industryOptions.map((option: industryTypeOption) => ({ label: option.label, value: option.id }) as industryTypeOption),
          }}
      />
    },
     {
      label:"企业地址", 
      value: <EditableFormItem
          value = {formData?.address}
          onChange={handleChange.bind(null, "address")}
          isEdit={isEdited}
          component={Input.TextArea}
          componentProps={{ placeholder: '请输入企业地址' }}
      />
    },
    {
      label:"用户上限", 
      value: <EditableFormItem
          value = {formData?.userLimit}
          onChange={handleChange.bind(null, "userLimit")}
          isEdit={isEdited}
          component={Input}
          componentProps={{ placeholder: '请输入用户上限' }}
      />
    },
    {
      label:"状态", 
      value: <EditableFormItem
          value = {formData?.status}
          onChange={handleChange.bind(null, "status")}
          isEdit={isEdited}
          component={Checkbox}
          componentProps={{ placeholder: '请选择是否启用', checked: formData?.status === 0 ? false : true }}
      />
    }
  ]

  return (
    <div className={styles.enterpriseWrapper}>
      {/* 主内容卡片 */}
      <Card bordered={false}>
        {/* 标签页组件 */}
        <Tabs activeTab={currentTab} onChange={setCurrentTab}>
          <Tabs.TabPane key="basic" title="基本信息">
            {/* 企业Logo展示 */}
            <Descriptions size='large' data={data} column={1} border={false} className={styles.infoPreview}/>
            {/* 编辑按钮 */}
            {isEdited ? <Space>
              <Button 
                icon={<IconEdit />}
                onClick={handleCancel}
              >
               取消
              </Button>
              <Button 
                type="primary" 
                icon={<IconEdit />}
                onClick={handleSubmitInfo}
              >
               保存修改
              </Button>
            </Space> : <Button 
                icon={<IconEdit />}
                onClick={toggleEdit}
              >
                编辑
              </Button>}
          </Tabs.TabPane>
          <Tabs.TabPane key="authorized" title="授权应用">
            <AuthorizedApp 
                visible={visible}
                setVisible={setVisible}
                addAppModalVisible={addAppModalVisible}
                pageination={pageInation} 
                loading={loading} 
                tableData={displayData} 
                className ={styles.tabPanel} 
                onSearch={handleSearchChange}
                onChange={handlePageChange}
                onUpdateTime={handleUpdateTime}
                onRemoveAuthorizedApp={handleRemoveAuthorizedApp}
                onSubmit={handleSubmitApp}
                setAddAppModalVisible={setAddAppModalVisible}
              />
          </Tabs.TabPane>
        </Tabs>
      </Card>
    </div>
  );
};

export default EnterpriseInfoPage;


