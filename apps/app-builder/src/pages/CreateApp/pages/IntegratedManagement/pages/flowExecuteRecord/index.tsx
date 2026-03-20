import React, { useEffect, useState } from 'react';
import { Space, Grid, Button, Table, Tag, type TableColumnProps } from '@arco-design/web-react';
import { IconDownload, IconRefresh, IconArrowDown, IconArrowUp } from '@arco-design/web-react/icon';
import TablePagination from '@/components/TablePagination';
import styles from './index.module.less';
import { useAppStore } from '@/store';
import { getFlowLogDetail, getFlowLogPage, getFlowLogStatistic } from '@onebase/app';
import { getHashQueryParam, formatTimeYMDHMS } from '@onebase/common';

interface ExecuteRecord {
  processName: string;
  executionUuid: string;
  id: string;
  executionResult: string;
  startTime: number;
  endTime: number;
  processId: string;
  executionTime: string;
}

const FlowExecuteRecordPage: React.FC = () => {
  const { curAppId } = useAppStore();

  const [cardList, setCardList] = useState<any[]>([
    { name: '今日执行次数', frequency: 0, type: 'rise', value: '0.00', describe: '较昨日' },
    { name: '执行成功', frequency: 0, type: 'rise', value: '0.00', describe: '较昨日' },
    { name: '执行失败', frequency: 0, type: 'rise', value: '0.00', describe: '较昨日' },
    { name: '平均执行时间', frequency: 0, unit: 's', type: 'rise', value: '0.00', describe: '较昨日' }
  ]);
  const [tableData, setTableData] = useState<ExecuteRecord[]>([]);
  const [tableLoading, setTableLoading] = useState<boolean>(false);
  // 分页器
  const [pagination, setPagination] = useState({
    total: 10,
    current: 1,
    pageSize: 10
  });
  // 执行记录 table
  const columns: TableColumnProps[] = [
    {
      title: '流程名称',
      dataIndex: 'processName',
      key: 'processName'
    },
    {
      title: '执行id',
      dataIndex: 'executionUuid',
      key: 'executionUuid'
    },
    {
      title: '状态',
      dataIndex: 'executionResult',
      key: 'executionResult',
      render: (text) => <Tag color={text === 'success' ? 'green' : 'red'}>{text === 'success' ? '成功' : '失败'}</Tag>
    },
    {
      title: '开始时间',
      dataIndex: 'startTime',
      key: 'startTime',
      render: (text: string) => <div>{formatTimeYMDHMS(text)}</div>
    },
    {
      title: '结束时间',
      dataIndex: 'endTime',
      key: 'endTime',
      render: (text: string) => <div>{formatTimeYMDHMS(text)}</div>
    },
    {
      title: '耗时',
      dataIndex: 'executionTime',
      key: 'executionTime',
      render: (text: string) => <div>{text ? text + 's' : ''}</div>
    },
    {
      title: '操作',
      dataIndex: 'operation',
      key: 'operation',
      width: 100,
      render: (_, record) => (
        <div
          style={{ color: 'rgb(var(--primary-6))', cursor: 'pointer' }}
          onClick={() => {
            openDetailDialog(record);
          }}
        >
          详情
        </div>
      )
    }
  ];

  // todo 打开详情弹窗
  const openDetailDialog = async (record: ExecuteRecord) => {
    const param = {
      id: record.id
    };
    const res = await getFlowLogDetail(param);
    console.log(res);
  };

  useEffect(() => {
    getData(pagination);
  }, []);

  // 初始化获取数据
  const getData = async (paginationConfig: { current: number; pageSize: number; total?: number }) => {
    const appId = curAppId || getHashQueryParam('appId');
    const statisticParam = {
      applicationId: appId
    };
    const statisticRes = await getFlowLogStatistic(statisticParam);
    const compareTotal = (statisticRes?.compareTotal || '0.00').replace('-', '');
    const compareSuccess = (statisticRes?.compareSuccess || '0.00').replace('-', '');
    const compareFailed = (statisticRes?.compareFailed || '0.00').replace('-', '');
    const compareAvgs = (statisticRes?.compareAvgs || '0.00').replace('-', '');

    const newCardList = [
      {
        name: '今日执行次数',
        frequency: statisticRes?.total || 0,
        type: Number(statisticRes?.compareTotal || '0.00') >= 0 ? 'rise' : 'decline',
        value: compareTotal,
        describe: '较昨日'
      },
      {
        name: '执行成功',
        frequency: statisticRes?.success || 0,
        type: Number(statisticRes?.compareSuccess || '0.00') >= 0 ? 'rise' : 'decline',
        value: compareSuccess,
        describe: '较昨日'
      },
      {
        name: '执行失败',
        frequency: statisticRes?.failed || 0,
        type: Number(statisticRes?.compareFailed || '0.00') >= 0 ? 'rise' : 'decline',
        value: compareFailed,
        describe: '较昨日'
      },
      {
        name: '平均执行时间',
        frequency: statisticRes?.avgs || 0,
        unit: 's',
        type: Number(statisticRes?.compareAvgs || '0.00') >= 0 ? 'rise' : 'decline',
        value: compareAvgs,
        describe: '较昨日'
      }
    ];
    setCardList(newCardList);

    const { current, pageSize } = paginationConfig;
    const tableParam = {
      pageNo: current,
      pageSize,
      processId: '',
      applicationId: appId
    };
    setTableLoading(true);
    const tableRes = await getFlowLogPage(tableParam);
    setPagination((prev) => ({ ...prev, total: tableRes.total || 0 }));
    setTableData(tableRes.list);
    setTableLoading(false);
  };

  // 导出记录
  const exportRecords = () => {};
  //  刷新
  const updateRecords = () => {
    const paginationConfig = {
      total: 10,
      current: 1,
      pageSize: 10
    };
    setPagination(paginationConfig);
    getData(paginationConfig);
  };

  return (
    <div className={styles.flowExecuteRecordPage}>
      <div className={styles.content}>
        <div className={styles.header}>
          <div className={styles.headerContent}>
            <div className={styles.headerContentTitle}>执行记录</div>
            <div className={styles.headerContentDescribe}>查看所有自动化流程的执行历史和详细记录</div>
          </div>
          <Space>
            <Button type="secondary" onClick={exportRecords} icon={<IconDownload style={{ fontSize: '16px' }} />}>
              导出记录
            </Button>
            <Button type="secondary" onClick={updateRecords} icon={<IconRefresh style={{ fontSize: '16px' }} />}>
              刷新
            </Button>
          </Space>
        </div>
        <div className={styles.card}>
          <Grid.Row gutter={24}>
            {cardList.map((item, index) => (
              <Grid.Col span={6} key={index}>
                <div className={styles.cardItem}>
                  <div className={styles.cardItemImage}></div>
                  <div className={styles.cardItemContent}>
                    <div className={styles.cardItemContentName}>{item.name}</div>
                    <div className={styles.cardItemContentFrequ}>
                      {item.frequency}
                      {item.unit || ''}
                    </div>
                    <div className={styles.cardItemContentValue}>
                      {item.type === 'rise' ? (
                        <IconArrowUp style={{ color: 'rgb(var(--primary-6))' }} />
                      ) : (
                        <IconArrowDown style={{ color: '#F53F3F' }} />
                      )}
                      <span
                        style={{ padding: '0 4px', color: item.type === 'rise' ? 'rgb(var(--primary-6))' : '#F53F3F' }}
                      >
                        {item.value}%
                      </span>
                      <span>{item.describe}</span>
                    </div>
                  </div>
                </div>
              </Grid.Col>
            ))}
          </Grid.Row>
        </div>
        <div className={styles.table}>
          <Table
            columns={columns}
            data={tableData}
            loading={tableLoading}
            pagination={false}
            border={false}
            rowKey="id"
          ></Table>
          <TablePagination
            total={pagination.total}
            current={pagination.current}
            pageSize={pagination.pageSize}
            onChange={(pNo) => {
              const newPagination = { ...pagination, current: pNo };
              setPagination(newPagination);
              getData(newPagination);
            }}
            onPageSizeChange={(pSize) => {
              const newPagination = { ...pagination, current: 1, pageSize: pSize };
              setPagination(newPagination);
              getData(newPagination);
            }}
          />
        </div>
      </div>
    </div>
  );
};

export default FlowExecuteRecordPage;
