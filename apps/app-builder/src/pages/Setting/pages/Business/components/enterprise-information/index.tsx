import { displayCorpLogo } from '@/utils';
import { phoneValidator } from '@/utils/validator';
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
  Upload,
  Form,
  Avatar,
  Modal,
  Divider
} from '@arco-design/web-react';
import {
  createCorpAppApi,
  getCorpAuthorizedAppListApi,
  getCorpDetailByIdApi,
  getFileUrlById,
  removeCorpAppApi,
  updateCorpApi,
  updateCorpAppApi,
  uploadFile,
  StatusEnum,
  type CorpAppParams,
  type corpListParams,
  type CorpDetailResponse
} from '@onebase/platform-center';
import React, { useEffect, useMemo, useRef, useState } from 'react';
import { useOutletContext, useParams } from 'react-router-dom';
import type { AppItem, cropItem, industryTypeOption, OutletContextType, updatedParams } from '../../types/appItem';
import { AuthorizedApp } from '../createApp/authorizedApp';
import { Cropper } from '@onebase/common';
import { IconUpload } from '@arco-design/web-react/icon';
import BackPrevPage from '@/pages/Setting/components/goPrevPage';
import styles from './index.module.less';

enum StatusLabelEnum {
  ENABLE = '启用',
  DISABLE = '禁用',
  EXPIRED = '过期'
}

const EnterpriseInfoPage: React.FC = () => {
  const { activeTab } = useParams();
  const { currentId, industryOptions, editable } = useOutletContext<OutletContextType>();
  const [addAppModalVisible, setAddAppModalVisible] = useState<boolean>(false);
  const [form] = Form.useForm();
  const [isImageFailed, setIsImageFailed] = useState<boolean>(false);
  const [avatarUrl, setAvatarUrl] = useState<string>('');
  const [visible, setVisible] = useState<boolean>(false);
  const [currentTab, setCurrentTab] = useState(activeTab === '授权应用' ? 'authorized' : 'basic');
  const [isEdited, setIsEdited] = useState<boolean>(false);
  const [tableData, setTableData] = useState<AppItem[]>([]);
  const [formData, setFormData] = useState<cropItem | CorpDetailResponse | null>(null);
  const [originalInfo, setOriginalInfo] = useState<cropItem | CorpDetailResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [submitLoading, setSubmitLoading] = useState<boolean>(false);
  const [searchValue, setSearchValue] = useState<string>('');
  const [pageInation, setPageInation] = useState({
    showTotal: true,
    total: 0,
    pageSize: 10,
    current: 1,
    sizeCanChange: true,
    pageSizeChangeResetCurrent: true
  });
  const [corpId, setCorpId] = useState<string>('');

  const uploadRef = useRef<any>(null);

  const fetchCorpDetail = async () => {
    try {
      const res = await getCorpDetailByIdApi(currentId);
      setFormData(res || null);
      setOriginalInfo(res || null);
      setIsEdited(editable || false);
      if (editable) {
        form.setFieldsValue({
          ...res
        });
      }
      if (res.corpLogo) {
        setIsImageFailed(false);
        setAvatarUrl(res.corpLogo);
      }
      if (res.id) {
        setCorpId(res.id);
      }
    } catch (error) {
      Message.error('获取详情失败');
      console.log(error);
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
        setPageInation((prev) => ({ ...prev, current: pageNo, pageSize, total: res.total || 0 }));
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

  const toggleEdit = () => {
    setIsEdited(!isEdited);
    form.setFieldsValue({
      ...formData
    });
  };

  const handleCancel = () => {
    setFormData(originalInfo);
    setIsEdited(false);
  };

  const handleSubmitInfo = () => {
    form.validate().then(async () => {
      try {
        setSubmitLoading(true);
        const param: CorpDetailResponse = form.getFieldsValue();
        console.log('formparams', param, avatarUrl);
        const res = await updateCorpApi({
          ...param,
          corpCode: param?.corpCode || '',
          corpName: param?.corpName || '',
          industryType: param?.industryType || '',
          address: param?.address || '',
          status: param?.status || 0,
          corpLogo: avatarUrl,
          userLimit: param?.userLimit || 0,
          id: currentId
        });
        if (res) {
          Message.success('企业基本信息保存成功');
          setFormData(param);
          setIsEdited(false);
        }
        setSubmitLoading(false);
      } catch (error) {
        console.log(error);
        setSubmitLoading(false);
      }
    });
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
      console.log(error);
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
      console.log(error);
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
      console.log(error);
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
    const fileFormData = new FormData();
    fileFormData.append('file', file);

    const progressAdapter = onProgress
      ? (progressEvent: ProgressEvent) => {
        if (progressEvent.lengthComputable) {
          const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          onProgress(percent, progressEvent);
        }
      }
      : undefined;

    const res = await uploadFile(fileFormData, progressAdapter);
    return res;
  };

  const handleImageError = () => {
    setIsImageFailed(true);
  };

  const getStatus = (status?: StatusEnum): string => {
    // 启用1 ENABLE，禁用0 DISABLE, 过期2 EXPIRED,
    if (status === StatusEnum.ENABLE) {
      return StatusLabelEnum.ENABLE;
    } else if (status === StatusEnum.DISABLE) {
      return StatusLabelEnum.DISABLE;
    } else if (status === StatusEnum.EXPIRED) {
      return StatusLabelEnum.EXPIRED;
    }
    return '--';
  };

  const data = [
    {
      label: '企业Logo',
      value: isImageFailed ? (
        <Button type="dashed" style={{ width: '160px', height: '80px', backgroundColor: '#F2F3F5' }}>
          {displayCorpLogo(formData?.corpName)}
        </Button>
      ) : (
        <Image className={styles.corpLogo} alt="头像" src={getFileUrlById(avatarUrl)} onError={handleImageError} width={160} height={80} />
      )
    },
    {
      label: '企业名称',
      value: formData?.corpName || '--'
    },
    {
      label: '企业管理员',
      value: formData?.adminName || '--'
    },
    {
      label: '手机号',
      value: formData?.adminMobile || '--'
    },
    {
      label: '企业编码',
      value: formData?.corpCode || '--'
    },
    {
      label: '行业类型',
      value: formData?.industryType
        ? industryOptions.find((option: industryTypeOption) => formData.industryType === option.id)?.label
        : '--'
    },
    {
      label: '企业地址',
      value: formData?.address || '--'
    },
    {
      label: '用户上限',
      value: formData?.userLimit || '--'
    },
    {
      label: '状态',
      // 启用1 ENABLE，禁用0 DISABLE, 过期2 EXPIRED,
      value: getStatus(formData?.status)
    }
  ];

  const renderForm = () => {
    return (
      <Form
        form={form}
        layout="horizontal"
        labelAlign="left"
        labelCol={{ span: 2, style: { width: '90px', flex: '0 0 auto' } }}
        wrapperCol={{ style: { flex: '1' } }}
        style={{ marginLeft: 20, maxWidth: 600 }}
      >
        <Form.Item label="企业Logo" field="corpLogo" triggerPropName="fileList">
          <Space direction="vertical">
            <Upload
              ref={uploadRef}
              limit={1}
              imagePreview
              accept="image/*"
              listType="picture-card"
              customRequest={async (option) => {
                const { onProgress, onError, onSuccess, file } = option;
                try {
                  const uploadImgUrl = await handleUpload(file, onProgress);
                  if (uploadImgUrl) {
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
              }}
              beforeUpload={(file) => {
                return new Promise((resolve) => {
                  const modal = Modal.confirm({
                    title: '裁剪图片',
                    onCancel: () => {
                      Message.info('取消上传');
                      resolve(false);
                      modal.close();
                    },
                    simple: false,
                    content: (
                      <Cropper
                        file={file}
                        aspect={2 / 1}
                        onOK={(file: any) => {
                          resolve(file);
                          modal.close();
                        }}
                        onCancel={() => {
                          resolve(false);
                          Message.info('取消上传');
                          modal.close();
                        }}
                      />
                    ),
                    footer: null
                  });
                });
              }}
            >
              {avatarUrl ? (
                <div onClick={(e) => e.stopPropagation()}>
                  <Image
                    className={styles.corpLogo}
                    preview
                    width={160}
                    height={80}
                    src={getFileUrlById(avatarUrl)}
                  />
                </div>
              ) : (
                <Avatar
                  shape="square"
                  style={{ width: 160, height: 80, backgroundColor: '#F7F8FA', borderRadius: 12 }}
                >
                  <span className={styles.avatarText}>{originalInfo?.corpName?.slice(0, 6)}</span>
                </Avatar>
              )}
            </Upload>
            <Space>
              <Button
                type="outline"
                icon={<IconUpload />}
                onClick={() => {
                  uploadRef.current?.getRootDOMNode()?.querySelector('input[type="file"]').click();
                }}
              >
                上传图片
              </Button>
              <div style={{ color: '#999', marginTop: 4 }}>建议比例 2:1</div>
            </Space>
          </Space>
        </Form.Item>

        <Form.Item label="企业名称" field="corpName" rules={[{ required: true, message: '请输入企业名称' }]}>
          <Input placeholder="输入企业名称" maxLength={50} />
        </Form.Item>

        <Form.Item label="企业管理员" field="adminName">
          <Input readOnly disabled />
        </Form.Item>

        <Form.Item
          label="手机号"
          field="adminMobile"
          rules={[{ required: true, message: '请输入手机号' }, { validator: phoneValidator }]}
        >
          <Input placeholder="输入手机号" maxLength={11} disabled />
        </Form.Item>

        <Form.Item label="企业编码" field="corpCode" rules={[{ required: true, message: '请输入企业编码' }]}>
          <Input placeholder="输入企业编码" />
        </Form.Item>

        <Form.Item label="行业类型" field="industryType" rules={[{ required: true, message: '请选择行业类型' }]}>
          <Select
            options={industryOptions.map((option: industryTypeOption) => ({ label: option.label, value: option.id }))}
            placeholder="行业类型"
          />
        </Form.Item>

        <Form.Item label="企业地址" field="address">
          <Input.TextArea placeholder="请输入详细地址" autoSize={{ minRows: 2, maxRows: 6 }} />
        </Form.Item>

        <Form.Item label="用户上限" field="userLimit" rules={[{ required: true }]}>
          <InputNumber placeholder="请输入用户上限" max={5000} min={0} />
        </Form.Item>

        <Form.Item label="状态" field="status" triggerPropName="checked" rules={[{ required: true }]}>
          <Checkbox>启用</Checkbox>
        </Form.Item>
      </Form>
    );
  };

  return (
    <div className={styles.enterpriseWrapper}>
      <div className={styles.enterpriseHeader}>
        <BackPrevPage title={formData?.corpName || ''} />
      </div>
      <Divider style={{ margin: 0 }} />
      {/* 主内容卡片 */}
      <Card bordered={false}>
        {/* 标签页组件 */}
        <Tabs activeTab={currentTab} onChange={setCurrentTab}>
          <Tabs.TabPane key="basic" title="基本信息">
            {isEdited ? (
              <>
                {renderForm()}
                <Button type="primary" onClick={handleSubmitInfo} loading={submitLoading} style={{ marginLeft: 110 }}>
                  保存修改
                </Button>
                <Button onClick={handleCancel}>取消</Button>
              </>
            ) : (
              <>
                {/* 企业信息展示 */}
                <Descriptions size="large" data={data} column={1} border={false} className={styles.infoPreview} />
                <Button onClick={toggleEdit} style={{ marginLeft: 110 }}>编辑</Button>
              </>
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
