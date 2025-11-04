import React, { useEffect, useRef, useState } from 'react';
import { Tabs, Button, Card, Input, Descriptions, Checkbox, Select, Upload, Image, Space, Message } from '@arco-design/web-react';
import { IconEdit } from '@arco-design/web-react/icon';
import EditableFormItem from '../formItem';
import styles from "./index.module.less";
import { AuthorizedApp } from '../createApp/authorizedApp';
import { useOutletContext, useParams } from 'react-router-dom';
import EditAuthorizedTime from '../modal/editAuthorizedTime';
import { CreateAppModal } from '../modal/createAppModal';
import type { AppItem, AuthorizedAppRef, cropItem, OutletContextType } from '../../types/appItem';
import { getDetailsApi, updateCorpApi } from "@onebase/platform-center";

const EnterpriseInfoPage: React.FC = () => {
  const {activeTab} = useParams();
  const { corpId } = useOutletContext<OutletContextType>();
  const authorizedAppRef = useRef<AuthorizedAppRef>(null);
  const [visible, setVisible] = useState<boolean>(false);
  const [currentTab, setCurrentTab] = useState(activeTab ==="授权应用" ? "authorized" : "basic");
  const [isEdited, setIsEdited] = React.useState(false);
  const [addAppModalVisible, setAddAppModalVisible] = useState<boolean>(false);
  const [tableData, setTableData] = useState([]);
  const [corpDetails, setCorpDetails] = useState<cropItem | null>(null);
  const [formData, setFormData] = useState<cropItem | null>(null);

  const fetchCorpDetail = async() => {
    try {
      const res = await getDetailsApi({corpId});
      setCorpDetails(res && res || null);
    }catch(error) {
      Message.error("获取详情失败");
    }
  }

  useEffect(()=>{
    fetchCorpDetail();
  },[])


  // 处理表单数据变更
  const handleChange = (field: string, value: number | string) => {
    setFormData((prev: any) => ({ ...prev, [field]: value }));
  };

  // 切换编辑状态
  const toggleEdit = () => {
    if (isEdited) {
      // 这里可以添加保存逻辑
    }
    setIsEdited(!isEdited);
  };

  const handleSubmitInfo = async() => {
    try {
      await updateCorpApi(formData);
      Message.success("保存成功");
    }catch(error) {
      Message.error("保存失败");
    }
  }

  //点击modal的取消按钮
  const handleCloseModal = () => {
      setAddAppModalVisible(false);
  }


  const handleEdit = () => {
    setVisible(true);
  }

  // 提交新应用（弹窗确认后调用）
  const handleAddSubmit = (newAppData: any) => {
      const newData: AppItem = {
          key: tableData.length + 1,
          appId: "113",
          effectTime: newAppData.appTime.effectTime,
          expireTime: newAppData.appTime.expireTime,
          appName: newAppData.appName[0],
          version: "V2.3"
      };
      authorizedAppRef.current?.addNewApp(newData);
      setAddAppModalVisible(false);
  };

  const handleUpdateTime = () => {

  }

  const data = [
    {
      label:"企业Logo", 
      value: <EditableFormItem
          value = {
          <Image
            width={200}
            src='http://localhost:4399/src/assets/images/ob_logo.svg'
            alt='lamp'
          />}
          onChange={handleChange.bind(null, "logo")}
          isEdit={isEdited}
          component={Upload}
          componentProps={{ listType:'picture-list', action: '/' }}
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
          value = {formData?.corpId}
          onChange={handleChange.bind(null, "corpId")}
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
          componentProps={{
            placeholder: '请选择行业',
            options: [
              { label: 'IT', value: 'IT' },
              { label: '金融', value: 'finance' },
              { label: '教育', value: 'education' }
            ]
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
          componentProps={{ placeholder: '请选择是否启用' }}
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
            <Descriptions data={data} column={1} border={false} className={styles.infoPreview}/>
            {/* 编辑按钮 */}
            {isEdited ? <Space>
              <Button 
                icon={<IconEdit />}
                onClick={toggleEdit}
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
            <AuthorizedApp ref={authorizedAppRef} className ={styles.tabPanel} onEdit={handleEdit} setAddAppModalVisible={setAddAppModalVisible}/>
          </Tabs.TabPane>
        </Tabs>
      </Card>
      <EditAuthorizedTime visible={visible} setVisible={setVisible} onUpdateData={handleUpdateTime} />
       {/* 创建应用modal */}
      <CreateAppModal visible={addAppModalVisible} onCloseAppModal={handleCloseModal} onSaveAppData={handleAddSubmit} />
    </div>
  );
};

export default EnterpriseInfoPage;


