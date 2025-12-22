import { useState, useEffect, type FC } from 'react';
import { Table, type TableColumnProps, Button, Tag, Message, Pagination, Avatar } from '@arco-design/web-react';
import { getMyCreatePageList } from '@onebase/app/src/services/app_runtime';
import { LISTTYPE, FLOWSTATUS_TYPE, FlowStatusMap } from '@onebase/app';
import dayjs from 'dayjs';
import TableSearch from './TableSearch';
import DetailPop from './DetailPop';
import '../style/tcPage.less';

const AvatarGroup = Avatar.Group;

const ICreated: FC = ({ appId }: any) => {
  const columns: TableColumnProps[] = [
    {
      title: '流程标题',
      dataIndex: 'processTitle',
      width: 250,
      ellipsis: true
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
      title: '当前节点处理人',
      dataIndex: 'currentNodeHandler',
      render: (userArr: any) => {
        return (
          <div className="flex-bw-center">
            {userArr?.length > 0 ? (
              <>
                <AvatarGroup className="color-avatar">
                  {userArr.map((item: any, i: number) => {
                    return (
                      <Avatar key={i}>{item?.avatar ? <img src={item?.avatar} /> : item?.userName?.charAt(0)}</Avatar>
                    );
                  })}
                </AvatarGroup>
                <div style={{ paddingLeft: '3px' }}>
                  {userArr.map((item: any, i: number) => {
                    return <span key={i}>{i > 0 ? `、${item?.userName}` : item?.userName}</span>;
                  })}
                </div>
              </>
            ) : (
              '-'
            )}
          </div>
        );
      },
      ellipsis: true
    },
    {
      title: '发起时间',
      dataIndex: 'submitTime',
      render: (value: number) => {
        return value ? dayjs(value).format('YYYY-MM-DD HH:mm:ss') : '-';
      }
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      render: (value: number) => {
        return dayjs(value).format('YYYY-MM-DD HH:mm:ss');
      }
    },
    {
      title: '操作',
      dataIndex: 'op',
      align: 'center',
      render: (_: any, record: any) => (
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
      const res = await getMyCreatePageList(queryParams);
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

  const handleSearch = (newFilters: any) => {
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

  return (
    <section className="page-content-rgt">
      <div className="table-title-box">
        <div>
          <b style={{ marginRight: '8px' }}>我创建的</b>
        </div>
        <TableSearch
          uiConfig={{ hasInput: true, hasFilter: { dateTimeLabel: '创建时间' }, hasSort: true, hasBatch: false }}
          onReset={handleReset}
          onFilterChange={handleSearch}
        />
      </div>
      <Table className="task-tb-box" columns={columns} data={data} pagination={false} loading={loading} />
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
          listType={LISTTYPE.ICREATED}
          onBack={onBack}
        />
      )}
    </section>
  );
};

export default ICreated;
