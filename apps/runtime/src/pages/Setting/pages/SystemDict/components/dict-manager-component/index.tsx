/**
 * 抽取字典管理模块为独立组件
 */
import InfoPanel from '@/components/InfoPanel';
import DictionaryTable from '@/pages/Setting/pages/SystemDict/components/dict-data-table';
import DictList from '@/pages/Setting/pages/SystemDict/components/dict-list';
import { Divider, Empty, Layout, Message, Modal, Space, Tabs } from '@arco-design/web-react';
import { StatusEnum, type DictData, type DictItem, type PageParam } from '@onebase/platform-center';
import {
  createDict,
  createDictData,
  deleteDict,
  deleteDictData,
  getAllDictList,
  getDictDataListByPage,
  updateDict,
  updateDictData,
  updateDictDataStatus,
  batchConfigDictData
} from '@onebase/platform-center';
import { debounce } from 'lodash-es';
import { useEffect, useState } from 'react';
import DictDataModal from '@/pages/Setting/pages/SystemDict/components/dict-data-modal';
import DictModal from '@/pages/Setting/pages/SystemDict/components/dict-modal';
import BatchConfigModal from '@/pages/Setting/pages/SystemDict/components/batch-config-modal';
import styles from '../../index.module.less';
import { TENANT_DICT_PERMISSION as ACTIONS } from '@/constants/permission';
import { PermissionButton as Button } from '@/components/PermissionControl';
import { useLocation } from 'react-router-dom';
import StatusTag, { getStatusLabel, StatusLabelEnum } from '@/components/StatusTag';

const Sider = Layout.Sider;
const Header = Layout.Header;
const Content = Layout.Content;

export interface DictManagerConfig {
  // 权限配置
  permissions?: {
    create?: string;
    update?: string;
    delete?: string;
    query?: string;
    status?: string;
  };
  // API 配置
  api?: {
    getDictList?: (dictOwnerType: string) => Promise<DictItem[]>;
    getDictDataList?: (params: any) => Promise<{ list: DictData[]; total: number }>;
    createDict?: (data: DictItem) => Promise<any>;
    updateDict?: (data: DictItem) => Promise<any>;
    deleteDict?: (id: number) => Promise<any>;
    createDictData?: (data: DictData) => Promise<any>;
    updateDictData?: (data: DictData) => Promise<any>;
    deleteDictData?: (id: number) => Promise<any>;
    updateDictDataStatus?: (params: { id: number; status: number }) => Promise<any>;
    batchConfigDictData?: (data: DictData[]) => Promise<any>;
    batchUpdateDictData?: (data: DictData[]) => Promise<any>;
  };
  // UI 配置
  ui?: {
    title?: string;
    emptyText?: string;
    dictSearchPlaceholder?: string;
    dictDataSearchPlaceholder?: string;
    addDictButtonText?: string;
    addDictDataButtonText?: string;
  };
  // Tabs 配置
  tabs?: {
    enabled?: boolean;
    systemDictTab?: {
      key: string;
      title: string;
      api?: DictManagerConfig['api'];
      permissions?: DictManagerConfig['permissions'];
    };
    customDictTab?: {
      key: string;
      title: string;
      api?: DictManagerConfig['api'];
      permissions?: DictManagerConfig['permissions'];
    };
  };
  // 样式配置
  className?: string;
  style?: React.CSSProperties;
}

interface DictManagerProps {
  config?: DictManagerConfig;
  onDictChange?: (dict: DictItem | undefined) => void;
  onDictDataChange?: (data: DictData[]) => void;
}

export default function DictManager({ config = {}, onDictChange, onDictDataChange }: DictManagerProps) {
  // 合并默认配置
  const finalConfig: Required<DictManagerConfig> = {
    permissions: {
      create: ACTIONS.CREATE,
      update: ACTIONS.UPDATE,
      delete: ACTIONS.DELETE,
      query: ACTIONS.QUERY,
      status: ACTIONS.STATUS,
      ...config.permissions
    },
    api: {
      getDictList: (dictOwnerType: string) => getAllDictList({ dictOwnerType }),
      getDictDataList: getDictDataListByPage,
      createDict,
      updateDict,
      deleteDict,
      createDictData,
      updateDictData,
      deleteDictData,
      updateDictDataStatus,
      batchConfigDictData,
      ...config.api
    },
    ui: {
      title: '字典管理',
      emptyText: '暂无数据',
      dictSearchPlaceholder: '输入字典名称',
      dictDataSearchPlaceholder: '搜索字典值',
      addDictButtonText: '新建',
      addDictDataButtonText: '添加',
      ...config.ui
    },
    tabs: {
      enabled: false,
      systemDictTab: {
        key: 'tenant',
        title: '公共字典',
        ...config.tabs?.systemDictTab
      },
      customDictTab: {
        key: 'app',
        title: '自定义字典',
        ...config.tabs?.customDictTab
      },
      ...config.tabs
    },
    className: '',
    style: {},
    ...config
  };
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const appId = searchParams.get('appId');

  // Tabs 相关状态
  const [activeTab, setActiveTab] = useState<string>(finalConfig.tabs.systemDictTab?.key || '');
  // 字典相关状态
  const [dictList, setDictList] = useState<DictItem[]>([]);
  const [activeDictId, setActiveDictId] = useState<number | undefined>(undefined);
  const [showEmpty, setShowEmpty] = useState<boolean>(false);
  const [activeDict, setActiveDict] = useState<DictItem | undefined>(undefined);
  const [tableData, setTableData] = useState<DictData[]>([]);
  const [_loading, setLoading] = useState<boolean>(false);
  const [currentPage, setCurrentPage] = useState<number>(1);
  const [pageSize, setPageSize] = useState<number>(10);
  const [total, setTotal] = useState<number>(0);
  const [dictSearch, setDictSearch] = useState('');
  const [filteredDictList, setFilteredDictList] = useState<DictItem[]>([]);
  const [dictDataSearch, setDictDataSearch] = useState('');
  const [addDictModalVisible, setAddDictModalVisible] = useState(false);
  const [editDict, setEditDict] = useState<DictItem | null>(null);
  const [modalLoading, setModalLoading] = useState(false);
  const [dictDataModalVisible, setDictDataModalVisible] = useState(false);
  const [dictDataModalLoading, setDictDataModalLoading] = useState(false);
  const [editItem, setEditItem] = useState<DictData | null>(null);
  const [batchConfigModalVisible, setBatchConfigModalVisible] = useState(false);
  const [batchConfigLoading, setBatchConfigLoading] = useState(false);

  // 获取当前tab的配置
  const getCurrentTabConfig = () => {
    if (activeTab === finalConfig.tabs.systemDictTab?.key) {
      return {
        api: { ...finalConfig.api, ...finalConfig.tabs.systemDictTab?.api },
        permissions: { ...finalConfig.permissions, ...finalConfig.tabs.systemDictTab?.permissions }
      };
    } else if (activeTab === finalConfig.tabs.customDictTab?.key) {
      return {
        api: { ...finalConfig.api, ...finalConfig.tabs.customDictTab.api },
        permissions: { ...finalConfig.permissions, ...finalConfig.tabs.customDictTab.permissions }
      };
    }
    return { api: finalConfig.api, permissions: finalConfig.permissions };
  };

  const currentTabConfig = getCurrentTabConfig();

  useEffect(() => {
    if (!activeDictId) {
      setShowEmpty(false);
    }
  }, [activeDictId]);

  const loadDictList = async () => {
    try {
      const data = await currentTabConfig.api?.getDictList?.(activeTab);
      setDictList(data || []);
      if (activeDictId && data.findIndex((item) => item.id === activeDictId) > -1) {
        setActiveDictId(activeDictId);
        setActiveDict(dictList.find((t) => t.id === activeDictId));
      } else if (data.length > 0) {
        setActiveDictId(data[0].id);
      }
    } catch (error) {
      console.error('加载字典列表失败:', error);
    }
  };

  useEffect(() => {
    loadDictList();
  }, []);

  // 当tab切换时，重新加载数据
  useEffect(() => {
    if (finalConfig.tabs.enabled) {
      setActiveDictId(undefined);
      setActiveDict(undefined);
      setTableData([]);
      setCurrentPage(1);
      setDictSearch('');
      setDictDataSearch('');
      loadDictList();
    }
  }, [activeTab]);

  const loadTableData = async (searchKeyword?: string) => {
    setLoading(true);
    const params: PageParam & { dictType: string; label?: string } = {
      dictType: dictList.find((t) => t.id === activeDictId)?.type || '',
      pageNo: currentPage,
      pageSize,
      ...(searchKeyword ? { label: searchKeyword } : {})
    };

    try {
      const res = await currentTabConfig.api.getDictDataList(params);
      setTableData(res.list);
      setTotal(res.total);
      onDictDataChange?.(res.list);
    } catch (error) {
      console.error('加载字典数据失败:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (activeDictId !== undefined) {
      loadTableData();
    }
  }, [activeDictId, currentPage, pageSize]);

  useEffect(() => {
    if (!activeDictId) return;
    const activeDict = dictList.find((item) => item.id === activeDictId);
    setActiveDict(activeDict);
    onDictChange?.(activeDict);
  }, [activeDictId, dictList, onDictChange]);

  useEffect(() => {
    setFilteredDictList(
      dictSearch ? dictList.filter((t) => t.name.includes(dictSearch) || t.type.includes(dictSearch)) : dictList
    );
  }, [dictList, dictSearch]);

  useEffect(() => {
    if (activeDictId !== undefined) {
      debouncedLoadTableData(dictDataSearch);
    }
    return () => {
      debouncedLoadTableData.cancel();
    };
  }, [dictDataSearch]);

  const debouncedLoadTableData = debounce((keyword: string) => {
    loadTableData(keyword);
  }, 300);

  const handleDictDataSearch = (value: string) => {
    setDictDataSearch(value);
    setCurrentPage(1);
  };

  const getStatusButtonText = (status: number) => {
    const isEnable = status === StatusEnum.ENABLE;
    return isEnable ? StatusLabelEnum.DISABLE : StatusLabelEnum.ENABLE;
  };

  // 禁用/编辑/删除字典按钮
  const OperationButtons = (
    <Space size="small">
      {/* <Button
        permission={currentTabConfig.permissions.update}
        type="secondary"
        onClick={() => {
          handleUpdateDictStatus(activeDict?.id!, activeDict?.status as StatusEnum);
        }}
      >
        {getStatusButtonText(activeDict?.status as StatusEnum)}
      </Button> */}
      <Button
        permission={currentTabConfig.permissions.update}
        type="secondary"
        onClick={() => {
          setAddDictModalVisible(true);
          setEditDict(activeDict!);
        }}
      >
        编辑
      </Button>
      <Button
        permission={currentTabConfig.permissions.delete}
        type="secondary"
        onClick={() => {
          handleDeleteDict(activeDict?.id!);
        }}
      >
        删除
      </Button>
    </Space>
  );

  const handleDictSelect = (id: number | undefined) => {
    setActiveDictId(id);
    setCurrentPage(1);
    setShowEmpty(false);
  };

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  const handleUpdateDictStatus = (id: number, status: StatusEnum) => {
    const label = getStatusButtonText(status);
    Modal.confirm({
      title: `确认${label}`,
      content: `确定要${label}这条数据吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        handleUpdateDictStatusOk(id, status);
      }
    });
  };

  const handleUpdateDictStatusOk = async (id: number, status: StatusEnum) => {
    try {
      const params = { ...activeDict, status: status === StatusEnum.ENABLE ? StatusEnum.DISABLE : StatusEnum.ENABLE };
      await currentTabConfig.api?.updateDict?.(params);
      Message.success('操作成功');
      loadDictList();
    } catch (error) {
      console.error('操作失败:', error);
    }
  };

  // 删除字典
  const handleDeleteDict = async (id: number) => {
    Modal.confirm({
      title: '确认删除',
      content: '确定要删除这条数据吗？',
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        handleDeleteDictOk(id);
      }
    });
  };

  const handleDeleteDictOk = async (id: number) => {
    try {
      await currentTabConfig.api.deleteDict(id);
      Message.success('删除成功');
      setShowEmpty(true);
      setDictList(dictList.filter((t) => t.id !== id));
    } catch (error) {
      console.error('删除字典失败:', error);
    }
  };

  // 删除字典数据
  const handleDeleteDictData = async (id: number) => {
    Modal.confirm({
      title: '确认删除',
      content: '确定要删除这条数据吗？',
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        try {
          await currentTabConfig.api.deleteDictData(id);
          Message.success('删除成功');

          const params: PageParam & { dictType: string } = {
            dictType: dictList.find((t) => t.id === activeDictId)?.type || '',
            pageNo: currentPage,
            pageSize
          };
          const res = await currentTabConfig.api.getDictDataList(params);
          setTableData(res.list);
          setTotal(res.total);
          onDictDataChange?.(res.list);
        } catch (error) {
          console.error('删除失败:', error);
          Message.error('删除失败');
        }
      }
    });
  };

  // 新增/编辑字典
  const handleDictModalOk = async (values: DictItem) => {
    setModalLoading(true);
    try {
      if (editDict && editDict.id) {
        await currentTabConfig.api.updateDict({ ...editDict, ...values });
        setDictList((prev) => prev.map((t) => (t.id === editDict.id ? { ...t, ...values } : t)));
      } else {
        await currentTabConfig.api.createDict({ ...values, dictOwnerType: activeTab });
        const data = await currentTabConfig.api?.getDictList?.(activeTab);
        setDictList(data);
      }
      setAddDictModalVisible(false);
      setEditDict(null);
      loadDictList();
    } finally {
      setModalLoading(false);
    }
  };

  const handleDictDataEdit = (item: DictData) => {
    setEditItem(tableData.find((d) => d.id === item.id) || null);
    setDictDataModalVisible(true);
  };

  // 字典数据新增/编辑提交
  const handleDictDataModalOk = async (values: DictData) => {
    setDictDataModalLoading(true);
    try {
      if (editItem && editItem.dictType) {
        await currentTabConfig.api.updateDictData({ ...values, dictType: editItem.dictType, id: editItem.id });
      } else if (activeDict?.type) {
        await currentTabConfig.api.createDictData({ dictType: activeDict.type, ...values });
      }
      // 刷新表格
      if (activeDictId !== undefined) {
        const params: PageParam & { dictType: string } = {
          dictType: dictList.find((t) => t.id === activeDictId)?.type || '',
          pageNo: currentPage,
          pageSize
        };
        const res = await currentTabConfig.api.getDictDataList(params);
        setTableData(res.list);
        setTotal(res.total);
        onDictDataChange?.(res.list);
      }
      setDictDataModalVisible(false);
      setEditItem(null);
    } finally {
      setDictDataModalLoading(false);
    }
  };

  const handleUpdateDictDataStatus = (id: number, status: number) => {
    const params = {
      id,
      status
    };
    currentTabConfig.api.updateDictDataStatus(params).then(() => {
      Message.success('操作成功');
      loadTableData();
    });
  };

  // 批量配置字典值
  const handleBatchConfig = () => {
    setBatchConfigModalVisible(true);
  };

  // 批量配置确认
  const handleBatchConfigOk = async (values: any[]) => {
    setBatchConfigLoading(true);
    try {
      if (!activeDict?.type) {
        Message.error('请先选择字典');
        return;
      }

      // 分离新增和更新的数据
      const newItems = values
        .filter((item) => item.id.startsWith('temp-'))
        .map((item) => ({
          ...item,
          id: '',
          dictType: activeDict.type
        }));
      const updateItems = values
        .filter((item) => !item.id.startsWith('temp-') && !item.isDelete)
        .map((item) => ({
          ...item,
          dictType: activeDict.type
        }));
      const deleteItems = values.filter((item) => item.isDelete).map((item) => item.id);

      console.log('batchConfigDictData', newItems, updateItems, deleteItems);
      await currentTabConfig.api?.batchConfigDictData({
        createList: newItems,
        updateList: updateItems,
        deleteIds: deleteItems
      });

      Message.success('批量配置成功');
      setBatchConfigModalVisible(false);
      loadTableData();
    } catch (error) {
      console.error('批量配置失败:', error);
      Message.error('批量配置失败');
    } finally {
      setBatchConfigLoading(false);
    }
  };

  return (
    <div className={`${styles.systemDictPage} ${finalConfig.className}`} style={finalConfig.style}>
      <Layout className={styles.pageLayout}>
        <Sider width={252} className={styles.leftPanel}>
          {finalConfig.tabs.enabled ? (
            <Tabs activeTab={activeTab} onChange={setActiveTab} type="line" className={styles.tabsContainer}>
              <Tabs.TabPane key={finalConfig.tabs.systemDictTab.key} title={finalConfig.tabs.systemDictTab.title}>
                <DictList
                  list={filteredDictList}
                  activeId={activeDictId || undefined}
                  searchValue={dictSearch}
                  onSearchChange={setDictSearch}
                  onAdd={() => {
                    setAddDictModalVisible(true);
                    setEditDict(null);
                  }}
                  onSelect={(id) => handleDictSelect(id)}
                />
              </Tabs.TabPane>
              <Tabs.TabPane key={finalConfig.tabs.customDictTab.key} title={finalConfig.tabs.customDictTab.title}>
                <DictList
                  list={filteredDictList}
                  activeId={activeDictId || undefined}
                  searchValue={dictSearch}
                  onSearchChange={setDictSearch}
                  onAdd={() => {
                    setAddDictModalVisible(true);
                    setEditDict(null);
                  }}
                  onSelect={(id) => handleDictSelect(id)}
                />
              </Tabs.TabPane>
            </Tabs>
          ) : (
            <DictList
              list={filteredDictList}
              activeId={activeDictId || undefined}
              searchValue={dictSearch}
              onSearchChange={setDictSearch}
              onAdd={() => {
                setAddDictModalVisible(true);
                setEditDict(null);
              }}
              onSelect={(id) => handleDictSelect(id)}
            />
          )}
        </Sider>
        <Content className={styles.rightPanel}>
          {!activeDictId || showEmpty ? (
            <Empty description={finalConfig.ui.emptyText} />
          ) : (
            <>
              <Header>
                <InfoPanel
                  title={activeDict?.name}
                  description={activeDict?.remark}
                  rightChildren={OperationButtons}
                  wrapperClassName={styles.infoPanel}
                  titleChildren={<StatusTag status={activeDict?.status} type="tag" />}
                />
                <Divider style={{ margin: '16px 0' }} />
              </Header>
              <DictionaryTable
                data={tableData}
                currentPage={currentPage}
                pageSize={pageSize}
                total={total}
                onPageChange={handlePageChange}
                onPageSizeChange={setPageSize}
                searchValue={dictDataSearch}
                onSearchChange={handleDictDataSearch}
                onBatchConfig={handleBatchConfig}
              />
            </>
          )}
        </Content>
      </Layout>
      {addDictModalVisible && (
        <DictModal
          visible={addDictModalVisible}
          loading={modalLoading}
          initialValues={
            editDict
              ? {
                type: editDict.type,
                name: editDict.name,
                remark: editDict.remark,
                status: editDict.status
              }
              : undefined
          }
          onOk={handleDictModalOk}
          onCancel={() => {
            setAddDictModalVisible(false);
            setEditDict(null);
          }}
          title={editDict ? '编辑数据字典' : '新建数据字典'}
        />
      )}
      <DictDataModal
        visible={dictDataModalVisible}
        loading={dictDataModalLoading}
        initialValues={
          editItem
            ? {
              label: editItem.label,
              value: editItem.value,
              remark: editItem.remark,
              sort: editItem.sort,
              status: editItem.status,
              dictType: editItem.dictType
            }
            : undefined
        }
        onOk={handleDictDataModalOk}
        onCancel={() => {
          setDictDataModalVisible(false);
          setEditItem(null);
        }}
        title={editItem ? '编辑字典值' : '添加字典值'}
      />
      <BatchConfigModal
        visible={batchConfigModalVisible}
        onCancel={() => setBatchConfigModalVisible(false)}
        onOk={handleBatchConfigOk}
        loading={batchConfigLoading}
        initialValues={tableData}
        dictType={activeDict?.type || ''}
      />
    </div>
  );
}
