import { useAppStore } from '@/store';
import { Button, Input, Pagination, Spin, Tabs } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import { ETL_SCHEDULE_STRATEGY, pageETLFlow, type ETLFlowMgmt } from '@onebase/app';
import { debounce } from 'lodash-es';
import React, { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import ETLFlowCard from './components/card';
import CreateModal from './components/createModal';
import styles from './index.module.less';

const TabPane = Tabs.TabPane;

const EtlDataFactoryPage: React.FC = () => {
  const [searchETLFlowProcessName, setSearchETLFlowProcessName] = useState('');
  const [searchETLFlowType, setSearchETLFlowType] = useState<ETL_SCHEDULE_STRATEGY>(ETL_SCHEDULE_STRATEGY.ALL);

  const navigate = useNavigate();

  const [loading, setLoading] = useState(false);

  const [pageSize, setPageSize] = useState<number>(8);
  const [pageNo, setPageNo] = useState(1);
  const [total, setTotal] = useState(0);
  const [eltFlowList, setEltFlowList] = useState<ETLFlowMgmt[]>([
    // {
    //   id: '1',
    //   applicationId: '1',
    //   flowName: '测试流程',
    //   enableStatus: ETL_FLOW_STATUS.ENABLED,
    //   scheduleStrategy: ETL_SCHEDULE_STRATEGY.FIXED,
    //   lastSuccessTime: '2021-01-01',
    //   sourceTables: ['生产管理系统', '年度计划表'],
    //   targetTable: '生产计划表',
    //   isSyncDone: IS_SYNC_DONE.NO
    // }
  ]);

  const { curAppId } = useAppStore();

  const [createModalVisible, setCreateModalVisible] = useState(false);

  const debouncedSearch = useCallback(
    debounce(() => {
      handleGetETLFlowList();
    }, 500),
    []
  );

  useEffect(() => {
    handleGetETLFlowList();
  }, [pageNo, pageSize]);

  useEffect(() => {
    return () => debouncedSearch.cancel();
  }, [debouncedSearch]);

  const handleGetETLFlowList = async () => {
    setLoading(true);
    const res = await pageETLFlow({
      pageNo,
      pageSize
    });
    console.log('handleGetETLFlowList res: ', res);
    setEltFlowList(res.list);
    setTotal(res.total);
    setLoading(false);
  };

  const handleOpenCreateModal = () => {
    setCreateModalVisible(true);
  };

  const handleCreateETLFlow = () => {
    setCreateModalVisible(false);
  };

  const handleCancelCreateETLFlow = () => {
    setCreateModalVisible(false);
  };

  const handleToEditor = (flowId: string) => {
    navigate(`/onebase/etl_editor?flowId=${flowId}&appId=${curAppId}`);
  };

  return (
    <div className={styles.etlDataFactoryPage}>
      <div className={styles.header}>数据工厂</div>
      <div className={styles.body}>
        <div className={styles.content}>
          <div className={styles.contentHeader}>
            <div className={styles.contentHeaderLeft}>
              <Button type="primary" icon={<IconPlus />} onClick={handleOpenCreateModal}>
                新建流程
              </Button>
            </div>
            <div className={styles.contentHeaderRight}>
              <Tabs
                onChange={(key) => {
                  setSearchETLFlowType(key as ETL_SCHEDULE_STRATEGY);
                }}
              >
                <TabPane key={ETL_SCHEDULE_STRATEGY.ALL} title="全部类型"></TabPane>
                <TabPane key={ETL_SCHEDULE_STRATEGY.FIXED} title="定时更新"></TabPane>
                <TabPane key={ETL_SCHEDULE_STRATEGY.OBSERVE} title="观察更新"></TabPane>
                <TabPane key={ETL_SCHEDULE_STRATEGY.MANUALLY} title="手动更新"></TabPane>
              </Tabs>

              <Input.Search
                allowClear
                placeholder="请输入流程名称"
                style={{ width: 240 }}
                onChange={(value) => {
                  setSearchETLFlowProcessName(value);
                }}
              />
            </div>
          </div>
          <div className={styles.contentBody}>
            <Spin loading={loading} size={40} style={{ width: '100%', height: '100%' }} tip="加载中...">
              <div className={styles.tableContainer}>
                {eltFlowList?.map((item, index) => (
                  <ETLFlowCard
                    key={`elt-flow-${index}`}
                    handleEdit={() => {}}
                    handleDelete={() => {}}
                    toFlowEditor={handleToEditor}
                    data={item}
                  />
                ))}
              </div>
            </Spin>
          </div>
          <div className={styles.contentFooter}>
            <Pagination
              className={styles.myAppPagination}
              total={total}
              current={pageNo}
              pageSize={pageSize}
              onChange={(pNo, pSize) => {
                setPageNo(pNo);
                setPageSize(pSize);
              }}
            />
          </div>
        </div>
      </div>

      <CreateModal visible={createModalVisible} onOk={handleCreateETLFlow} onCancel={handleCancelCreateETLFlow} />
    </div>
  );
};

export default EtlDataFactoryPage;
