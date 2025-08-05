import {  Space, Table, Button, Modal, Input, Message, Tooltip } from '@arco-design/web-react';
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
  const [passwordForm, setPasswordForm] = useState({ account: '', oldPassword: '', newPassword: '', confirmPassword: '' });
  const [emailForm, setEmailForm] = useState({ account: '', oldEmail: '', newEmail: '' });
  // const [deleteConfirmVisible, setDeleteConfirmVisible] = useState(false);
  const [selectedRecord, setSelectedRecord] = useState(null);

  const columns = [
    {
      title: '序号',
      dataIndex: 'id',
      key: 'id',
    },
    {
      title: '账号',
      dataIndex: 'account',
      key: 'account',
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      key: 'email',
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
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
            <Tooltip
              position="tr"
              trigger="hover"
              content={
                <div style={{ background: '#e6fffa', padding: '12px', borderRadius: '4px' }}>
                  <div style={{ display: 'flex', alignItems: 'center' }}>
                    <span style={{ color: '#faad14', marginRight: '8px' }}>!</span>
                    <span>Are you sure you want to delete?</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '12px' }}>
                    <Button type="default" style={{ marginRight: '8px' }}>取消</Button>
                    <Button type="primary">确定</Button>
                  </div>
                </div>
              }
            >
              <Button type="text">
                删除
              </Button>
            </Tooltip>
          )}
        </Space>
      ),
    },
  ];

  const dataSource = [
    {
      id: 1,
      account: '默认租户',
      email: 'xxx@csmr.com',
      type: '系统默认账号',
      createTime: '2025-08-14 10:30',
    },
    {
      id: 2,
      account: '测试环境验证租户',
      email: 'xxx1@csmr.com',
      type: '新建账号',
      createTime: '2025-08-14 10:30',
    },
  ];

  const handleEditEmail = (record: AdminRecord) => {
    setEmailForm({ account: record.account, oldEmail: record.email, newEmail: '' });
    setModalVisible(true);
  };

  const handleEditPassword = (record: AdminRecord) => {
    setPasswordForm({ account: record.account, oldPassword: '', newPassword: '', confirmPassword: '' });
    setModalVisible(true);
  };

  // const handleDeleteConfirm = (record: AdminRecord) => {
  //   setSelectedRecord(record);
  //   setDeleteConfirmVisible(true);
  // };

  // const handleDelete = () => {
  //   // 实现删除逻辑
  //   Message.success('删除成功');
  //   setDeleteConfirmVisible(false);
  // };

  return (
    <div className={styles.administrator}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <Button type="primary" onClick={() => console.log('新建')}>+ 新建</Button>
        <Table
          columns={columns}
          data={dataSource}
          pagination={{
            pageSize: 10,
            showTotal: (total) => `共 ${total} 条`
          }}
        />
        <Modal
          visible={modalVisible}
          title={selectedRecord?.type === '系统默认账号' ? '修改密码' : '修改邮箱'}
          onCancel={() => setModalVisible(false)}
          onOk={() => console.log('提交')}
        >
          {selectedRecord?.type === 'password' ? (
            <>
              <Input value={passwordForm.account} disabled />
              <Input
                placeholder="原密码"
                type="password"
                value={passwordForm.oldPassword}
                onChange={
                  (value) => setEmailForm({ ...emailForm, newEmail: value })
                }
              />
              <Input
                placeholder="新密码"
                type="password"
                value={passwordForm.newPassword}
                onChange={
                  (value) => setPasswordForm({ ...passwordForm, newPassword: value })
                }
              />
              <Input
                placeholder="确认密码"
                type="password"
                value={passwordForm.confirmPassword}
                onChange={
                  (value) => setPasswordForm({ ...passwordForm, confirmPassword: value })
                }
              />
            </>
          ) : (
            <>
              <Input value={emailForm.account} disabled />
              <Input value={emailForm.oldEmail} disabled />
              <Input
                placeholder="新邮箱"
                value={emailForm.newEmail}
                onChange={
                  (value) => setEmailForm({ ...emailForm, newEmail: value })
                }
              />
            </>
          )}
        </Modal>
      </Space>
    </div>
  );
};

export default Administrator;