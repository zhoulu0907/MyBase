import InfoPanel from '@/components/InfoPanel';
import DictionaryTable from '@/pages/Setting/pages/SystemDict/components/dict-data-table';
import DictList from '@/pages/Setting/pages/SystemDict/components/dict-list';
import { Divider, Empty, Layout, Message, Modal, Space } from '@arco-design/web-react';
import type { DictData, DictItem, PageParam } from '@onebase/platform-center';
import {
  createDict,
  createDictData,
  deleteDict,
  deleteDictData,
  getAllDictList,
  getDictDataListByPage,
  updateDict,
  updateDictData,
  updateDictDataStatus
} from '@onebase/platform-center';
import { debounce } from 'lodash-es';
import { useEffect, useState } from 'react';
import DictDataModal from './components/dict-data-modal';
import DictModal from './components/dict-modal';
import styles from './index.module.less';
import { TENANT_DICT_PERMISSION as ACTIONS } from '@/constants/permission';
import { PermissionButton as Button } from '@/components/PermissionControl';

const Sider = Layout.Sider;
const Header = Layout.Header;
const Content = Layout.Content;

export default function SystemDictPage() {
  const [dictList, setDictList] = useState<DictItem[]>([]);
  const [activeDictId, setActiveDictId] = useState<number | undefined>(undefined);
  const [showEmpty, setShowEmpty] = useState<boolean>(false); // 显示空页面
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

  useEffect(() => {
    // console.log('activeDictId:', activeDictId);
    // console.log('showEmpty:', showEmpty);
    console.log('!activeDictId || activeDictId:', !activeDictId || showEmpty);
    if (!activeDictId) {
      setShowEmpty(false);
    }
  }, [activeDictId]);
  const loadDictList = async () => {
    getAllDictList().then((data) => {
      setDictList(data);
      if (activeDictId && data.findIndex((item) => item.id === activeDictId) > -1) {
        setActiveDictId(activeDictId);
        setActiveDict(dictList.find((t) => t.id === activeDictId));
      } else if (data.length > 0) {
        setActiveDictId(data[0].id);
      }
    });
  };
  useEffect(() => {
    loadDictList();
  }, []);
  const loadTableData = async (searchKeyword?: string) => {
    setLoading(true);
    const params: PageParam & { dictType: string; label?: string } = {
      dictType: dictList.find((t) => t.id === activeDictId)?.type || '',
      pageNo: currentPage,
      pageSize,
      ...(searchKeyword ? { label: searchKeyword } : {})
    };
    setLoading(false);
    getDictDataListByPage(params)
      .then((res) => {
        setTableData(res.list);
        setTotal(res.total);
      })
      .finally(() => {
        setLoading(false);
      });
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
  }, [activeDictId, dictList]);

  useEffect(() => {
    setFilteredDictList(
      dictSearch ? dictList.filter((t) => t.name.includes(dictSearch) || t.type.includes(dictSearch)) : dictList
    );
  }, [dictList, dictSearch]);

  const debouncedLoadTableData = debounce((keyword: string) => {
    loadTableData(keyword);
  }, 300);

  useEffect(() => {
    if (activeDictId !== undefined) {
      loadTableData(dictDataSearch);
    }
  }, [activeDictId, currentPage, pageSize]);

  useEffect(() => {
    if (activeDictId !== undefined) {
      debouncedLoadTableData(dictDataSearch);
    }
    return () => {
      debouncedLoadTableData.cancel();
    };
  }, [dictDataSearch]);

  const handleDictDataSearch = (value: string) => {
    setDictDataSearch(value);
    setCurrentPage(1);
  };

  // 编辑/删除字典按钮
  const OperationButtons = (
    <Space size="small">
      <Button
        permission={ACTIONS.UPDATE}
        type="secondary"
        onClick={() => {
          setAddDictModalVisible(true);
          setEditDict(activeDict!);
        }}
      >
        编辑
      </Button>
      <Button
        permission={ACTIONS.DELETE}
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
    // console.log('handleDictSelect id:', id);
    // console.log('handleDictSelect !activeDictId || activeDictId:', !activeDictId || showEmpty);
    setActiveDictId(id);
    setCurrentPage(1);
    setShowEmpty(false);
  };

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
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
      await deleteDict(id);
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
        await deleteDictData(id);
        Message.success('删除成功');
        setTableData(tableData.filter((t) => t.id !== id));
      }
    });
  };

  // 新增/编辑字典
  const handleDictModalOk = async (values: DictItem) => {
    setModalLoading(true);
    try {
      if (editDict && editDict.id) {
        await updateDict({ ...editDict, ...values });
        setDictList((prev) => prev.map((t) => (t.id === editDict.id ? { ...t, ...values } : t)));
      } else {
        await createDict(values);
        const data = await getAllDictList();
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
        await updateDictData({ ...values, dictType: editItem.dictType, id: editItem.id });
      } else if (activeDict?.type) {
        await createDictData({ dictType: activeDict.type, ...values });
      }
      // 刷新表格
      if (activeDictId !== undefined) {
        const params: PageParam & { dictType: string } = {
          dictType: dictList.find((t) => t.id === activeDictId)?.type || '',
          pageNo: currentPage,
          pageSize
        };
        const res = await getDictDataListByPage(params);
        setTableData(res.list);
        setTotal(res.total);
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
    updateDictDataStatus(params).then(() => {
      Message.success('操作成功');
      loadTableData();
    });
  };

  return (
    <div className={styles.systemDictPage}>
      <Layout className={styles.pageLayout}>
        <Sider width={252} className={styles.leftPanel}>
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
        </Sider>
        <Content className={styles.rightPanel}>
          {!activeDictId || showEmpty ? (
            <>
              <Empty />
            </>
          ) : (
            <>
              <Header>
                <InfoPanel
                  title={activeDict?.name}
                  description={activeDict?.remark}
                  rightChildren={OperationButtons}
                  wrapperClassName={styles.infoPanel}
                ></InfoPanel>
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
                onAdd={() => {
                  setDictDataModalVisible(true);
                  setEditItem(null);
                }}
                onEdit={(item) => handleDictDataEdit(item)}
                onDelete={(id) => handleDeleteDictData(id)}
                onUpdateStatus={(id, status) => handleUpdateDictDataStatus(id, status)}
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
    </div>
  );
}
