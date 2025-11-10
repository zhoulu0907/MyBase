import { useState, useEffect, type FC } from 'react';
import { Table, type TableColumnProps, Button, Link } from '@arco-design/web-react';
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
      dataIndex: 'processTitle'
    },
    {
      title: '发起人',
      dataIndex: 'initiator',
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
      render: (_:any, record:any) => (
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
  let [detailPopVisible, setPopVisible] = useState(false);
  const [data, setData] = useState<any>();
  const [rowData, setRowData] = useState();
  function handleDetailPage(row: any) {
    console.log('click to detail page === row ===', row);
    setRowData(row);
    setPopVisible(true);
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
    const res = await getDonePageList(req);
    if (Array.isArray(res?.list)) {
      setData(res.list.map((item: object, i: number) => {
        return {
          ...(item || {}),
          key: i
        }
      }));
    }
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
      {detailPopVisible && (
        <DetailPop
          detailPopVisible={detailPopVisible}
          setPopVisible={setPopVisible}
          listType={LISTTYPE.IDONE}
          rowData={rowData}
        />
      )}
    </section>
  );
};

export default IDone;
