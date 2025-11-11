import TableIcon from '@/assets/images/etl/table.svg';
import { Button, Input } from '@arco-design/web-react';
import { listETLTables, previewETLDatasource, type ETLTable } from '@onebase/app';
import { ETLDrawerTab, etlEditorSignal } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useEffect, useState } from 'react';
import DataPreview from '../../components/dataPreview';
import DatasourceModal from './components/datasourceModal';
import styles from './index.module.less';

const { TextArea } = Input;

export const InputNodeConfig: React.FC = () => {
  useSignals();

  const { curDrawerTab, nodeData, curNode } = etlEditorSignal;

  const [isModalVisible, setIsModalVisible] = useState(false);
  const [remark, setRemark] = useState<string>(nodeData.value[curNode.value.id]?.description || '');
  const [tables, setTables] = useState<ETLTable[]>([]);
  const [previewData, setPreviewData] = useState<{
    columns: any[];
    data: any[];
  }>({
    columns: [],
    data: []
  });

  const handleChangeRemark = (value: string) => {
    if (!curNode.value.id) {
      return;
    }
    nodeData.value[curNode.value.id].description = value;
    setRemark(value);
  };

  useEffect(() => {
    if (nodeData.value[curNode.value.id]?.config?.datasourceId && nodeData.value[curNode.value.id]?.config?.tableId) {
      handleListETLTables(nodeData.value[curNode.value.id]?.config?.datasourceId);
    }

    handlePreviewData();
  }, [nodeData.value[curNode.value.id]?.config?.tableId]);

  const handleListETLTables = async (datasourceId: string) => {
    const res = await listETLTables({ id: datasourceId });
    setTables(res);
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
    const previewData = {
      columns: res.columns.map((column: any) => ({
        title: column,
        dataIndex: column,
        key: column
      })),
      data: res.data.map((row: any[]) => {
        const obj: any = {};
        res.columns.forEach((col: any, idx: number) => {
          obj[col] = row[idx];
        });
        return obj;
      })
    };
    setPreviewData(previewData);
  };

  return (
    <div className={styles.config}>
      {curDrawerTab.value === ETLDrawerTab.DATA_PREVIEW && (
        <div className={styles.dataPreview}>
          <div className={styles.dataSource}>
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
                  {
                    tables.find((table: ETLTable) => table.id === nodeData.value[curNode.value.id]?.config?.tableId)
                      ?.name
                  }
                </div>
                <div className={styles.selectedFields}>已选字段</div>

                <div className={styles.dataSourceContentItems}>
                  {nodeData.value[curNode.value.id]?.config?.fields.map((field: any) => (
                    <div key={field.fieldName} className={styles.dataSourceContentItem}>
                      <img src={TableIcon} alt="column" />
                      {field.fieldName}
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
          <div className={styles.dataPreviewContent}>
            {<DataPreview data={previewData.data} columns={previewData.columns} />}
          </div>
        </div>
      )}
      {curDrawerTab.value === ETLDrawerTab.NODE_REMARK && (
        <TextArea
          onChange={handleChangeRemark}
          value={remark}
          placeholder="请输入节点备注"
          autoSize={{ minRows: 3, maxRows: 6 }}
          allowClear
        />
      )}

      <DatasourceModal
        isModalVisible={isModalVisible}
        onClose={() => setIsModalVisible(false)}
        onOk={() => setIsModalVisible(false)}
      />
    </div>
  );
};
