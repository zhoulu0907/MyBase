import DatabaseIcon from '@/assets/images/etl/database.svg';
import TableIcon from '@/assets/images/etl/table.svg';
import TableIconActive from '@/assets/images/etl/table_active.svg';
import { Button, Checkbox, Input, Modal, Popover, Tabs } from '@arco-design/web-react';
import { IconPlus, IconSwap } from '@arco-design/web-react/icon';
import {
  listAppETLDatasource,
  listETLTableColumns,
  listETLTables,
  type ELTColumn,
  type ETLDatasource,
  type ETLTable
} from '@onebase/app';
import { etlEditorSignal, getHashQueryParam } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useEffect, useMemo, useState } from 'react';
import { clearDownStreamNodeConfig, setNodeDataAndResetDownstream } from '../../../utils';
import CreateExternalModal from '../createExternalModal';
import styles from './index.module.less';

const TabPane = Tabs.TabPane;

interface DatasourceModalProps {
  // 控制弹窗是否显示
  isModalVisible: boolean;
  // 关闭弹窗的回调
  onClose: () => void;
  // （可选）确定按钮的回调
  onOk?: () => void;
}

const DatasourceModal: React.FC<DatasourceModalProps> = ({ isModalVisible, onClose, onOk }) => {
  useSignals();

  const { curNode, setNodeData, nodeData, graphData } = etlEditorSignal;

  const [activeTab, setActiveTab] = useState('external');
  const [allDatasources, setAllDatasources] = useState<ETLDatasource[]>([]);
  const [curDatasourceId, setCurDatasourceId] = useState<string>(
    nodeData.value[curNode.value.id]?.config?.datasourceId || ''
  );
  const [curTables, setCurTables] = useState<ETLTable[]>([]);
  const [selectedTableId, setSelectedTableId] = useState(nodeData.value[curNode.value.id]?.config?.tableId || '');
  const [curColumns, setCurColumns] = useState<ELTColumn[]>([]);

  const [selectedColumns, setSelectColumns] = useState<ELTColumn[]>(
    nodeData.value[curNode.value.id]?.config?.fields || []
  );

  useEffect(() => {
    if (isModalVisible) {
      handleListETLDatasources();
    }
  }, [isModalVisible]);

  useEffect(() => {
    if (curDatasourceId) {
      handleListETLTables(curDatasourceId);
    }
  }, [curDatasourceId]);

  useEffect(() => {
    if (selectedTableId) {
      handlelistETLTableColumns(selectedTableId);
    }
  }, [selectedTableId]);

  useEffect(() => {
    console.log(selectedColumns);
  }, [selectedColumns]);

  const [createExternalModalVisible, setCreateExternalModalVisible] = useState(false);

  const handleListETLTables = async (datasourceId: string) => {
    const res = await listETLTables({
      id: datasourceId
    });

    setCurTables(res);
  };

  const handlelistETLTableColumns = async (tableId: string) => {
    const res = await listETLTableColumns({ tableId });
    console.log(res);
    setCurColumns(res);
  };

  const handleListETLDatasources = async () => {
    const curAppId = getHashQueryParam('appId');
    if (!curAppId) {
      return;
    }
    const res = await listAppETLDatasource({
      applicationId: curAppId,
      writable: 0
    });
    console.log(res);
    setAllDatasources(res);
  };

  const handleCreateExternalModalClose = () => {
    setCreateExternalModalVisible(false);
  };

  const handleCreateExternalModalCreate = (datasourceId: string) => {
    setCreateExternalModalVisible(false);
    setCurDatasourceId(datasourceId);
  };

  const handleTableSelect = (tableId: string) => {
    setSelectedTableId(tableId);
    setSelectColumns([]);
  };

  const handleOk = () => {
    console.log(selectedColumns);

    let payload = nodeData.value[curNode.value.id];

    payload.config = {
      datasourceId: curDatasourceId,
      tableId: selectedTableId,
      fields: selectedColumns.map((column) => ({
        fieldName: column.fieldName,
        fieldType: column.fieldType
      }))
    };

    payload.output = {
      verified: true,
      fields: selectedColumns.map((column) => ({
        fieldFqn: `${curNode.value.id}.${column.fieldName}`,
        fieldName: column.fieldName,
        fieldType: column.fieldType
      }))
    };

    setNodeDataAndResetDownstream(payload, curNode.value.id, graphData.value, nodeData.value);

    onOk?.();
  };
  const handleClose = () => {
    onClose?.();
  };

  const handleColumnSelect = (columnId: string) => {
    setSelectColumns((prevSelected: ELTColumn[]) => {
      // 判断该字段是否已经被选中
      if (prevSelected.some((col) => col.fieldName === columnId)) {
        // 如果已经选中则移除
        return prevSelected.filter((col) => col.fieldName !== columnId);
      }
      // 否则将该字段加入已选中列表
      const column = curColumns.find((col) => col.fieldName === columnId);
      return column ? [...prevSelected, column] : prevSelected;
    });
  };

  const isAllSelected = useMemo(() => {
    if (!curColumns.length) {
      return false;
    }
    return selectedColumns.length === curColumns.length;
  }, [curColumns, selectedColumns]);

  const isIndeterminate = useMemo(() => {
    if (!curColumns.length) {
      return false;
    }
    return selectedColumns.length > 0 && selectedColumns.length < curColumns.length;
  }, [curColumns, selectedColumns]);

  const handleSelectAllColumns = (checked: boolean) => {
    if (checked) {
      setSelectColumns(curColumns);
      return;
    }
    setSelectColumns([]);
  };

  return (
    <Modal
      onCancel={handleClose}
      onOk={handleOk}
      visible={isModalVisible}
      title="更改输入源"
      style={{
        width: '720px'
      }}
    >
      <div className={styles.datasourceModal}>
        <div className={styles.left}>
          <div className={styles.leftHeader}>第一步: 选择数据源</div>
          <div className={styles.leftContent}>
            <Tabs defaultActiveTab={activeTab} onChange={setActiveTab}>
              <TabPane key="internal" title="内部数据源" disabled></TabPane>
              <TabPane key="external" title="外部数据源"></TabPane>
            </Tabs>

            {activeTab == 'internal' && (
              <div className={styles.internalDatasource}>
                <div className={styles.internalDatasourceHeader}>
                  <Input.Search placeholder="搜索内部数据源" />
                </div>
                <div className={styles.internalDatasourceContent}></div>
              </div>
            )}

            {activeTab == 'external' && (
              <div className={styles.externalDatasource}>
                <div className={styles.externalDatasourceHeader}>
                  <Input.Search placeholder="搜索外部数据源" />
                  <div className={styles.externalDatasourceCreate}>
                    <div>已创建外部数据源</div>
                    <Button type="text" icon={<IconPlus />} onClick={() => setCreateExternalModalVisible(true)}>
                      新建
                    </Button>
                  </div>
                </div>
                <div className={styles.externalDatasourceContent}>
                  <div className={styles.externalDatasourceContentItem}>
                    <div className={styles.databaseItem}>
                      <img src={DatabaseIcon} alt="database" />
                      <div>
                        {curDatasourceId
                          ? allDatasources.find((datasource) => datasource.id === curDatasourceId)?.name
                          : '数据库'}
                      </div>
                    </div>
                    <Popover
                      content={
                        <div className={styles.datasourceContent}>
                          <Input.Search placeholder="搜索数据源" style={{ marginBottom: '8px' }} />
                          {allDatasources.map((datasource) => (
                            <div key={datasource.id} className={styles.datasourceItem}>
                              <div className={styles.datasourceItemName}>{datasource.name}</div>
                              <div>
                                {curDatasourceId == datasource.id ? (
                                  <div className={styles.datasourceItemCurrent}>当前数据库</div>
                                ) : (
                                  <IconSwap onClick={() => setCurDatasourceId(datasource.id)} />
                                )}
                              </div>
                            </div>
                          ))}
                        </div>
                      }
                    >
                      <Button type="text" icon={<IconSwap />}></Button>
                    </Popover>
                  </div>
                  <div className={styles.tableList}>
                    {curTables.map((table) => (
                      <div key={table.id} className={styles.tableItem} onClick={() => handleTableSelect(table.id)}>
                        <img src={selectedTableId === table.id ? TableIconActive : TableIcon} alt="table" />
                        <div style={{ color: selectedTableId === table.id ? '#009e9e' : '#4e5969' }}>{table.name}</div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
        <div className={styles.right}>
          <div className={styles.rightHeader}>
            <div className={styles.rightHeaderTitle}>第二步: 选择字段</div>
            <div className={styles.rightHeaderSearch}>
              <Input.Search placeholder="搜索字段" />
            </div>
          </div>
          <div className={styles.rightContent}>
            <div className={styles.columnItem}>
              <Checkbox
                checked={isAllSelected}
                indeterminate={isIndeterminate}
                onChange={handleSelectAllColumns}
                style={{ height: '32px' }}
              >
                全选
              </Checkbox>
            </div>
            {curColumns.map((column: ELTColumn) => (
              <div key={column.fieldName} className={styles.columnItem}>
                <Checkbox
                  checked={selectedColumns.some((col: ELTColumn) => col.fieldName === column.fieldName)}
                  onChange={() => handleColumnSelect(column.fieldName)}
                  className={styles.columnItemName}
                  style={{ height: '32px' }}
                >
                  {column.fieldName}
                </Checkbox>
              </div>
            ))}
          </div>
        </div>
      </div>
      <CreateExternalModal
        visible={createExternalModalVisible}
        onClose={handleCreateExternalModalClose}
        onCreate={handleCreateExternalModalCreate}
      />
    </Modal>
  );
};

export default DatasourceModal;
