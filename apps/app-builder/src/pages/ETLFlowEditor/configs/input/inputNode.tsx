import { Button, Table } from '@arco-design/web-react';
import { listETLTableColumns, listETLTables, previewETLDatasource, type ELTColumn, type ETLTable } from '@onebase/app';
import { ETLDrawerTab, etlEditorSignal } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import { cloneDeep } from 'lodash-es';
import React, { useEffect, useState } from 'react';
import DataPreview from '../../components/dataPreview';
import DataRemark from '../../components/dataRemark';
import { setNodeDataAndResetDownstream } from '../utils';
import DatasourceModal from './components/datasourceModal';
import styles from './index.module.less';

type InputNodeConfigProps = { onRegisterSave?: (fn: () => void) => void };

export const InputNodeConfig: React.FC<InputNodeConfigProps> = ({ onRegisterSave }) => {
  useSignals();

  const { curDrawerTab, nodeData, curNode, graphData } = etlEditorSignal;

  const [isModalVisible, setIsModalVisible] = useState(false);
  const [tables, setTables] = useState<ETLTable[]>([]);
  const [previewData, setPreviewData] = useState<{
    columns: any[];
    data: any[];
  }>({
    columns: [],
    data: []
  });

  const [curColumns, setCurColumns] = useState<ELTColumn[]>([]);
  const [selectedColumns, setSelectColumns] = useState<ELTColumn[]>(
    cloneDeep(nodeData.value[curNode.value.id]?.config?.fields) || []
  );
  const [newPayload, setNewPayload] = useState<any>(cloneDeep(nodeData.value[curNode.value.id]));

  useEffect(() => {
    onRegisterSave?.(handleSaveInner);
  }, [onRegisterSave]);

  const handleSaveInner = () => {
    setNodeDataAndResetDownstream(newPayload, curNode.value.id, graphData.value, nodeData.value);
  };

  useEffect(() => {
    if (newPayload?.config?.datasourceUuid && newPayload?.config?.tableUuid) {
      handleListETLTables(newPayload?.config?.datasourceUuid);
      handlelistETLTableColumns(newPayload?.config?.tableUuid);
    }
  }, [newPayload?.config?.tableUuid]);

  useEffect(() => {
    if (curDrawerTab.value == ETLDrawerTab.DATA_PREVIEW) {
      handlePreviewData();
    }
  }, [curDrawerTab.value]);

  useEffect(() => {
    let payload = newPayload;

    payload.config = {
      ...payload.config,
      fields: selectedColumns
    };

    payload.output = {
      verified: true,
      fields: selectedColumns.map((column) => ({
        fieldFqn: `${curNode.value.id}.${column.fieldName}`,
        fieldName: column.fieldName,
        fieldType: column.fieldType
      }))
    };

    setNewPayload(payload);
  }, [selectedColumns]);

  const handleListETLTables = async (datasourceUuid: string) => {
    const res = await listETLTables({ uuid: datasourceUuid });
    console.log(res);
    setTables(res);
  };

  const handlelistETLTableColumns = async (tableUuid: string) => {
    const res = await listETLTableColumns({ tableUuid });
    console.log(res);
    setCurColumns(res);
  };

  const handlePreviewData = async () => {
    const datasourceUuid = newPayload?.config?.datasourceUuid;
    const tableUuid = newPayload?.config?.tableUuid;

    if (!datasourceUuid || !tableUuid) {
      return;
    }
    const res = await previewETLDatasource({
      datasourceUuid: datasourceUuid,
      tableUuid: tableUuid
    });

    console.log('res: ', res);
    if (res) {
      setPreviewData(res);
    }
  };

  const handleOk = () => {
    const payload = newPayload;
    if (payload?.config?.tableUuid) {
      handlelistETLTableColumns(payload?.config?.tableUuid);
    }

    setIsModalVisible(false);
  };

  const handleUpdate = (datasourceUuid: string, tableUuid: string, columns: ELTColumn[]) => {
    setCurColumns(columns);

    if (tableUuid !== newPayload?.config?.tableUuid) {
      setSelectColumns([]);
      let payload = newPayload;

      payload.config = {
        ...payload.config,
        datasourceUuid: datasourceUuid,
        tableUuid: tableUuid,
        fields: []
      };

      payload.output = {
        verified: true,
        fields: []
      };

      setNewPayload(payload);
    }
  };

  return (
    <div className={styles.config}>
      {curDrawerTab.value === ETLDrawerTab.DATA_CONFIG && (
        <div className={styles.dataConfig}>
          <div className={styles.dataSourceHeader}>
            <div className={styles.dataSourceHeaderLeft}>输入源</div>
            <div className={styles.dataSourceHeaderRight}>
              <Button type="text" onClick={() => setIsModalVisible(true)}>
                更改输入源
              </Button>
            </div>
          </div>
          {newPayload?.config?.tableUuid && (
            <div className={styles.dataSourceContent}>
              <div className={styles.dataSourceName}>
                {tables.find((table: ETLTable) => table.uuid === newPayload?.config?.tableUuid)?.name}
              </div>

              <div className={styles.columnContent}>
                <Table
                  data={curColumns.map((col) => ({ ...col, key: col.fieldName }))}
                  pagination={false}
                  columns={[
                    {
                      title: '字段类型',
                      dataIndex: 'fieldType'
                    },
                    {
                      title: '字段名',
                      dataIndex: 'fieldName'
                    },
                    {
                      title: '显示名称',
                      dataIndex: 'displayName'
                    }
                  ]}
                  rowSelection={{
                    type: 'checkbox',
                    selectedRowKeys: selectedColumns.map((column) => column.fieldName),
                    onChange: (_selectedRowKeys: (string | number)[], selectedRows: ELTColumn[]) => {
                      // 根据选中的行数据更新 selectedColumns
                      setSelectColumns(selectedRows);
                    },
                    onSelect: (_selected: boolean, _record: ELTColumn, selectedRows: ELTColumn[]) => {
                      // 同步更新 selectedColumns，确保状态一致
                      setSelectColumns(selectedRows);
                    }
                  }}
                  className={styles.columnTable}
                />
              </div>
            </div>
          )}
        </div>
      )}
      {curDrawerTab.value === ETLDrawerTab.DATA_PREVIEW && (
        <DataPreview data={previewData.data} columns={previewData.columns} />
      )}
      {curDrawerTab.value === ETLDrawerTab.NODE_REMARK && <DataRemark />}

      <DatasourceModal
        isModalVisible={isModalVisible}
        onClose={() => setIsModalVisible(false)}
        onOk={handleOk}
        onUpdate={handleUpdate}
      />
    </div>
  );
};
