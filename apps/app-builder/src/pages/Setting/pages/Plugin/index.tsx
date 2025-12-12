import { Card, Input, Message, Space, Switch, Tabs } from '@arco-design/web-react';
import { IconSettings } from '@arco-design/web-react/icon';
import { TokenManager } from '@onebase/common';
import { type corpAppListParams, getCorpAuthorizedAppListApiInCorp } from '@onebase/platform-center';
import { useEffect, useMemo, useState } from 'react';
import { StatusEnumLabel, statusMapping } from './constants';
import styles from './index.module.less';

export interface PluginItem {
  id: string;
  icon: string;
  name: string;
  desc: string;
  status: number;
}

const PluginPage = () => {
  const [loading, setLoading] = useState<boolean>(false);
  const [pluginData, setPluginData] = useState<PluginItem[]>([
    { id: '1', icon: '', name: '插件管理', desc: '开启后，可创建SaaS模式应用和企业', status: 1 },
    { id: '1', icon: '', name: 'SaaS模式', desc: '开启后，可创建SaaS模式应用和企业', status: 2 }
  ]);
  const [searchValue, setSearchValue] = useState<string>('');
  const [currentTab, setCurrentTab] = useState<string>('0');
  const tokenInfo = TokenManager.getTokenInfo();

  const fetchCorpAuthorizedList = async (pageNo = 1, pageSize = 10, status: string = '0') => {
    setLoading(true);
    const params: corpAppListParams = {
      pageNo,
      pageSize,
      status: status ? Number(status) : 0,
      corpId: tokenInfo?.corpId || ''
    };
    try {
      const res = await getCorpAuthorizedAppListApiInCorp(params);
      if (res && Array.isArray(res.list)) {
        setPluginData(res.list);
      } else {
        console.warn('Invalid response format:', res);
      }
    } catch (error) {
      Message.error('获取企业授权应用列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    // fetchCorpAuthorizedList();
  }, []);

  const handleSearchChange = (searchValue: string) => {
    setSearchValue(searchValue);
  };

  const handleChangeTab = (value: string) => {
    setCurrentTab(value);
    // fetchCorpAuthorizedList(1, 10, value);
  };

  // 切换开关状态
  const handleSwitchChange = (id: string, checked: boolean) => {
    // 实际项目中这里会调用接口更新状态
    console.log(`插件 ${id} 状态切换为：${checked ? '已启用' : '未启用'}`);
    const newPluginData = pluginData.map((item) => {
      if (item.id === id) {
        item.status = checked === true ? 1 : 2;
      }
      return item;
    });
    setPluginData(newPluginData);
  };

  const filterPlugin = useMemo(() => {
    if (!searchValue.trim()) return pluginData;
    const lowerSearch = searchValue.toLowerCase();
    return pluginData.filter((item) => item.name?.toLowerCase().includes(lowerSearch));
  }, [pluginData, searchValue]);

  return (
    <div className={styles.pluginPage}>
      <Tabs
        activeTab={currentTab}
        onChange={handleChangeTab}
        type="rounded"
        extra={<Input.Search allowClear placeholder="搜索插件" onChange={handleSearchChange} />}
      >
        {statusMapping.map((plugin: any) => {
          return (
            <Tabs.TabPane key={plugin.status} title={plugin.label}>
              <Space size={16}>
                {filterPlugin?.map((plugin, index) => {
                  return (
                    <Card className={styles.card} key={index} hoverable>
                      <Space size={48}>
                        <Space size={16}>
                          <IconSettings />
                          <div className={styles.textContent}>
                            <span className={styles.name}>{plugin.name}</span>
                            <span className={styles.description}>{plugin.desc}</span>
                          </div>
                        </Space>
                        <Space>
                          <div>{plugin.status === 1 ? StatusEnumLabel.ENABLE : StatusEnumLabel.DISABLE}</div>
                          <Switch
                            checked={plugin.status === 1 ? true : false}
                            onChange={(checked) => handleSwitchChange(plugin.id, checked)}
                          />
                        </Space>
                      </Space>
                    </Card>
                  );
                })}
              </Space>
            </Tabs.TabPane>
          );
        })}
      </Tabs>
    </div>
  );
};

export default PluginPage;
