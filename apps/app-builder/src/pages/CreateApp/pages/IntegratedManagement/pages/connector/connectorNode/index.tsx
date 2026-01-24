import { Button, Input, Message, Spin, Tabs } from '@arco-design/web-react';
import { IconClose } from '@arco-design/web-react/icon';
import { getConnectorNodeTypes, type ConnectorItem } from '@onebase/app';
import { getHashQueryParam } from '@onebase/common';
import { debounce } from 'lodash-es';
import React, { useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import ConnectorCard from '../../../components/ConnectNodeCategoryCard';
import { CATEGORY_MAP, transformConnectorData } from '../../../utils/transform';
import styles from './index.module.less';

const TabPane = Tabs.TabPane;

const CATEGORIES = [
  { key: 'all', label: '全部' },
  { key: 'database', label: '数据库' },
  { key: 'http', label: 'HTTP' },
  { key: 'mq', label: '消息队列' },
  { key: 'saas', label: '三化连接器' },
  { key: 'search', label: '搜索引擎' },
  { key: 'storage', label: '对象存储' }
];

const ConnectorPage: React.FC = () => {
  const navigate = useNavigate();
  const { tenantId } = useParams();
  const [loading, setLoading] = useState(false);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [activeTab, setActiveTab] = useState<string>('all');

  const [connectorList, setConnectorList] = useState<ConnectorItem[]>([]);

  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [isSelectMode, setIsSelectMode] = useState(false);

  useEffect(() => {
    const mode = getHashQueryParam('mode');
    setIsSelectMode(mode === 'select' || mode === 'create');
    fetchConnectorList();
  }, []);

  const fetchConnectorList = async () => {
    setLoading(true);
    try {
      const rawData = await getConnectorNodeTypes();

      if (!rawData || !Array.isArray(rawData)) {
        console.error('API返回数据格式不正确:', rawData);
        Message.error('获取连接器列表失败：数据格式错误');
        setConnectorList([]);
        return;
      }

      const transformedData = transformConnectorData(rawData);
      setConnectorList(transformedData);
    } catch (error) {
      console.error('获取连接器列表失败:', error);
      Message.error(error instanceof Error ? error.message : '获取连接器列表失败');
      setConnectorList([]);
    } finally {
      setLoading(false);
    }
  };

  const debouncedSearch = useMemo(
    () =>
      debounce((value: string) => {
        setSearchKeyword(value);
      }, 500),
    []
  );

  useEffect(() => {
    return () => {
      debouncedSearch.cancel();
    };
  }, [debouncedSearch]);

  const filteredList = useMemo(() => {
    let list = connectorList;

    if (activeTab !== 'all') {
      list = list.filter((item) => item.category === CATEGORY_MAP[activeTab]);
    }

    if (searchKeyword) {
      const keyword = searchKeyword.toLowerCase();
      list = list.filter((item) => item.name.toLowerCase().includes(keyword));
    }

    return list;
  }, [connectorList, activeTab, searchKeyword]);

  const handleTabChange = (key: string) => {
    setActiveTab(key);
  };

  const handleSearch = (value: string) => {
    debouncedSearch(value);
  };


  const handleCardClick = (data: ConnectorItem) => {
    if (isSelectMode) {
      setSelectedId(data.id);
    } else {
      console.log('Card clicked not in select mode:', data);
    }
  };

  const handleConfirm = () => {
    if (!selectedId) {
      Message.warning('请选择连接器类型');
      return;
    }

    const selectedItem = connectorList.find(item => item.id === selectedId);
    if (!selectedItem) return;

    const curAppId = getHashQueryParam('appId');
    if (!curAppId) {
      Message.error('应用ID获取失败，无法进入配置页面');
      return;
    }

    navigate(
      `/onebase/${tenantId}/home/create-app/integrated-management/connector-detail?appId=${curAppId}&mode=create&connectorType=${encodeURIComponent(selectedItem.id)}&connectorName=${encodeURIComponent(selectedItem.name)}&instanceCount=${selectedItem.fields.instanceCount}`
    );
  };

  const handleClose = () => {
    const curAppId = getHashQueryParam('appId');
    navigate(`/onebase/${tenantId}/home/create-app/integrated-management/connector-instances?appId=${curAppId}`);
  };

  return (
    <div className={styles.connectorPage}>
      <div className={styles.header}>
        <div className={styles.title}>
          {isSelectMode ? '新建连接器实例' : '连接器类型'}
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
          <Input.Search
            allowClear
            placeholder="请输入类型名称搜索"
            style={{ width: 240 }}
            onChange={handleSearch}
          />
          {isSelectMode && (
            <Button icon={<IconClose />} shape="circle" type="text" onClick={handleClose} />
          )}
        </div>
      </div>

      <div className={styles.body}>
        <Tabs
          activeTab={activeTab}
          onChange={(key) => handleTabChange(key)}
          className={styles.tabsContainer}
        >
          {CATEGORIES.map(category => (
            <TabPane key={category.key} title={category.label} />
          ))}
        </Tabs>

        <div className={styles.content}>
          <Spin loading={loading} size={40} style={{ width: '100%', height: '100%' }} tip="加载中...">
            <div className={styles.tableContainer}>
              {filteredList.length > 0 ? (
                filteredList.map(item => (
                  <ConnectorCard
                    key={item.id}
                    data={item}
                    isSelected={selectedId === item.id}
                    onClick={handleCardClick}
                  />
                ))
              ) : (
                <div className={styles.emptyState}>暂无数据</div>
              )}
            </div>
          </Spin>
        </div>
      </div>

      {isSelectMode && (
        <div className={styles.footer}>
          <Button type="primary" disabled={!selectedId} onClick={handleConfirm} style={{ width: 80 }}>
            确定
          </Button>
        </div>
      )}
    </div>
  );
};

export default ConnectorPage;
