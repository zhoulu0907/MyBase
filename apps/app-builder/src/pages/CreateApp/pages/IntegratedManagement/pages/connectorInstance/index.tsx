import { Button, Input, Pagination, Spin } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import { createConnectInstance, listConnectInstance, type ListConnectInstanceReq } from '@onebase/app';
import { getCommonPaginationList, getHashQueryParam } from '@onebase/common';
import { debounce } from 'lodash-es';
import React, { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import ConnectNodeCategoryCard from '../../components/ConnectNodeCategoryCard';
import styles from './index.module.less';

/**
 * 流程管理页面
 * 目前集成触发器编辑器作为主内容
 */
const ConnectorInstancesPage: React.FC = () => {
  const navigate = useNavigate();

  const [loading, setLoading] = useState(false);
  const [searchInstanceName, setSearchInstanceName] = useState('');
  const [searchLevel1Code, setSearchLevel1Code] = useState('');

  const [pageSize, setPageSize] = useState<number>(8);
  const [pageNo, setPageNo] = useState(1);

  // 连接器实例列表
  const [instanceList, setInstanceList] = useState<any[]>();
  const [total, setTotal] = useState(0);

  useEffect(() => {
    pageSize && getConnectInstanceList(searchInstanceName, searchLevel1Code);
  }, [pageNo, pageSize, searchInstanceName, searchLevel1Code]);

  const debouncedSearch = useCallback(
    debounce((typeName: string, level1Code: string) => {
      getConnectInstanceList(typeName, level1Code);
    }, 500),
    []
  );

  useEffect(() => {
    return () => debouncedSearch.cancel();
  }, [debouncedSearch]);

  const getConnectInstanceList = async (typeName?: string, level1Code?: string) => {
    setLoading(true);
    const curAppId = getHashQueryParam('appId');
    const req: ListConnectInstanceReq = {
      applicationId: curAppId || '',
      pageNo: pageNo,
      pageSize: pageSize || 8,
      connectorName: typeName || '',
      level1Code: level1Code || ''
    };
    const res = await getCommonPaginationList(
      (param) => listConnectInstance(param as ListConnectInstanceReq),
      req,
      setPageNo
    );
    console.log('res :', res);
    if (res) {
      setInstanceList(res.list || []);
      setTotal(res.total || 0);
      setLoading(false);
    }
  };

  const handleCreateInstance = async () => {
    const curAppId = getHashQueryParam('appId');
    const res = await createConnectInstance({
      applicationId: curAppId || '',
      connectionName: '连接器实例',
      description: '',
      typeCode: ''
    });

    console.log('res :', res);
    // navigate(`/onebase/create-app/integrated-management/connector-instance-detail?appId=${curAppId}&id=${res.id}`);
  };

  return (
    <div className={styles.connectorInstancesPage}>
      <div className={styles.header}>
        <div className={styles.title}>连接器实例</div>
      </div>
      <div className={styles.searchContainer}>
        <Button
          type="primary"
          icon={<IconPlus />}
          onClick={() => {
            // form.resetFields();
            // setModalVisible('create');
          }}
        >
          创建实例
        </Button>

        <Input.Search
          allowClear
          placeholder="请输入连接器实例名称"
          style={{ width: 240 }}
          onChange={(value) => {
            setSearchInstanceName(value);
          }}
        />
      </div>

      <div className={styles.body}>
        <div className={styles.content}>
          <Spin loading={loading} size={40} style={{ width: '100%', height: '100%' }} tip="加载中...">
            <div className={styles.tableContainer}>
              {instanceList?.map((item, index) => (
                <ConnectNodeCategoryCard key={`flow-${index}`} data={item} />
              ))}
            </div>
          </Spin>
        </div>
        <div className={styles.footer}>
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
  );
};

export default ConnectorInstancesPage;
