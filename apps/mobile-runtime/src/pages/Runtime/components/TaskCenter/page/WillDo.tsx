import { useState, type FC, useEffect } from 'react';
import { Table, type TableColumnProps, Button, Tag, Link } from '@arco-design/web-react';
import avatar from '@assets/images/avatar.svg';
import TableSearch from './TableSearch';
import DetailPop from './DetailPop';
import BatchApproveModal from '../modal/batchApprove';
import { FLOWSTATUS_TYPE, FlowStatusMap, LISTTYPE } from '@onebase/app';
import { getTodoPageList } from '@onebase/app/src/services/app_runtime';
import dayjs from 'dayjs';
import '../style/tcPage.less';
import TaskList from './TaskList';

 const getTimeAgo = (time) => {
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

const WillDo: FC = ({ appId }) => {
  const columns: TableColumnProps[] = [
    {
      title: '流程标题',
      dataIndex: 'processTitle'
    },
    {
      title: '发起人',
      dataIndex: 'initiator',
      render: (val, record) => (
        <span className="flex-bw-center">
          <img src={avatar} />
          {val}
        </span>
      )
    },
    {
      title: '流程状态',
      dataIndex: 'flowStatus',
      render: (val, record) => {
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
      dataIndex: 'formSummary'
    },
    {
      title: '到达时间',
      dataIndex: 'arrivalTime',
      render: (value: number) => <span style={{ color: '#FF7D00' }}>{getTimeAgo(value)}</span>
    },
    {
      title: '发起时间',
      dataIndex: 'submitTime',
      render: (value: number) => {
        return dayjs(value).format('YYYY-MM-DD HH:mm:ss');
      }
    },
    {
      title: '操作',
      dataIndex: 'op',
      align: 'center',
      render: (_, record) => (
        <Button
          type="text"
          status="success"
          onClick={() => {
            handleDetailPage(record);
          }}
        >
          详情
        </Button>
      )
    }
  ];
  let [tbRowSelection, setTbRowSelection] = useState<any>();
  const [selectedRowKeys, setSelectedRowKeys] = useState<any>();
  const [data, setData] = useState<any>();
  const [rowData, setRowData] = useState();
  const [taskId, setTaskId] = useState('');
  let [detailPopVisible, setPopVisible] = useState(false);
  let [approveVisible, setApproveVisible] = useState(false);

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
    console.log('click to detail page === row ===', row);
    setTaskId(row?.taskId);
    setPopVisible(true);
    setRowData(row);
  }

  const fetchFormData = async () => {
    const req = {
      appId
      //   pageNo: 1,
      //   pageSize: 10,
      //   processTitle: '',
      //   initiator: 'admin',
      //   formSummary: '',
      //   sortType: '',
      //   submitTimeStart: '',
      //   submitTimeEnd: ''
    };
    return await getTodoPageList(req);
    // setData(res?.list);
  };

  const onBack = () => {
    setPopVisible(false);
    fetchFormData();
  };

  useEffect(() => {
    fetchFormData();
  }, []);

  return (
    <section className="page-will-do">
      <TaskList 
        title="待我处理"
        dataFetch={fetchFormData}
        columns={columns}
      />
    </section>
  );
};

export default WillDo;
