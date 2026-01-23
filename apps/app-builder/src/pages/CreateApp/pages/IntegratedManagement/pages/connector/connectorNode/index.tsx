import { Input, Message, Spin, Tabs } from '@arco-design/web-react';
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

  useEffect(() => {
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

  const handleCardClick = async (data: ConnectorItem) => {
    console.log('Card clicked:', data);

    // 检查是否是从实例列表页进入的（mode=select）
    const mode = getHashQueryParam('mode');
    const isSelectMode = mode === 'select' || mode === 'create';

    if (isSelectMode) {
      // 从实例列表进入，跳转到连接器详情页面（创建模式）
      const curAppId = getHashQueryParam('appId');
      if (!curAppId) {
        Message.error('应用ID获取失败，无法进入配置页面');
        return;
      }

      navigate(
        `/onebase/${tenantId}/home/create-app/integrated-management/connector-create?appId=${curAppId}&connectorType=${encodeURIComponent(data.id)}&connectorName=${encodeURIComponent(data.name)}`
      );
    } else {
      // 直接从连接器类型页面进入，显示提示
      Message.info('尚未支持编辑');
    }
  };

  const handleEdit = (id: string) => {
    console.log('Edit clicked:', id);
    Message.info('编辑功能待实现');
  };

  return (
    <div className={styles.connectorPage}>
      <div className={styles.header}>
        <div className={styles.title}>
          连接器类型
        </div>
        <Input.Search allowClear placeholder="请输入类型名称搜索" style={{ width: 240 }} onChange={handleSearch} />
      </div>

      <div className={styles.body}>
        <div className={styles.tabsContainer}>
          <Tabs activeTabKey={activeTab} onChange={handleTabChange}>
            {CATEGORIES.map((category) => (
              <TabPane key={category.key} title={category.label} />
            ))}
          </Tabs>
        </div>

        <div className={styles.content}>
          <Spin loading={loading} size={40} style={{ width: '100%', height: '100%' }} tip="加载中...">
            <div className={styles.tableContainer}>
              {filteredList.length > 0 ? (
                filteredList.map((item) => (
                  <ConnectorCard key={item.id} data={item} onClick={handleCardClick} onEdit={handleEdit} />
                ))
              ) : (
                <div className={styles.emptyState}>暂无数据</div>
              )}
            </div>
          </Spin>
        </div>
      </div>
    </div>
  );
};

export default ConnectorPage;
