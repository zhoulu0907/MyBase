import { generateTimestampString } from '@/utils/date';
import { getPlatformFeDomain } from '@/utils/domain';
import {
  Button,
  Checkbox,
  Form,
  Input,
  InputNumber,
  Message,
  Modal,
  Select,
  Space,
  Upload
} from '@arco-design/web-react';
import { IconUpload } from '@arco-design/web-react/icon';
import { Cropper, TokenManager } from '@onebase/common';
import {
  addPlatformTenantApi,
  getPlatformTenantAdminListApi,
  PlatformTenantPublishMode,
  PlatformTenantStatus,
  uploadFile,
  type CreateTenantParams,
  type UserVO
} from '@onebase/platform-center';
import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './index.module.less';

const CreateSpace = () => {
  const [form] = Form.useForm();
  const nav = useNavigate();
  const [adminList, setAdminList] = useState<UserVO[]>([]);
  const [logoUrl, setLogoUrl] = useState<string>(); // logo

  const tokenInfo = TokenManager.getTokenInfo();

  const uploadRef = useRef(null);

  useEffect(() => {
    getPlatformAdminList();
  }, []);

  // 获取Tenant参数
  useEffect(() => {
    form.resetFields();
    form.setFieldsValue({
      status: PlatformTenantStatus.enabled,
      publishModel: false,
      accountCount: 5000,
      tenantAdminUserList: adminList.some((u) => u.id === tokenInfo?.userId) ? [tokenInfo?.userId] : []
    });
  }, [adminList]);

  // 生成空间编码
  const generateTenantCode = () => {
    const timestamp = generateTimestampString();
    return `tenant_${timestamp}`;
  };

  // 获取管理员列表
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
   * 创建新空间
   */
  const createTenant = async (values: any) => {
    try {
      const formattedAdmin = values.tenantAdminUserList.map((id: string) => {
        const user = adminList.find((u) => u.id === id);
        return {
          adminNickName: user?.nickname || '',
          adminUserName: user?.username || '',
          adminMobile: user?.mobile || '',
          adminEmail: user?.email || '',
          platformUserId: user?.id
        };
      });

      const newTenantData: CreateTenantParams = {
        name: values.name,
        tenantCode: generateTenantCode(),
        status: values.status ? 1 : 0,
        accountCount: values.accountCount,
        website: values.website,
        publishModel: values.publishModel ? PlatformTenantPublishMode.saas : PlatformTenantPublishMode.inner,
        tenantAdminUserReqVOList: formattedAdmin,
        logoUrl
      };
      await addPlatformTenantApi(newTenantData);
      Message.success('创建空间成功');
      nav('/onebase/tenant');
    } catch (error: any) {
      console.error('创建空间失败:', error);
    }
  };

  // 提交表单
  const handleSubmit = async () => {
    try {
      const values = await form.validate();
      console.log('提交数据:', values);

      // 创建空间
      await createTenant(values);
    } catch (error) {
      console.log('表单验证失败', error);
    }
  };

  const handleBack = () => {
    window.history.back();
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

  return (
    <div className={styles.createPage}>
      <Form
        form={form}
        layout="horizontal"
        autoComplete="off"
        onValuesChange={(values: any, changeValues: any) => {
          console.log(changeValues);
        }}
      >
        <Form.Item label="空间名称" field="name" rules={[{ required: true, message: '请输入空间名称' }]}>
          <Input placeholder="输入空间名称" />
        </Form.Item>

        <Form.Item label="空间 Logo" field="logoUrl">
          <Space direction="vertical" style={{ margin: 0 }}>
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
            <Space>
              <Button
                type="outline"
                icon={<IconUpload />}
                onClick={() => {
                  (uploadRef as any).current?.getRootDOMNode()?.querySelector('input[type="file"]').click();
                }}
              >
                上传图片
              </Button>
              <div style={{ color: '#999', marginTop: 4 }}>建议比例 2:1</div>
            </Space>
          </Space>
        </Form.Item>

        <Form.Item
          label="访问地址"
          field="website"
          rules={[{ required: true, message: '请输入访问地址' }]}
          validateTrigger={['onBlur']}
        >
          <Input
            addBefore={<div style={{ width: '250px' }}>{getPlatformFeDomain()}</div>}
            placeholder="请输入访问地址"
          />
        </Form.Item>

        <Form.Item
          label="用户上限"
          field="accountCount"
          rules={[
            { required: true, message: '请输入用户上限' },
            { type: 'number', min: 1, message: '必须大于0' }
          ]}
        >
          <InputNumber placeholder="请输入用户上限数量" min={1} />
        </Form.Item>

        <Form.Item
          label="管理员"
          field="tenantAdminUserList"
          rules={[{ required: true, message: '请选择管理员' }]}
          extra="当前用户将作为空间所有者"
        >
          <Select
            placeholder="选择管理员"
            mode="multiple"
            allowClear
            showSearch
            style={{ width: '100%' }}
            filterOption={(inputValue: any, option: any) => {
              return option.props.children?.includes(inputValue);
            }}
          >
            {adminList.map((u) => (
              <Select.Option key={u.id} value={u.id}>
                {u.nickname || u.username}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item label="状态" field="status" triggerPropName="checked">
          <Checkbox>启用</Checkbox>
        </Form.Item>

        <Form.Item label="SaaS 功能" field="publishModel">
          <Checkbox>启用</Checkbox>
        </Form.Item>

        <Space style={{ margin: 'auto' }}>
          <Button onClick={handleBack}>返回</Button>
          <Button type="primary" onClick={handleSubmit}>
            提交
          </Button>
        </Space>
      </Form>
    </div>
  );
};

export default CreateSpace;
