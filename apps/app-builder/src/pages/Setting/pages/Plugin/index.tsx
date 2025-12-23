import externalUserSVG from '@/assets/images/external_user.svg';
import { Input, Message, Modal, Space, Spin, Switch, Tabs } from '@arco-design/web-react';
import { getPluginListApi, type pluginParams, updatePluginStatusApi } from '@onebase/platform-center';
import { useEffect, useMemo, useState } from 'react';
import { StatusEnumLabel, statusMapping, StatusValue } from './constants';
import styles from './index.module.less';

export interface PluginItem {
  id: string;
  icon: string;
  name: string;
  remark: string;
  status: number;
}

const PluginPage = () => {
  const [loading, setLoading] = useState<boolean>(false);
  const [pluginData, setPluginData] = useState<PluginItem[]>([]);
  const [searchValue, setSearchValue] = useState<string>('');
  const [currentTab, setCurrentTab] = useState<string>(StatusValue.ALL);

  const renderStatus = (status: any) => {
    let value = null;
    if (status === StatusValue.ALL) {
      value = null;
    } else if (status === StatusValue.DISABLE) {
      value = 0;
    } else if (status === StatusValue.ENABLE) {
      value = 1;
    }
    return value;
  };

  const fetchPluginList = async (status: string = '') => {
    setLoading(true);
    const params: pluginParams = {
      status: renderStatus(status),
      name: ''
    };
    try {
      const res = await getPluginListApi(params);
      if (res && Array.isArray(res)) {
        setPluginData(res);
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
      await fetchPluginList();
      Message.success('当前已启用模式');
    }
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
        style={{ width: '100%' }}
        extra={<Input.Search className={styles.search} placeholder="搜索插件" onChange={handleSearchChange} />}
      >
        {statusMapping.map((data: any) => {
          return (
            <Tabs.TabPane key={data.value} title={data.label}>
              <Spin loading={loading} style={{ width: '100%' }}>
                <div className={styles.cardContainer}>
                  {filterPlugin?.map((plugin, index) => {
                    return (
                      <div className={styles.card} key={index}>
                        <Space size={16}>
                          <div className={styles.icon} style={{ backgroundColor: '#009E9E' }}>
                            <img
                              src={externalUserSVG}
                              style={{ width: '32px', height: '32px', filter: 'brightness(0) invert(1)' }}
                            />
                          </div>
                          <div className={styles.textContent}>
                            <span className={styles.name}>{plugin.name}</span>
                            <span className={styles.description}>{plugin.remark}</span>
                          </div>
                        </Space>
                        <Space>
                          <div>{plugin.status === 1 ? StatusEnumLabel.ENABLE : StatusEnumLabel.DISABLE}</div>
                          <Switch
                            checked={plugin.status === 1 ? true : false}
                            onChange={(checked) => handleSwitchChange(plugin.id, plugin.name, checked)}
                          />
                        </Space>
                      </div>
                    );
                  })}
                </div>
              </Spin>
            </Tabs.TabPane>
          );
        })}
      </Tabs>
    </div>
  );
};

export default PluginPage;
