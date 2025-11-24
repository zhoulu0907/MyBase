import { Button, Table } from '@arco-design/web-react';
import { listETLTableColumns, listETLTables, previewETLDatasource, type ELTColumn, type ETLTable } from '@onebase/app';
import { ETLDrawerTab, etlEditorSignal } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useEffect, useMemo, useState } from 'react';
import DataPreview from '../../components/dataPreview';
import DataRemark from '../../components/dataRemark';
import { setNodeDataAndResetDownstream } from '../utils';
import DatasourceModal from './components/datasourceModal';
import styles from './index.module.less';

export const InputNodeConfig: React.FC = () => {
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
    nodeData.value[curNode.value.id]?.config?.fields || []
  );

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

  useEffect(() => {
    if (nodeData.value[curNode.value.id]?.config?.datasourceId && nodeData.value[curNode.value.id]?.config?.tableId) {
      handleListETLTables(nodeData.value[curNode.value.id]?.config?.datasourceId);
      handlelistETLTableColumns(nodeData.value[curNode.value.id]?.config?.tableId);
    }

    handlePreviewData();
  }, [nodeData.value[curNode.value.id]?.config?.tableId]);

  useEffect(() => {
    let payload = nodeData.value[curNode.value.id];

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

    setNodeDataAndResetDownstream(payload, curNode.value.id, graphData.value, nodeData.value);
  }, [selectedColumns]);

  const handleListETLTables = async (datasourceId: string) => {
    const res = await listETLTables({ id: datasourceId });
    setTables(res);
  };

  const handlelistETLTableColumns = async (tableId: string) => {
    const res = await listETLTableColumns({ tableId });
    console.log(res);
    setCurColumns(res);
  };

  const handlePreviewData = async () => {
    const datasourceId = nodeData.value[curNode.value.id]?.config?.datasourceId;
    const tableId = nodeData.value[curNode.value.id]?.config?.tableId;

    if (!datasourceId || !tableId) {
      return;
    }
    const res = await previewETLDatasource({
      datasourceId: datasourceId,
      tableId: tableId
    });

    console.log('res: ', res);
    if (res) {
      setPreviewData(res);
    }
  };

  const handleOk = () => {
    const payload = nodeData.value[curNode.value.id];
    if (payload?.config?.tableId) {
      handlelistETLTableColumns(payload?.config?.tableId);
    }

    setIsModalVisible(false);
  };

  const handleUpdate = (tableId: string, columns: ELTColumn[]) => {
    setCurColumns(columns);

    if (tableId !== nodeData.value[curNode.value.id]?.config?.tableId) {
      setSelectColumns([]);
      let payload = nodeData.value[curNode.value.id];

      payload.config = {
        ...payload.config,
        tableId: tableId,
        fields: []
      };

      payload.output = {
        verified: true,
        fields: []
      };
      setNodeDataAndResetDownstream(payload, curNode.value.id, graphData.value, nodeData.value);
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
          {nodeData.value[curNode.value.id]?.config?.tableId && (
            <div className={styles.dataSourceContent}>
              <div className={styles.dataSourceName}>
                {tables.find((table: ETLTable) => table.id === nodeData.value[curNode.value.id]?.config?.tableId)?.name}
              </div>

              <div className={styles.columnContent}>
                <Table
                  data={curColumns}
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
