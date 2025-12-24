import {
  Button,
  Card,
  Checkbox,
  Descriptions,
  Image,
  Input,
  InputNumber,
  Message,
  Select,
  Space,
  Tabs,
  Typography,
  Upload
} from '@arco-design/web-react';
import {
  createCorpAppApi,
  getCorpAuthorizedAppListApi,
  getCorpDetailByIdApi,
  removeCorpAppApi,
  updateCorpApi,
  updateCorpAppApi,
  uploadFile,
  type CorpAppParams,
  type corpListParams
} from '@onebase/platform-center';
import React, { useEffect, useMemo, useRef, useState } from 'react';
import { useOutletContext, useParams } from 'react-router-dom';
import { allowedFormats } from '../../constants';
import type { AppItem, cropItem, industryTypeOption, OutletContextType, updatedParams } from '../../types/appItem';
import { AuthorizedApp } from '../createApp/authorizedApp';
import EditableFormItem from '../formItem';
import styles from './index.module.less';
import { displayCorpLogo } from '@/utils';

const EnterpriseInfoPage: React.FC = () => {
  const { activeTab } = useParams();
  const { currentId, industryOptions, editable } = useOutletContext<OutletContextType>();
  const [addAppModalVisible, setAddAppModalVisible] = useState<boolean>(false);
  const uploadRef = useRef(null);
  const [isImageFailed, setIsImageFailed] = useState<boolean>(false);
  const [avatarUrl, setAvatarUrl] = useState<string>('');
  const [visible, setVisible] = useState<boolean>(false);
  const [currentTab, setCurrentTab] = useState(activeTab === '授权应用' ? 'authorized' : 'basic');
  const [isEdited, setIsEdited] = useState<boolean>(editable ? editable : false);
  const [tableData, setTableData] = useState<AppItem[]>([]);
  const [formData, setFormData] = useState<cropItem | null>(null);
  const [originalInfo, setOriginalInfo] = useState<cropItem | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [searchValue, setSearchValue] = useState<string>('');
  const [pageInation, setPagination] = useState({
    showTotal: true,
    total: 0,
    pageSize: 10,
    current: 1,
    sizeCanChange: true,
    pageSizeChangeResetCurrent: true
  });
  const [corpId, setCorpId] = useState<string>('');

  const fetchCorpDetail = async () => {
    try {
      const res = await getCorpDetailByIdApi(currentId);
      setFormData((res && res) || null);
      setOriginalInfo((res && res) || null);
      if (res.corpLogo) {
        setIsImageFailed(false);
        setAvatarUrl(res.corpLogo);
      }
      if (res.id) {
        setCorpId(res.id);
      }
    } catch (error) {
      Message.error('获取详情失败');
    }
  };

  useEffect(() => {
    fetchCorpDetail();
  }, []);

  useEffect(() => {
    if (corpId !== '') {
      fetchCorpAuthorizedList();
    }
  }, [corpId]);

  const fetchCorpAuthorizedList = async (pageNo = 1, pageSize = 10) => {
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
    } catch (error) {
      console.log(error);
    } finally {
      setLoading(false);
    }
  };

  //授权应用分页切换
  const handlePageChange = (current: number, pageSize: number) => {
    fetchCorpAuthorizedList(current, pageSize);
  };

  const handleChange = (field: string, value: number | string | boolean) => {
    let newValue = value;
    if (field === 'status') {
      newValue = newValue === true ? 1 : 0;
    } else if (field === 'corpLogo') {
      newValue = (value as any)?.[0]?.response || '';
    }
    setFormData((prev: any) => ({ ...prev, [field]: newValue }));
  };

  const toggleEdit = () => {
    setIsEdited(!isEdited);
  };

  const handleCancel = () => {
    setFormData(originalInfo);
    setIsEdited(false);
  };

  const handleSubmitInfo = async () => {
    try {
      await updateCorpApi({ ...formData, id: currentId });
      Message.success('企业基本信息保存成功');
    } catch (error) {
      Message.error('企业基本信息保存失败');
    } finally {
      setIsEdited(false);
    }
  };

  const handleUpdateTime = async (params: updatedParams) => {
    const newParams = {
      ...params,
      authorizationTime: new Date(params.authorizationTime).getTime(),
      expiresTime: new Date(params.expiresTime).getTime()
    };
    try {
      const res = await updateCorpAppApi(newParams);
      if (res) {
        await fetchCorpAuthorizedList(pageInation.current, pageInation.pageSize);
        Message.success('更新授权时间成功');
      }
    } catch (error) {
      Message.error('更新授权时间失败');
    } finally {
      setVisible(false);
    }
  };

  const handleRemoveAuthorizedApp = async (id: string) => {
    try {
      const res = await removeCorpAppApi(id);
      if (res) {
        await fetchCorpAuthorizedList(pageInation.current, pageInation.pageSize);
        Message.success('授权应用删除成功');
      } else {
        Message.success('未删除成功');
      }
    } catch (error) {
      Message.error('接口返回异常, 授权应用删除失败');
    }
  };

  const handleSearchChange = (searchValue: string) => {
    setSearchValue(searchValue);
  };

  const handleSubmitApp = async (data: CorpAppParams) => {
    const IdList = data.applicationIdList?.map((item: any) => item.id).filter(Boolean);
    data.applicationIdList = IdList;
    data.authorizationTime = new Date(data.authorizationTime).getTime();
    data.expiresTime = new Date(data.expiresTime).getTime();
    try {
      const res = await createCorpAppApi(data);
      if (res) {
        await fetchCorpAuthorizedList(pageInation.current, pageInation.pageSize);
        Message.success('创建授权应用成功');
      } else {
        Message.error('接口返回异常');
      }
    } catch (error) {
      Message.error('创建授权应用失败');
    } finally {
      setAddAppModalVisible(false);
    }
  };

  const displayData = useMemo(() => {
    if (!searchValue.trim()) return tableData;
    const lowerSearch = searchValue.toLowerCase();
    return tableData.filter((item) => item.applicationName?.toLowerCase().includes(lowerSearch));
  }, [tableData, searchValue]);

  // 头像上传
  const handleUpload = async (file: File, onProgress?: (percent: number, event?: ProgressEvent) => void) => {
    setIsImageFailed(false);
    const formData = new FormData();
    formData.append('file', file);

    const progressAdapter = onProgress
      ? (progressEvent: ProgressEvent) => {
          if (progressEvent.lengthComputable) {
            const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total);
            onProgress(percent, progressEvent);
          }
        }
      : undefined;

    const res = await uploadFile(formData, progressAdapter);
    return res;
  };

  const beforeUpload = async (file: any) => {
    if (!allowedFormats.includes(file.type)) {
      Message.warning(`不支持该格式，仅支持 JPG / JPEG / PNG / GIF`);
      return false;
    }
  };

  const customRequest = async (option: any) => {
    const { onProgress, onError, onSuccess, file } = option;
    try {
      const uploadImgUrl = await handleUpload(file, onProgress);
      if (uploadImgUrl !== '') {
        setIsImageFailed(false);
        setAvatarUrl(uploadImgUrl);
        onSuccess(uploadImgUrl);
      } else {
        onError({
          status: 'error',
          msg: '上传失败'
        });
      }
    } catch (error) {
      onError({
        status: 'error',
        msg: '上传失败'
      });
    }
  };

  const handleImageError = () => {
    setIsImageFailed(true);
  };

  const data = [
    {
      label: '企业Logo',
      value: (
        <EditableFormItem
          value={
            isImageFailed ? (
              <Button type="dashed" style={{ width: '160px', height: '80px', backgroundColor: '#F2F3F5' }}>
                {displayCorpLogo(formData?.corpName)}
              </Button>
            ) : (
              <Image alt="头像" src={avatarUrl} onError={handleImageError} width={160} height={80} />
            )
          }
          label="logo"
          onChange={handleChange.bind(null, 'corpLogo')}
          isEdit={isEdited}
          component={Upload}
          componentProps={{
            ref: uploadRef,
            accept: 'image/*',
            listType: 'picture-card',
            limit: 1,
            showUploadList: false,
            beforeUpload: beforeUpload,
            customRequest: customRequest,
            headers: { authorization: 'authorization-text' }
          }}
          logoContent={
            <Space style={{ display: 'flex', alignItems: 'flex-end' }}>
              <Button type="primary">重新上传</Button>
              <Typography.Text type="secondary">建议比例2:1</Typography.Text>
            </Space>
          }
        />
      )
    },
    {
      label: '企业名称',
      value: (
        <EditableFormItem
          value={formData?.corpName}
          onChange={handleChange.bind(null, 'corpName')}
          isEdit={isEdited}
          component={Input}
          componentProps={{ placeholder: '请输入企业名称' }}
        />
      )
    },
    {
      label: '企业管理员',
      value: (
        <EditableFormItem
          value={formData?.adminName}
          onChange={() => null}
          isEdit={isEdited}
          component={Input}
          componentProps={{ disabled: true }}
        />
      )
    },
    {
      label: '手机号',
      value: (
        <EditableFormItem
          value={formData?.adminMobile}
          onChange={handleChange.bind(null, 'adminMobile')}
          isEdit={isEdited}
          component={Input}
          componentProps={{ placeholder: '请输入手机号' }}
        />
      )
    },
    {
      label: '企业编码',
      value: (
        <EditableFormItem
          value={formData?.corpCode}
          onChange={handleChange.bind(null, 'corpCode')}
          isEdit={isEdited}
          component={Input}
          componentProps={{ placeholder: '请输入企业编码' }}
        />
      )
    },
    {
      label: '行业类型',
      value: (
        <EditableFormItem
          value={formData?.industryType}
          onChange={handleChange.bind(null, 'industryType')}
          isEdit={isEdited}
          component={Select}
          label="industryType"
          componentProps={{
            placeholder: '请选择行业',
            options: industryOptions.map(
              (option: industryTypeOption) => ({ label: option.label, value: option.id }) as industryTypeOption
            )
          }}
        />
      )
    },
    {
      label: '企业地址',
      value: (
        <EditableFormItem
          value={formData?.address}
          onChange={handleChange.bind(null, 'address')}
          isEdit={isEdited}
          component={Input.TextArea}
          componentProps={{ placeholder: '请输入企业地址' }}
        />
      )
    },
    {
      label: '用户上限',
      value: (
        <EditableFormItem
          value={formData?.userLimit}
          onChange={handleChange.bind(null, 'userLimit')}
          isEdit={isEdited}
          component={InputNumber}
          componentProps={{ placeholder: '请输入用户上限', max: 5000, min: 0 }}
        />
      )
    },
    {
      label: '状态',
      value: (
        <EditableFormItem
          value={formData?.status}
          onChange={handleChange.bind(null, 'status')}
          isEdit={isEdited}
          component={Checkbox}
          componentProps={{ placeholder: '请选择是否启用', checked: formData?.status === 0 ? false : true }}
        />
      )
    }
  ];

  return (
    <div className={styles.enterpriseWrapper}>
      {/* 主内容卡片 */}
      <Card bordered={false}>
        {/* 标签页组件 */}
        <Tabs activeTab={currentTab} onChange={setCurrentTab}>
          <Tabs.TabPane key="basic" title="基本信息">
            {/* 企业Logo展示 */}
            <Descriptions size="large" data={data} column={1} border={false} className={styles.infoPreview} />
            {/* 编辑按钮 */}
            {isEdited ? (
              <Space>
                <Button onClick={handleCancel}>取消</Button>
                <Button type="primary" onClick={handleSubmitInfo}>
                  保存修改
                </Button>
              </Space>
            ) : (
              <Button onClick={toggleEdit}>编辑</Button>
            )}
          </Tabs.TabPane>
          <Tabs.TabPane key="authorized" title="授权应用">
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
