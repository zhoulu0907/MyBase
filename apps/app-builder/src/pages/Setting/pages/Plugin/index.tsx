import { Card, Input, Message, Modal, Space, Spin, Switch, Tabs } from '@arco-design/web-react';
import { IconSettings } from '@arco-design/web-react/icon';
import {
  getPluginListApi,
  type pluginParams,
  updatePluginStatusApi
} from '@onebase/platform-center';
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
  const [pluginData, setPluginData] = useState<PluginItem[]>([]);
  const [searchValue, setSearchValue] = useState<string>('');
  const [currentTab, setCurrentTab] = useState<string>('0');

  const fetchPluginList = async (status: string = '0') => {
    setLoading(true);
    const params: pluginParams = {
      status: status ? Number(status) : 0,
      name: ''
    };
    try {
      const res = await getPluginListApi(params);
      if (res && Array.isArray(res.list)) {
        setPluginData(res.list);
      } else {
        console.warn('Invalid response format:', res);
      }
    } catch (error) {
      Message.error('获取插件管理列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPluginList();
  }, []);

  const handleSearchChange = (searchValue: string) => {
    setSearchValue(searchValue);
  };

  const handleChangeTab = (value: string) => {
    setCurrentTab(value);
    fetchPluginList(value);
  };

  // 切换开关状态
  const handleSwitchChange = async (id: string, name: string, checked: boolean) => {
    // 实际项目中这里会调用接口更新状态
    console.log(`插件 ${id} 状态切换为：${checked ? '已启用' : '未启用'}`);
    if (checked === false) {
      return Modal.confirm({
        title: `关闭"${name}"插件？`,
        content: '关闭后，外部用户将无法访问应用，再次启用时可恢复正常访问',
        okButtonProps: {
          status: 'danger'
        },
        onOk: async () => {
          await updatePluginStatusApi(id, 0);
          await fetchPluginList();
          Message.success('关闭成功');
        }
      });
    } else {
      await updatePluginStatusApi(id, 1);
      Message.success('当前已启用模式');
    }
    const newPluginData = pluginData.map((item) => {
      if (item.id !== id) {
        item.status = checked === true ? 0 : 1;
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
        extra={
          <Input.Search
            className={styles.search}
            placeholder="搜索插件"
            onChange={handleSearchChange}
          />
        }
      >
        {statusMapping.map((plugin: any) => {
          return (
            <Tabs.TabPane key={plugin.status} title={plugin.label}>
              <Spin loading={loading} style={{ width: '100%' }}>
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
                              onChange={(checked) => handleSwitchChange(plugin.id, plugin.name, checked)}
                            />
                          </Space>
                        </Space>
                      </Card>
                    );
                  })}
                </Space>
              </Spin>
            </Tabs.TabPane>
          );
        })}
      </Tabs>
    </div>
  );
};

export default PluginPage;
