import { Input, Message, Spin, Tabs } from '@arco-design/web-react';
import {
  getConnectorNodeTypes,
  type ConnectorItem,
  type ConnectFlowNode
} from '@onebase/app';
import { debounce } from 'lodash-es';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import ConnectorCard from '../../../components/ConnectNodeCategoryCard';
import { CATEGORY_MAP, transformConnectorData } from '../../../utils/transform';
import { getHashQueryParam } from '@onebase/common';
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
  const [selectingMode, setSelectingMode] = useState(false); // 是否处于选择模式

  const [connectorList, setConnectorList] = useState<ConnectorItem[]>([]);

  // 检查URL参数，判断是否为选择模式
  useEffect(() => {
    const mode = getHashQueryParam('mode');
    setSelectingMode(mode === 'select');
  }, []);

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
    () => debounce((value: string) => {
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
      list = list.filter(item => item.category === CATEGORY_MAP[activeTab]);
    }

    if (searchKeyword) {
      const keyword = searchKeyword.toLowerCase();
      list = list.filter(item => item.name.toLowerCase().includes(keyword));
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

    const curAppId = getHashQueryParam('appId');

    if (!curAppId) {
      Message.error('应用ID获取失败，无法进入配置页面');
      return;
    }

    // 如果是选择模式
    if (selectingMode) {
      // 跳转到配置页面，传递连接器类型信息（不创建实例）
      navigate(
        `/onebase/${tenantId}/home/create-app/integrated-management/connector-detail?appId=${curAppId}&connectorType=${encodeURIComponent(data.id)}&connectorName=${encodeURIComponent(data.name)}&mode=create`
      );
    } else {
      // 非选择模式：查看连接器详情或配置
      // 目前只支持Script类型的连接器配置，其他类型显示"建设中"
      if (data.id === 'script' || data.id?.toString().toLowerCase() === 'script') {
        // Script类型连接器：跳转到配置页面（不创建实例）
        navigate(
          `/onebase/${tenantId}/home/create-app/integrated-management/connector-detail?appId=${curAppId}&connectorType=${encodeURIComponent(data.id)}&connectorName=${encodeURIComponent(data.name)}&mode=create`
        );
      } else {
        // 其他类型连接器：显示"建设中"提示
        Message.info(`${data.name} 连接器配置功能建设中，敬请期待！`);
      }
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
          {selectingMode ? '选择连接器类型' : '连接器类型'}
          {selectingMode && <span style={{ fontSize: 14, color: '#999', marginLeft: 10, fontWeight: 'normal' }}>点击卡片创建实例</span>}
        </div>
        <Input.Search
          allowClear
          placeholder="请输入类型名称搜索"
          style={{ width: 240 }}
          onChange={handleSearch}
        />
      </div>

      <div className={styles.body}>
        <div className={styles.tabsContainer}>
          <Tabs activeTabKey={activeTab} onChange={handleTabChange}>
            {CATEGORIES.map(category => (
              <TabPane key={category.key} title={category.label} />
            ))}
          </Tabs>
        </div>

        <div className={styles.content}>
          <Spin loading={loading} size={40} style={{ width: '100%', height: '100%' }} tip="加载中...">
            <div className={styles.tableContainer}>
              {filteredList.length > 0 ? (
                filteredList.map(item => (
                  <ConnectorCard
                    key={item.id}
                    data={item}
                    onClick={handleCardClick}
                    onEdit={handleEdit}
                  />
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
