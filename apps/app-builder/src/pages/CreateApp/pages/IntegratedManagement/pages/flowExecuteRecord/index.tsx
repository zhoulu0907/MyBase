import React, { useEffect, useState } from 'react';
import { Space, Grid, Button, Table, Switch, type TableColumnProps } from '@arco-design/web-react';
import { IconDownload, IconRefresh, IconArrowDown, IconArrowUp, IconMoreVertical } from '@arco-design/web-react/icon';
import styles from './index.module.less';

const FlowExecuteRecordPage: React.FC = () => {
  const [cardList, setCardList] = useState<any[]>([]);
  const [tableData, setTableData] = useState<any[]>([]);
  // 分页器
  const [pagination, setPagination] = useState({
    showTotal: true,
    total: 10,
    pageSize: 10,
    current: 1
  });
  // 认证记录table结构
  const columns: TableColumnProps[] = [
    {
      title: '流程名称',
      dataIndex: 'flowName',
      key: 'flowName'
    },
    {
      title: '触发类型',
      dataIndex: 'triggerType',
      key: 'triggerType'
    },
    {
      title: '交互方式',
      dataIndex: 'interactiveMethod',
      key: 'interactiveMethod'
    },
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id'
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime'
    },
    {
      title: '最后执行',
      dataIndex: 'lastExecuteTime',
      key: 'lastExecuteTime'
    },
    {
      title: '执行次数',
      dataIndex: 'frequency',
      key: 'frequency'
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (text, record) => <Switch checked={record.status} checkedText="已启用" uncheckedText="禁用" />
    },
    {
      title: '操作',
      dataIndex: 'operation',
      key: 'operation',
      width: 120,
      render: (_, record) => (
        <Space>
          <div style={{ color: 'rgb(var(--primary-6))' }}>编辑</div>
          <div style={{ color: 'rgb(var(--primary-6))' }}>调试</div>
          <IconMoreVertical />
        </Space>
      )
    }
  ];

  useEffect(() => {
    getData();
  }, []);

  // 初始化获取数据
  const getData = () => {
    // todo 接口查询数据
    // mock数据
    const newCardList = [
      { name: '今日执行次数', frequency: 44, type: 'rise', value: '12.8%', describe: '较昨日' },
      { name: '执行成功', frequency: 22, type: 'rise', value: '12.8%', describe: '较昨日' },
      { name: '执行失败', frequency: 22, type: 'rise', value: '12.8%', describe: '较昨日' },
      { name: '平均执行时间', frequency: 12, unit: 's', type: 'rise', value: '12.8%', describe: '较昨日' }
    ];
    setCardList(newCardList);
    // mock数据
    const newTabeleData = [
      {
        flowName: '这是一个流程名称',
        triggerType: '表单触发',
        interactiveMethod: '交互流执行',
        id: 'flowid-wudh-001',
        createTime: '2025-10-10 12:23:12',
        lastExecuteTime: '10天前',
        frequency: 2,
        status: true
      },
      {
        flowName: '这是一个流程名称',
        triggerType: '界面触发',
        interactiveMethod: '交互流执行',
        id: 'flowid-wudh-002',
        createTime: '2025-10-10 12:23:12',
        lastExecuteTime: '10天前',
        frequency: 2,
        status: true
      },
      {
        flowName: '这是一个流程名称',
        triggerType: '定时触发',
        interactiveMethod: '后台执行',
        id: 'flowid-wudh-003',
        createTime: '2025-10-10 12:23:12',
        lastExecuteTime: '10天前',
        frequency: 2,
        status: true
      },
      {
        flowName: '这是一个流程名称',
        triggerType: 'API触发',
        interactiveMethod: '交互流执行',
        id: 'flowid-wudh-004',
        createTime: '2025-10-10 12:23:12',
        lastExecuteTime: '10天前',
        frequency: 2,
        status: true
      },
      {
        flowName: '这是一个流程名称',
        triggerType: '界面触发',
        interactiveMethod: '交互流执行',
        id: 'flowid-wudh-005',
        createTime: '2025-10-10 12:23:12',
        lastExecuteTime: '10天前',
        frequency: 2,
        status: true
      },
      {
        flowName: '这是一个流程名称',
        triggerType: '定时触发',
        interactiveMethod: '后台执行',
        id: 'flowid-wudh-006',
        createTime: '2025-10-10 12:23:12',
        lastExecuteTime: '10天前',
        frequency: 2,
        status: true
      },
      {
        flowName: '这是一个流程名称',
        triggerType: '界面触发',
        interactiveMethod: '交互流执行',
        id: 'flowid-wudh-001',
        createTime: '2025-10-10 12:23:12',
        lastExecuteTime: '10天前',
        frequency: 2,
        status: true
      },
      {
        flowName: '这是一个流程名称',
        triggerType: 'API触发',
        interactiveMethod: '交互流执行',
        id: 'flowid-wudh-004',
        createTime: '2025-10-10 12:23:12',
        lastExecuteTime: '10天前',
        frequency: 2,
        status: true
      },
      {
        flowName: '这是一个流程名称',
        triggerType: '界面触发',
        interactiveMethod: '交互流执行',
        id: 'flowid-wudh-001',
        createTime: '2025-10-10 12:23:12',
        lastExecuteTime: '10天前',
        frequency: 2,
        status: true
      },
      {
        flowName: '这是一个流程名称',
        triggerType: '表单触发',
        interactiveMethod: '交互流执行',
        id: 'flowid-wudh-002',
        createTime: '2025-10-10 12:23:12',
        lastExecuteTime: '10天前',
        frequency: 2,
        status: true
      }
    ];
    setTableData(newTabeleData);
  };

  // 导出记录
  const exportRecords = () => {};
  //  刷新
  const updateRecords = () => {};

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
          <Table columns={columns} data={tableData} pagination={pagination}></Table>
        </div>
      </div>
    </div>
  );
};

export default FlowExecuteRecordPage;
