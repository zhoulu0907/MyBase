import DictList from '@/pages/SystemDict/components/dict-list';
import DictionaryTable from '@/pages/SystemDict/components/dict-table';
import { Layout, Message, Modal, Button, Space, Empty, Divider } from '@arco-design/web-react';
import {
    createDict,
    deleteDict,
    deleteDictData,
    getAllDictList,
    getDictDataListByPage,
    updateDict,
    getDictDetail
} from '@onebase/platform-center';
import type { PageParam } from '@onebase/platform-center';
import type { DictData, DictItem } from '@onebase/platform-center';
import { useEffect, useState } from 'react';
import DictDataModal from './components/dict-data-modal';
import DictModal from './components/dict-modal';
import InfoPanel from '@/components/InfoPanel';
import styles from './index.module.less';

const Sider = Layout.Sider;
const Header = Layout.Header;
const Content = Layout.Content;

export default function SystemDictPage() {
  const [dictList, setDictList] = useState<DictItem[]>([]);
  const [activeDictId, setActiveDictId] = useState<number | undefined>(undefined);
  const [activeDict, setActiveDict] = useState<DictItem | undefined>(undefined);
  const [tableData, setTableData] = useState<DictData[]>([]);
  const [_loading, setLoading] = useState<boolean>(false);
  const [currentPage, setCurrentPage] = useState<number>(1);
  const [pageSize, _setPageSize] = useState<number>(10);
  const [total, setTotal] = useState<number>(0);
  const [dictSearch, setDictSearch] = useState('');
  const [filteredDictList, setFilteredDictList] = useState<DictItem[]>([]);
  const [itemSearch, setItemSearch] = useState('');
  const [addDictModalVisible, setAddDictModalVisible] = useState(false);
  const [editDict, setEditDict] = useState<DictItem | null>(null);
  const [modalLoading, setModalLoading] = useState(false);
  const [dictDataModalVisible, setDictDataModalVisible] = useState(false);
  const [itemModalLoading, _setItemModalLoading] = useState(false);
  const [editItem, setEditItem] = useState<DictData | null>(null);

  useEffect(() => {
    const loadDictList = async () => {
      // TODO: 联调后移除mock数据
      // const data = await getAllDictList();
      // setDictList(data);
      // if (data.length > 0) {
      //   setActiveDictId(data[0].id);
      // }
      getAllDictList().then(data => {
        setDictList(data)
      }).catch(err => {
        console.log(err)
        setDictList([{ id: 1, name: '用户类型', type: 'user_type', status: 1, remark: '描述' },
          { id: 2, name: '角色权限', type: 'role_permission', status: 0 },
          { id: 3, name: '系统配置', type: 'system_config', remark: '这是一段描述', status: 1 }])
        })
      if (dictList.length > 0) {
        setActiveDictId(dictList[0].id);
      }
    };
    loadDictList();
  }, []);

  useEffect(() => {
    if (activeDictId !== undefined) {
      const loadTableData = async () => {
        setLoading(true);
        const params: PageParam & { dictType: string } = {
          dictType: dictList.find(t => t.id === activeDictId)?.type || '',
          pageNo: currentPage,
          pageSize,
        };
        // TODO: 调接口后移除mock data
        // const res = await getDictDataListByPage(params);
        // setTableData(res.list);
        // setTotal(res.total);
        // setLoading(false);
        getDictDataListByPage(params).then(() => { 
        }).catch(() => {
          setTableData([{ id: 10, label: '1', value: '1', status: 1, remark: '1', sort: 1}]);
        })
        
      };
      loadTableData();
    }
  }, [activeDictId, currentPage, pageSize, dictList]);

  useEffect(() => {
    if (!activeDictId) 
      return;
    const activeDict = dictList.find(item => item.id === activeDictId);
    setActiveDict(activeDict);
    // 描述从详情接口获取
    getDictDetail(activeDictId).then(res => {
      setActiveDict(prev => ({ ...prev, ...res }))
    });
  }, [activeDictId])

  useEffect(() => {
    setFilteredDictList(
      dictSearch
        ? dictList.filter((t) => t.name.includes(dictSearch) || t.type.includes(dictSearch))
        : dictList
    );
  }, [dictList, dictSearch]);

  // 编辑/删除字典按钮
  const OperationButtons = (
    <Space size='small'>
      <Button
        type="secondary"
        onClick={() => {
          setAddDictModalVisible(true);
          setEditDict(activeDict!)
        }}
      >编辑</Button>
      <Button
        type="secondary"
        onClick={() => {
          handleDeleteDict(activeDict?.id!)
        }}
      >删除</Button>
    </Space>
  )

  const handleDictSelect = (id: number | undefined) => {
    setActiveDictId(id);
    setCurrentPage(1);
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
        await deleteDict(id);
        Message.success('删除成功');
        setDictList(dictList.filter((t) => t.id !== id));
      }
    })
  }
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
        setDictList(dictList.filter((t) => t.id !== id));
      },
    });
  };

  // 新增/编辑字典
  const handleDictModalOk = async (values: DictItem) => {
    setModalLoading(true);
    try {
      if (editDict && editDict.id) {
        await updateDict({ ...editDict, ...values });
        setDictList(prev => prev.map(t => t.id === editDict.id ? { ...t, ...values } : t));
      } else {
        await createDict(values);
        const data = await getAllDictList();
        setDictList(data);
      }
      setAddDictModalVisible(false);
      setEditDict(null);
    } finally {
      setModalLoading(false);
    }
  };

  const handleDictDataEdit= (item: DictData) => {
    setEditItem(tableData.find(d => d.id === item.id) || null)
    setDictDataModalVisible(true)
  };
// 字典项新增/编辑提交
//   const handleDictDataModalOk = async (values: DictData) => {
//     setItemModalLoading(true);
//     try {
//       if (editItem && editItem.id) {
//         await updateDictData({ ...editItem, ...values });
//       } else {
//         await createDictData({ ...values });
//       }
//       // 刷新表格
//       if (activeDictId !== undefined) {
//         const params: PageParam & { dictType: string } = {
//           dictType: dictList.find(t => t.id === activeDictId)?.type || '',
//           pageNo: currentPage,
//           pageSize,
//         };
//         const res = await getDictDataListByPage(params);
//         setTableData(res.list);
//         setTotal(res.total);
//       }
//       setDictDataModalVisible(false);
//       setEditItem(null);
//     } finally {
//       setItemModalLoading(false);
//     }
//   };

  return (
    <div className={styles.systemDictPage}>
      <Layout className={styles.pageLayout}>
        <Sider width={252} className={styles.leftPanel}>
          <DictList
            list={filteredDictList}
            activeId={activeDictId || undefined}
            searchValue={dictSearch}
            onSearchChange={setDictSearch}
            onAdd={() => { setAddDictModalVisible(true); setEditDict(null); }}
            onSelect={(id) => handleDictSelect(id)}
          />
        </Sider>
        <Content className={styles.rightPanel}>
          {!activeDictId ? 
            <Empty /> :
            (
              <>
                <Header>
                  <InfoPanel
                    title={activeDict?.name}
                    description={activeDict?.remark}
                    rightChildren={OperationButtons}
                    wrapperClassName={styles.infoPanel}
                  >
                  </InfoPanel>
                  <Divider style={{ margin: '16px 0' }} />
                </Header>
                <DictionaryTable
                  data={tableData}
                  currentPage={currentPage}
                  pageSize={pageSize}
                  total={total}
                  onPageChange={handlePageChange}
                  searchValue={itemSearch}
                  onSearchChange={setItemSearch}
                  onAdd={() => { setDictDataModalVisible(true); setEditItem(null); }}
                  onEdit={(item) => handleDictDataEdit(item)}
                  onDelete={(id) => handleDeleteDictData(id)}
                />
              </>
            )}
        </Content>
      </Layout>
      <DictModal
        visible={addDictModalVisible}
        loading={modalLoading}
        initialValues={editDict ? {
          type: editDict.type,
          name: editDict.name,
          remark: editDict.remark,
          status: editDict.status,
        } : undefined}
        onOk={handleDictModalOk}
        onCancel={() => { setAddDictModalVisible(false); setEditDict(null); }}
        title={editDict ? '编辑数据字典' : '新建数据字典'}
      />
      <DictDataModal
        visible={dictDataModalVisible}
        loading={itemModalLoading}
        initialValues={editItem ? {
          label: editItem.label,
          value: editItem.value,
          remark: editItem.remark,
          sort: editItem.sort,
          status: editItem.status,
          dictType: editItem.dictType,
        } : undefined}
        onOk={()=>{}}
        // TODO(Fix Bug): 调通后解除注释
        // onOk={handleDictDataModalOk}
        onCancel={() => { setDictDataModalVisible(false); setEditItem(null); }}
        title={editItem ? '编辑字典值' : '添加字典值'}
      />
    </div>
  );
}