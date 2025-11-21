import StatusTag from '@/components/StatusTag';
import { useAppStore } from '@/store';
import { Button, Dropdown, Menu, Message, Modal, Space, Tabs, Tag } from '@arco-design/web-react';
import { IconMore } from '@arco-design/web-react/icon';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { statusMapping } from '../../constants';
import { useTableData } from '../../hooks/useTable';
import { CommonTable } from '../table/commonTable';
import { TopHeader } from '../topHeader';
import styles from './index.module.less';

// 模拟数据
const initialAuthorizedAppData = Array(5)
  .fill()
  .map((_, index) => ({
    key: index + 1,
    appName: 'CustomerRM_1c',
    appId: 'CustomerRM_1c',
    version: 'v1.2.3',
    effectTime: '2025-03-29 12:46:21',
    expireTime: '2025-03-29 12:46:21',
    status: 1
  }));

initialAuthorizedAppData.push(
  {
    key: initialAuthorizedAppData.length + 1,
    appName: 'CustomerRM_2c',
    appId: 'CustomerRM_2c',
    version: 'v1.2.3',
    effectTime: '2025-03-29 12:46:21',
    expireTime: '2025-03-29 12:46:21',
    status: 0
  },
  {
    key: initialAuthorizedAppData.length + 1,
    appName: 'CustomerRM_3c',
    appId: 'CustomerRM_3c',
    version: 'v1.2.3',
    effectTime: '2025-03-29 12:46:21',
    expireTime: '2025-03-29 12:46:21',
    status: 2
  }
);

const AuthorizedApplication = () => {
  const { displayData, currentPage, setSearchValue, setCurrentPage } = useTableData(initialAuthorizedAppData);

  const [currentTab, setCurrentTab] = useState<string>('all');
  const navigate = useNavigate();
  const { curAppId } = useAppStore();

  const handleChange = (name: string, key: string) => {
    //权限管理
    navigate(`/onebase/create-app/app-setting?appId=${curAppId}`);
  };

  const handleDisabled = (record: any) => {
    Modal.confirm({
      title: `禁用应用(${record.appName})? `,
      content: '禁用状态下，企业用户无法使用该应用，再次启用时用户可恢复正常使用',
      okButtonProps: {
        status: 'danger'
      },
      onOk: async () => {
        // await updateUserStatus(record.id, newStatus);
        Message.success('禁用成功');
        // getUserList();
      }
    });
  };

  const authorizedApplicationColumns = [
    {
      title: '应用名称',
      dataIndex: 'appName',
      width: 150,
      render: (text: string) => (
        <Space size={12} align="center">
          {text}
        </Space>
      )
    },
    {
      title: '应用ID',
      dataIndex: 'appId',
      width: 180
    },
    {
      title: '版本号',
      dataIndex: 'version',
      width: 100,
      render: (text: string) => (
        <Tag color="gray" size="small">
          {text}
        </Tag>
      )
    },
    {
      title: '授权启效时间',
      dataIndex: 'effectTime',
      width: 180
    },
    {
      title: '过期时间',
      dataIndex: 'expireTime',
      width: 180
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 120,
      render: (status: string) => <StatusTag status={status} />
    },
    {
      title: '操作',
      width: 180,
      render: (_: any, record: any) => (
        <Space size={4}>
          <Button size="small" type="text" onClick={handleChange.bind(null, record.name, 'authorized')}>
            权限管理
          </Button>
          <Dropdown
            trigger="click"
            droplist={
              <Menu>
                <Menu.Item key="disable" onClick={() => handleDisabled(record)}>
                  禁用
                </Menu.Item>
              </Menu>
            }
            position="bl"
          >
            <Button size="small" type="text" icon={<IconMore />} />
          </Dropdown>
        </Space>
      )
    }
  ];

  const getCurrentStatus = (value: string) => {
    switch (value) {
      case 'all':
        return -1;
      case 'started':
        return 1;
      case 'disabled':
        return 0;
      case 'expired':
        return 2;
      default:
        return '';
    }
  };

  const filteredTableData = displayData.filter((item: any) => item.status === getCurrentStatus(currentTab));

  const getCurrentTableData = () => {
    return currentTab === 'all' ? displayData : filteredTableData;
  };

  const currentTableData = getCurrentTableData();

  return (
    <div className={styles.authorizedApplication}>
      <Tabs
        activeTab={currentTab}
        onChange={setCurrentTab}
        type="rounded"
        extra={
          <TopHeader title="" isBusiness={false} type="authorized-application" setSearchInputValue={setSearchValue} />
        }
      >
        {statusMapping.map((item) => {
          return (
            <Tabs.TabPane key={item.value} title={item.label}>
              <div
                style={{
                  tableLayout: 'fixed',
                  width: '100%',
                  maxWidth: '1200px' // 强制最大宽度，与容器一致
                }}
              >
                <CommonTable
                  data={currentTableData}
                  columns={authorizedApplicationColumns}
                  scroll={{ x: 1200 }}
                  pageination={{
                    sizeCanChange: true,
                    showTotal: true,
                    total: currentTableData.length,
                    pageSize: 5,
                    current: currentPage,
                    pageSizeChangeResetCurrent: true,
                    onChange: setCurrentPage
                  }}
                />
              </div>
            </Tabs.TabPane>
          );
        })}
      </Tabs>
    </div>
  );
};

export default AuthorizedApplication;
