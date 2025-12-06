import { Button, Form,Input, Message, Select, Spin, Tabs } from '@arco-design/web-react';
import { UploadAvatarComponent } from '@onebase/common';
import { getLoginedUser, updateLoginedUser, updateLoginedUserPwd, uploadFile } from '@onebase/platform-center';
import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import styles from './index.module.less';

const TabPane = Tabs.TabPane;
const { Item: FormItem } = Form;

interface IEditPageProps {
  avatarUrl: string;
  setAvatarUrl: (data: string) => void;
}

const EditPage: React.FC<IEditPageProps> = ({ avatarUrl, setAvatarUrl }) => {
  const nav = useNavigate();
  const { tenantId } = useParams();

  const [form] = Form.useForm();
  const [passwordForm] = Form.useForm();
  const [userInfo, setUserInfo] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchUserInfo();
    form.resetFields();
    passwordForm.resetFields();
  }, []);

  const fetchUserInfo = async () => {
    try {
      setLoading(true);
      const res = await getLoginedUser();
      setUserInfo(res);
      setAvatarUrl(res.avatar || "");
      form.setFieldsValue({
        nickname: res.nickname,
        username: res.username,
        mobile: res.mobile,
        email: res.email,
        dept: res.dept?.name,
        id: res.id
      });
    } finally {
      setLoading(false);
    }
  };

  // 表单提交
  const handleSubmit = async () => {
    try {
      const values = await form.validate();
      const req = {
        nickname: values.nickname,
        mobile: values.mobile,
        email: values.email,
        avatar: avatarUrl
      };
      await updateLoginedUser(req);
      form.resetFields();
      nav(`/onebase/${tenantId}/setting/tenant`);
      Message.success('保存成功');
    } catch (error) {
      console.error('保存失败', error);
    }
  };

  const handleSubmitPassword = async () => {
    try {
      const values = await passwordForm.validate();
      const req = {
        oldPassword: values.oldPassword,
        newPassword: values.confirmNewPassword
      };
      await updateLoginedUserPwd(req);
      passwordForm.resetFields();
      nav(`/onebase/${tenantId}/setting/tenant`);
      Message.success('保存成功');
    } catch (error) {
      console.error('保存密码失败', error);
    }
  };

  // 显示加载状态
  if (loading) {
    return (
      <div className={styles.tenantPage}>
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '300px' }}>
          <Spin tip="加载中..." />
        </div>
      </div>
    );
  }

  // 数据加载完成后但没有租户信息
  if (!userInfo) {
    return (
      <div className={styles.tenantPage}>
        <div style={{ textAlign: 'center', padding: '40px' }}>
          <p>无法加载个人中心信息</p>
        </div>
      </div>
    );
  }

  const defaultNickName = userInfo?.nickname?.charAt(0) || 'U';

  return (
    <div className={styles.editPage}>
      <Tabs tabPosition="left">
        <TabPane key="tab1" title="基本资料">
          <div
            style={{
              maxWidth: 600,
              padding: 32,
              background: '#fff',
              borderRadius: 8
            }}
          >
            <Form form={form} layout="horizontal" onSubmit={handleSubmit}>
              <FormItem label="头像" field="avatar">
                <div>
                   <UploadAvatarComponent
                    getUploadFile={uploadFile}
                    avatarUrl={avatarUrl}
                    onUpdateUrl={setAvatarUrl}
                    defaultPlaceholder={defaultNickName}
                    buttonName='修改头像'
                  />
                </div>
              </FormItem>

              <FormItem label="姓名" field="nickname" required rules={[{ required: true, message: '请输入姓名' }]}>
                <Input placeholder="请输入姓名" />
              </FormItem>

              <FormItem label="账号" field="username" required>
                <Input disabled />
              </FormItem>

              <FormItem
                label="手机号"
                field="mobile"
                required
                rules={[
                  { required: true, message: '请输入手机号' },
                  { match: /^1[3-9]\d{9}$/, message: '手机号格式错误' }
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
                  { type: 'email', message: '邮箱格式错误' }
                ]}
              >
                <Input placeholder="请输入邮箱" />
              </FormItem>

              <FormItem label="所属部门" field="dept" required disabled>
                <Select placeholder="请选择所属部门"></Select>
              </FormItem>

              <FormItem label="OneID" field="id" required disabled>
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

        <TabPane key="tab2" title="修改密码">
          <div
            style={{
              maxWidth: 600,
              padding: 32,
              background: '#fff',
              borderRadius: 8
            }}
          >
            <Form form={passwordForm} layout="horizontal" onSubmit={handleSubmitPassword}>
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
                dependencies={['newPassword']}
                rules={[
                  { required: true, message: '请再次输入密码' },
                  {
                    validator: (value, cb) => {
                      if (!value) return cb();
                      const newPassword = passwordForm.getFieldValue('newPassword');
                      if (value !== newPassword) {
                        return cb('两次输入的密码不一致');
                      }
                      return cb();
                    }
                  }
                ]}
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
