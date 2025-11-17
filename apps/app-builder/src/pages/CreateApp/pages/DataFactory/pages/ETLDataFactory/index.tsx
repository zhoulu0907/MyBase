import { useAppStore } from '@/store';
import { Button, Input, Message, Pagination, Spin, Tabs } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import {
  deleteETLFlow,
  ETL_SCHEDULE_STRATEGY,
  getETLFlowScheduleInfo,
  pageETLFlow,
  updateETLFlowScheduleInfo,
  type ETLFlowMgmt,
  type UpdateWorkflowScheduleInfoReq
} from '@onebase/app';
import { debounce } from 'lodash-es';
import React, { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import ETLFlowCard from './components/card';
import EditModal from './components/editModal';
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
  const [eltFlowList, setEltFlowList] = useState<ETLFlowMgmt[]>([]);

  const { curAppId } = useAppStore();

  const [editModalVisible, setEditModalVisible] = useState(false);
  const [curFlowStrategyInfo, setCurFlowStrategyInfo] = useState<any>(null);

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
      applicationId: curAppId,
      pageNo,
      pageSize
    });
    setEltFlowList(res.list);
    setTotal(res.total);
    setLoading(false);
  };

  const handleCreateFlow = () => {
    handleToEditor();
  };

  const handleEditFlow = async (flowId: string) => {
    const res = await getETLFlowScheduleInfo(flowId);
    setCurFlowStrategyInfo(res);
    setEditModalVisible(true);
  };

  const handleUpdateETLFlow = async (req: UpdateWorkflowScheduleInfoReq) => {
    const res = await updateETLFlowScheduleInfo(req);
    if (res) {
      Message.success('更新成功');
    }

    setEditModalVisible(false);
    handleGetETLFlowList();
  };

  const handleCancelUpdateETLFlow = () => {
    setEditModalVisible(false);
  };

  const handleToEditor = (flowId?: string) => {
    if (flowId) {
      navigate(`/onebase/etl_editor?flowId=${flowId}&appId=${curAppId}`);
      return;
    }
    navigate(`/onebase/etl_editor?appId=${curAppId}`);
  };

  const handleDeleteFlow = async (flowId: string) => {
    const res = await deleteETLFlow(flowId);
    console.log('handleDeleteFlow res: ', res);
    if (res) {
      Message.success('删除成功');
      handleGetETLFlowList();
    }
  };

  return (
    <div className={styles.etlDataFactoryPage}>
      <div className={styles.header}>数据工厂</div>
      <div className={styles.body}>
        <div className={styles.content}>
          <div className={styles.contentHeader}>
            <div className={styles.contentHeaderLeft}>
              <Button type="primary" icon={<IconPlus />} onClick={handleCreateFlow}>
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
                    handleEdit={() => {
                      handleEditFlow(item.id);
                    }}
                    handleDelete={handleDeleteFlow}
                    handlePage={handleGetETLFlowList}
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

      <EditModal
        initData={curFlowStrategyInfo || {}}
        visible={editModalVisible}
        onOk={handleUpdateETLFlow}
        onCancel={handleCancelUpdateETLFlow}
      />
    </div>
  );
};

export default EtlDataFactoryPage;
