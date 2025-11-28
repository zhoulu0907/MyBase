import { useState, useEffect } from 'react';
import { Table, type TableColumnProps, Button, Tag, Radio, Space } from '@arco-design/web-react';
import { IconPlusCircle } from '@arco-design/web-react/icon';
import TableSearch from './TableSearch';
import EditProxyModal from '../modal/editProxyForm';
import { agentPage, agentRevoke } from '@onebase/app/src/services';
import { UserPermissionManager } from '@/utils/permission';
import dayjs from 'dayjs';
import '../style/tcPage.less';

enum AgentStatus {
  'active' = '代理中',
  'inactive' = '待生效',
  'expired' = '已失效',
  'revoked' = '已撤销'
}

const radioList = [
  { label: '全部', value: 'all' },
  { label: '代理中', value: 'active' },
  { label: '待生效', value: 'inactive' },
  { label: '已失效', value: 'expired' },
  { label: '已撤销', value: 'revoked' }
];
let defaultCheck = 'all';

const TaskProxy = ({ appId }: { appId: string | null }) => {
  let [editVisible, setEditVisible] = useState(false);
  const [loading, setLoading] = useState(false);
  let [rowData, setRowData] = useState();
  let [agentStatus, setAgentStatus] = useState(defaultCheck);
  const [searchKey, setSearchKey] = useState('');
  const [tableData, setTableData] = useState([]);
  const [userInfo, setUserInfo] = useState<any>({});
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0
  });

  const columns: TableColumnProps[] = [
    {
      title: '被代理人',
      dataIndex: 'principal',
      render: (val) => (
        <span className="flex-bw-center">
          <div className="photo-img">{val?.avatar && <img src={val?.avatar} />}</div>
          {val.name}
        </span>
      )
    },
    {
      title: '代理人',
      dataIndex: 'agent',
      render: (val) => (
        <span className="flex-bw-center">
          <div className="photo-img">{val?.avatar && <img src={val?.avatar} />}</div>
          {val.name}
        </span>
      )
    },
    {
      title: '代理有效期',
      dataIndex: 'startTime',
      render: (val, record) => {
        const formatDate = (timestamp: number) => {
          return dayjs(timestamp).format('YYYY-MM-DD HH:mm:ss');
        };
        return (
          <span>
            {formatDate(val)} 至 {formatDate(record.endTime)}
          </span>
        );
      }
    },
    {
      title: '代理状态',
      dataIndex: 'agentStatus',
      render: (val: keyof typeof AgentStatus) => {
        const getColor = (status: keyof typeof AgentStatus) => {
          switch (status) {
            case 'inactive':
              return 'green';
            case 'active':
              return 'blue';
            default:
              return 'gray';
          }
        };
        return (
          <Tag color={getColor(val)} size="medium">
            {AgentStatus[val]}
          </Tag>
        );
      }
    },
    {
      title: '创建人',
      dataIndex: 'creator',
      render: (val) => (
        <span className="flex-bw-center">
          <div className="photo-img">{val?.avatar && <img src={val?.avatar} />}</div>
          {val.name}
        </span>
      )
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      render: (val) => {
        return dayjs(val).format('YYYY-MM-DD HH:mm:ss');
      }
    },
    {
      title: '操作',
      dataIndex: 'op',
      align: 'center',
      render: (_, record) => (
        <>
          <Button
            type="text"
            onClick={() => fetchAgentRevoke(record)}
            disabled={record.principal.userId !== userInfo?.id}
          >
            撤销
          </Button>
          <Button
            type="text"
            onClick={() => {
              setRowData(record);
              setEditVisible(true);
            }}
          >
            编辑
          </Button>
        </>
      )
    }
  ];

  const fetchAgentRevoke = async (record: any) => {
    try {
      await agentRevoke({ id: record.id });
      fetchProxyList();
    } catch (error) {
      console.error('撤销代理失败:', error);
    } finally {
    }
  };

  const fetchProxyList = async () => {
    try {
      const params = {
        page: pagination.current,
        pageSize: pagination.pageSize,
        appId: appId || '',
        agentStatus: agentStatus === 'all' ? undefined : agentStatus,
        personName: searchKey
      };
      setLoading(true);
      const res = await agentPage(params);
      setTableData(res.list || []);
      setPagination((prev) => ({
        ...prev,
        total: res.total || 0
      }));
    } catch (error) {
      console.error('获取代理列表失败:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleTableChange = (pagination: any) => {
    setPagination((prev) => ({
      ...prev,
      current: pagination.current,
      pageSize: pagination.pageSize
    }));
  };

  const handleSearch = (newFilters: any) => {
    setSearchKey(newFilters.keyword || '');
  };

  useEffect(() => {
    setPagination((prev) => ({
      ...prev,
      current: 1
    }));
  }, [agentStatus]);

  useEffect(() => {
    fetchProxyList();
  }, [pagination.current, pagination.pageSize, agentStatus, searchKey]);
  useEffect(() => {
    const permissionInfo = UserPermissionManager.getUserPermissionInfo();
    setUserInfo(permissionInfo?.user);
  });

  return (
    <section className="page-content-rgt">
      <div className="normal-box-title">
        <b>我已处理</b>
        <span>可授权指定人员在有效期内代理您的流程处理事务</span>
      </div>
      <div className="table-title-box">
        <Radio.Group
          value={agentStatus}
          onChange={(value) => setAgentStatus(value)}
          name="button-radio-group"
          className="created-radio-group task-proxy-radio"
        >
          {radioList.map((item) => {
            return (
              <Radio key={item.value} value={item.value}>
                {({ checked }) => {
                  return (
                    <Button key={item.value} type="text" className={`${checked ? 'rdo-checked' : ''}`}>
                      {item.label}
                    </Button>
                  );
                }}
              </Radio>
            );
          })}
        </Radio.Group>
        <Space>
          <TableSearch
            onFilterChange={handleSearch}
            uiConfig={{ hasInput: true, hasFilter: false, hasSort: false, hasBatch: false }}
          />
          <Button
            type="primary"
            onClick={() => {
              setRowData(undefined);
              setEditVisible(true);
            }}
          >
            <IconPlusCircle style={{ transform: 'scale(1.3)' }} />
            新增代理
          </Button>
        </Space>
      </div>
      <Table
        className="task-tb-box task-proxy-tb"
        columns={columns}
        data={tableData}
        pagination={pagination}
        onChange={handleTableChange}
        rowKey="id"
        loading={loading}
      />
      {editVisible && (
        <EditProxyModal
          visible={editVisible}
          setVisible={setEditVisible}
          initRowData={rowData}
          fetchProxyList={fetchProxyList}
          appId={appId}
          userInfo={userInfo}
        />
      )}
    </section>
  );
};

export default TaskProxy;
