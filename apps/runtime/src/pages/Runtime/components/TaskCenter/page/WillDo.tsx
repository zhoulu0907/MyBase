import { useState, type FC, useEffect } from 'react';
import { Table, type TableColumnProps, Button, Tag, Link } from '@arco-design/web-react';
import TableSearch from './TableSearch';
import DetailPop from './DetailPop';
import BatchApproveModal from '../modal/batchApprove';
import { getTodoPageList } from '@onebase/app/src/services/app_runtime';
// import { getTodoPageList } from '../../../../../../../../packages/app/src/services/app_runtime';
import dayjs from 'dayjs';
import '../style/tcPage.less';

const WillDo: FC = () => {
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
      title: '当前节点状态',
      dataIndex: 'address',
      render: (val, record) => (
        <Tag color="blue" size="medium">
          {val}
        </Tag>
      )
    },
    {
      title: '表单摘要',
      dataIndex: 'formSummary'
    },
    {
      title: '到达时间',
      dataIndex: 'arrivalTime',
      render: (value: number) => {
        return dayjs(value).format('YYYY-MM-DD HH:mm:ss');
      }
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
  const mockData = [
    {
      key: '1',
      name: 'Jane Doe',
      salary: 23000,
      address: '32 Park Road, London',
      email: '3jane.doe@example.com',
      email1: 'e@example.com',
      email2: 'ample.com'
    },
    {
      key: '2',
      name: 'Alisa Ross',
      salary: 25000,
      address: '35 Park Road, London',
      email: '6alisa.ross@example.com',
      email1: '12e@example.com',
      email2: '3333ample.com'
    },
    {
      key: '3',
      name: 'Kevin Sandra',
      salary: 22000,
      address: '31 Park Road, London',
      email: '1kevin.sandra@example.com',
      email1: 'aaae@example.com',
      email2: 'bbbample.com'
    }
  ];
  let [tbRowSelection, setTbRowSelection] = useState<any>();
  const [selectedRowKeys, setSelectedRowKeys] = useState<any>();
  const [data, setData] = useState<any>();
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
    setPopVisible(true);
  }

  const fetchFormData = async () => {
    // todo：模拟参数
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
    const res = await getTodoPageList(req);
    console.log(res, '==========');
    setData(res?.list);

    // setData(mockData)
  };

  const onBack=()=>{
      setPopVisible(false);
      fetchFormData();
  
  }

  useEffect(() => {
    fetchFormData();
  }, []);

  return (
    <section className="page-content-rgt">
      <div className="table-title-box">
        <b>待我处理</b>
        <TableSearch
          uiConfig={{ hasInput: true, hasFilter: true, hasSort: true, hasBatch: true }}
          batchEvent={handleBatchClick}
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
      <Table className="task-tb-box" rowKey="name" rowSelection={tbRowSelection} columns={columns} data={data} />
      {detailPopVisible && (
        <DetailPop detailPopVisible={detailPopVisible} setPopVisible={setPopVisible} onBack={onBack} />
      )}
      {approveVisible && <BatchApproveModal approveVisible={approveVisible} setApproveVisible={setApproveVisible} />}
    </section>
  );
};

export default WillDo;
