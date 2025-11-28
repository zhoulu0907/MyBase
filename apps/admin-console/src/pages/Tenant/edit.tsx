import Cropper from '@/components/Cropper';
import { copyToClipboard, simplifyUrl } from '@/utils/date';
import { getPlatformFeDomain } from '@/utils/domain';
import {
  Avatar,
  Button,
  Checkbox,
  Form,
  Image,
  Input,
  Message,
  Modal,
  Select,
  Space,
  Tabs,
  Tag,
  Tooltip,
  Upload
} from '@arco-design/web-react';
import { IconCopy, IconUpload } from '@arco-design/web-react/icon';
import {
  getPlatformTenantAdminInfoApi,
  getPlatformTenantAdminListApi,
  PlatformTenantPublishMode,
  updatePlatformTenantApi,
  uploadFile,
  type TenantAdminUserResVO,
  type UpdateTenantParams,
  type UserVO
} from '@onebase/platform-center';
import { useEffect, useRef, useState } from 'react';
import { useLocation } from 'react-router-dom';
import styles from './index.module.less';

const EditTenant = () => {
  const [form] = Form.useForm();
  const location = useLocation();

  // 通过 URLSearchParams 获取查询参数
  const searchParams = new URLSearchParams(location.search);
  const id = searchParams.get('id');

  const [isEdit, setIsEdit] = useState(false);
  const [saasChecked, setSaaSChecked] = useState<boolean>(false);
  const [tenantInfo, setTenantInfo] = useState<any>();
  const [adminList, setAdminList] = useState<UserVO[]>([]);
  const [logoUrl, setLogoUrl] = useState<string>();
  const platformFe = getPlatformFeDomain();
  const fullUrl = `${platformFe}/#/tenant/${tenantInfo?.id}/${tenantInfo?.website}/`;
  const displayUrl = simplifyUrl(fullUrl);

  const uploadRef = useRef(null);

  useEffect(() => {
    if (id) {
      getTenantInfo(id);
      getPlatformAdminList();
    }
  }, [id]);

  useEffect(() => {
    if (tenantInfo) {
      console.log('tenantInfo: ', tenantInfo);
      const initialValues = {
        id: tenantInfo.id,
        name: tenantInfo.name,
        website: tenantInfo.website,
        status: tenantInfo.status === 1,
        accountCount: tenantInfo.accountCount,
        tenantAdminUserList: tenantInfo.tenantAdminUserList.map((ten: TenantAdminUserResVO) => ten.platformUserId),
        publishModel: tenantInfo.publishModel === PlatformTenantPublishMode.saas
      };
      setSaaSChecked(tenantInfo.publishModel === PlatformTenantPublishMode.saas ? true : false);
      setLogoUrl(tenantInfo.logoUrl);
      form.setFieldsValue(initialValues);
    }
  }, [tenantInfo]);

  const getTenantInfo = async (id: string) => {
    const res = await getPlatformTenantAdminInfoApi(id);
    setTenantInfo(res);
  };

  // 获取用户列表
  const getPlatformAdminList = async () => {
    try {
      //   const adminListResp = await getSimpleUserList();
      const adminListResp = await getPlatformTenantAdminListApi();
      console.log('adminListResp: ', adminListResp);
      setAdminList(adminListResp);
    } catch (error) {
      console.error('Error fetching adminList:', error);
    }
  };

  /**
   * 更新租户信息
   */
  const handleSave = async () => {
    try {
      const values = await form.validate();
      console.log('提交表单:', values);

      // 构建更新参数
      if (tenantInfo?.id) {
        const formattedAdmin = values.tenantAdminUserList.map((id: string) => {
          const user = adminList.find((u) => u.id === id);
          if (user?.id) {
            return {
              adminNickName: user?.nickname || '',
              adminUserName: user?.username || '',
              adminMobile: user?.mobile || '',
              platformUserId: user.id
            };
          } else {
            const adminUser = tenantInfo?.tenantAdminUserList.find((t: any) => t.platformUserId === id);
            return adminUser || {};
          }
        });
        const updateParams: UpdateTenantParams = {
          id: tenantInfo.id,
          name: values.name,
          tenantCode: values.tenantCode,
          status: values.status ? 1 : 0,
          accountCount: values.accountCount,
          website: values.website,
          publishModel: values.publishModel ? PlatformTenantPublishMode.saas : PlatformTenantPublishMode.inner,
          tenantAdminUserUpdateReqVOSList: formattedAdmin,
          logoUrl
        };
        // 调用 updatePlatformTenantApi
        await updatePlatformTenantApi(updateParams);
        if (id) {
          await getTenantInfo(id);
        }
        Message.success('更新成功');
      } else {
        // 添加错误处理，以防万一id不存在
        Message.error('租户信息不完整，无法更新');
      }

      setIsEdit(false);
    } catch (error) {
      console.error('更新租户信息失败:', error);
    }
  };

  const handleUpload = async (file: File, onProgress?: (percent: number, event?: ProgressEvent) => void) => {
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

  const handleChecked = (value: boolean) => {
    setSaaSChecked(value);
  };

  /* 获取当前管理员集合 */
  const findMatchingItemsById = (arrA: any[], targetArr: any[]) => {
    if (!Array.isArray(targetArr)) return;
    const cutAdminList = targetArr.map((item) => item.platformUserId);
    const result = arrA.filter((item) => cutAdminList.includes(item.id));

    return result;
  };

  return (
    <div className={styles.editPage}>
      <Tabs defaultActiveTab="1" destroyOnHide={false} style={{ width: '100%' }}>
        <Tabs.TabPane key="1" title="基本信息">
          <Form form={form} layout="horizontal" autoComplete="off" labelCol={{ span: 2 }} wrapperCol={{ span: 22 }}>
            <Form.Item label="空间名称" field="name" rules={[{ required: isEdit, message: '请输入空间名称' }]}>
              {isEdit ? <Input placeholder="输入空间名称" /> : <span>{tenantInfo?.name}</span>}
            </Form.Item>

            <Form.Item label="空间 Logo" field="logoUrl" triggerPropName="fileList">
              <Space direction="vertical">
                {isEdit ? (
                  <>
                    <Upload
                      ref={uploadRef}
                      limit={1}
                      imagePreview
                      accept="image/*"
                      listType="picture-card"
                      showUploadList
                      customRequest={async (option) => {
                        const { onProgress, onError, onSuccess, file } = option;
                        try {
                          const uploadImgUrl = await handleUpload(file, onProgress);
                          if (uploadImgUrl !== '') {
                            setLogoUrl(uploadImgUrl);
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
                      style={{
                        display: 'none'
                      }}
                    />
                    {isEdit && (
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
                    )}
                  </>
                ) : (
                  <>
                    {logoUrl ? (
                      <Image className={styles.tenantLogo} preview width={160} height={80} src={logoUrl} />
                    ) : (
                      <div className={styles.tenantLogo}>{tenantInfo?.name.slice(0, 6)}</div>
                    )}
                  </>
                )}
              </Space>
            </Form.Item>

            <Form.Item label="空间ID" field="id">
              {isEdit ? <Input type="number" disabled /> : <span>{tenantInfo?.id}</span>}
            </Form.Item>

            <Form.Item
              label="访问地址"
              field="website"
              rules={[{ required: isEdit, message: '请输入访问地址' }]}
              validateTrigger={['onBlur']}
            >
              {isEdit ? (
                <Input
                  addBefore={<div style={{ width: '250px' }}>{getPlatformFeDomain()}</div>}
                  placeholder="www.onebase.com"
                />
              ) : (
                <div className={styles.urlWrapper}>
                  <Tooltip content={displayUrl}>
                    <span className={styles.url}>{displayUrl}</span>
                  </Tooltip>
                  <IconCopy className={styles.copyIcon} onClick={() => copyToClipboard(fullUrl)} />
                </div>
              )}
            </Form.Item>

            <Form.Item label="用户上限" field="accountCount" rules={[{ required: isEdit, message: '请输入用户上限' }]}>
              {isEdit ? <Input type="number" /> : <span>{tenantInfo?.accountCount}</span>}
            </Form.Item>

            <Form.Item
              label="管理员"
              field="tenantAdminUserList"
              rules={[{ required: isEdit, message: '请选择管理员' }]}
              extra={isEdit && '当前用户将作为空间所有者'}
            >
              {isEdit ? (
                <Select
                  placeholder="选择管理员"
                  mode="multiple"
                  allowClear
                  style={{ width: '100%' }}
                  options={[...(tenantInfo?.tenantAdminUserList || []), ...(adminList || [])].map((u) => ({
                    label: u.nickname || u.username || u.adminNickName || u.adminUserName,
                    value: u.id || u.platformUserId
                  }))}
                  filterOption={(inputValue: any, option: any) => {
                    return option.props.children?.includes(inputValue);
                  }}
                ></Select>
              ) : (
                <div className={styles.tagWrapper}>
                  {tenantInfo?.tenantAdminUserList?.map((tag: any, index: number) => (
                    <Tag className={styles.adminTag} key={index} size="large" style={{ borderRadius: 16 }}>
                      <Avatar size={24} style={{ marginRight: 4 }}>
                        {tag.adminNickName.slice(0, 1)}
                      </Avatar>
                      {tag.adminNickName}
                    </Tag>
                  ))}
                </div>
              )}
            </Form.Item>

            <Form.Item label="状态" field="status" triggerPropName="checked" rules={[{ required: isEdit }]}>
              {isEdit ? <Checkbox>启用</Checkbox> : <span>{tenantInfo?.status ? '已启用' : '未启用'}</span>}
            </Form.Item>

            <Form.Item label="SaaS 功能" field="publishModel">
              {isEdit ? (
                <Checkbox checked={saasChecked} onChange={handleChecked}>
                  启用
                </Checkbox>
              ) : (
                <span>{tenantInfo?.publishModel === PlatformTenantPublishMode.saas ? '已启用' : '未启用'}</span>
              )}
            </Form.Item>

            <Form.Item wrapperCol={{ offset: 5 }}>
              <Space>
                <Button onClick={() => setIsEdit((pre) => !pre)}>{isEdit ? '取消' : '编辑'}</Button>
                {isEdit && (
                  <Button type="primary" onClick={handleSave}>
                    保存修改
                  </Button>
                )}
              </Space>
            </Form.Item>
          </Form>
        </Tabs.TabPane>
      </Tabs>
    </div>
  );
};

export default EditTenant;
