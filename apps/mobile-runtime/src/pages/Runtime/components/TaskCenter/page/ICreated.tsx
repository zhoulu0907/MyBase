import { useState, useEffect,type FC } from 'react';
import { Table, type TableColumnProps, Button, Tag, Space, Radio } from '@arco-design/web-react';
import { getMyCreatePageList } from '@onebase/app/src/services/app_runtime';
import { LISTTYPE, FLOWSTATUS_TYPE, FlowStatusMap } from '@onebase/app';
import dayjs from 'dayjs';
import TableSearch from './TableSearch';
import DetailPop from './DetailPop'
import '../style/tcPage.less'

const radioList = [
    {label: '全部', value: 'all'},
    {label: '草稿', value: '1'},
    {label: '审批中', value: '2'},
    {label: '已通过', value: '3'},
    {label: '已拒绝', value: '4'},
    {label: '已撤回', value: '5'},
    {label: '已终止', value: '6'}
]
let defaultCheck = 'all'

const ICreated: FC = ({ appId }) => {
  const columns: TableColumnProps[] = [
    {
      title: '流程标题',
      dataIndex: 'processTitle'
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
      title: '当前节点处理人',
      dataIndex: 'currentNodeHandler',
       render: (val, record) => {
        const userNames = val?.map((item:any) => item.userName) || [];
        return (
            <span className="flex-bw-center">
              {userNames.length > 0 ? (
                <>
                  <img src="/src/assets/images/avatar.svg" />
                  {userNames.join('、')}
                </>
              ) : (
                '-'
              )}
            </span>
        );
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

  let [detailPopVisible, setPopVisible] = useState(false);
  const [data, setData] = useState<any>();
  const [rowData, setRowData] = useState();

  function CreatedRadioChange(val: string) {
    console.log('radio ====', val);
  }

  function handleDetailPage(row: any) {
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
    const res = await getMyCreatePageList(req);
    setData(res?.list);
  };

  const onBack = () => {
    setPopVisible(false);
    fetchFormData();
  };

  useEffect(() => {
    fetchFormData();
  }, []);

  return (
    <section className="page-content-rgt">
      <div className="table-title-box">
        <div>
          <b style={{ marginRight: '8px' }}>我创建的</b>
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
        <TableSearch uiConfig={{ hasInput: true, hasFilter: true, hasSort: true, hasBatch: false }} />
      </div>
      <Table className="task-tb-box" columns={columns} data={data} />
      {detailPopVisible && (
        <DetailPop
          detailPopVisible={detailPopVisible}
          setPopVisible={setPopVisible}
          rowData={rowData}
          listType={LISTTYPE.ICREATED}
        />
      )}
    </section>
  );
};

export default ICreated;