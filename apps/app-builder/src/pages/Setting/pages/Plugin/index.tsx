import externalUserSVG from '@/assets/images/external_user.svg';
import { Input, Message, Modal, Space, Spin, Switch, Tabs, Pagination, Drawer, Dropdown, Menu, Button, Form, Upload, Table, Popconfirm, Divider } from '@arco-design/web-react';
import { IconSettings, IconEdit, IconMoreVertical, IconPlus, IconUpload } from '@arco-design/web-react/icon';
import { getPluginListApi, type pluginParams, updatePluginStatusApi, getPluginPageListApi, type pluginPageParams, createPluginApi, getPluginDetailApi, updatePluginInfoApi, uploadPluginVersionApi, getPluginVersionListApi, deletePluginVersionApi, activePluginVersionApi, enablePluginApi, disablePluginApi, getPluginConfigTemplateApi, savePluginConfigApi, getPluginConfigDetailApi, type PluginDetailRespVO, type PluginVersionVO } from '@onebase/platform-center';
import dayjs from 'dayjs';
import { useEffect, useMemo, useState } from 'react';
import { TENANT_CONFIG_UPDATE, hasPermission } from '@onebase/common';
import MenuComp from '@/components/MenuIcon';
import DynamicForm from '@/components/DynamicForm';
import { createForm } from '@formily/core';
import { webMenuIcons } from '@onebase/ui-kit';
import { statusMapping, StatusValue } from './constants';
import styles from './index.module.less';
import { mockDynamicPlugins, mockPluginDetails, mockPluginVersions } from './mock';

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
  //TODO MOCK 数据开关
  const enableDynamicMock = false;
  const [loading, setLoading] = useState<boolean>(false);
  const [systemPlugins, setSystemPlugins] = useState<PluginItem[]>([]);
  const [dynamicPlugins, setDynamicPlugins] = useState<PluginItem[]>([]);
  const [searchValue, setSearchValue] = useState<string>('');
  const [currentTab, setCurrentTab] = useState<string>(StatusValue.ALL);
  
  // 分页状态
  const [currentPage, setCurrentPage] = useState<number>(1);
  const [pageSize, setPageSize] = useState<number>(100);
  const [total, setTotal] = useState<number>(0);
  const [configVisible, setConfigVisible] = useState<boolean>(false);
  const [configPlugin, setConfigPlugin] = useState<PluginItem | null>(null);
  const [configContent, setConfigContent] = useState<string>('');
  const [configTemplates, setConfigTemplates] = useState<any[]>([]); // Store raw config templates
  const [editVisible, setEditVisible] = useState<boolean>(false);
  const configForm = useMemo(() => createForm(), []); // Create Formily form instance
  const [currentPluginDetail, setCurrentPluginDetail] = useState<PluginDetailRespVO | null>(null);
  const [versionList, setVersionList] = useState<PluginVersionVO[]>([]);
  const [editLoading, setEditLoading] = useState<boolean>(false);
  const [versionCreateVisible, setVersionCreateVisible] = useState<boolean>(false);
  const [editForm] = Form.useForm();
  const [versionForm] = Form.useForm();

  const [createVisible, setCreateVisible] = useState<boolean>(false);
  const [createLoading, setCreateLoading] = useState<boolean>(false);
  const [iconSelectorVisible, setIconSelectorVisible] = useState<boolean>(false);
  const [iconSelectorSource, setIconSelectorSource] = useState<'create' | 'edit'>('create');
  const [isEditPluginDynamic, setIsEditPluginDynamic] = useState<boolean>(false);
  const [editingPluginId, setEditingPluginId] = useState<string>('');
  const [form] = Form.useForm();

  const allWebMenuIcons = useMemo(() => {
    if (!webMenuIcons) return [];
    return webMenuIcons.reduce((acc: any[], current: any) => acc.concat(current.children || []), []);
  }, []);

  const getIconUrl = (iconCode: string) => {
      const iconItem = allWebMenuIcons.find((item: any) => item.code === iconCode);
      return iconItem?.icon || '';
  };

  const fetchPluginDetailWrapper = async (id: string | number) => {
      const idStr = String(id);
      if (enableDynamicMock && mockPluginDetails[idStr]) {
          return { code: 200, data: mockPluginDetails[idStr], msg: 'success' };
      }
      const res = await getPluginDetailApi(idStr);
      // Compatibility handling for direct data response
      if (res && res.code === undefined && (res.id || res.pluginId)) {
          return { code: 0, data: res };
      }
      return res;
  };

  const fetchVersions = async (pluginId: string) => {
    try {
        if (enableDynamicMock && mockPluginVersions[pluginId]) {
           setVersionList(mockPluginVersions[pluginId]);
           return;
      }
      const res = await getPluginVersionListApi(pluginId);
      
      // Compatibility handling
      if (Array.isArray(res)) {
           setVersionList(res);
      } else if (res && (res.code === 200 || res.code === 0)) {
          setVersionList(res.data || []);
      }
    } catch (error) {
        console.error(error);
        Message.error('获取版本列表失败');
    }
  };

  const sortedVersions = useMemo(() => {
    if (!versionList) return [];
    return [...versionList].sort((a, b) => 
        dayjs(b.updateTime).valueOf() - dayjs(a.updateTime).valueOf()
    );
  }, [versionList]);

  const handleCreateVersion = async () => {
      try {
          const values = await versionForm.validate();
          setEditLoading(true); // Reuse edit loading or add a new one? Reuse is fine for now or local loading
          // Actually better to use a local loading if we want to keep the drawer open and responsive?
          // But I'll use editLoading for simplicity as it's inside the drawer context mostly, 
          // wait, the modal is on top. I should use a separate loading state or just reuse createLoading?
          // Let's use createLoading since it's a "create" action.
          setCreateLoading(true);

          const formData = new FormData();
          if (currentPluginDetail) {
              formData.append('pluginId', currentPluginDetail.pluginId);
          }
          formData.append('pluginVersion', values.version);
          formData.append('pluginVersionDescription', values.versionDescription);
          if (values.file && values.file.length > 0) {
              formData.append('file', values.file[0].originFile);
          }

          const res = await uploadPluginVersionApi(formData);
          if (res) {
              Message.success('版本发布成功');
              setVersionCreateVisible(false);
              versionForm.resetFields();
              // Refresh details
              if (currentPluginDetail) {
                  fetchVersions(currentPluginDetail.pluginId);
                  // Also refresh detail for other info if needed
                  const detailRes = await fetchPluginDetailWrapper(currentPluginDetail.id);
                  if (detailRes.code === 200 || detailRes.code === 0) {
                      setCurrentPluginDetail(detailRes.data);
                  }
              }
          } else {
              Message.error(res.msg || '版本发布失败');
          }
      } catch (error) {
          console.error(error);
      } finally {
          setCreateLoading(false);
          setEditLoading(false);
      }
  };

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
      setDynamicPlugins(mockDynamicPlugins as unknown as PluginItem[]);
      setTotal(mockDynamicPlugins.length);
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
      
      let rawList: any[] = [];
      let rawTotal: number | string = 0;
      let isValidResponse = false;

      // Case 1: Standard response { code: 0, data: { list: [], total: 0 } }
      if (res && (res.code === 200 || res.code === 0) && res.data) {
          rawList = res.data.list || res.data.records || [];
          rawTotal = res.data.total || 0;
          isValidResponse = true;
      } 
      // Case 2: Direct data response { list: [], total: 0 }
      else if (res && (Array.isArray(res.list) || Array.isArray(res.records))) {
          rawList = res.list || res.records || [];
          rawTotal = res.total || 0;
          isValidResponse = true;
      }

      if (isValidResponse) {
        // 转换动态插件数据格式
        const formattedPlugins: PluginItem[] = rawList.map((plugin: any) => ({
          id: String(plugin.id),
          name: plugin.pluginName || plugin.name || '',
          remark: plugin.pluginDescription || plugin.remark || '',
          status: typeof plugin.status === 'number' ? plugin.status : 0,
          icon: String(plugin.pluginIcon || plugin.icon || ''),
          pluginId: plugin.pluginId,
          pluginVersion: plugin.pluginVersion,
          versionCount: plugin.versionCount,
          createTime: plugin.createTime,
          updateTime: plugin.updateTime,
          isDynamic: true
        }));
        
        setDynamicPlugins(formattedPlugins);
        setTotal(Number(rawTotal));
      } else {
        console.warn('Invalid response format:', res);
        setDynamicPlugins([]);
        setTotal(0);
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

  const openConfig = async (plugin: PluginItem) => {
    setConfigPlugin(plugin);
    setConfigVisible(true);
    if (plugin.isDynamic && plugin.pluginId && plugin.pluginVersion) {
        try {
            const res = await getPluginConfigTemplateApi({
                pluginId: plugin.pluginId,
                pluginVersion: plugin.pluginVersion
            });
            
            // Handle response data, compatible with different structures
            let displayData = res;
            if (res && typeof res === 'object') {
                if ('code' in res && 'data' in res) {
                     // Standard response structure
                     displayData = res.data;
                }
            }
            
            // Store raw config templates if available
            if (displayData && displayData.configTemplates) {
                setConfigTemplates(displayData.configTemplates);
                
                // Initial values from template (defaults)
                const initialValues: Record<string, any> = {};
                displayData.configTemplates.forEach((tpl: any) => {
                    if (tpl.configKey && tpl.configValue !== undefined) {
                        initialValues[tpl.configKey] = tpl.configValue;
                    }
                });

                // Fetch saved config values
                try {
                    const detailRes = await getPluginConfigDetailApi({
                        pluginId: plugin.pluginId,
                        pluginVersion: plugin.pluginVersion
                    });
                    
                    let savedConfigs: Record<string, any> = {};
                    if (detailRes && (detailRes.code === 200 || detailRes.code === 0) && detailRes.data && detailRes.data.configs) {
                        savedConfigs = detailRes.data.configs;
                    } else if (detailRes && detailRes.configs) {
                         // Direct data response case
                        savedConfigs = detailRes.configs;
                    }

                    // Merge saved values into initialValues
                    // Handle dot notation keys from savedConfigs (e.g. "baidu.apiKey")
                    // and unflatten them into nested objects for Formily if needed.
                    // However, Formily usually expects values matching the schema structure.
                    // If the schema is nested, we need nested values.
                    
                    // Helper to unflatten dot notation keys to nested object
                    const unflatten = (data: Record<string, any>) => {
                        const result: Record<string, any> = {};
                        for (const key in data) {
                             let value = data[key];
                             // Check if value is wrapped in configValue object
                             if (value && typeof value === 'object' && 'configValue' in value) {
                                 value = value.configValue;
                             }
                             
                             if (value === undefined || value === null) continue;

                             const parts = key.split('.');
                             let current = result;
                             for (let i = 0; i < parts.length - 1; i++) {
                                 const part = parts[i];
                                 if (!current[part]) current[part] = {};
                                 current = current[part];
                             }
                             current[parts[parts.length - 1]] = value;
                        }
                        return result;
                    };

                    // We don't use savedValues directly, we merge into mergedValues first
                    // const savedValues = unflatten(savedConfigs);
                    
                    const mergedValues = { ...initialValues };
                    Object.keys(savedConfigs).forEach(key => {
                        if (savedConfigs[key]?.configValue !== undefined) {
                            mergedValues[key] = savedConfigs[key].configValue;
                        }
                    });
                    
                    const finalValues = unflatten(mergedValues);
                    
                    configForm.setInitialValues(finalValues);
                    configForm.setValues(finalValues);

                } catch (err) {
                    console.error('Failed to fetch config detail', err);
                    // Fallback to template defaults if detail fetch fails
                    // But we need to unflatten template defaults too if they are dot-notation
                    const unflattenedDefaults = (() => {
                         const result: Record<string, any> = {};
                         for (const key in initialValues) {
                             const value = initialValues[key];
                             const parts = key.split('.');
                             let current = result;
                             for (let i = 0; i < parts.length - 1; i++) {
                                 const part = parts[i];
                                 if (!current[part]) current[part] = {};
                                 current = current[part];
                             }
                             current[parts[parts.length - 1]] = value;
                         }
                         return result;
                    })();
                    
                    configForm.setInitialValues(unflattenedDefaults);
                    configForm.setValues(unflattenedDefaults);
                }
            } else {
                setConfigTemplates([]);
                configForm.reset();
            }

            // Check for pluginConfigInfo field and use it if available
            if (displayData && typeof displayData === 'object' && 'pluginConfigInfo' in displayData) {
                try {
                    const configInfo = displayData.pluginConfigInfo;
                    // If it's a string, try to parse it to format it nicely
                    if (typeof configInfo === 'string') {
                         const parsed = JSON.parse(configInfo);
                         setConfigContent(JSON.stringify(parsed, null, 2));
                    } else {
                         setConfigContent(JSON.stringify(configInfo, null, 2));
                    }
                } catch (e) {
                    // Fallback to raw string if parsing fails
                    setConfigContent(String(displayData.pluginConfigInfo));
                }
            } else {
                // Fallback to displaying the whole data
                const content = typeof displayData === 'string' ? displayData : JSON.stringify(displayData, null, 2);
                setConfigContent(content);
            }
        } catch (e) {
            console.error(e);
            setConfigContent('');
            setConfigTemplates([]);
            configForm.reset();
        }
    } else {
        setConfigContent('');
        setConfigTemplates([]);
        configForm.reset();
    }
  };
  const closeConfig = () => {
    setConfigVisible(false);
    setConfigPlugin(null);
    setConfigContent('');
    setConfigTemplates([]);
    configForm.reset();
  };

  const handleSaveConfig = async () => {
      try {
          if (!configPlugin?.pluginId || !configPlugin?.pluginVersion) {
              Message.error('缺少插件信息');
              return;
          }

          const values = await configForm.submit();
          
          const configs: Record<string, any> = {};
          const formValues = values as Record<string, any>;
          
          // Helper function to recursively collect configs
          const collectConfigs = (data: any, prefix = '') => {
              Object.keys(data).forEach(key => {
                  const value = data[key];
                  const fullKey = prefix ? `${prefix}.${key}` : key;
                  const template = configTemplates.find(t => t.configKey === fullKey);
                  
                  // Case 1: Key exists in templates -> Use it directly
                  if (template) {
                      configs[fullKey] = {
                          configValue: value,
                          valueType: template.valueType || 'normal'
                      };
                  } 
                  // Case 2: Value is an object and not in templates -> Recurse
                  else if (value && typeof value === 'object' && !Array.isArray(value)) {
                      collectConfigs(value, fullKey);
                  }
                  // Case 3: Primitive value not in templates -> Add as normal config
                  else {
                       configs[fullKey] = {
                          configValue: value,
                          valueType: 'normal'
                      };
                  }
              });
          };

          collectConfigs(formValues);
          
          const payload = {
              pluginId: configPlugin.pluginId,
              pluginVersion: configPlugin.pluginVersion,
              configs
          };
          
          const res = await savePluginConfigApi(payload);
          if (res === true || (res && (res.code === 200 || res.code === 0))) {
              Message.success('保存配置成功');
              closeConfig();
          } else {
              Message.error(res?.msg || '保存配置失败');
          }
      } catch (error) {
          console.error(error);
          Message.error('保存配置失败');
      }
  };

  const openEdit = async (plugin: PluginItem) => {
    setEditVisible(true);
    setEditLoading(true);
    setVersionList([]);
    setIsEditPluginDynamic(plugin.isDynamic || false);
    setEditingPluginId(plugin.id);
    try {
      const res = await fetchPluginDetailWrapper(plugin.id);
      if (res && (res.code === 200 || res.code === 0)) {
        // Ensure id exists in data, fallback to plugin.id
        const detailData = { ...res.data };
        if (!detailData.id) {
            detailData.id = plugin.id;
        }
        setCurrentPluginDetail(detailData);
        editForm.setFieldsValue({
            pluginName: res.data.pluginName,
            icon: res.data.pluginIcon?.toString(),
            pluginDescription: res.data.pluginDescription
        });
        fetchVersions(res.data.pluginId);
      } else {
        Message.error(res?.msg || '获取插件详情失败');
      }
    } catch (error) {
      console.error(error);
      Message.error('获取插件详情失败');
    } finally {
      setEditLoading(false);
    }
  };

  const closeEdit = () => {
    setEditVisible(false);
    setCurrentPluginDetail(null);
    setEditingPluginId('');
    editForm.resetFields();
  };

  const handleSaveBasicInfo = async () => {
    try {
        const values = await editForm.validate();
        setEditLoading(true);
        const formData = new FormData();
        if (currentPluginDetail) {
             formData.append('pluginId', currentPluginDetail.pluginId);
        }
        formData.append('pluginName', values.pluginName);
        formData.append('pluginDescription', values.pluginDescription);
        if (values.icon) {
            formData.append('pluginIcon', values.icon);
        }
        
        const res = await updatePluginInfoApi(formData);
        if (res) {
            Message.success('保存成功');
            fetchPluginList(currentTab);
            // Use editingPluginId if currentPluginDetail.id is missing or unreliable
            const refreshId = currentPluginDetail?.id || editingPluginId;
            if (refreshId) {
                const detailRes = await fetchPluginDetailWrapper(refreshId);
                if (detailRes.code === 200 || detailRes.code === 0) {
                     // Ensure id exists in refreshed data
                    const detailData = { ...detailRes.data };
                    if (!detailData.id) {
                        detailData.id = refreshId;
                    }
                    setCurrentPluginDetail(detailData);
                }
            }
        } else {
            Message.error(res.msg || '保存失败');
        }
    } catch (error) {
        console.error(error);
    } finally {
        setEditLoading(false);
    }
  };

  const handleVersionAction = async (action: string, record: PluginVersionVO) => {
      try {
        if (action === 'active') {
            let res;
            if (isEditPluginDynamic) {
                 const pluginId = record.pluginId || currentPluginDetail?.pluginId;
                 const version = record.version || (record as any).pluginVersion; // Fallback to pluginVersion if version is missing
                 
                 if (pluginId && version) {
                     res = await enablePluginApi({ pluginId, pluginVersion: version });
                 } else {
                     console.error('Missing info:', { record, currentPluginDetail });
                     Message.error('缺少插件ID或版本信息');
                     return;
                 }
            } else {
                res = await activePluginVersionApi(record.id);
            }
            
            if (res) {
                Message.success('已设为生效版本');
            } else {
                Message.error(res?.msg || '操作失败');
                return;
            }
        } else if (action === 'delete') {
            const res = await deletePluginVersionApi(record.id);
            if (res) {
                Message.success('删除成功');
            } else {
                Message.error(res?.msg || '删除失败');
                return;
            }
        } else if (action === 'update') {
            Message.info('功能开发中');
            return;
        }
        const refreshId = currentPluginDetail?.id || editingPluginId;
        if (currentPluginDetail && refreshId) {
            fetchVersions(currentPluginDetail.pluginId);
            const res = await fetchPluginDetailWrapper(refreshId);
            if (res.code === 200 || res.code === 0) {
                 // Ensure id exists in refreshed data
                const detailData = { ...res.data };
                if (!detailData.id) {
                    detailData.id = refreshId;
                }
                setCurrentPluginDetail(detailData);
            }
        }
      } catch (error) {
          console.error(error);
          Message.error('操作失败');
      }
  };


  const handleCreate = async () => {
    try {
      const values = await form.validate();
      setCreateLoading(true);

      // Create Plugin
      const createFormData = new FormData();
      createFormData.append('pluginName', values.pluginName);
      createFormData.append('pluginVersion', values.pluginVersion);
      createFormData.append('pluginDescription', values.pluginDescription);
      createFormData.append('pluginVersionDescription', values.versionDescription);
      
      if (values.icon) {
        createFormData.append('pluginIcon', values.icon);
      }
      
      if (values.file && values.file.length > 0) {
        createFormData.append('file', values.file[0].originFile);
      }

      const res = await createPluginApi(createFormData);
      if (res) {
          Message.success('插件创建成功');
          setCreateVisible(false);
          form.resetFields();
          fetchPluginList(currentTab);
      } else {
          Message.error(res.msg || '创建失败');
      }
    } catch (error) {
      console.error(error);
      // Message.error('创建失败，请检查表单');
    } finally {
      setCreateLoading(false);
    }
  };

  // 切换开关状态
  const handleSwitchChange = async (plugin: PluginItem, checked: boolean) => {
    const { id, name, isDynamic, pluginId, pluginVersion } = plugin;

    if (checked === false) {
      return Modal.confirm({
        title: `关闭"${name}"插件？`,
        content: '关闭后，外部用户将无法访问应用，再次启用时可恢复正常访问',
        okButtonProps: {
          status: 'danger'
        },
        onOk: async () => {
          let res;
          if (isDynamic) {
             if (pluginId && pluginVersion) {
                 res = await disablePluginApi({ pluginId, pluginVersion });
             } else {
                 Message.error('缺少插件ID或版本信息');
                 return;
             }
          } else {
             res = await updatePluginStatusApi(id, 0);
          }

          if (res) {
               await fetchPluginList(currentTab);
               Message.success(`已关闭插件:${name}`);
           } else {
               Message.error(res?.msg || '关闭插件失败');
           }
        }
      });
    } else {
      let res;
      if (isDynamic) {
          if (pluginId && pluginVersion) {
              res = await enablePluginApi({ pluginId, pluginVersion });
          } else {
              Message.error('缺少插件ID或版本信息');
              return;
          }
      } else {
          res = await updatePluginStatusApi(id, 1);
      }
      
      if (res) {
          await fetchPluginList(currentTab);
          Message.success(`已启用插件:${name}`);
      } else {
          Message.error(res?.msg || '启用插件失败');
      }
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
        extra={
            <Space>
                <Input.Search className={styles.search} placeholder="搜索插件" onChange={handleSearchChange} />
                <Button type="primary" icon={<IconPlus />} onClick={() => setCreateVisible(true)}>新增插件</Button>
            </Space>
        }
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
                            <div style={{ display: 'flex', alignItems: 'center', flex: 1, minWidth: 0, gap: '16px' }}>
                              <div className={styles.icon} style={{ backgroundColor: '#009E9E', flexShrink: 0 }}>
                                <img
                                  src={externalUserSVG}
                                  style={{ width: '32px', height: '32px', filter: 'brightness(0) invert(1)' }}
                                />
                              </div>
                              <div className={styles.textContent}>
                                <span className={styles.name}>{plugin.name}</span>
                                <span className={styles.description}>{plugin.remark}</span>
                              </div>
                            </div>
                            {hasPermission(TENANT_CONFIG_UPDATE) && (
                              <div style={{ paddingLeft: '16px', flexShrink: 0 }}>
                                <Switch
                                  checked={plugin.status === 1 ? true : false}
                                  onChange={(checked) => handleSwitchChange(plugin, checked)}
                                />
                              </div>
                            )}
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
                            <div style={{ display: 'flex', alignItems: 'center', flex: 1, minWidth: 0, gap: '16px' }}>
                              <div className={styles.icon} style={{ backgroundColor: '#165DFF', flexShrink: 0 }}>
                                {getIconUrl(plugin.icon) ? (
                                    <img
                                      src={getIconUrl(plugin.icon)}
                                      style={{ width: '32px', height: '32px', filter: 'brightness(0) invert(1)' }}
                                    />
                                ) : (
                                    <img
                                      src={externalUserSVG}
                                      style={{ width: '32px', height: '32px', filter: 'brightness(0) invert(1)' }}
                                    />
                                )}
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
                            </div>
                            {hasPermission(TENANT_CONFIG_UPDATE) && (
                              <div style={{ paddingLeft: '16px', flexShrink: 0 }}>
                                <Switch
                                  checked={plugin.status === 1 ? true : false}
                                  onChange={(checked) => handleSwitchChange(plugin, checked)}
                                />
                              </div>
                            )}
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
        width={800}
        title="编辑插件"
        onCancel={closeEdit}
        footer={
          <div style={{ textAlign: 'right' }}>
            <Space>
              <Button onClick={closeEdit}>取消</Button>
              <Button type="primary" onClick={handleSaveBasicInfo}>保存</Button>
            </Space>
          </div>
        }
        unmountOnExit
        maskClosable={false}
      >
        <Spin loading={editLoading} style={{ width: '100%', minHeight: 200 }}>
            <div style={{ marginBottom: 24 }}>
                <div style={{ fontSize: 16, fontWeight: 500, marginBottom: 16, borderLeft: '4px solid rgb(var(--primary-6))', paddingLeft: 8 }}>基础信息</div>
                <Form form={editForm} layout="vertical">
                    <Form.Item label="插件名称" field="pluginName" rules={[{ required: true, message: '请输入插件名称' }]}>
                        <Input placeholder="请输入插件名称" />
                    </Form.Item>
                    <Form.Item label="插件图标" field="icon" rules={[{ required: true, message: '请选择插件图标' }]}>
                        {(formData) => {
                            const iconCode = formData?.icon;
                            const iconUrl = iconCode ? getIconUrl(iconCode) : '';
                            return (
                                <div
                                    style={{
                                    width: 80,
                                    height: 80,
                                    border: '1px dashed var(--color-border-3)',
                                    backgroundColor: 'var(--color-fill-2)',
                                    borderRadius: 2,
                                    display: 'flex',
                                    flexDirection: 'column',
                                    alignItems: 'center',
                                    justifyContent: 'center',
                                    cursor: 'pointer',
                                    color: 'var(--color-text-3)',
                                    transition: 'all 0.1s cubic-bezier(0, 0, 1, 1)',
                                    }}
                                    onClick={() => {
                                        setIconSelectorSource('edit');
                                        setIconSelectorVisible(true);
                                    }}
                                >
                                    {iconUrl ? (
                                        <img src={iconUrl} style={{ width: 32, height: 32 }} alt="icon" />
                                    ) : (
                                        <IconPlus style={{ fontSize: 16 }} />
                                    )}
                                    <div style={{ marginTop: 8, fontSize: 12 }}>选择图标</div>
                                </div>
                            );
                        }}
                    </Form.Item>
                    <Form.Item label="插件描述" field="pluginDescription" rules={[{ required: true, message: '请输入描述信息' }]}>
                        <Input.TextArea placeholder="请输入描述信息" rows={3} />
                    </Form.Item>
                </Form>
            </div>
            
            <Divider />
            
            <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
                    <div style={{ fontSize: 16, fontWeight: 500, borderLeft: '4px solid rgb(var(--primary-6))', paddingLeft: 8 }}>版本管理</div>
                    <Button type="primary" icon={<IconPlus />} onClick={() => setVersionCreateVisible(true)}>新增版本</Button>
                </div>
                <Table
                    columns={[
                        {
                          title: '插件版本',
                          dataIndex: 'version',
                          key: 'version',
                          render: (text: string, record: PluginVersionVO) => (
                            <Space>
                               <span>{text || (record as any).pluginVersion}</span>
                               {record.status === 1 && <span style={{ color: '#00B42A', backgroundColor: '#E8FFEA', padding: '2px 8px', borderRadius: '2px', fontSize: '12px' }}>已生效</span>}
                               {record.status === 0 && <span style={{ color: '#86909C', backgroundColor: '#F2F3F5', padding: '2px 8px', borderRadius: '2px', fontSize: '12px' }}>已停用</span>}
                            </Space>
                          )
                        },
                        {
                          title: '版本描述',
                          dataIndex: 'pluginVersionDescription',
                          key: 'pluginVersionDescription',
                          render: (text: string) => text || '-'
                        },
                        {
                          title: '创建时间',
                          dataIndex: 'createTime',
                          key: 'createTime',
                          render: (text: string) => text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : '-'
                        },
                        {
                          title: '操作',
                          key: 'action',
                          width: 250,
                          render: (_: any, record: PluginVersionVO) => (
                            <Space split={<Divider type="vertical" />}>
                              <Button 
                                type="text" 
                                size="small" 
                                disabled={record.status === 1}
                                onClick={() => handleVersionAction('active', record)}
                              >
                                设为生效
                              </Button>
                              <Popconfirm
                                title="确定删除该版本？"
                                onOk={() => handleVersionAction('delete', record)}
                                disabled={record.status === 1}
                              >
                                <Button 
                                    type="text" 
                                    status="danger" 
                                    size="small"
                                    disabled={record.status === 1}
                                >
                                    删除
                                </Button>
                              </Popconfirm>
                            </Space>
                          )
                        }
                    ]}
                    data={sortedVersions}
                    rowKey="id"
                    pagination={false}
                />
            </div>
        </Spin>
      </Drawer>
      <Modal
        visible={versionCreateVisible}
        title="发布新版本"
        onCancel={() => {
            setVersionCreateVisible(false);
            versionForm.resetFields();
        }}
        onOk={handleCreateVersion}
        confirmLoading={createLoading}
        unmountOnExit
        maskClosable={false}
      >
        <Form form={versionForm} layout="vertical">
            <Form.Item label="版本号" field="version" rules={[{ required: true, message: '请输入版本号' }]}>
                <Input placeholder="例如：v1.0.1" />
            </Form.Item>
            <Form.Item label="版本描述" field="versionDescription" rules={[{ required: true, message: '请输入版本描述' }]}>
                <Input.TextArea placeholder="请输入版本描述" rows={3} />
            </Form.Item>
            <Form.Item label="上传文件" field="file" rules={[{ required: true, message: '请上传插件安装包' }]}>
                <Upload
                    drag
                    limit={1}
                    accept=".zip"
                    autoUpload={false}
                    tip="支持zip格式"
                />
            </Form.Item>
        </Form>
      </Modal>
      <Drawer
        visible={configVisible}
        width={800}
        title={configPlugin?.name || '插件配置'}
        onCancel={closeConfig}
        unmountOnExit
        maskClosable
        footer={
          <div style={{ textAlign: 'right' }}>
            <Space>
              <Button onClick={closeConfig}>取消</Button>
              <Button type="primary" onClick={handleSaveConfig}>保存</Button>
            </Space>
          </div>
        }
      >
        <div className={styles.configContent}>
          {(() => {
              let schema = null;
              try {
                  schema = configContent ? JSON.parse(configContent) : null;
              } catch(e) {}
              
              if (schema && typeof schema === 'object' && (schema.properties || schema.type === 'object')) {
                   return (
                       <div style={{ padding: '0 16px', height: 'calc(100vh - 150px)', overflowY: 'auto' }}>
                           <DynamicForm schema={schema} form={configForm} />
                       </div>
                   );
              }
          })()}
        </div>
      </Drawer>
      <Modal
        visible={createVisible}
        title="新建自定义插件"
        onCancel={() => {
            setCreateVisible(false);
            form.resetFields();
        }}
        onOk={handleCreate}
        confirmLoading={createLoading}
        unmountOnExit
        maskClosable={false}
        style={{ width: 600 }}
      >
        <Form form={form} layout="vertical">
          <Form.Item label="插件名称" field="pluginName" rules={[{ required: true, message: '请输入插件名称' }]}>
            <Input placeholder="请输入插件名称" />
          </Form.Item>
          <Form.Item label="插件图标" field="icon" rules={[{ required: true, message: '请选择插件图标' }]}>
            {(formData) => {
                const iconCode = formData?.icon;
                const iconUrl = iconCode ? getIconUrl(iconCode) : '';
                return (
                    <div
                        style={{
                        width: 80,
                        height: 80,
                        border: '1px dashed var(--color-border-3)',
                        backgroundColor: 'var(--color-fill-2)',
                        borderRadius: 2,
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                        justifyContent: 'center',
                        cursor: 'pointer',
                        color: 'var(--color-text-3)',
                        transition: 'all 0.1s cubic-bezier(0, 0, 1, 1)',
                        }}
                        onClick={() => setIconSelectorVisible(true)}
                    >
                        {iconUrl ? (
                            <img src={iconUrl} style={{ width: 32, height: 32 }} alt="icon" />
                        ) : (
                            <IconPlus style={{ fontSize: 16 }} />
                        )}
                        <div style={{ marginTop: 8, fontSize: 12 }}>选择图标</div>
                    </div>
                );
            }}
          </Form.Item>
          <Form.Item label="插件描述" field="pluginDescription" rules={[{ required: true, message: '请输入描述信息' }]}>
            <Input.TextArea placeholder="请输入描述信息" rows={3} />
          </Form.Item>
          <Form.Item label="插件版本" field="pluginVersion" rules={[{ required: true, message: '请输入插件版本，例如：v1.0.0' }]}>
            <Input placeholder="例如：v1.0.0" />
          </Form.Item>
          <Form.Item label="版本描述" field="versionDescription" rules={[{ required: true, message: '请输入版本描述' }]}>
            <Input.TextArea placeholder="请输入版本描述" />
          </Form.Item>
          <Form.Item label="插件安装包" field="file" rules={[{ required: true, message: '请上传插件安装包' }]}>
             <Upload
                className={styles.uploadWrapper}
                drag
                limit={1}
                accept=".zip"
                autoUpload={false}
                tip="支持zip、jar格式，单个文件不超过200MB"
             >
                <div style={{ 
                    padding: '20px 0', 
                    color: 'var(--color-text-3)', 
                    textAlign: 'center',
                    backgroundColor: 'var(--color-fill-2)',
                    border: '1px dashed var(--color-border-3)',
                    borderRadius: 4
                }}>
                    <IconUpload style={{ fontSize: 24, color: 'var(--color-text-3)', marginBottom: 10 }} />
                    <div style={{ marginTop: 0 }}>拖拽文件到此处，或<span style={{ color: 'rgb(var(--primary-6))' }}>点击上传</span></div>
                </div>
             </Upload>
          </Form.Item>
        </Form>
      </Modal>
      <Modal
        title="选择图标"
        visible={iconSelectorVisible}
        onCancel={() => setIconSelectorVisible(false)}
        footer={null}
        style={{ width: '800px' }}
        unmountOnExit
      >
        <div style={{ padding: 0, height: '600px', overflow: 'hidden' }}>
          <MenuComp
            style={{ position: 'relative', transform: 'none', height: '100%' }}
            onSelected={(iconCode) => {
                if (iconSelectorSource === 'create') {
                    form.setFieldValue('icon', iconCode);
                } else {
                    editForm.setFieldValue('icon', iconCode);
                }
                setIconSelectorVisible(false);
            }}
            handleBack={() => setIconSelectorVisible(false)}
          />
        </div>
      </Modal>
    </div>
  );
};

export default PluginPage;
