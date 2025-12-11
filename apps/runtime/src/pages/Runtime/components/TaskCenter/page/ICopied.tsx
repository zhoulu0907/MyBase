import { useState, useEffect, type FC } from 'react';
import { Table, type TableColumnProps, Button, Tag, Message, Radio, Pagination } from '@arco-design/web-react';
import { getMyCCPageList } from '@onebase/app/src/services/app_runtime';
import { FLOWSTATUS_TYPE, FlowStatusMap, LISTTYPE } from '@onebase/app';
import TableSearch from './TableSearch';
import DetailPop from './DetailPop';
import '../style/tcPage.less';

const getTimeAgo = (time: number) => {
  const now = Date.now();
  const diff = now - time;
  const minutes = Math.floor(diff / 60000);
  const hours = Math.floor(minutes / 60);
  const days = Math.floor(hours / 24);
  if (days > 0) return `${days}天前`;
  if (hours > 0) return `${hours}小时前`;
  if (minutes > 0) return `${minutes}分钟前`;
  return '刚刚';
};
const radioList = [
  { label: '未读', value: 'false' },
  { label: '已读', value: 'true' },
  { label: '全部', value: 'all' }
];
let defaultCheck = 'all';

const ICopied: FC = ({ appId }: any) => {
  const columns: TableColumnProps[] = [
    {
      title: '流程标题',
      dataIndex: 'processTitle'
    },
     {
      title: '发起人',
      dataIndex: 'initiator',
      ellipsis: true,
      render: (obj: any) => (
        <span className="flex-bw-center">
          <div className="photo-img">{obj?.avatar ? <img src={obj?.avatar} /> : obj?.name?.charAt(0)}</div>
          {obj?.name}
        </span>
      )
    },
    {
      title: '流程状态',
      dataIndex: 'flowStatus',
      render: (val: FLOWSTATUS_TYPE) => {
        if (val === FLOWSTATUS_TYPE.APPROVED) {
          return (
            <Tag color="green" size="medium">
              {FlowStatusMap[val]}
            </Tag>
          );
        } else if (val === FLOWSTATUS_TYPE.IN_APPROVAL) {
          return (
            <Tag color="blue" size="medium">
              {FlowStatusMap[val]}
            </Tag>
          );
        } else if (val === FLOWSTATUS_TYPE.REJECTED || val === FLOWSTATUS_TYPE.WITHDRAWN) {
          return (
            <Tag color="red" size="medium">
              {FlowStatusMap[val]}
            </Tag>
          );
        } else {
          return (
            <Tag color="gray" size="medium">
              {FlowStatusMap[val]}
            </Tag>
          );
        }
      }
    },
    {
      title: '到达时间',
      dataIndex: 'arrivalTime',
      ellipsis: true,
      render: (value: number) => <span style={{ color: '#FF7D00' }}>{getTimeAgo(value)}</span>
    },
    {
      title: '操作',
      dataIndex: 'op',
      align: 'center',
      render: (_, record) => (
        <Button
          type="text"
          onClick={() => {
            handleDetailPage(record);
          }}
        >
          详情
        </Button>
      )
    }
  ];

  const [detailPopVisible, setPopVisible] = useState(false);
  const [data, setData] = useState<any>();
  const [rowData, setRowData] = useState();
  const [pagination, setPagination] = useState<any>({
    current: 1,
    pageSize: 10,
    total: 0
  });
  const [loading, setLoading] = useState(false);
  const [filters, setFilters] = useState<any>({});
  const [viewed, setViewed] = useState<any>();
  const defaultPageNo = 1;

  function handleDetailPage(row: any) {
    setRowData(row);
    setPopVisible(true);
  }

  const fetchFormData = async (currentParams = filters, currentPage = 1, currentPageSize = pagination.pageSize) => {
    setLoading(true);
    try {
      const queryParams = {
        ...currentParams,
        pageNo: currentPage,
        pageSize: currentPageSize,
        appId
      };
      const res = await getMyCCPageList(queryParams);
      if (Array.isArray(res?.list)) {
        setData(
          res.list.map((item: object, i: number) => {
            return {
              ...(item || {}),
              key: i
            };
          })
        );
      }
      setPagination({
        current: currentPage,
        pageSize: currentPageSize,
        total: res.total || 0
      });
      setFilters(currentParams);
    } catch (error) {
      Message.error('加载失败');
    } finally {
      setLoading(false);
    }
  };

  const onBack = () => {
    setPopVisible(false);
    fetchFormData(filters, defaultPageNo);
  };

  const handleSearch = (params: any) => {
    let newFilters = {};
    if (viewed === 'all') {
      newFilters = params;
    } else {
      newFilters = { ...params, viewed: viewed };
    }
    fetchFormData(newFilters, defaultPageNo);
  };
  const handleReset = () => {
    fetchFormData({}, defaultPageNo);
  };

  const handlePageChange = (current: number, pageSize: number) => {
    fetchFormData(filters, current, pageSize);
  };

  useEffect(() => {
    fetchFormData({}, defaultPageNo);
  }, []);

  function CreatedRadioChange(val: string) {
    setViewed(val);
    let newFilters = {};
    if (val === 'all') {
      const { viewed, ...rest } = filters;
      newFilters = rest;
    } else {
      newFilters = { ...filters, viewed: val };
    }
    fetchFormData(newFilters, defaultPageNo);
  }

  return (
    <section className="page-content-rgt">
      <div className="table-title-box">
        <div>
          <b style={{ marginRight: '8px' }}>抄送我的</b>
          <Radio.Group
            defaultValue={defaultCheck}
            onChange={CreatedRadioChange}
            name="button-radio-group"
            className="created-radio-group"
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
        </div>
        <TableSearch
          uiConfig={{ hasInput: true, hasFilter: true, hasSort: true, hasBatch: false }}
          onReset={handleReset}
          onFilterChange={handleSearch}
        />
      </div>
      <Table className="task-tb-box" columns={columns} data={data} />
      <Pagination
        current={pagination.current}
        pageSize={pagination.pageSize}
        total={pagination.total}
        onChange={handlePageChange}
        showTotal={(total: any) => `共 ${total} 项数据`}
        showJumper
        sizeCanChange
      />
      {detailPopVisible && (
        <DetailPop
          detailPopVisible={detailPopVisible}
          setPopVisible={setPopVisible}
          rowData={rowData}
          onBack={onBack}
          listType={LISTTYPE.ICOPIED}
        />
      )}
    </section>
  );
};

export default ICopied;
