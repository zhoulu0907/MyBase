import StatusTag from '@/components/StatusTag';
import { Button, Dropdown, Input, Menu, Message, Modal, Space, Table, Tabs, Tag } from '@arco-design/web-react';
import { IconMore } from '@arco-design/web-react/icon';
import { TokenManager, formatTimeYMDHMS } from '@onebase/common';
import { type corpAppListParams, getCorpAuthorizedAppListApiInCorp, updateAuthAppStatus } from '@onebase/platform-center';
import { useEffect, useMemo, useState } from 'react';
import { statusMapping } from '../../../../constants';
import styles from './index.module.less';

export interface AppItem {
  id: string;
  applicationName: string;
  applicationId?: string;
  applicationUid?: string;
  applicationCode: string;
  authorizationTime: string;
  versionNumber: string;
  expiresTime: string;
  statusDesc: string;
}

const AuthorizedApplication = () => {
  const [loading, setLoading] = useState<boolean>(false);
  const [visible, setVisible] = useState<boolean>(false);
  const [tableData, setTableData] = useState<AppItem[]>([]);
  const [searchValue, setSearchValue] = useState<string>('');
  const [currentTab, setCurrentTab] = useState<string>('0');
  const [pageInation, setPagination] = useState({
    showTotal: true,
    total: 0,
    pageSize: 10,
    current: 1,
    sizeCanChange: true,
    pageSizeChangeResetCurrent: true
  });
  const tokenInfo = TokenManager.getTokenInfo();

  const authorizedApplicationColumns = [
    {
      title: '应用名称',
      dataIndex: 'applicationName',
      width: 150,
      render: (text: string) => (
        <Space size={12} align="center">
          {text}
        </Space>
      )
    },
    {
      title: '应用ID',
      dataIndex: 'applicationCode',
      width: 180
    },
    {
      title: '版本号',
      dataIndex: 'versionNumber',
      width: 100,
      render: (text: string) => (
        <Tag color="gray" size="small">
          {text}
        </Tag>
      )
    },
    {
      title: '授权启效时间',
      dataIndex: 'authorizationTime',
      width: 180,
      render: (timeValue: string) => <div>{formatTimeYMDHMS(timeValue)}</div>
    },
    {
      title: '过期时间',
      dataIndex: 'expiresTime',
      width: 180,
      render: (timeValue: string) => <div>{formatTimeYMDHMS(timeValue)}</div>
    },
    {
      title: '状态',
      dataIndex: 'showStatus',
      width: 120,
      render: (status: string) => <StatusTag status={status as any} />
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
                  {record.showStatus === 1 ?  "禁用" : "启用" }
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

  const fetchCorpAuthorizedList = async (pageNo = 1, pageSize = 10, status: string = '0') => {
    setLoading(true);
    const params: corpAppListParams = {
      pageNo,
      pageSize,
      status: status ? Number(status) : 0,
      corpId: tokenInfo?.corpId || ''
    };
    try {
      const res = await getCorpAuthorizedAppListApiInCorp(params);
      if (res && Array.isArray(res.list)) {
        setTableData(res.list);
        setPagination((prev) => ({ ...prev, current: pageNo, pageSize, total: res.total || 0 }));
      } else {
        console.warn('Invalid response format:', res);
      }
    } catch (error) {
      Message.error('获取企业授权应用列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCorpAuthorizedList();
  }, []);

  //授权应用分页切换
  const handleChangePagination = (current: number, pageSize: number) => {
    fetchCorpAuthorizedList(current, pageSize);
  };

  const handleChange = (name: string, key: string) => {
    //todo
  };

  const handleSearchChange = (searchValue: string) => {
    setSearchValue(searchValue);
  };

  const handleDisabled = async(record: any) => {
    if(record.showStatus === 2) {
      const params = {id: record.id, status: 1};
      try {
          await updateAuthAppStatus(params);
          await fetchCorpAuthorizedList(pageInation.current,pageInation.pageSize);
      }catch(error) {
          Message.error("启用失败");
      }
    }else {
      const params = {id: record.id, status: 0};
      return (
          Modal.confirm({
            title: `禁用应用(${record.applicationName})? `,
            content: '禁用状态下，企业用户无法使用该应用，再次启用时用户可恢复正常使用',
            okButtonProps: {
              status: 'danger'
            },
            onOk: async () => {
              await updateAuthAppStatus(params);
              await fetchCorpAuthorizedList(pageInation.current,pageInation.pageSize);
              Message.success('禁用成功');
            }
          })
        );
      }
  };

  const handleChangeTab = (value: string) => {
    setCurrentTab(value);
    fetchCorpAuthorizedList(1, 10, value);
  };
  const displayData = useMemo(() => {
    if (!searchValue.trim()) return tableData;
    const lowerSearch = searchValue.toLowerCase();
    return tableData.filter((item) => item.applicationName?.toLowerCase().includes(lowerSearch));
  }, [tableData, searchValue]);

  return (
    <div className={styles.authorizedApplication}>
      <Tabs
        activeTab={currentTab}
        onChange={handleChangeTab}
        type="rounded"
        extra={
          <div className={styles.topHeader}>
            <div className={styles.searchContent}>
              <Input.Search
                allowClear
                placeholder="输入应用名称"
                className={styles.searchInput}
                onChange={handleSearchChange}
              />
            </div>
          </div>
        }
      >
        {statusMapping.map((item: any) => {
          return (
            <Tabs.TabPane key={item.status} title={item.label}>
              <div
                style={{
                  tableLayout: 'fixed',
                  width: '100%',
                  maxWidth: '1200px'
                }}
              >
                <Table
                  rowKey="id"
                  border={false}
                  loading={loading}
                  columns={authorizedApplicationColumns}
                  data={displayData}
                  pagination={{
                    ...pageInation,
                    onChange: handleChangePagination
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
