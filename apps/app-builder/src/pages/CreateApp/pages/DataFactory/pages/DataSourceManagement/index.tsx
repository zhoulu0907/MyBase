import CreateExternalModal from '@/components/CreateExternalModal';
import { useAppStore } from '@/store';
import { Button, Input, Message, Modal, Pagination, Spin } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import {
  collectETLDatasource,
  deleteETLDatasource,
  getETLDatasource,
  pageETLDatasource,
  type PageDatasourceItem
} from '@onebase/app';
import { debounce } from 'lodash-es';
import React, { useEffect, useRef, useState } from 'react';
import DataSourceCard from './card';
import styles from './index.module.less';
/**
 * 数据源管理页面
 */
const DataSourceManagementPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [searchDatasourceName, setSearchDatasourceName] = useState('');
  const [createExternalModalVisible, setCreateExternalModalVisible] = useState(false);
  const [editInitialData, setEditInitialData] = useState<any>(null);

  const [datasourceList, setDatasourceList] = useState<PageDatasourceItem[]>([]);

  const [pageSize, setPageSize] = useState<number>(8);
  const [pageNo, setPageNo] = useState(1);
  const [total, setTotal] = useState(0);

  const { curAppId } = useAppStore();

  // 使用 ref 存储最新的搜索关键字，避免闭包问题
  const searchDatasourceNameRef = useRef(searchDatasourceName);
  useEffect(() => {
    searchDatasourceNameRef.current = searchDatasourceName;
  }, [searchDatasourceName]);

  const handleGetDatasourceList = async () => {
    setLoading(true);
    const currentSearchName = searchDatasourceNameRef.current;
    const res = await pageETLDatasource({
      applicationId: curAppId,
      pageNo,
      pageSize,
      datasourceName: currentSearchName || undefined
    });

    setDatasourceList(res.list);
    setTotal(res.total);
    setLoading(false);
  };

  const handleCollectDatasource = async (datasourceId: string) => {
    const res = await collectETLDatasource(datasourceId);
    if (res) {
      Message.success('采集任务已提交，请稍后刷新查看采集结果');
      handleGetDatasourceList();
    }
  };

  const debouncedSearchRef = useRef(
    debounce(() => {
      setPageNo(1); // 搜索时重置到第一页
      handleGetDatasourceList();
    }, 500)
  );

  useEffect(() => {
    handleGetDatasourceList();
  }, [pageNo, pageSize]);

  useEffect(() => {
    debouncedSearchRef.current();
    return () => {
      debouncedSearchRef.current.cancel();
    };
  }, [searchDatasourceName]);

  useEffect(() => {
    return () => {
      debouncedSearchRef.current.cancel();
    };
  }, []);

  const handleCreateDatasource = () => {
    setEditInitialData(null);
    setCreateExternalModalVisible(true);
  };

  const handleCreateExternalModalClose = () => {
    setCreateExternalModalVisible(false);
    setEditInitialData(null);
  };

  const handleEditDatasource = async (datasourceId: string) => {
    const res = await getETLDatasource(datasourceId);
    setEditInitialData(res);
    setCreateExternalModalVisible(true);
  };

  const handleDeleteDatasource = async (datasourceId: string) => {
    Modal.confirm({
      title: '确认删除',
      content: '确定要删除吗？删除后无法恢复。',
      onOk: async () => {
        const res = await deleteETLDatasource(datasourceId);
        if (res) {
          Message.success('删除成功');
          handleGetDatasourceList();
        }
      }
    });
  };

  const handleCreateExternalModalCreate = (datasourceUuid: string) => {
    setCreateExternalModalVisible(false);
    setEditInitialData(null);
    // 刷新列表
    handleGetDatasourceList();
  };

  return (
    <div className={styles.dataSourceManagementPage}>
      <div className={styles.header}>输入源</div>
      <div className={styles.body}>
        <div className={styles.content}>
          <div className={styles.contentHeader}>
            <div className={styles.contentHeaderLeft}>
              <Button type="primary" icon={<IconPlus />} onClick={handleCreateDatasource}>
                新建输入源
              </Button>
            </div>
            <div className={styles.contentHeaderRight}>
              <Input.Search
                allowClear
                placeholder="请输入数据源名称"
                style={{ width: 240 }}
                onChange={(value) => {
                  setSearchDatasourceName(value);
                }}
              />
            </div>
          </div>
          <div className={styles.contentBody}>
            <Spin loading={loading} size={40} style={{ width: '100%', height: '100%' }} tip="加载中...">
              <div className={styles.tableContainer}>
                {datasourceList?.map((item, index) => (
                  <DataSourceCard
                    key={`datasource-${index}`}
                    handleEdit={() => {
                      handleEditDatasource(item.id);
                    }}
                    handleDelete={handleDeleteDatasource}
                    handlePage={handleGetDatasourceList}
                    handleCollect={handleCollectDatasource}
                    data={item}
                  />
                ))}
              </div>
            </Spin>
          </div>

          <div className={styles.contentFooter}>
            <Pagination
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

      <CreateExternalModal
        visible={createExternalModalVisible}
        onClose={handleCreateExternalModalClose}
        onCreate={handleCreateExternalModalCreate}
        initialData={editInitialData}
      />
    </div>
  );
};

export default DataSourceManagementPage;
