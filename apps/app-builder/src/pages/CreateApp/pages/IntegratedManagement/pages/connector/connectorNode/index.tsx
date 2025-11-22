import { Input, Pagination, Spin, Tabs } from '@arco-design/web-react';
import {
  getConnectFlowNodeCategoryList,
  listConnectFlowNode,
  type ConnectFlowNodeCategory,
  type ListConnectFlowNodeReq
} from '@onebase/app';
import { getCommonPaginationList } from '@onebase/common';
import { debounce } from 'lodash-es';
import React, { useCallback, useEffect, useState } from 'react';
import ConnectNodeCategoryCard from '../../../components/ConnectNodeCategoryCard';
import styles from './index.module.less';

/**
 * 流程管理页面
 * 目前集成触发器编辑器作为主内容
 */
const ConnectorPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [searchTypeName, setSearchTypeName] = useState('');
  const [searchLevel1Code, setSearchLevel1Code] = useState('');

  const [pageSize, setPageSize] = useState<number>(8);
  const [pageNo, setPageNo] = useState(1);

  // 节点类别列表
  const [connectFlowNodeCategoryList, setConnectFlowNodeCategoryList] = useState<ConnectFlowNodeCategory[]>();
  // 节点列表
  const [connectFlowNodeList, setConnectFlowNodeList] = useState<any[]>();
  const [total, setTotal] = useState(0);

  useEffect(() => {
    handleListConnectFlowNodeCategory();
  }, []);

  useEffect(() => {
    pageSize && getConnectFlowNodeList(searchTypeName, searchLevel1Code);
  }, [pageNo, pageSize, searchTypeName, searchLevel1Code]);

  const debouncedSearch = useCallback(
    debounce((typeName: string, level1Code: string) => {
      getConnectFlowNodeList(typeName, level1Code);
    }, 500),
    []
  );

  useEffect(() => {
    return () => debouncedSearch.cancel();
  }, [debouncedSearch]);

  const getConnectFlowNodeList = async (typeName?: string, level1Code?: string) => {
    setLoading(true);

    const req: ListConnectFlowNodeReq = {
      pageNo: pageNo,
      pageSize: pageSize || 8,
      typeName: typeName || '',
      level1Code: level1Code || ''
    };
    const res = await getCommonPaginationList(listConnectFlowNode, req, setPageNo);
    console.log('res :', res);
    if (res) {
      setConnectFlowNodeList(res.list || []);
      setTotal(res.total || 0);
      setLoading(false);
    }
  };

  const handleListConnectFlowNodeCategory = async () => {
    const res = await getConnectFlowNodeCategoryList();
    console.log('res: ', res);
    if (res) {
      setConnectFlowNodeCategoryList(res);
    }
  };

  return (
    <div className={styles.connectorPage}>
      <div className={styles.header}>
        <div className={styles.title}>连接器类型</div>
        <Input.Search
          allowClear
          placeholder="请输入流程名称"
          style={{ width: 240 }}
          onChange={(value) => {
            setSearchTypeName(value);
          }}
        />
      </div>

      <div className={styles.body}>
        <div>
          <Tabs
            onChange={(key) => {
              if (key === 'all') {
                setSearchLevel1Code('');
              } else {
                setSearchLevel1Code(key as string);
              }
            }}
          >
            <Tabs.TabPane key="all" title="全部"></Tabs.TabPane>
            {connectFlowNodeCategoryList?.map((category: ConnectFlowNodeCategory) => (
              <Tabs.TabPane key={category.code} title={category.name} />
            ))}
          </Tabs>
        </div>
        <div className={styles.content}>
          <Spin loading={loading} size={40} style={{ width: '100%', height: '100%' }} tip="加载中...">
            <div className={styles.tableContainer}>
              {connectFlowNodeList?.map((item, index) => (
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

export default ConnectorPage;
