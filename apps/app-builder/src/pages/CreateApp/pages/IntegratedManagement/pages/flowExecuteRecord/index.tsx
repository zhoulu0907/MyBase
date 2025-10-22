import React, { useEffect, useState } from 'react';
import { Space, Grid, Button, Table, Tag, type TableColumnProps, type PaginationProps } from '@arco-design/web-react';
import { IconDownload, IconRefresh, IconArrowDown, IconArrowUp, IconMoreVertical } from '@arco-design/web-react/icon';
import styles from './index.module.less';
import { useAppStore } from '@/store';
import { getFlowLogDetail, getFlowLogPage } from '@onebase/app';
import { getHashQueryParam, formatTimeYMDHMS } from '@onebase/common';

const FlowExecuteRecordPage: React.FC = () => {
  const { curAppId } = useAppStore();

  const [cardList, setCardList] = useState<any[]>([]);
  const [tableData, setTableData] = useState<any[]>([]);
  // 分页器
  const [pagination, setPagination] = useState<PaginationProps>({
    total: 10,
    current: 1,
    pageSize: 10,
    showTotal: true,
    sizeCanChange: true,
    pageSizeChangeResetCurrent: true
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
      render: (text) => (
        <Tag color={text === 'success' ? 'green' : 'red'}>{text === 'success' ? '成功' : '失败'}</Tag>
      )
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
      dataIndex: 'frequency',
      key: 'frequency'
    },
    {
      title: '操作',
      dataIndex: 'operation',
      key: 'operation',
      width: 100,
      render: (_, record) => <div style={{ color: 'rgb(var(--primary-6))' }}>详情</div>
    }
  ];

  useEffect(() => {
    getData(pagination);
  }, []);

  // 初始化获取数据
  const getData = async (pagination: PaginationProps) => {
    const appId = curAppId || getHashQueryParam('appId');
    // todo 接口查询数据
    const { current, pageSize } = pagination;
    const newCardList = [
      { name: '今日执行次数', frequency: 44, type: 'rise', value: '12.8%', describe: '较昨日' },
      { name: '执行成功', frequency: 22, type: 'rise', value: '12.8%', describe: '较昨日' },
      { name: '执行失败', frequency: 22, type: 'rise', value: '12.8%', describe: '较昨日' },
      { name: '平均执行时间', frequency: 12, unit: 's', type: 'rise', value: '12.8%', describe: '较昨日' }
    ];
    setCardList(newCardList);

    const tableParam = {
      pageNo: current,
      pageSize,
      processId: '',
      appId
    };

    const tableRes = await getFlowLogPage(tableParam);
    setPagination((prev) => ({ ...prev, total: tableRes.total || 0 }));
    setTableData(tableRes.list);
  };

  // 导出记录
  const exportRecords = () => {};
  //  刷新
  const updateRecords = () => {};

  // 分页改变时的回调
  const onChangeTable = (pagination: PaginationProps) => {
    const { current, pageSize } = pagination;
    getData(pagination);
    setPagination((prev) => ({ ...prev, current, pageSize }));
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
                        <IconArrowUp style={{ color: '#24B28F' }} />
                      ) : (
                        <IconArrowDown style={{ color: '#F53F3F' }} />
                      )}
                      <span style={{ padding: '0 4px', color: '#24B28F' }}>{item.value}</span>
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
            pagination={pagination}
            onChange={onChangeTable}
            border={false}
            rowKey="id"
          ></Table>
        </div>
      </div>
    </div>
  );
};

export default FlowExecuteRecordPage;
