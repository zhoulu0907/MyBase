import externalUserSVG from '@/assets/images/external_user.svg';
import { Input, Message, Modal, Space, Spin, Switch, Tabs, Pagination, Drawer, Dropdown, Menu } from '@arco-design/web-react';
import { IconSettings, IconEdit, IconMoreVertical } from '@arco-design/web-react/icon';
import { getPluginListApi, type pluginParams, updatePluginStatusApi, getPluginPageListApi, type pluginPageParams, type PluginInfoRespVO } from '@onebase/platform-center';
import dayjs from 'dayjs';
import { useEffect, useMemo, useState } from 'react';
import { StatusEnumLabel, statusMapping, StatusValue } from './constants';
import styles from './index.module.less';

export interface PluginItem {
  id: string;
  icon: string;
  name: string;
  remark: string;
  status: number;
  pluginId?: string;
  pluginVersion?: string;
  versionCount?: number;
  createTime?: string;
  updateTime?: string;
  isDynamic?: boolean; // 是否为动态插件
}

const PluginPage = () => {
  const enableDynamicMock = true;
  const [loading, setLoading] = useState<boolean>(false);
  const [systemPlugins, setSystemPlugins] = useState<PluginItem[]>([]);
  const [dynamicPlugins, setDynamicPlugins] = useState<PluginItem[]>([]);
  const [searchValue, setSearchValue] = useState<string>('');
  const [currentTab, setCurrentTab] = useState<string>(StatusValue.ALL);
  
  // 分页状态
  const [currentPage, setCurrentPage] = useState<number>(1);
  const [pageSize, setPageSize] = useState<number>(10);
  const [total, setTotal] = useState<number>(0);
  const [configVisible, setConfigVisible] = useState<boolean>(false);
  const [configPlugin, setConfigPlugin] = useState<PluginItem | null>(null);
  const [editVisible, setEditVisible] = useState<boolean>(false);
  const [editPlugin, setEditPlugin] = useState<PluginItem | null>(null);
  const [editName, setEditName] = useState<string>('');
  const [editRemark, setEditRemark] = useState<string>('');

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

  const fetchSystemPlugins = async (status: string = '') => {
    setLoading(true);
    
    const params: pluginParams = {
      status: renderStatus(status),
      name: searchValue || ''
    };
    
    try {
      const res = await getPluginListApi(params);
      if (res && Array.isArray(res)) {
        const formattedPlugins: PluginItem[] = res.map(plugin => ({
          ...plugin,
          isDynamic: false
        }));
        setSystemPlugins(formattedPlugins);
      } else {
        console.warn('Invalid response format:', res);
      }
    } catch (error) {
      Message.error('获取系统插件列表失败');
    } finally {
      setLoading(false);
    }
  };

  const fetchDynamicPlugins = async (status: string = '') => {
    if (enableDynamicMock) {
      const mockPlugins: PluginItem[] = [
        {
          id: 'mock-1',
          icon: '1',
          name: '审批中心插件',
          remark: '用于外部用户审批流程的动态插件',
          status: 1,
          pluginId: 'approval-center',
          pluginVersion: '1.0.0',
          versionCount: 3,
          createTime: '2025-01-01 10:00:00',
          updateTime: '2025-01-10 12:00:00',
          isDynamic: true
        },
        {
          id: 'mock-2',
          icon: '2',
          name: '数据报表插件',
          remark: '提供数据看板与报表导出的动态插件',
          status: 0,
          pluginId: 'report-center',
          pluginVersion: '2.3.1',
          versionCount: 5,
          createTime: '2025-02-15 09:30:00',
          updateTime: '2025-03-01 18:20:00',
          isDynamic: true
        }
      ];
      setDynamicPlugins(mockPlugins);
      setTotal(mockPlugins.length);
      return;
    }
    setLoading(true);
    
    const params: pluginPageParams = {
      pageNo: currentPage,
      pageSize: pageSize,
      pluginName: searchValue || undefined,
      status: renderStatus(status) as number | undefined
    };
    
    try {
      const res = await getPluginPageListApi(params);
      if (res && res.data) {
        const { list, total } = res.data;
        // 转换动态插件数据格式
        const formattedPlugins: PluginItem[] = list.map((plugin: PluginInfoRespVO) => ({
          id: plugin.id.toString(),
          name: plugin.pluginName,
          remark: plugin.pluginDescription,
          status: plugin.status,
          icon: plugin.pluginIcon.toString(),
          pluginId: plugin.pluginId,
          pluginVersion: plugin.pluginVersion,
          versionCount: plugin.versionCount,
          createTime: plugin.createTime,
          updateTime: plugin.updateTime,
          isDynamic: true
        }));
        setDynamicPlugins(formattedPlugins);
        setTotal(total);
      } else {
        console.warn('Invalid response format:', res);
      }
    } catch (error) {
      Message.error('获取动态插件列表失败');
    } finally {
      setLoading(false);
    }
  };

  const fetchPluginList = async (status: string = '') => {
    // 同时获取系统插件和动态插件
    await Promise.all([
      fetchSystemPlugins(status),
      fetchDynamicPlugins(status)
    ]);
  };

  useEffect(() => {
    fetchPluginList();
  }, []);

  const handleSearchChange = (searchValue: string) => {
    setSearchValue(searchValue);
    setCurrentPage(1); // 搜索时重置到第一页
  };

  const handleChangeTab = (value: string) => {
    setCurrentTab(value);
    setCurrentPage(1); // 切换标签时重置到第一页
    fetchPluginList(value);
  };

  // 分页处理
  const handlePageChange = (page: number, pageSize: number) => {
    setCurrentPage(page);
    setPageSize(pageSize);
    fetchDynamicPlugins(currentTab);
  };

  const openConfig = (plugin: PluginItem) => {
    setConfigPlugin(plugin);
    setConfigVisible(true);
  };
  const closeConfig = () => {
    setConfigVisible(false);
    setConfigPlugin(null);
  };

  const openEdit = (plugin: PluginItem) => {
    setEditPlugin(plugin);
    setEditName(plugin.name || '');
    setEditRemark(plugin.remark || '');
    setEditVisible(true);
  };
  const closeEdit = () => {
    setEditVisible(false);
    setEditPlugin(null);
    setEditName('');
    setEditRemark('');
  };
  const saveEdit = () => {
    if (!editPlugin) return;
    const updater = (p: PluginItem) =>
      p.id === editPlugin.id ? { ...p, name: editName, remark: editRemark } : p;
    if (editPlugin.isDynamic) {
      setDynamicPlugins((prev) => prev.map(updater));
    } else {
      setSystemPlugins((prev) => prev.map(updater));
    }
    Message.success('已保存编辑');
    closeEdit();
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

  const filterSystemPlugins = useMemo(() => {
    // 系统插件 - 客户端过滤
    if (!searchValue.trim()) return systemPlugins;
    const lowerSearch = searchValue.toLowerCase();
    return systemPlugins.filter((item: PluginItem) => item.name?.toLowerCase().includes(lowerSearch));
  }, [systemPlugins, searchValue]);

  const filterDynamicPlugins = useMemo(() => {
    // 动态插件 - 服务端已过滤，直接返回
    return dynamicPlugins;
  }, [dynamicPlugins]);

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
                {/* 系统插件区域 */}
                {filterSystemPlugins.length > 0 && (
                  <div className={styles.pluginSection}>
                    <h3 className={styles.sectionTitle}>系统插件</h3>
                    <div className={styles.cardContainer}>
                      {filterSystemPlugins.map((plugin: PluginItem, index: number) => {
                        return (
                          <div className={styles.card} key={`system-${index}`}>
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
                  </div>
                )}
                
                {/* 动态插件区域 */}
                {filterDynamicPlugins.length > 0 && (
                  <div className={styles.pluginSection}>
                    <h3 className={styles.sectionTitle}>动态插件</h3>
                    <div className={styles.cardContainer}>
                      {filterDynamicPlugins.map((plugin: PluginItem, index: number) => {
                        return (
                          <div
                            className={styles.card}
                            key={`dynamic-${index}`}
                            style={{ borderColor: plugin.isDynamic ? '#165DFF' : '#009E9E', height: 'auto' }}
                          >
                            <Space size={16}>
                              <div className={styles.icon} style={{ backgroundColor: '#165DFF' }}>
                                <img
                                  src={externalUserSVG}
                                  style={{ width: '32px', height: '32px', filter: 'brightness(0) invert(1)' }}
                                />
                              </div>
                              <div className={styles.textContent}>
                                <span className={styles.name}>{plugin.name}</span>
                                <span className={styles.description}>{plugin.remark}</span>
                                <div className={styles.cardActions}>
                                  <button type="button" className={styles.actionBtn} onClick={() => openConfig(plugin)} title="配置">
                                    <IconSettings />
                                  </button>
                                  <button type="button" className={styles.actionBtn} onClick={() => openEdit(plugin)} title="编辑">
                                    <IconEdit />
                                  </button>
                                  <Dropdown
                                    droplist={
                                      <Menu>
                                        <Menu.Item
                                          key="delete"
                                          onClick={() => {
                                            setDynamicPlugins((prev) => prev.filter((p) => p.id !== plugin.id));
                                            Message.success('已删除');
                                          }}
                                        >
                                          删除
                                        </Menu.Item>
                                      </Menu>
                                    }
                                  >
                                    <button type="button" className={styles.actionBtn} title="更多">
                                      <IconMoreVertical />
                                    </button>
                                  </Dropdown>
                                </div>
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
                  </div>
                )}
                
                {/* 动态插件分页 */}
                {total > 0 && (
                  <div className={styles.pagination}>
                    <Pagination
                      current={currentPage}
                      pageSize={pageSize}
                      total={total}
                      onChange={handlePageChange}
                      showTotal={(total) => `共 ${total} 条`}
                    />
                  </div>
                )}
              </Spin>
            </Tabs.TabPane>
          );
        })}
      </Tabs>
      <Drawer
        visible={editVisible}
        width="40%"
        title={editPlugin?.name ? `编辑：${editPlugin.name}` : '编辑插件'}
        onCancel={closeEdit}
        onOk={saveEdit}
        unmountOnExit
        maskClosable
      >
        <div className={styles.configContent}>
          <div className={styles.configItem}>
            <span className={styles.configLabel}>显示名称</span>
            <Input value={editName} onChange={setEditName} placeholder="请输入显示名称" />
          </div>
          <div className={styles.configItem}>
            <span className={styles.configLabel}>描述</span>
            <Input.TextArea value={editRemark} onChange={setEditRemark} placeholder="请输入描述" rows={3} />
          </div>
        </div>
      </Drawer>
      <Drawer
        visible={configVisible}
        width="40%"
        title={configPlugin?.name || '插件配置'}
        onCancel={closeConfig}
        unmountOnExit
        maskClosable
      >
        <div className={styles.configContent}>
          <div className={styles.configItem}>
            <span className={styles.configLabel}>状态</span>
            <Switch
              checked={configPlugin?.status === 1}
              onChange={(checked) => {
                if (!configPlugin) return;
                handleSwitchChange(configPlugin.id, configPlugin.name, checked);
              }}
            />
          </div>
          <div className={styles.configItem}>
            <span className={styles.configLabel}>显示名称</span>
            <Input defaultValue={configPlugin?.name} placeholder="请输入显示名称" />
          </div>
          <div className={styles.configItem}>
            <span className={styles.configLabel}>描述</span>
            <Input.TextArea defaultValue={configPlugin?.remark} placeholder="请输入描述" rows={3} />
          </div>
          <div className={styles.configItem}>
            <span className={styles.configLabel}>版本</span>
            <Input disabled value={configPlugin?.pluginVersion} />
          </div>
        </div>
      </Drawer>
    </div>
  );
};

export default PluginPage;
