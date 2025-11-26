import { Space, Table, Button, Modal, Input, Message, Tooltip, Form, Tag, Select } from '@arco-design/web-react';
import React, { useEffect, useState, useCallback } from 'react';
import styles from './index.module.less';
import { IconSearch } from '@arco-design/web-react/icon';
import { getPlatformAdminListApi, PlatformAdminUserType, createPlatformAdminApi, updatePlatformAdminPasswordApi, updatePlatformAdminMailApi, deletePlatformAdminApi, type PlatformAdminInfo, type cratePlatformAdminReq } from '@onebase/platform-center';
import { formatTimestamp } from '@/utils/date';
import Text from '@arco-design/web-react/es/Typography/text';

const { useForm } = Form;
const { Option } = Select;

const Administrator: React.FC = () => {
  const [modalVisible, setModalVisible] = useState(false);
  const [createModalVisible, setCreateModalVisible] = useState(false);
  const [passwordForm, setPasswordForm] = useState({ id: '0', username: '', newPassword: '', confirmPassword: '' });
  const [emailForm, setEmailForm] = useState({ id: '0', username: '', oldEmail: '', newEmail: '' });
  // const [deleteConfirmVisible, setDeleteConfirmVisible] = useState(false);
  const [modalType, setModalType] = useState<'email' | 'password' | null>(null);
  const [createForm] = useForm();
  const [searchKeyword, setSearchKeyword] = useState('');
  const [searchDebounceTimer, setSearchDebounceTimer] = useState<NodeJS.Timeout | null>(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [dataSource, setDataSource] = useState<PlatformAdminInfo[]>([]);
  const [total, setTotal] = useState(null)
  const [delVisible, setDelVisible] = useState(false);
  const [currentUser, setCurrentUser] = useState<PlatformAdminInfo | null>(null);
  const columns = [
    // order
    { 
      title: '序号',
      dataIndex: 'order',
      key: 'order',
      render: (_text: any, _record: any, index: number) => index + 1,
      width: '5%',
      // fixed: 'left',
    },
    {
      title: '账号',
      dataIndex: 'username',
      key: 'username',
    },
    {
      title: '姓名',
      dataIndex: 'nickname',
      key: 'nickname',
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      key: 'email',
    },
    {
      title: '手机号',
      dataIndex: 'mobile',
      key: 'mobile',
      width: 130,
    },
    {
      title: '类型',
      dataIndex: 'adminType',
      key: 'adminType',
      render: (val: PlatformAdminUserType) => (
        <Tag color={val === PlatformAdminUserType.系统默认账号 ? 'green' : 'gray'}>{val === PlatformAdminUserType.系统默认账号 ? '系统默认账号' : '普通账号'}</Tag>
      )
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 180,
      render: (text: string) => (
        <div>{formatTimestamp(text)}</div>
      )
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_: any, record: PlatformAdminInfo) => (
        <Space>
          <Text className={styles.tableBtn} onClick={() => handleEditEmail(record)}>
            修改邮箱
          </Text>
          <Text className={styles.tableBtn} onClick={() => handleEditPassword(record)}>
            修改密码
          </Text>
          {record.adminType !== PlatformAdminUserType.系统默认账号 && (
             <Tooltip
              position="tr"
              trigger="click"
              color="#fff"
              popupVisible={delVisible && currentUser?.id === record.id}
              onVisibleChange={(visible) => {
                if (visible) {
                  setCurrentUser(record);
                } else {
                  setCurrentUser(null);
                }
                setDelVisible(visible);
              }}
              content={(
                <div className={styles.tooltipContainer}>
                  <div className={styles.tooltipText}>Are you sure you want to delete?</div>
                  <Space className={styles.tooltipButton}>
                    <Button 
                      type="text" 
                      onClick={() => {
                        setDelVisible(false);
                        setCurrentUser(null);
                      }}
                    >
                      取消
                    </Button>
                    <Button 
                      type="primary" 
                      onClick={() => {
                        handleDeleteConfirm(record);
                        setDelVisible(false);
                        setCurrentUser(null);
                      }}
                    >
                      确定
                    </Button>
                  </Space>
                </div>
              )}
            >
              <Text
                className={styles.tableBtn}
                key={`delete-${record.id}`}
              >
                删除
              </Text>
            </Tooltip>
          )}
        </Space>
      )
    }
  ];
  

  const getPlatformAdminList = async (pageNo: number = 1, keyword: string = searchKeyword) => {
    const res = await getPlatformAdminListApi({
      pageNo: pageNo,
      pageSize: 10,
      keyword: keyword // 添加搜索关键词参数
    });
    setDataSource(res.list);
    setTotal(res.total);
    
    // 如果删除后当前页没有数据且不是第一页，则自动跳转到上一页
    if (res.list.length === 0 && pageNo > 1) {
      const prevPage = pageNo - 1;
      setCurrentPage(prevPage);
      return getPlatformAdminList(prevPage, keyword);
    }
  };

  useEffect(() => { 
    getPlatformAdminList();
  }, []);

  const addAdmin = () => { 
    createForm.resetFields();
    setPasswordForm({ id: '0', username: '', newPassword: '', confirmPassword: '' }); // 重置密码表单状态
    setEmailForm({ id: '0', username: '', oldEmail: '', newEmail: '' }); // 重置邮箱表单状态
    setTimeout(() => {
      setCreateModalVisible(true);
    }, 0);
  };

  const handleCreateAdmin = async () => {
    try {
      const values = await createForm.validate();

      // 构建符合 cratePlatformAdminReq 类型的提交数据
      const submitData: cratePlatformAdminReq = {
        username: values.account,
        nickname: values.nickname,
        password: values.password,
        email: values.email,
        mobile: values.mobile,
        adminType: values.adminType,
      };
      // 调用创建管理员的API
      await createPlatformAdminApi(submitData);
      
      Message.success('创建管理员成功');
      setCreateModalVisible(false);
      getPlatformAdminList();
      setCurrentPage(1)
    } catch (error) {
      console.error('表单验证失败或创建失败:', error);
      Message.error('创建管理员失败');
    } finally {
    }
  };

  const handleEditEmail = (record: PlatformAdminInfo) => {
    setEmailForm({ id: record.id, username: record.username, oldEmail: record.email, newEmail: '' });
    setModalType('email');
    setModalVisible(true);
  };

  const handleEditPassword = (record: PlatformAdminInfo) => {
    setPasswordForm({ id: record.id, username: record.username, newPassword: '', confirmPassword: '' });
    setModalType('password');
    setModalVisible(true);
  };

  const handleDeleteConfirm = async (record: PlatformAdminInfo) => {
    try {
      await deletePlatformAdminApi(record.id);
      Message.success('删除成功');
      
      // 重新获取列表数据
      // 如果当前页只有一条数据，删除后需要回到上一页
      if (dataSource.length === 1 && currentPage > 1) {
        // 如果当前页只有一条数据且不是第一页，返回上一页
        getPlatformAdminList(currentPage - 1, searchKeyword);
        setCurrentPage(currentPage - 1);
      } else {
        // 否则刷新当前页
        getPlatformAdminList(currentPage, searchKeyword);
      }
    } catch (error) {
      console.log(error);
      Message.error('删除失败');
    }

    // setDeleteConfirmVisible(true);
  };

  const handleUpdata = async () => {
    const { id: passwordId, newPassword, confirmPassword } = passwordForm;
    const { id: emailId, newEmail } = emailForm;
    // modalType
    if (modalType === 'password') { 
      if (!newPassword || newPassword.length < 6) {
        Message.error('新密码至少需要6位');
        return;
      }
  
      if (newPassword !== confirmPassword) {
        Message.error('新密码和确认密码不一致');
        return;
      }
      try {
        await updatePlatformAdminPasswordApi({id: passwordId, password: confirmPassword});
        getPlatformAdminList();
        Message.success('密码修改成功');
      } catch (error) {
        console.log(error)
      }
    } else {
      try {
        await updatePlatformAdminMailApi({id: emailId, email: newEmail})
        getPlatformAdminList();
        Message.success('邮箱修改成功');
      } catch (error) {
        console.log(error)
      }
    }
    setModalVisible(false);
  };

  const handleCancelUpdata = () => {
    setModalVisible(false);
  }

  const handleCreateModalCancel = () => {
    setCreateModalVisible(false);
    setTimeout(() => {
      createForm.resetFields();
    }, 300);
  };

// 处理搜索（带防抖）
  const handleSearch = useCallback((value: string) => {
    setSearchKeyword(value);
    setCurrentPage(1); // 重置到第一页
    
    // 清除之前的定时器
    if (searchDebounceTimer) {
      clearTimeout(searchDebounceTimer);
    }
    
    // 设置新的定时器
    const timer = setTimeout(() => {
      getPlatformAdminList(1, value);
    }, 500); // 500ms 防抖延迟
    
    setSearchDebounceTimer(timer);
  }, [searchDebounceTimer]);

  // 处理分页变化
  const handlePageChange = async (pageNo: number) => {
    try {
      await getPlatformAdminList(pageNo, searchKeyword);
      setCurrentPage(pageNo);
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <div className={styles.administrator}>
      <Space direction="vertical" size="large" className={styles.container}>
        <div className={styles.title}>
          <Button type="primary" onClick={addAdmin}>+ 新建</Button>
          <Input.Search
            placeholder="搜索平台管理员"
            style={{ width: 300 }}
            allowClear
            value={searchKeyword}
            onChange={handleSearch}
            suffix={<IconSearch />}
          />
        </div>
        {/* 表格 */}
        <Table
          border={false}
          columns={columns}
          data={dataSource}
          pagination={{
            current: currentPage,
            pageSize: 10,
            showTotal: true,
            total: total || 10,
            onChange: handlePageChange
          }}
          rowKey="id"
        />
        {/* 修改邮箱/密码弹窗 */}
        <Modal
          visible={modalVisible}
          title={modalType === 'password' ? '修改密码' : '修改邮箱'}
          onCancel={() => setModalVisible(false)}
          footer={[
            <Button key="return" onClick={handleCancelUpdata}>取消</Button>,
            <Button key="submit" type="primary" onClick={handleUpdata}>确定</Button>
          ]}
        >
          {modalType === 'password' ? (
            <Form layout="vertical">
              <Form.Item label="账号">
                <Input value={passwordForm.username} disabled />
              </Form.Item>
              <Form.Item 
                label="密码" 
                field="password"
                rules={[
                  { required: true, message: '请输入密码' },
                  { minLength: 6, message: '密码至少6位' }
                ]}
                validateTrigger={['onBlur']}
              >
                <Input
                  placeholder="新密码"
                  type="password"
                  value={passwordForm.newPassword}
                  onChange={(value) => setPasswordForm({ ...passwordForm, newPassword: value })}
                  autoComplete="new-password"
                />
              </Form.Item>
              <Form.Item 
                label="确认密码" 
                field="confirmPassword"
                dependencies={['password']}
                rules={[
                  { required: true, message: '请确认密码' },
                  {
                    validator: (value, cb) => {
                      const formValues = createForm.getFieldsValue();
                      const password = formValues.password;
                      if (value !== password) {
                        return cb('两次输入的密码不一致');
                      }
                      return cb();
                    },
                  },
                ]}
                validateTrigger={['onBlur']}
              >
                <Input
                  placeholder="确认密码"
                  type="password"
                  value={passwordForm.confirmPassword}
                  onChange={(value) => setPasswordForm({ ...passwordForm, confirmPassword: value })}
                  autoComplete="new-password"
                />
              </Form.Item>
            </Form>
          ) : (
            <Form layout="vertical">
              <Form.Item label="账号">
                <Input value={emailForm.username} disabled />
              </Form.Item>
              <Form.Item label="原邮箱">
                <Input value={emailForm.oldEmail} disabled />
              </Form.Item>
              <Form.Item 
                label="邮箱" 
                field="email"
                rules={[
                  { required: true, message: '请输入邮箱'},
                  { type: 'email', message: '请输入正确的邮箱格式'}
                ]}
                validateTrigger={['onBlur']}
              >
                <Input
                  placeholder="新邮箱"
                  value={emailForm.newEmail}
                  onChange={(value) => setEmailForm({ ...emailForm, newEmail: value })}
                />
              </Form.Item>
            </Form>
          )}
        </Modal>
        
        {/* 新建管理员弹窗 */}
        <Modal
          title="新建管理员"
          visible={createModalVisible}
          onOk={handleCreateAdmin}
          onCancel={handleCreateModalCancel}
          style={{ width: 600 }}
        >
          <Form form={createForm} layout="vertical">
            <Form.Item 
              label="账号" 
              field="account"
              rules={[
                { required: true, message: '请输入账号' },
                // 用户账号由 数字+字母 组成
                {
                  validator: (value, cb) => {
                    if (!value) {
                      return Promise.resolve();
                    }
                    const pattern = /^[a-zA-Z0-9]+$/;
                    if (!pattern.test(String(value))) {
                      return cb('用户账号由数字、字母组成')
                    }
                    return cb();
                  }
                }
              ]}
              validateTrigger={['onBlur']}
            >
              <Input placeholder="请输入账号" autoComplete="off" />
            </Form.Item>
            <Form.Item 
              label="姓名" 
              field="nickname"
              rules={[
                { required: true, message: '请输入姓名' },
              ]}
              validateTrigger={['onBlur']}
            >
              <Input placeholder="请输入姓名" autoComplete="off" />
            </Form.Item>
            <Form.Item 
              label="手机号" 
              field="mobile"
              rules={[
                { required: true, message: '请输入手机号' },
                { 
                  validator: (value, cb) => {
                    const mobileRegex = /^1[3-9]\d{9}$/;
                    if (!mobileRegex.test(value)) {
                      return cb('请输入正确的手机号');
                    }
                    return cb();
                  }
                }
              ]}
              validateTrigger={['onBlur']}
            >
              <Input placeholder="请输入手机号" />
            </Form.Item>
            <Form.Item 
              label="邮箱" 
              field="email"
              rules={[
                { required: true, message: '请输入邮箱'},
                { type: 'email', message: '请输入正确的邮箱格式'}
              ]}
              validateTrigger={['onBlur']}
            >
              <Input placeholder="请输入邮箱" autoComplete="off" />
            </Form.Item>
            <Form.Item 
              label="密码" 
              field="password"
              rules={[
                { required: true, message: '请输入密码' },
                { minLength: 6, message: '密码至少6位' }
              ]}
              validateTrigger={['onBlur']}
            >
              <Input.Password placeholder="请输入密码" autoComplete="new-password" />
            </Form.Item>
            <Form.Item 
              label="确认密码" 
              field="confirmPassword"
              dependencies={['password']}
              rules={[
                { required: true, message: '请确认密码' },
                {
                  validator: (value, cb) => {
                    const formValues = createForm.getFieldsValue();
                    const password = formValues.password;
                    if (value !== password) {
                      return cb('两次输入的密码不一致');
                    }
                    return cb();
                  },
                },
              ]}
              validateTrigger={['onBlur']}
            >
              <Input.Password placeholder="请再次输入密码" autoComplete="new-password" />
            </Form.Item>
            <Form.Item 
              label="类型" 
              field="adminType"
              initialValue={PlatformAdminUserType.普通账号}
              rules={[{ required: true, message: '请选择类型' }]}
              validateTrigger={['onBlur']}
            >
              <Select placeholder="请选择类型">
                <Option value={PlatformAdminUserType.系统默认账号}>系统默认账号</Option>
                <Option value={PlatformAdminUserType.普通账号}>普通账号</Option>
              </Select>
            </Form.Item>
          </Form>
        </Modal>
      </Space>
    </div>
  );
};

export default Administrator;
