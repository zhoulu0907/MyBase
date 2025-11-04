import { copyToClipboard, getDomainPrefix, simplifyUrl } from '@/utils/date';
import {
  Avatar,
  Button,
  Checkbox,
  Form,
  Input,
  Message,
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
  updatePlatformTenantApi,
  type UpdateTenantParams
} from '@onebase/platform-center';
import { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import WorkspaceSecurity from './components/security';
import styles from './index.module.less';

const Option = Select.Option;

const EditTenant = () => {
  const [form] = Form.useForm();
  const location = useLocation();

  // 通过 URLSearchParams 获取查询参数
  const searchParams = new URLSearchParams(location.search);
  const id = searchParams.get('id');

  const [isEdit, setIsEdit] = useState(false);
  const [tenantInfo, setTenantInfo] = useState<any>(); // todo
  const [adminList, setAdminList] = useState<{ id: string; nickname: string; username: string; mobile: string }[]>([]);

  const domainPrefix = getDomainPrefix();
  const fullUrl = `${domainPrefix}/v0/obappbuilder/#/tenant/${tenantInfo?.id}/${tenantInfo?.website}/`;
  const displayUrl = simplifyUrl(fullUrl);

  console.log(fullUrl === displayUrl);

  useEffect(() => {
    if (id) {
      getTenantInfo(id);
      getPlatformAdminList();
    }
  }, [id]);

  useEffect(() => {
    if (tenantInfo) {
      const initialValues = {
        name: tenantInfo?.name,
        id: tenantInfo?.id,
        logo: '',
        url: fullUrl,
        userLimit: 5000,
        admin: [tenantInfo?.adminNickName],
        status: tenantInfo?.status === 1,
        saas: false
      };
      form.setFieldsValue(initialValues);
    }
  }, [tenantInfo]);

  const getTenantInfo = async (id: string) => {
    const res = await getPlatformTenantAdminInfoApi({ id });
    setTenantInfo(res);
    console.log(res, 999);
  };

  // 获取管理员列表
  const getPlatformAdminList = async () => {
    try {
      const adminListResp = await getPlatformTenantAdminListApi();
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

      // 检查管理员是否发生变化
      const newAdminId = values.admin; // 这里是 id
      const originalAdminId = tenantInfo?.adminUserName || ''; // 原始 contactName 是 username

      // 根据 id 查找管理员
      const selectedAdmin = adminList.find((admin) => admin.id === newAdminId);
      const adminUsername = selectedAdmin ? selectedAdmin.username : '';
      const adminNickname = selectedAdmin ? selectedAdmin.nickname : '';
      const adminMobile = selectedAdmin ? selectedAdmin.mobile : '';

      // 构建更新参数
      if (tenantInfo?.id) {
        const updateParams: UpdateTenantParams = {
          id: tenantInfo.id,
          name: values.tenantName,
          tenantCode: values.tenantCode,
          // 只有管理员发生变化时才传递管理员信息，否则传递空字符串
          adminNickName: newAdminId !== originalAdminId ? adminNickname : '',
          adminUserName: newAdminId !== originalAdminId ? adminUsername : '',
          adminMobile: newAdminId !== originalAdminId ? adminMobile : '',
          status: values.status,
          accountCount: values.allocatedCount,
          website: values.website
        };
        // 调用 updatePlatformTenantApi
        await updatePlatformTenantApi(updateParams);
        Message.success('更新成功');
      } else {
        // 添加错误处理，以防万一id不存在
        Message.error('租户信息不完整，无法更新');
      }

      setIsEdit(false);
      Message.success('保存成功');
    } catch (error) {
      console.error('更新租户信息失败:', error);
      Message.error('更新租户信息失败');
    }
  };

  return (
    <div className={styles.editPage}>
      <Tabs defaultActiveTab="1" destroyOnHide={false}>
        <Tabs.TabPane key="1" title="基本信息">
          <div className={styles.basicInfoForm}>
            <Form
              form={form}
              layout="horizontal"
              autoComplete="off"
              initialValues={{
                name: tenantInfo?.name,
                id: tenantInfo?.id,
                logo: '',
                url: tenantInfo?.website,
                userLimit: 5000,
                admin: [tenantInfo?.adminNickName],
                status: tenantInfo?.status === 1,
                saas: false
              }}
            >
              <Form.Item label="空间名称" field="name" rules={[{ required: isEdit, message: '请输入空间名称' }]}>
                {isEdit ? <Input placeholder="输入空间名称" /> : <span>{tenantInfo?.name}</span>}
              </Form.Item>

              <Form.Item label="空间 Logo" field="logo">
                {isEdit ? (
                  <>
                    <Upload listType="picture-card" action="/" showUploadList={false}>
                      <Button type="outline" icon={<IconUpload />}>
                        上传图片
                      </Button>
                    </Upload>
                    <div style={{ color: '#999', marginTop: 4 }}>建议比例 2:1</div>
                  </>
                ) : (
                  <div className={styles.tenantLogo}>{tenantInfo?.name.slice(0, 6)}</div>
                )}
              </Form.Item>

              <Form.Item label="空间ID" field="id">
                {isEdit ? <Input type="number" disabled /> : <span>{tenantInfo?.id}</span>}
              </Form.Item>

              <Form.Item label="访问地址" field="url" rules={[{ required: isEdit, message: '请输入访问地址' }]}>
                {isEdit ? (
                  <Input prefix="http://" placeholder="www.onebase.com/" />
                ) : (
                  <div className={styles.urlWrapper}>
                    <Tooltip content={displayUrl}>
                      <span className={styles.url}>{displayUrl}</span>
                    </Tooltip>
                    <IconCopy className={styles.copyIcon} onClick={() => copyToClipboard(fullUrl)} />
                  </div>
                )}
              </Form.Item>

              <Form.Item label="用户上限" field="userLimit" rules={[{ required: isEdit, message: '请输入用户上限' }]}>
                {isEdit ? <Input type="number" /> : <span>{tenantInfo?.userLimit || 5000}</span>}
              </Form.Item>

              <Form.Item
                label="管理员"
                field="admin"
                rules={[{ required: isEdit, message: '请选择管理员' }]}
                extra={isEdit && '当前用户将作为空间所有者'}
              >
                {isEdit ? (
                  <Select placeholder="选择管理员" mode="multiple" allowClear style={{ width: '100%' }}>
                    <Option key="1" value="王少青">
                      王少青
                    </Option>
                    <Option key="2" value="张三">
                      张三
                    </Option>
                    <Option key="3" value="李四">
                      李四
                    </Option>
                  </Select>
                ) : (
                  <div className={styles.tagWrapper}>
                    {Array(3)
                      .fill('')
                      .map((tag, index) => (
                        <Tag className={styles.adminTag} key={index} size="large" style={{ borderRadius: 16 }}>
                          <Avatar size={24} style={{ marginRight: 4 }}>
                            A
                          </Avatar>
                          王少青
                        </Tag>
                      ))}
                  </div>
                )}
              </Form.Item>

              <Form.Item label="状态" field="status" triggerPropName="checked" rules={[{ required: isEdit }]}>
                {isEdit ? <Checkbox>启用</Checkbox> : <span>{tenantInfo?.status ? '已启用' : '未启用'}</span>}
              </Form.Item>

              <Form.Item label="SaaS 功能" field="saas" triggerPropName="checked" rules={[{ required: isEdit }]}>
                {isEdit ? <Checkbox>启用</Checkbox> : <span>{tenantInfo?.saas ? '已启用' : '未启用'}</span>}
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
          </div>
        </Tabs.TabPane>
        <Tabs.TabPane key="2" title="安全设置">
          <WorkspaceSecurity />
        </Tabs.TabPane>
      </Tabs>
    </div>
  );
};

export default EditTenant;
