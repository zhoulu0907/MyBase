import { Button, Input, Pagination, Spin } from '@arco-design/web-react';
import { IconPlus } from '@douyinfe/semi-icons';
import { debounce } from 'lodash-es';
import React, { useCallback, useEffect, useState } from 'react';
import DataSourceCard from './card';
import styles from './index.module.less';
/**
 * 数据源管理页面
 */
const DataSourceManagementPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [searchDatasourceName, setSearchDatasourceName] = useState('');

  const [datasourceList, setDatasourceList] = useState<any[]>([]);

  const [pageSize, setPageSize] = useState<number>(8);
  const [pageNo, setPageNo] = useState(1);
  const [total, setTotal] = useState(0);

  const debouncedSearch = useCallback(
    debounce(() => {
      handleGetDatasourceList();
    }, 500),
    []
  );

  useEffect(() => {
    handleGetDatasourceList();
  }, [pageNo, pageSize]);

  const handleGetDatasourceList = async () => {
    setLoading(true);
    // const res = await pageETLFlow({
    //   applicationId: curAppId,
    //   pageNo,
    //   pageSize
    // });
    // setDatasourceList(res.list);
    // setTotal(res.total);
    setLoading(false);
  };

  const handleCreateDatasource = () => {};

  const handleEditDatasource = (datasourceId: string) => {
    console.log('handleEditDatasource', datasourceId);
  };

  const handleDeleteDatasource = (datasourceId: string) => {
    console.log('handleDeleteDatasource', datasourceId);
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
    </div>
  );
};

export default DataSourceManagementPage;
