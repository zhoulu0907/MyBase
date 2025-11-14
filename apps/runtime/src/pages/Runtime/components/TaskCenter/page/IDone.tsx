import { useState, useEffect, type FC } from 'react';
import { Table, type TableColumnProps, Button, Message, Pagination } from '@arco-design/web-react';
import TableSearch from './TableSearch';
import DetailPop from './DetailPop';
import { getDonePageList } from '@onebase/app/src/services/app_runtime';
import { LISTTYPE, TaskStatusMap } from '@onebase/app';
// import { getDonePageList } from '../../../../../../../../packages/app/src/services/app_runtime';
import dayjs from 'dayjs';

const IDone: FC = ({ appId }: any) => {
  const columns: TableColumnProps[] = [
    {
      title: '流程标题',
      dataIndex: 'processTitle',
      width: 250,
      ellipsis: true
    },
    {
      title: '发起人',
      dataIndex: 'initiator',
      ellipsis: true,
      render: (obj: any) => (
        <span className="flex-bw-center">
          <div className="photo-img">{obj?.avatar && <img src={obj?.avatar} />}</div>
          {obj?.name}
        </span>
      )
    },
    {
      title: '处理操作',
      dataIndex: 'taskStatus',
      render: (val: TaskStatusMap) => {
        if (val === TaskStatusMap.SUBMITTED || val === TaskStatusMap.AGREED || val === TaskStatusMap.PASS) {
          return <span style={{ color: '#00B42A' }}>{val}</span>;
        } else if (
          val === TaskStatusMap.REJECTED ||
          val === TaskStatusMap.RETURNED ||
          val === TaskStatusMap.WITHDRAWN ||
          val === TaskStatusMap.AUTOREJECTED
        ) {
          return <span style={{ color: '#F53F3F' }}>{val}</span>;
        } else {
          return <span style={{ color: '#4E5969' }}>{val}</span>;
        }
      }
    },
    {
      title: '处理时间',
      dataIndex: 'handleTime',
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
  let [detailPopVisible, setPopVisible] = useState(false);
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
      const res = await getDonePageList(queryParams);
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
        <b>我已处理</b>
        <TableSearch
          uiConfig={{ hasInput: true, hasFilter: true, hasSort: true, hasBatch: false }}
          onReset={handleReset}
          onFilterChange={handleSearch}
        />
      </div>
      <Table className="task-tb-box created-tb" columns={columns} data={data} pagination={false} loading={loading} />
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
          listType={LISTTYPE.IDONE}
          rowData={rowData}
          onBack={onBack}
        />
      )}
    </section>
  );
};

export default IDone;
