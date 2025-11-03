import { Avatar, Divider, Spin, Typography, Message, Tabs, Form, Select, Input, Space, Button, Upload } from '@arco-design/web-react';
import { IconCopy, IconEye, IconEyeInvisible, IconUpload } from '@arco-design/web-react/icon';
import type { TenantInfo } from '@onebase/platform-center';
import { getTenantInfo, updateTenant } from '@onebase/platform-center';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';
import PlaceholderPanel from '@/components/PlaceholderPanel';
import { hasPermission } from '@/utils/permission';
import { TENANT_INFO_PERMISSION as ACTIONS } from '@/constants/permission';

const TabPane = Tabs.TabPane;
const { Option } = Select;
const { Item: FormItem } = Form;

const EditPage: React.FC = () => {
  const [form] = Form.useForm();
  const [passwordForm] = Form.useForm();
  const [avatarUrl, setAvatarUrl] = useState(
    'https://cdn.example.com/avatar-default.jpg' // 默认头像
  );
  const [tenantInfo, setTenantInfo] = useState<TenantInfo | null>(null);
  const [loading, setLoading] = useState(true);
  const [secretVisible, setSecretVisible] = useState(false);
  const [tenantName, setTenantName] = useState('');

  const fetchTenantInfo = async () => {
    try {
      setLoading(true);
      const res = await getTenantInfo();
      setTenantInfo(res);
      setTenantName(res.name);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    // fetchTenantInfo();
  }, []);


  const handleAvatarChange = (file) => {
    // 模拟上传后更新头像
    const reader = new FileReader();
    reader.onload = (e) => {
      setAvatarUrl(e.target.result);
      Message.success('头像上传成功');
    };
    reader.readAsDataURL(file.originFile);
    return false; // 阻止自动上传
  };

  const handleSubmit = (values) => {
    console.log('提交数据：', values);
    Message.success('保存成功');
  };

  // 显示加载状态
  // if (loading) {
  //   return (
  //     <div className={styles.tenantPage}>
  //       <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '300px' }}>
  //         <Spin tip="加载中..." />
  //       </div>
  //     </div>
  //   );
  // }

  // 数据加载完成后但没有租户信息
  // if (!tenantInfo) {
  //   return (
  //     <div className={styles.tenantPage}>
  //       <div style={{ textAlign: 'center', padding: '40px' }}>
  //         <p>无法加载租户信息</p>
  //       </div>
  //     </div>
  //   );
  // }


  return (
    <div className={styles.editPage}>
      <Tabs tabPosition='left'>
        <TabPane key='tab1' title='基本资料'>
          <div style={{
            maxWidth: 600,
            padding: 32,
            background: '#fff',
            borderRadius: 8,
          }}>
            <Form
              form={form}
              layout="horizontal"
              onSubmit={handleSubmit}
              initialValues={{
                name: '王少青',
                account: 'wangshaoqing',
                phone: '137 0193 5734',
                email: 'wangshaoqing@cmsr.chinamobile.com',
                department: '湖北交通行业空间 / 科创中心',
                oneid: '123566424512',
              }}
            >
              <FormItem label="头像" field="avatar">
                <div>
                  <img
                    src={avatarUrl}
                    alt="头像"
                    style={{
                      width: 120,
                      height: 120,
                      borderRadius: '50%',
                      objectFit: 'cover',
                      marginBottom: 16,
                      display: 'block'
                    }}
                  />
                  <div>
                    <Upload
                      showUploadList={false}
                      beforeUpload={handleAvatarChange}
                      accept="image/*"
                    >
                      <Button type="outline" icon={<IconUpload />}>
                        修改头像
                      </Button>
                    </Upload>
                  </div>
                </div>
              </FormItem>

              <FormItem
                label="姓名"
                field="name"
                required
                rules={[{ required: true, message: '请输入姓名' }]}
              >
                <Input placeholder="请输入姓名" />
              </FormItem>

              <FormItem label="账号" field="account" required>
                <Input disabled />
              </FormItem>

              <FormItem
                label="手机号"
                field="phone"
                required
                rules={[
                  { required: true, message: '请输入手机号' },
                  { match: /^1[3-9]\d{9}$/, message: '手机号格式错误' },
                ]}
              >
                <Input placeholder="请输入手机号" />
              </FormItem>

              <FormItem
                label="邮箱"
                field="email"
                required
                rules={[
                  { required: true, message: '请输入邮箱' },
                  { type: 'email', message: '邮箱格式错误' },
                ]}
              >
                <Input placeholder="请输入邮箱" />
              </FormItem>

              <FormItem label="所属部门" field="department" required>
                <Select placeholder="请选择所属部门">
                  <Option value="湖北交通行业空间 / 科创中心">
                    湖北交通行业空间 / 科创中心
                  </Option>
                  <Option value="技术运营部">技术运营部</Option>
                </Select>
              </FormItem>

              <FormItem label="OneID" field="oneid" required>
                <Input placeholder="请输入 OneID" />
              </FormItem>

              <FormItem wrapperCol={{ offset: 5 }}>
                <Button type="primary" htmlType="submit">
                  保存修改
                </Button>
              </FormItem>
            </Form>
          </div>
        </TabPane>
        <TabPane key='tab2' title='修改密码'>
          <div style={{
            maxWidth: 600,
            padding: 32,
            background: '#fff',
            borderRadius: 8,
          }}>
            <Form
              form={passwordForm}
              layout="horizontal"
              onSubmit={handleSubmit}
              initialValues={{
                oldPassword: '123',
                newPassword: '678',
                confirmNewPassword: '678'
              }}
            >
              <FormItem
                label="旧密码"
                field="oldPassword"
                required
                rules={[{ required: true, message: '请输入旧密码' }]}
              >
                <Input placeholder="请输入旧密码" />
              </FormItem>

              <FormItem
                label="新密码"
                field="newPassword"
                required
                rules={[{ required: true, message: '请输入新密码' }]}
              >
                <Input placeholder="请输入新密码" />
              </FormItem>

              <FormItem
                label="确认新密码"
                field="confirmNewPassword"
                required
                rules={[{ required: true, message: '请再次输入新密码' }]}
              >
                <Input placeholder="请再次输入新密码" />
              </FormItem>

              <FormItem wrapperCol={{ offset: 5 }}>
                <Button type="primary" htmlType="submit">
                  保存修改
                </Button>
              </FormItem>
            </Form>
          </div>
        </TabPane>
      </Tabs>
    </div>
  );
};

export default EditPage;
