import TablePagination from '@/components/TablePagination';
import DeleteConfirmModal from '@/components/DeleteConfirmModal';
import { useAppStore } from '@/store';
import { Button, Input, Message, Spin, Tabs } from '@arco-design/web-react';
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
import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import ETLFlowCard from './components/card';
import EditModal from './components/editModal';
import styles from './index.module.less';

const TabPane = Tabs.TabPane;

const EtlDataFactoryPage: React.FC = () => {
  const [searchETLFlowProcessName, setSearchETLFlowProcessName] = useState('');
  const [searchETLFlowType, setSearchETLFlowType] = useState<ETL_SCHEDULE_STRATEGY>(ETL_SCHEDULE_STRATEGY.ALL);

  const navigate = useNavigate();
  const { tenantId } = useParams();

  const [loading, setLoading] = useState(false);

  const [pageSize, setPageSize] = useState<number>(9);
  const [pageNo, setPageNo] = useState(1);
  const [total, setTotal] = useState(0);
  const [eltFlowList, setEltFlowList] = useState<ETLFlowMgmt[]>([]);

  const { curAppId } = useAppStore();

  const [editModalVisible, setEditModalVisible] = useState(false);
  const [curFlowStrategyInfo, setCurFlowStrategyInfo] = useState<any>(null);
  const [deleteModalVisible, setDeleteModalVisible] = useState(false);
  const [deleteTargetId, setDeleteTargetId] = useState<string>('');

  useEffect(() => {
    handleGetETLFlowList();
  }, [pageNo, pageSize, searchETLFlowProcessName, searchETLFlowType]);

  const handleGetETLFlowList = async () => {
    setLoading(true);
    const res = await pageETLFlow({
      applicationId: curAppId,
      pageNo,
      pageSize,
      flowName: searchETLFlowProcessName || undefined,
      scheduleStrategy: searchETLFlowType === ETL_SCHEDULE_STRATEGY.ALL ? undefined : searchETLFlowType
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
      navigate(`/onebase/${tenantId}/etl_editor?flowId=${flowId}&appId=${curAppId}`);
      return;
    }
    navigate(`/onebase/${tenantId}/etl_editor?appId=${curAppId}`);
  };

  const handleDeleteFlow = (flowId: string) => {
    setDeleteTargetId(flowId);
    setDeleteModalVisible(true);
  };

  const handleDeleteConfirm = async () => {
    const res = await deleteETLFlow(deleteTargetId);
    if (res) {
      Message.success('删除成功');
      setDeleteModalVisible(false);
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
                  setPageNo(1);
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
                  setPageNo(1);
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
            <TablePagination
              className={styles.myAppPagination}
              total={total}
              current={pageNo}
              pageSize={pageSize}
              onChange={(pNo) => {
                setPageNo(pNo);
              }}
              onPageSizeChange={(pSize) => {
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
      <DeleteConfirmModal
        visible={deleteModalVisible}
        onVisibleChange={setDeleteModalVisible}
        onConfirm={handleDeleteConfirm}
      />
    </div>
  );
};

export default EtlDataFactoryPage;
