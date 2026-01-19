import DatabaseIcon from '@/assets/images/etl/database.svg';
import TableIcon from '@/assets/images/etl/table.svg';
import TableIconActive from '@/assets/images/etl/table_active.svg';
import CreateExternalModal from '@/components/CreateExternalModal';
import { Button, Input, Modal, Popover, Tabs } from '@arco-design/web-react';
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
import { debounce } from 'lodash-es';
import React, { useEffect, useState, useCallback } from 'react';
import styles from './index.module.less';

const TabPane = Tabs.TabPane;

interface DatasourceModalProps {
  // 控制弹窗是否显示
  isModalVisible: boolean;
  // 关闭弹窗的回调
  onClose: () => void;
  // （可选）确定按钮的回调
  onOk?: () => void;

  onUpdate: (datasourceUuid: string, tableUuid: string, columns: ELTColumn[]) => void;
}

const DatasourceModal: React.FC<DatasourceModalProps> = ({ isModalVisible, onClose, onOk, onUpdate }) => {
  useSignals();

  const { curNode, setNodeData, nodeData, graphData } = etlEditorSignal;

  const [activeTab, setActiveTab] = useState('external');
  const [dataSourceSearchValue, setDataSourceSearchValue] = useState('');
  const [allDatasources, setAllDatasources] = useState<ETLDatasource[]>([]);
  const [curDatasourceUUID, setCurDatasourceUUID] = useState<string>(
    nodeData.value[curNode.value.id]?.config?.datasourceUuid || ''
  );
  const [tablesSearchValue, setTablesSearchValue] = useState('');
  const [curTables, setCurTables] = useState<ETLTable[]>([]);
  const [selectedTableUUID, setSelectedTableUUID] = useState(nodeData.value[curNode.value.id]?.config?.tableUuid || '');
  const [popupVisible, setPopupVisible] = useState(false);
  const [columns, setColumns] = useState<ELTColumn[]>([]);

  useEffect(() => {
    if (isModalVisible) {
      handleListETLDatasources();
      setSelectedTableUUID(nodeData.value[curNode.value.id]?.config?.tableUuid || '');
    }
  }, [isModalVisible]);

  useEffect(() => {
    if (curDatasourceUUID) {
      handleListETLTables(curDatasourceUUID);
    }
  }, [curDatasourceUUID]);

  useEffect(() => {
    if (selectedTableUUID) {
      handlelistETLTableColumns(selectedTableUUID);
    }
  }, [selectedTableUUID]);

  const [createExternalModalVisible, setCreateExternalModalVisible] = useState(false);

  const handleListETLTables = async (datasourceUuid: string) => {
    const res = await listETLTables({
      uuid: datasourceUuid
    });

    setCurTables(res);
  };

  const handlelistETLTableColumns = async (tableUuid: string) => {
    const res = await listETLTableColumns({ tableUuid });
    setColumns(res);
  };

  const handleListETLDatasources = async () => {
    const curAppId = getHashQueryParam('appId');
    if (!curAppId) {
      return;
    }
    const res = await listAppETLDatasource({
      applicationId: curAppId
    });
    console.log(res);
    setAllDatasources(res);
  };

  const handleCreateExternalModalClose = () => {
    setCreateExternalModalVisible(false);
  };

  const handleCreateExternalModalCreate = (datasourceUuid: string) => {
    setCreateExternalModalVisible(false);
    setCurDatasourceUUID(datasourceUuid);
    handleListETLDatasources();
  };

  const handleTableSelect = (tableUuid: string) => {
    setSelectedTableUUID(tableUuid);
  };

  const handleOk = () => {
    setColumns(columns);
    onUpdate(curDatasourceUUID, selectedTableUUID, columns ?? []);

    onOk?.();
  };
  const handleClose = () => {
    onClose?.();
  };

  // 搜索数据表 外部
  const externalSearchTables = useCallback(
    debounce((value) => {
      setTablesSearchValue(value);
    }, 500),
    []
  );
  // 搜索数据源 外部
  const externalSearchDatasource = useCallback(
    debounce((value) => {
      setDataSourceSearchValue(value);
    }, 500),
    []
  );

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
        <div className={styles.contentWrapper}>
          <div className={styles.content}>
            <Tabs defaultActiveTab={activeTab} onChange={setActiveTab}>
              <TabPane key="internal" title="内部数据源" disabled></TabPane>
              <TabPane key="external" title="外部数据源"></TabPane>
            </Tabs>

            {activeTab == 'internal' && (
              <div className={styles.internalDatasource}>
                <div className={styles.internalDatasourceHeader}>
                  <Input.Search placeholder="搜索数据表" />
                </div>
                <div className={styles.internalDatasourceContent}></div>
              </div>
            )}

            {activeTab == 'external' && (
              <div className={styles.externalDatasource}>
                <div className={styles.externalDatasourceHeader}>
                  <Input.Search placeholder="搜索数据表" onChange={externalSearchTables} />
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
                        {curDatasourceUUID
                          ? allDatasources.find((datasource) => datasource.uuid === curDatasourceUUID)?.name
                          : '数据库'}
                      </div>
                    </div>
                    <Popover
                      popupVisible={popupVisible}
                      content={
                        <div className={styles.datasourceContent}>
                          <Input.Search
                            placeholder="搜索数据源"
                            style={{ marginBottom: '8px' }}
                            onChange={externalSearchDatasource}
                          />
                          {/* dataSourceSearchValue */}
                          {allDatasources
                            .filter(
                              (datasource) =>
                                dataSourceSearchValue === '' || datasource.name.includes(dataSourceSearchValue)
                            )
                            .map((datasource) => (
                              <div key={datasource.uuid} className={styles.datasourceItem}>
                                <div className={styles.datasourceItemName}>{datasource.name}</div>
                                <div>
                                  {curDatasourceUUID == datasource.uuid ? (
                                    <div className={styles.datasourceItemCurrent}>当前数据库</div>
                                  ) : (
                                    <IconSwap onClick={() => setCurDatasourceUUID(datasource.uuid)} />
                                  )}
                                </div>
                              </div>
                            ))}
                        </div>
                      }
                      blurToHide={false}
                      triggerProps={{
                        onClickOutside: () => {
                          setPopupVisible(false);
                        }
                      }}
                    >
                      <Button
                        type="text"
                        icon={<IconSwap />}
                        onClick={() => {
                          setPopupVisible(true);
                          setDataSourceSearchValue('');
                        }}
                      >
                        更换数据源
                      </Button>
                    </Popover>
                  </div>
                  <div className={styles.tableList}>
                    {curTables
                      .filter((table) => tablesSearchValue === '' || table.name.includes(tablesSearchValue))
                      .map((table) => (
                        <div
                          key={table.uuid}
                          className={styles.tableItem}
                          onClick={() => handleTableSelect(table.uuid)}
                        >
                          <img src={selectedTableUUID === table.uuid ? TableIconActive : TableIcon} alt="table" />
                          <div style={{ color: selectedTableUUID === table.uuid ? '#009e9e' : '#4e5969' }}>
                            {table.name}
                          </div>
                        </div>
                      ))}
                  </div>
                </div>
              </div>
            )}
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
