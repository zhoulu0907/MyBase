import { Space, Table, Button, Modal, Input, Message, Tooltip, Form } from '@arco-design/web-react';
import React, { useState } from 'react';
import styles from './index.module.less';

interface AdminRecord {
  id: number;
  account: string;
  email: string;
  type: string;
  createTime: string;
}
const Administrator: React.FC = () => {
  const [modalVisible, setModalVisible] = useState(false);
  const [passwordForm, setPasswordForm] = useState({
    account: '',
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  });
  const [emailForm, setEmailForm] = useState({
    account: '',
    oldEmail: '',
    newEmail: ''
  });
  const [deleteConfirmVisible, setDeleteConfirmVisible] = useState(false);
  const [selectedRecord, setSelectedRecord] = useState(null);
  const [modalType, setModalType] = useState<'email' | 'password' | null>(null);

  const columns = [
    {
      title: '序号',
      dataIndex: 'id',
      key: 'id'
    },
    {
      title: '账号',
      dataIndex: 'account',
      key: 'account'
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      key: 'email'
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type'
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime'
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_, record: AdminRecord) => (
        <Space>
          <Button type="text" onClick={() => handleEditEmail(record)}>
            修改邮箱
          </Button>
          <Button type="text" onClick={() => handleEditPassword(record)}>
            修改密码
          </Button>
          {record.type !== '系统默认账号' && (
            <Tooltip position="tr" trigger="hover" content="Are you sure you want to delete?">
              <Button key={`delete-${record.id}`} type="text" onClick={() => handleDeleteConfirm(record)}>
                删除
              </Button>
            </Tooltip>
          )}
        </Space>
      )
    }
  ];

  const dataSource = [
    {
      id: 1,
      account: '默认租户',
      email: 'xxx@csmr.com',
      type: '系统默认账号',
      createTime: '2025-08-14 10:30'
    },
    {
      id: 2,
      account: '测试环境验证租户',
      email: 'xxx1@csmr.com',
      type: '新建账号',
      createTime: '2025-08-14 10:30'
    }
  ];

  const handleEditEmail = (record: AdminRecord) => {
    setEmailForm({
      account: record.account,
      oldEmail: record.email,
      newEmail: ''
    });
    setModalType('email');
    setModalVisible(true);
  };

  const handleEditPassword = (record: AdminRecord) => {
    setPasswordForm({
      account: record.account,
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    });
    setModalType('password');
    setModalVisible(true);
  };

  const handleDeleteConfirm = (record: AdminRecord) => {
    setSelectedRecord(record);
    setDeleteConfirmVisible(true);
  };

  const handlePasswordSubmit = () => {
    const { oldPassword, newPassword, confirmPassword } = passwordForm;

    if (!oldPassword) {
      Message.error('请输入原密码');
      return;
    }

    if (!newPassword || newPassword.length < 6) {
      Message.error('新密码至少需要6位');
      return;
    }

    if (newPassword !== confirmPassword) {
      Message.error('新密码和确认密码不一致');
      return;
    }

    // 实际的提交逻辑
    console.log('提交密码修改:', passwordForm);
    Message.success('密码修改成功');
    setModalVisible(false);
  };
  return (
    <div className={styles.administrator}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <Button type="primary" onClick={() => console.log('新建')}>
          + 新建
        </Button>
        <Table
          columns={columns}
          data={dataSource}
          pagination={{
            pageSize: 10,
            showTotal: (total) => `共 ${total} 条`
          }}
          rowKey="id"
        />
        <Modal
          visible={modalVisible}
          title={modalType === 'password' ? '修改密码' : '修改邮箱'}
          onCancel={() => setModalVisible(false)}
          footer={[
            <Button key="return" onClick={() => setModalVisible(false)}>
              Return
            </Button>,
            <Button key="submit" type="primary" onClick={handlePasswordSubmit}>
              Submit
            </Button>
          ]}
        >
          {modalType === 'password' ? (
            <Form layout="vertical">
              <Form.Item label="账号">
                <Input value={passwordForm.account} disabled />
              </Form.Item>
              <Form.Item label="原密码">
                <Input
                  placeholder="原密码"
                  type="password"
                  value={passwordForm.oldPassword}
                  onChange={(value) => setPasswordForm({ ...passwordForm, oldPassword: value })}
                />
              </Form.Item>
              <Form.Item label="新密码">
                <Input
                  placeholder="新密码"
                  type="password"
                  value={passwordForm.newPassword}
                  onChange={(value) => setPasswordForm({ ...passwordForm, newPassword: value })}
                />
              </Form.Item>
              <Form.Item label="确认密码">
                <Input
                  placeholder="确认密码"
                  type="password"
                  value={passwordForm.confirmPassword}
                  onChange={(value) => setPasswordForm({ ...passwordForm, confirmPassword: value })}
                />
              </Form.Item>
            </Form>
          ) : (
            <Form layout="vertical">
              <Form.Item label="账号">
                <Input value={emailForm.account} disabled />
              </Form.Item>
              <Form.Item label="原邮箱">
                <Input value={emailForm.oldEmail} disabled />
              </Form.Item>
              <Form.Item label="新邮箱">
                <Input
                  placeholder="新邮箱"
                  value={emailForm.newEmail}
                  onChange={(value) => setEmailForm({ ...emailForm, newEmail: value })}
                />
              </Form.Item>
            </Form>
          )}
        </Modal>
      </Space>
    </div>
  );
};

export default Administrator;
