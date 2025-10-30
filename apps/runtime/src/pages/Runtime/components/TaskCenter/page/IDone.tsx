import { useState, useEffect, type FC } from 'react';
import { Table, type TableColumnProps, Button, Link } from '@arco-design/web-react';
import TableSearch from './TableSearch';
import DetailPop from './DetailPop';
import { getDonePageList } from '@onebase/app/src/services/app_runtime';
// import { getDonePageList } from '../../../../../../../../packages/app/src/services/app_runtime';
import dayjs from 'dayjs';
const HandleStatus = {
  APPROVE: 'approve',
  EXECUT: 'execut',
  REFUSE:'refuse',
  GOBACK:'goback',
  PASSON:'passon'
}

const IDone: FC = () => {
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
          <img src="/src/assets/images/avatar.svg" />
          {val}
        </span>
      )
    },
    {
      title: '处理操作',
      dataIndex: 'handleOperation',
      render: (val, record) => {
        if (val === HandleStatus.APPROVE || val === HandleStatus.EXECUT) {
          return <span style={{ color: '#00B42A' }}>{val}</span>;
        } else if (val === HandleStatus.GOBACK || val === HandleStatus.REFUSE) {
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
  const mockData = [
    {
      key: '1',
      name: 'Jane Doe',
      salary: 23000,
      address: '32 Park Road, London',
      handleOperation: 'approve',
      email1: 'e@example.com',
      email2: 'ample.com'
    },
    {
      key: '2',
      name: 'Alisa Ross',
      salary: 25000,
      address: '35 Park Road, London',
      handleOperation: 'goback',
      email1: '12e@example.com',
      email2: '3333ample.com'
    },
    {
      key: '3',
      name: 'Kevin Sandra',
      salary: 22000,
      address: '31 Park Road, London',
      handleOperation: '1kevin.sandra@example.com',
      email1: 'aaae@example.com',
      email2: 'bbbample.com'
    }
  ];
  let [detailPopVisible, setPopVisible] = useState(false);
  const [data, setData] = useState<any>();
  function handleDetailPage(row: any) {
    console.log('click to detail page === row ===', row);
    setPopVisible(true);
  }
  const fetchFormData = async () => {
    //todo：模拟参数
    const req = {
      appId: '1332334434343'
      //   pageNo: 1,
      //   pageSize: 10,
      //   processTitle: '',
      //   initiator: 'admin',
      //   formSummary: '',
      //   sortType: '',
      //   submitTimeStart: '',
      //   submitTimeEnd: ''
    };
    const res = await getDonePageList(req);
    setData(res?.list);
  };

  const onBack = () => {
    fetchFormData();
  };

  useEffect(() => {
    fetchFormData();
  }, []);
  return (
    <section className="page-content-rgt">
      <div className="table-title-box">
        <b>我已处理</b>
        <TableSearch uiConfig={{ hasInput: true, hasFilter: true, hasSort: true, hasBatch: false }} />
      </div>
      <Table className="task-tb-box created-tb" columns={columns} data={data} />
      {detailPopVisible && <DetailPop detailPopVisible={detailPopVisible} setPopVisible={setPopVisible} />}
    </section>
  );
};

export default IDone;
