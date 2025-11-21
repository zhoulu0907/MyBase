import { useState, type FC, useEffect } from 'react';
import { Table, type TableColumnProps, Button, Tag, Pagination, Message } from '@arco-design/web-react';
import TableSearch from './TableSearch';
import DetailPop from './DetailPop';
import BatchApproveModal from '../modal/batchApprove';
import { FLOWSTATUS_TYPE, FlowStatusMap, LISTTYPE } from '@onebase/app';
import { getTodoPageList } from '@onebase/app/src/services/app_runtime';
import dayjs from 'dayjs';
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

interface WillDoProps {
  appId: string;
}
const WillDo: FC<WillDoProps> = ({ appId }) => {
  const columns: TableColumnProps[] = [
    {
      title: '流程标题',
      dataIndex: 'processTitle',
      width:250,
      ellipsis: true
    },
    {
      title: '发起人',
      dataIndex: 'initiator',
      render: (obj: any) => (
        <span className="flex-bw-center">
          <div className="photo-img">{obj?.avatar && <img src={obj?.avatar} />}</div>
          {obj?.name}
        </span>
      ),
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
      title: '表单摘要',
      dataIndex: 'formSummary',
      width:320,
      ellipsis: true
    },
    {
      title: '到达时间',
      dataIndex: 'arrivalTime',
      ellipsis: true,
      render: (value: number) => <span style={{ color: '#FF7D00' }}>{getTimeAgo(value)}</span>
    },
    {
      title: '发起时间',
      dataIndex: 'submitTime',
      ellipsis: true,
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
  const [tbRowSelection, setTbRowSelection] = useState<any>();
  const [selectedRowKeys, setSelectedRowKeys] = useState<any>();
  const [data, setData] = useState<any>();
  const [rowData, setRowData] = useState();
  const [detailPopVisible, setPopVisible] = useState(false);
  const [approveVisible, setApproveVisible] = useState(false);
  const [pagination, setPagination] = useState<any>({
    current: 1,
    pageSize: 10,
    total: 0
  });
  const [loading, setLoading] = useState(false);
  const [filters, setFilters] = useState<any>({});
  const defaultPageNo = 1;

  function handleBatchClick(hasRowCheck: boolean) {
    console.log('batch click!', hasRowCheck);
    if (hasRowCheck) {
      setTbRowSelection({
        type: 'checkbox',
        selectedRowKeys,
        onChange: (selectedKeys: Array<string>, selectedRows: Array<any>) => {
          console.log('onChange:', selectedKeys, selectedRows);
          setSelectedRowKeys(selectedRowKeys);
        }
      });
    } else {
      setTbRowSelection(undefined);
    }
  }
  function handleBatch2Click() {
    setApproveVisible(true);
  }

  function handleDetailPage(row: any) {
    setPopVisible(true);
    setRowData(row);
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
      const res = await getTodoPageList(queryParams);
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
        <b>待我处理</b>
        <TableSearch
          uiConfig={{ hasInput: true, hasFilter: {hasStartMan: true}, hasSort: true, hasBatch: true }}
          batchEvent={handleBatchClick}
          onReset={handleReset}
          onFilterChange={handleSearch}
        />
      </div>
      {tbRowSelection && (
        <div className="flex-bw-center title-batch-box">
          <span>已选中3/20条</span>
          <div className="batch-btns">
            <Button type="outline" onClick={() => setTbRowSelection(undefined)}>
              取消操作
            </Button>
            <Button type="outline" onClick={() => handleBatch2Click()}>
              批量拒绝
            </Button>
            <Button type="primary" onClick={() => handleBatch2Click()}>
              批量同意
            </Button>
          </div>
        </div>
      )}
      <Table
        className="task-tb-box"
        loading={loading}
        rowSelection={tbRowSelection}
        columns={columns}
        data={data}
        pagination={false}
      />
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
          onBack={onBack}
          rowData={rowData}
          listType={LISTTYPE.WILLDO}
        />
      )}
      {approveVisible && <BatchApproveModal approveVisible={approveVisible} setApproveVisible={setApproveVisible} />}
    </section>
  );
};

export default WillDo;
